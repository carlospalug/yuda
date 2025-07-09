package com.nightroll.app.ui.map

import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nightroll.app.data.models.Bar
import com.nightroll.app.data.models.WeeklyVibeEvent

class MapViewModel : ViewModel() {

    private val _allBars = MutableLiveData<List<Bar>>()
    private val _filteredBars = MutableLiveData<List<Bar>>()
    val filteredBars: LiveData<List<Bar>> = _filteredBars
    
    private val _selectedBar = MutableLiveData<Bar?>()
    val selectedBar: LiveData<Bar?> = _selectedBar
    
    private val _userLocation = MutableLiveData<Location?>()
    val userLocation: LiveData<Location?> = _userLocation
    
    private val _mapStyle = MutableLiveData<Boolean>()
    val mapStyle: LiveData<Boolean> = _mapStyle
    
    private val _followedBars = MutableLiveData<Set<String>>()
    val followedBars: LiveData<Set<String>> = _followedBars
    
    private var allBars: List<Bar> = emptyList()

    init {
        _mapStyle.value = false // Default to normal map
        _followedBars.value = setOf("1", "3") // Mock followed bars
    }

    fun loadAllBars() {
        // Enhanced mock data with more bars
        val mockBars = listOf(
            Bar(
                barId = "1",
                name = "The Rooftop Lounge",
                type = "Rooftop Bar",
                address = "123 Main St, Downtown",
                location = com.nightroll.app.data.models.Location(40.7128, -74.0060),
                imageUrl = "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
                weeklyVibe = listOf(
                    WeeklyVibeEvent("Friday", "Live DJ Set"),
                    WeeklyVibeEvent("Saturday", "Cocktail Night")
                ),
                followersCount = 1250,
                isActive = true,
                vibe = "High Energy",
                openHours = "6 PM - 2 AM"
            ),
            Bar(
                barId = "2",
                name = "Underground Club",
                type = "Nightclub",
                address = "456 Club Ave, Midtown",
                location = com.nightroll.app.data.models.Location(40.7589, -73.9851),
                imageUrl = "https://images.pexels.com/photos/2747449/pexels-photo-2747449.jpeg",
                weeklyVibe = listOf(
                    WeeklyVibeEvent("Thursday", "House Music Night"),
                    WeeklyVibeEvent("Saturday", "Electronic Dance")
                ),
                followersCount = 2100,
                isActive = true,
                vibe = "Electric",
                openHours = "9 PM - 4 AM"
            ),
            Bar(
                barId = "3",
                name = "Skyline Bar",
                type = "Cocktail Lounge",
                address = "789 High St, Upper East",
                location = com.nightroll.app.data.models.Location(40.7831, -73.9712),
                imageUrl = "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
                weeklyVibe = listOf(
                    WeeklyVibeEvent("Wednesday", "Wine Tasting"),
                    WeeklyVibeEvent("Friday", "Jazz Night")
                ),
                followersCount = 890,
                isActive = true,
                vibe = "Sophisticated",
                openHours = "5 PM - 1 AM"
            ),
            Bar(
                barId = "4",
                name = "Neon Nights",
                type = "Dance Club",
                address = "321 Party Blvd, SoHo",
                location = com.nightroll.app.data.models.Location(40.7234, -74.0020),
                imageUrl = "https://images.pexels.com/photos/2747449/pexels-photo-2747449.jpeg",
                weeklyVibe = listOf(
                    WeeklyVibeEvent("Friday", "80s Night"),
                    WeeklyVibeEvent("Saturday", "Top 40 Hits")
                ),
                followersCount = 1800,
                isActive = false,
                vibe = "Retro Vibes",
                openHours = "8 PM - 3 AM"
            ),
            Bar(
                barId = "5",
                name = "Whiskey & Wine",
                type = "Speakeasy",
                address = "654 Hidden St, Greenwich",
                location = com.nightroll.app.data.models.Location(40.7359, -74.0036),
                imageUrl = "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
                weeklyVibe = listOf(
                    WeeklyVibeEvent("Tuesday", "Whiskey Tasting"),
                    WeeklyVibeEvent("Thursday", "Live Blues")
                ),
                followersCount = 650,
                isActive = false,
                vibe = "Intimate",
                openHours = "7 PM - 12 AM"
            ),
            Bar(
                barId = "6",
                name = "Rooftop Paradise",
                type = "Rooftop Bar",
                address = "987 Sky Ave, Chelsea",
                location = com.nightroll.app.data.models.Location(40.7505, -74.0014),
                imageUrl = "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
                weeklyVibe = listOf(
                    WeeklyVibeEvent("Sunday", "Brunch & Beats"),
                    WeeklyVibeEvent("Friday", "Sunset Sessions")
                ),
                followersCount = 1400,
                isActive = true,
                vibe = "Chill Vibes",
                openHours = "4 PM - 1 AM"
            )
        )
        allBars = mockBars
        _allBars.value = mockBars
        _filteredBars.value = mockBars.filter { it.isActive } // Default to active bars
    }
    
    fun filterBars(filter: String) {
        val userLoc = _userLocation.value
        val filtered = when (filter) {
            "all" -> allBars
            "active" -> allBars.filter { it.isActive }
            "nearby" -> if (userLoc != null) {
                allBars.filter { bar ->
                    val distance = calculateDistance(
                        userLoc.latitude, userLoc.longitude,
                        bar.location.lat, bar.location.lng
                    )
                    distance < 5.0 // Within 5km
                }
            } else allBars
            else -> allBars
        }
        _filteredBars.value = filtered
    }
    
    fun selectBar(bar: Bar) {
        _selectedBar.value = bar
    }
    
    fun setUserLocation(location: Location) {
        _userLocation.value = location
    }
    
    fun toggleMapStyle() {
        _mapStyle.value = !(_mapStyle.value ?: false)
    }
    
    fun toggleFollowBar(bar: Bar) {
        val currentFollowed = _followedBars.value ?: setOf()
        val newFollowed = if (currentFollowed.contains(bar.barId)) {
            currentFollowed - bar.barId
        } else {
            currentFollowed + bar.barId
        }
        _followedBars.value = newFollowed
    }
    
    fun getDirections(bar: Bar) {
        // This would typically be handled by the fragment/activity
        // For now, we'll just update the selected bar
        _selectedBar.value = bar
    }
    
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // Earth's radius in kilometers
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return earthRadius * c
    }
}