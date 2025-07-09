package com.nightroll.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.nightroll.app.MainActivity
import com.nightroll.app.R
import com.nightroll.app.data.models.DirectionRoute
import com.nightroll.app.data.models.DirectionStep
import com.nightroll.app.utils.LocationUtils
import com.nightroll.app.utils.PolylineUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NavigationService : Service(), TextToSpeech.OnInitListener {
    
    companion object {
        private const val CHANNEL_ID = "navigation_channel"
        private const val NOTIFICATION_ID = 2001
        private const val LOCATION_UPDATE_INTERVAL = 2000L // 2 seconds
        private const val FASTEST_UPDATE_INTERVAL = 1000L // 1 second
        private const val INSTRUCTION_DISTANCE_THRESHOLD = 100 // meters
        private const val RECALCULATION_DISTANCE_THRESHOLD = 50 // meters off route
    }
    
    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private val binder = NavigationBinder()
    private var textToSpeech: TextToSpeech? = null
    private var isNavigating = false
    private var currentRoute: DirectionRoute? = null
    private var currentStepIndex = 0
    private var lastAnnouncedStep = -1
    
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private val _navigationState = MutableStateFlow<NavigationState>(NavigationState.Idle)
    val navigationState: StateFlow<NavigationState> = _navigationState
    
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation
    
    private val _currentInstruction = MutableStateFlow<String?>(null)
    val currentInstruction: StateFlow<String?> = _currentInstruction
    
    private val _remainingDistance = MutableStateFlow<String?>(null)
    val remainingDistance: StateFlow<String?> = _remainingDistance
    
    private val _estimatedTime = MutableStateFlow<String?>(null)
    val estimatedTime: StateFlow<String?> = _estimatedTime
    
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                _currentLocation.value = location
                if (isNavigating) {
                    processLocationUpdate(location)
                }
            }
        }
    }
    
    inner class NavigationBinder : Binder() {
        fun getService(): NavigationService = this@NavigationService
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeTextToSpeech()
    }
    
    override fun onBind(intent: Intent?): IBinder = binder
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START_NAVIGATION" -> {
                val route = intent.getParcelableExtra<DirectionRoute>("route")
                route?.let { startNavigation(it) }
            }
            "STOP_NAVIGATION" -> stopNavigation()
        }
        return START_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopNavigation()
        textToSpeech?.shutdown()
        serviceScope.cancel()
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.getDefault()
            textToSpeech?.setSpeechRate(0.9f)
        }
    }
    
    fun startNavigation(route: DirectionRoute) {
        currentRoute = route
        currentStepIndex = 0
        lastAnnouncedStep = -1
        isNavigating = true
        
        _navigationState.value = NavigationState.Navigating
        
        startLocationUpdates()
        startForeground(NOTIFICATION_ID, createNavigationNotification())
        
        // Announce first instruction
        route.legs.firstOrNull()?.steps?.firstOrNull()?.let { firstStep ->
            announceInstruction(firstStep.html_instructions)
        }
    }
    
    fun stopNavigation() {
        isNavigating = false
        currentRoute = null
        currentStepIndex = 0
        lastAnnouncedStep = -1
        
        _navigationState.value = NavigationState.Idle
        _currentInstruction.value = null
        _remainingDistance.value = null
        _estimatedTime.value = null
        
        stopLocationUpdates()
        stopForeground(true)
        stopSelf()
    }
    
    private fun startLocationUpdates() {
        if (!LocationUtils.hasLocationPermission(this)) return
        
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            LOCATION_UPDATE_INTERVAL
        ).apply {
            setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL)
            setMaxUpdateDelayMillis(LOCATION_UPDATE_INTERVAL * 2)
        }.build()
        
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                serviceScope.coroutineContext.asExecutor()
            )
        } catch (e: SecurityException) {
            // Handle permission error
        }
    }
    
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    
    private fun processLocationUpdate(location: Location) {
        val route = currentRoute ?: return
        val currentLeg = route.legs.firstOrNull() ?: return
        
        // Update remaining distance and time
        updateNavigationInfo(location, currentLeg)
        
        // Check if we need to move to next step
        checkStepProgress(location, currentLeg.steps)
        
        // Check if we're off route
        checkOffRoute(location, currentLeg.steps)
    }
    
    private fun updateNavigationInfo(location: Location, leg: com.nightroll.app.data.models.DirectionLeg) {
        val remainingSteps = leg.steps.drop(currentStepIndex)
        val totalRemainingDistance = remainingSteps.sumOf { it.distance.value }
        val totalRemainingTime = remainingSteps.sumOf { it.duration.value }
        
        _remainingDistance.value = formatDistance(totalRemainingDistance)
        _estimatedTime.value = formatDuration(totalRemainingTime)
    }
    
    private fun checkStepProgress(location: Location, steps: List<DirectionStep>) {
        if (currentStepIndex >= steps.size) {
            // Navigation complete
            _navigationState.value = NavigationState.Completed
            announceInstruction("You have arrived at your destination")
            stopNavigation()
            return
        }
        
        val currentStep = steps[currentStepIndex]
        val distanceToStepEnd = LocationUtils.calculateDistance(
            location.latitude, location.longitude,
            currentStep.end_location.lat, currentStep.end_location.lng
        ) * 1000 // Convert to meters
        
        // If we're close to the end of current step, move to next step
        if (distanceToStepEnd < 20) { // 20 meters threshold
            currentStepIndex++
            
            if (currentStepIndex < steps.size) {
                val nextStep = steps[currentStepIndex]
                _currentInstruction.value = cleanHtmlInstructions(nextStep.html_instructions)
                
                // Announce next instruction if we haven't already
                if (currentStepIndex != lastAnnouncedStep) {
                    announceInstruction(nextStep.html_instructions)
                    lastAnnouncedStep = currentStepIndex
                }
            }
        } else if (distanceToStepEnd < INSTRUCTION_DISTANCE_THRESHOLD && currentStepIndex != lastAnnouncedStep) {
            // Announce upcoming instruction
            announceInstruction(currentStep.html_instructions)
            lastAnnouncedStep = currentStepIndex
        }
    }
    
    private fun checkOffRoute(location: Location, steps: List<DirectionStep>) {
        if (currentStepIndex >= steps.size) return
        
        val currentStep = steps[currentStepIndex]
        val polylinePoints = PolylineUtils.decode(currentStep.polyline.points)
        
        // Find closest point on route
        var minDistance = Double.MAX_VALUE
        for (point in polylinePoints) {
            val distance = LocationUtils.calculateDistance(
                location.latitude, location.longitude,
                point.latitude, point.longitude
            ) * 1000 // Convert to meters
            
            if (distance < minDistance) {
                minDistance = distance
            }
        }
        
        // If user is too far from route, trigger recalculation
        if (minDistance > RECALCULATION_DISTANCE_THRESHOLD) {
            _navigationState.value = NavigationState.Recalculating
            announceInstruction("Recalculating route")
            // Here you would trigger route recalculation
        }
    }
    
    private fun announceInstruction(instruction: String) {
        val cleanInstruction = cleanHtmlInstructions(instruction)
        _currentInstruction.value = cleanInstruction
        
        textToSpeech?.speak(
            cleanInstruction,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "navigation_instruction"
        )
    }
    
    private fun cleanHtmlInstructions(html: String): String {
        return html
            .replace("<[^>]*>".toRegex(), "") // Remove HTML tags
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .trim()
    }
    
    private fun formatDistance(meters: Int): String {
        return if (meters < 1000) {
            "$meters m"
        } else {
            val km = meters / 1000.0
            String.format("%.1f km", km)
        }
    }
    
    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        return if (minutes < 60) {
            "$minutes min"
        } else {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            "${hours}h ${remainingMinutes}m"
        }
    }
    
    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(this, this)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Navigation",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Turn-by-turn navigation"
                setSound(null, null)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNavigationNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val stopIntent = Intent(this, NavigationService::class.java).apply {
            action = "STOP_NAVIGATION"
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Nightroll Navigation")
            .setContentText(_currentInstruction.value ?: "Navigating...")
            .setSmallIcon(R.drawable.ic_directions)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_arrow_back, "Stop", stopPendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_NAVIGATION)
            .build()
    }
    
    sealed class NavigationState {
        object Idle : NavigationState()
        object Navigating : NavigationState()
        object Recalculating : NavigationState()
        object Completed : NavigationState()
    }
}