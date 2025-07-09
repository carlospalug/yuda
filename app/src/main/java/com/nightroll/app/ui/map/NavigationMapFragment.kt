package com.nightroll.app.ui.map

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.nightroll.app.R
import com.nightroll.app.data.models.DirectionRoute
import com.nightroll.app.data.models.PlaceResult
import com.nightroll.app.databinding.FragmentNavigationMapBinding
import com.nightroll.app.service.NavigationService
import com.nightroll.app.utils.LocationUtils
import com.nightroll.app.utils.PolylineUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NavigationMapFragment : Fragment(), OnMapReadyCallback {
    
    private var _binding: FragmentNavigationMapBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: NavigationMapViewModel by viewModels()
    
    private lateinit var googleMap: GoogleMap
    private var navigationService: NavigationService? = null
    private var isServiceBound = false
    
    private var currentLocationMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var routePolyline: Polyline? = null
    private var nearbyPlaceMarkers = mutableListOf<Marker>()
    
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as NavigationService.NavigationBinder
            navigationService = binder.getService()
            isServiceBound = true
            observeNavigationService()
        }
        
        override fun onServiceDisconnected(name: ComponentName?) {
            navigationService = null
            isServiceBound = false
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNavigationMapBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupMap()
        setupObservers()
        setupClickListeners()
        bindNavigationService()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        unbindNavigationService()
        _binding = null
    }
    
    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Enable location if permission granted
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = false // We'll handle location ourselves
            getCurrentLocation()
        }
        
        // Customize map
        googleMap.uiSettings.apply {
            isZoomControlsEnabled = false
            isCompassEnabled = true
            isMapToolbarEnabled = false
            isMyLocationButtonEnabled = false
        }
        
        // Set map click listener for place selection
        googleMap.setOnMapClickListener { latLng ->
            viewModel.searchNearbyPlace(latLng.latitude, latLng.longitude)
        }
        
        // Set marker click listener
        googleMap.setOnMarkerClickListener { marker ->
            val place = marker.tag as? PlaceResult
            place?.let {
                showPlaceBottomSheet(it)
            }
            true
        }
        
        // Load nearby bars
        viewModel.loadNearbyBars()
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentLocation.collect { location ->
                location?.let { updateCurrentLocationMarker(it) }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.nearbyPlaces.collect { places ->
                updateNearbyPlaceMarkers(places)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedPlace.collect { place ->
                place?.let { showPlaceBottomSheet(it) }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentRoute.collect { route ->
                route?.let { drawRoute(it) }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isNavigating.collect { isNavigating ->
                updateNavigationUI(isNavigating)
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnMyLocation.setOnClickListener {
            getCurrentLocation()
        }
        
        binding.btnStopNavigation.setOnClickListener {
            stopNavigation()
        }
        
        binding.btnRecenter.setOnClickListener {
            recenterMap()
        }
        
        binding.btnNavigateToPlace.setOnClickListener {
            viewModel.selectedPlace.value?.let { place ->
                startNavigationToPlace(place)
            }
        }
    }
    
    private fun getCurrentLocation() {
        viewModel.getCurrentLocation()
    }
    
    private fun updateCurrentLocationMarker(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        
        currentLocationMarker?.remove()
        currentLocationMarker = googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )
        
        // Move camera to current location
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, 16f)
        )
    }
    
    private fun updateNearbyPlaceMarkers(places: List<PlaceResult>) {
        // Clear existing markers
        nearbyPlaceMarkers.forEach { it.remove() }
        nearbyPlaceMarkers.clear()
        
        // Add new markers
        places.forEach { place ->
            val latLng = LatLng(place.geometry.location.lat, place.geometry.location.lng)
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(place.name)
                    .snippet(place.vicinity ?: "")
                    .icon(getMarkerIcon(place))
            )
            
            marker?.tag = place
            marker?.let { nearbyPlaceMarkers.add(it) }
        }
    }
    
    private fun getMarkerIcon(place: PlaceResult): BitmapDescriptor {
        return when {
            place.types.contains("night_club") -> 
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
            place.types.contains("bar") -> 
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            place.name.contains("lounge", ignoreCase = true) -> 
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
            else -> 
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        }
    }
    
    private fun showPlaceBottomSheet(place: PlaceResult) {
        binding.apply {
            bottomSheetPlaceInfo.visibility = View.VISIBLE
            textPlaceName.text = place.name
            textPlaceAddress.text = place.vicinity ?: place.formatted_address ?: ""
            textPlaceRating.text = place.rating?.let { "★ $it" } ?: "No rating"
            
            // Show/hide open status
            place.opening_hours?.let { hours ->
                textOpenStatus.visibility = View.VISIBLE
                textOpenStatus.text = if (hours.open_now == true) "Open now" else "Closed"
                textOpenStatus.setTextColor(
                    if (hours.open_now == true) 
                        resources.getColor(R.color.success_color, null)
                    else 
                        resources.getColor(R.color.error_color, null)
                )
            } ?: run {
                textOpenStatus.visibility = View.GONE
            }
            
            btnCloseBottomSheet.setOnClickListener {
                bottomSheetPlaceInfo.visibility = View.GONE
            }
            
            btnGetDirections.setOnClickListener {
                startNavigationToPlace(place)
            }
        }
        
        // Move camera to place
        val latLng = LatLng(place.geometry.location.lat, place.geometry.location.lng)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
    }
    
    private fun startNavigationToPlace(place: PlaceResult) {
        val currentLoc = viewModel.currentLocation.value
        if (currentLoc != null) {
            viewModel.getDirections(
                currentLoc.latitude,
                currentLoc.longitude,
                place.geometry.location.lat,
                place.geometry.location.lng
            )
            
            // Add destination marker
            destinationMarker?.remove()
            destinationMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(place.geometry.location.lat, place.geometry.location.lng))
                    .title(place.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
            
            binding.bottomSheetPlaceInfo.visibility = View.GONE
        }
    }
    
    private fun drawRoute(route: DirectionRoute) {
        // Remove existing route
        routePolyline?.remove()
        
        // Decode and draw new route
        val polylinePoints = PolylineUtils.decode(route.overview_polyline.points)
        
        routePolyline = googleMap.addPolyline(
            PolylineOptions()
                .addAll(polylinePoints)
                .color(Color.BLUE)
                .width(8f)
                .pattern(listOf(Dot(), Gap(10f)))
        )
        
        // Fit camera to route bounds
        val boundsBuilder = LatLngBounds.Builder()
        polylinePoints.forEach { boundsBuilder.include(it) }
        val bounds = boundsBuilder.build()
        
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(bounds, 100)
        )
        
        // Start navigation service
        startNavigationService(route)
    }
    
    private fun startNavigationService(route: DirectionRoute) {
        val intent = Intent(requireContext(), NavigationService::class.java).apply {
            action = "START_NAVIGATION"
            putExtra("route", route)
        }
        requireContext().startForegroundService(intent)
        
        viewModel.setNavigating(true)
    }
    
    private fun stopNavigation() {
        val intent = Intent(requireContext(), NavigationService::class.java).apply {
            action = "STOP_NAVIGATION"
        }
        requireContext().startService(intent)
        
        // Clear route and destination
        routePolyline?.remove()
        destinationMarker?.remove()
        
        viewModel.setNavigating(false)
        viewModel.clearRoute()
    }
    
    private fun updateNavigationUI(isNavigating: Boolean) {
        binding.apply {
            if (isNavigating) {
                navigationControls.visibility = View.VISIBLE
                btnStopNavigation.visibility = View.VISIBLE
                btnRecenter.visibility = View.VISIBLE
            } else {
                navigationControls.visibility = View.GONE
                btnStopNavigation.visibility = View.GONE
                btnRecenter.visibility = View.GONE
            }
        }
    }
    
    private fun recenterMap() {
        viewModel.currentLocation.value?.let { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(latLng, 18f)
            )
        }
    }
    
    private fun bindNavigationService() {
        val intent = Intent(requireContext(), NavigationService::class.java)
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
    
    private fun unbindNavigationService() {
        if (isServiceBound) {
            requireContext().unbindService(serviceConnection)
            isServiceBound = false
        }
    }
    
    private fun observeNavigationService() {
        navigationService?.let { service ->
            viewLifecycleOwner.lifecycleScope.launch {
                service.currentInstruction.collect { instruction ->
                    binding.textCurrentInstruction.text = instruction ?: ""
                }
            }
            
            viewLifecycleOwner.lifecycleScope.launch {
                service.remainingDistance.collect { distance ->
                    binding.textRemainingDistance.text = distance ?: ""
                }
            }
            
            viewLifecycleOwner.lifecycleScope.launch {
                service.estimatedTime.collect { time ->
                    binding.textEstimatedTime.text = time ?: ""
                }
            }
        }
    }
}