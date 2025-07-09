package com.nightroll.app.ui.map

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.nightroll.app.data.models.DirectionRoute
import com.nightroll.app.data.models.PlaceResult
import com.nightroll.app.data.repository.PlacesRepository
import com.nightroll.app.utils.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationMapViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {
    
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation
    
    private val _nearbyPlaces = MutableStateFlow<List<PlaceResult>>(emptyList())
    val nearbyPlaces: StateFlow<List<PlaceResult>> = _nearbyPlaces
    
    private val _selectedPlace = MutableStateFlow<PlaceResult?>(null)
    val selectedPlace: StateFlow<PlaceResult?> = _selectedPlace
    
    private val _currentRoute = MutableStateFlow<DirectionRoute?>(null)
    val currentRoute: StateFlow<DirectionRoute?> = _currentRoute
    
    private val _isNavigating = MutableStateFlow(false)
    val isNavigating: StateFlow<Boolean> = _isNavigating
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    fun getCurrentLocation() {
        viewModelScope.launch {
            try {
                val location = LocationUtils.getCurrentLocation(
                    context = null, // Will be handled in the utils
                    fusedLocationClient = fusedLocationClient
                )
                _currentLocation.value = location
                
                // Load nearby places when location is obtained
                location?.let {
                    loadNearbyBars(it.latitude, it.longitude)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun loadNearbyBars(latitude: Double? = null, longitude: Double? = null) {
        val lat = latitude ?: _currentLocation.value?.latitude ?: return
        val lng = longitude ?: _currentLocation.value?.longitude ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                placesRepository.getNearbyBars(lat, lng).collect { places ->
                    _nearbyPlaces.value = places
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun searchNearbyPlace(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                placesRepository.getNearbyBars(latitude, longitude, radius = 100).collect { places ->
                    places.firstOrNull()?.let { place ->
                        _selectedPlace.value = place
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun getDirections(
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                placesRepository.getDirections(
                    originLat, originLng, destLat, destLng
                ).collect { directionsResponse ->
                    directionsResponse?.routes?.firstOrNull()?.let { route ->
                        _currentRoute.value = route
                    }
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun setNavigating(navigating: Boolean) {
        _isNavigating.value = navigating
    }
    
    fun clearRoute() {
        _currentRoute.value = null
        _selectedPlace.value = null
    }
    
    fun selectPlace(place: PlaceResult) {
        _selectedPlace.value = place
    }
}