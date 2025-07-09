package com.nightroll.app.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.nightroll.app.R
import com.nightroll.app.data.models.PlaceResult
import com.nightroll.app.databinding.FragmentMapBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var mapViewModel: MapViewModel
    private val navigationMapViewModel: NavigationMapViewModel by viewModels()
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var realTimePlaceMarkers = mutableListOf<Marker>()
    
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        
        setupMap()
        setupObservers()
        setupViews()
        
        return binding.root
    }
    
    private fun setupViews() {
        // Setup filter buttons
        binding.btnMapAll.setOnClickListener { 
            mapViewModel.filterBars("all")
            updateFilterButtons("all")
        }
        binding.btnMapActive.setOnClickListener { 
            mapViewModel.filterBars("active")
            updateFilterButtons("active")
        }
        binding.btnMapNearby.setOnClickListener { 
            mapViewModel.filterBars("nearby")
            updateFilterButtons("nearby")
        }
        
        // Setup location button
        binding.btnMyLocation.setOnClickListener {
            getCurrentLocation()
        }
        
        // Setup map style toggle
        binding.btnMapStyle.setOnClickListener {
            mapViewModel.toggleMapStyle()
        }
    }
    
    private fun updateFilterButtons(selected: String) {
        val primaryColor = resources.getColor(R.color.button_primary_background, null)
        val transparentColor = resources.getColor(android.R.color.transparent, null)
        val whiteColor = resources.getColor(android.R.color.white, null)
        val blackColor = resources.getColor(android.R.color.black, null)
        
        // Reset all buttons
        listOf(binding.btnMapAll, binding.btnMapActive, binding.btnMapNearby).forEach { button ->
            button.setBackgroundColor(transparentColor)
            button.setTextColor(blackColor)
        }
        
        // Highlight selected button
        when (selected) {
            "all" -> {
                binding.btnMapAll.setBackgroundColor(primaryColor)
                binding.btnMapAll.setTextColor(whiteColor)
            }
            "active" -> {
                binding.btnMapActive.setBackgroundColor(primaryColor)
                binding.btnMapActive.setTextColor(whiteColor)
            }
            "nearby" -> {
                binding.btnMapNearby.setBackgroundColor(primaryColor)
                binding.btnMapNearby.setTextColor(whiteColor)
            }
        }
    }
    
    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    
    private fun setupObservers() {
        mapViewModel.filteredBars.observe(viewLifecycleOwner) { bars ->
            if (::googleMap.isInitialized) {
                googleMap.clear()
                realTimePlaceMarkers.clear()
                bars.forEach { bar ->
                    val position = LatLng(bar.location.lat, bar.location.lng)
                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title(bar.name)
                            .snippet("${bar.type} • ${bar.vibe}")
                            .icon(if (bar.isActive) 
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                                else BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREY)) // 여기가 수정된 부분
                    )
                    marker?.tag = bar
                }
            }
        }
        
        // Observe real-time places from Google Places API
        viewLifecycleOwner.lifecycleScope.launch {
            navigationMapViewModel.nearbyPlaces.collect { places ->
                updateRealTimePlaceMarkers(places)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            navigationMapViewModel.currentLocation.collect { location ->
                location?.let { 
                    mapViewModel.setUserLocation(it)
                    // Load real-time places when location updates
                    navigationMapViewModel.loadNearbyBars(it.latitude, it.longitude)
                }
            }
        }
        
        mapViewModel.mapStyle.observe(viewLifecycleOwner) { isDark ->
            if (::googleMap.isInitialized) {
                googleMap.mapType = if (isDark) GoogleMap.MAP_TYPE_SATELLITE else GoogleMap.MAP_TYPE_NORMAL
            }
        }
        
        mapViewModel.userLocation.observe(viewLifecycleOwner) { location ->
            if (::googleMap.isInitialized && location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)
                googleMap.addMarker(
                    MarkerOptions()
                        .position(userLatLng)
                        .title("You are here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                )
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
            }
        }
    }

    private fun updateRealTimePlaceMarkers(places: List<PlaceResult>) {
        // Clear existing real-time markers
        realTimePlaceMarkers.forEach { it.remove() }
        realTimePlaceMarkers.clear()
        
        // Add new real-time place markers
        places.forEach { place ->
            val latLng = LatLng(place.geometry.location.lat, place.geometry.location.lng)
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(place.name)
                    .snippet(place.vicinity ?: "")
                    .icon(getRealTimePlaceIcon(place))
            )
            
            marker?.tag = place
            marker?.let { realTimePlaceMarkers.add(it) }
        }
    }
    
    private fun getRealTimePlaceIcon(place: PlaceResult): com.google.android.gms.maps.model.BitmapDescriptor {
        return when {
            place.types.contains("night_club") -> 
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)
            place.types.contains("bar") -> 
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            place.name.contains("lounge", ignoreCase = true) -> 
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
            place.rating != null && place.rating >= 4.0 -> 
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            else -> 
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        }
    }
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.setOnMarkerClickListener(this)
        
        // Enable location if permission granted
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            getCurrentLocation()
        } else {
            requestLocationPermission()
        }
        
        // Set default location (New York City)
        val defaultLocation = LatLng(40.7128, -74.0060)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
        
        // Customize map UI
        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = false
        }
        
        // Set up camera change listener to load places as user moves
        googleMap.setOnCameraIdleListener {
            val center = googleMap.cameraPosition.target
            navigationMapViewModel.loadNearbyBars(center.latitude, center.longitude)
        }
        
        mapViewModel.loadAllBars()
        navigationMapViewModel.getCurrentLocation()
    }
    
    override fun onMarkerClick(marker: Marker): Boolean {
        when (val tag = marker.tag) {
            is com.nightroll.app.data.models.Bar -> {
                mapViewModel.selectBar(tag)
                showBarInfoBottomSheet(tag)
            }
            is PlaceResult -> {
                showRealTimePlaceBottomSheet(tag)
            }
        }
        return true
    }
    
    private fun showBarInfoBottomSheet(bar: com.nightroll.app.data.models.Bar) {
        // Update bottom sheet with bar info
        binding.apply {
            bottomSheetBarInfo.visibility = View.VISIBLE
            textBarName.text = bar.name
            textBarType.text = bar.type
            textBarAddress.text = bar.address
            textBarVibe.text = bar.vibe
            textBarHours.text = bar.openHours
            textFollowers.text = "${bar.followersCount} followers"
            
            // Load bar image
            com.bumptech.glide.Glide.with(requireContext())
                .load(bar.imageUrl)
                .centerCrop()
                .into(imageBarInfo)
            
            btnGetDirections.setOnClickListener {
                mapViewModel.getDirections(bar)
                startNavigationToBar(bar)
            }
            
            btnFollowBar.setOnClickListener {
                mapViewModel.toggleFollowBar(bar)
            }
            
            btnCloseBottomSheet.setOnClickListener {
                bottomSheetBarInfo.visibility = View.GONE
            }
        }
    }
    
    private fun showRealTimePlaceBottomSheet(place: PlaceResult) {
        binding.apply {
            bottomSheetBarInfo.visibility = View.VISIBLE
            textBarName.text = place.name
            textBarType.text = place.types.firstOrNull()?.replace("_", " ")?.capitalize() ?: "Place"
            textBarAddress.text = place.vicinity ?: place.formatted_address ?: ""
            textBarVibe.text = place.rating?.let { "★ $it" } ?: "No rating"
            textBarHours.text = if (place.opening_hours?.open_now == true) "Open now" else "Hours unknown"
            textFollowers.text = "Google Places"
            
            // Load place photo if available
            place.photos?.firstOrNull()?.let { photo ->
                val photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${photo.photo_reference}&key=${com.nightroll.app.utils.Constants.GOOGLE_MAPS_API_KEY}"
                com.bumptech.glide.Glide.with(requireContext())
                    .load(photoUrl)
                    .centerCrop()
                    .into(imageBarInfo)
            }
            
            btnGetDirections.setOnClickListener {
                startNavigationToPlace(place)
            }
            
            btnFollowBar.text = "View Details"
            btnFollowBar.setOnClickListener {
                // Could open place details or add to favorites
            }
            
            btnCloseBottomSheet.setOnClickListener {
                bottomSheetBarInfo.visibility = View.GONE
            }
        }
    }
    
    private fun startNavigationToBar(bar: com.nightroll.app.data.models.Bar) {
        val currentLoc = navigationMapViewModel.currentLocation.value
        if (currentLoc != null) {
            // Start navigation using NavigationMapFragment logic
            val intent = android.content.Intent(requireContext(), com.nightroll.app.ui.map.NavigationActivity::class.java).apply {
                putExtra("destination_lat", bar.location.lat)
                putExtra("destination_lng", bar.location.lng)
                putExtra("destination_name", bar.name)
            }
            startActivity(intent)
        }
    }
    
    private fun startNavigationToPlace(place: PlaceResult) {
        val currentLoc = navigationMapViewModel.currentLocation.value
        if (currentLoc != null) {
            val intent = android.content.Intent(requireContext(), com.nightroll.app.ui.map.NavigationActivity::class.java).apply {
                putExtra("destination_lat", place.geometry.location.lat)
                putExtra("destination_lng", place.geometry.location.lng)
                putExtra("destination_name", place.name)
            }
            startActivity(intent)
        }
    }
    
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    mapViewModel.setUserLocation(it)
                    navigationMapViewModel.getCurrentLocation()
                }
            }
        }
    }
    
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (::googleMap.isInitialized) {
                        if (ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            googleMap.isMyLocationEnabled = true
                            getCurrentLocation()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}