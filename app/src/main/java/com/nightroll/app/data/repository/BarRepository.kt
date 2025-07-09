package com.nightroll.app.data.repository

import com.nightroll.app.data.models.Bar
import com.nightroll.app.data.models.Location
import com.nightroll.app.data.models.WeeklyVibeEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarRepository @Inject constructor() {
    
    // Mock data - will be replaced with actual API calls when backend is added
    private val mockBars = listOf(
        Bar(
            barId = "1",
            name = "The Rooftop Lounge",
            type = "Rooftop Bar",
            address = "123 Main St, Downtown",
            location = Location(40.7128, -74.0060),
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
            location = Location(40.7589, -73.9851),
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
            location = Location(40.7831, -73.9712),
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
            location = Location(40.7234, -74.0020),
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
            location = Location(40.7359, -74.0036),
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
            location = Location(40.7505, -74.0014),
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
    
    fun getAllBars(): Flow<List<Bar>> = flow {
        delay(500) // Simulate network delay
        emit(mockBars)
    }
    
    fun getActiveBars(): Flow<List<Bar>> = flow {
        delay(300)
        emit(mockBars.filter { it.isActive })
    }
    
    fun getNearbyBars(userLocation: Location, radiusKm: Double = 5.0): Flow<List<Bar>> = flow {
        delay(400)
        // Simple distance calculation for mock data
        val nearbyBars = mockBars.filter { bar ->
            val distance = calculateDistance(
                userLocation.lat, userLocation.lng,
                bar.location.lat, bar.location.lng
            )
            distance <= radiusKm
        }
        emit(nearbyBars)
    }
    
    fun getTrendingBars(): Flow<List<Bar>> = flow {
        delay(350)
        emit(mockBars.sortedByDescending { it.followersCount }.take(5))
    }
    
    fun getBarById(barId: String): Flow<Bar?> = flow {
        delay(200)
        emit(mockBars.find { it.barId == barId })
    }
    
    fun searchBars(query: String): Flow<List<Bar>> = flow {
        delay(300)
        val results = mockBars.filter { bar ->
            bar.name.contains(query, ignoreCase = true) ||
            bar.type.contains(query, ignoreCase = true) ||
            bar.address.contains(query, ignoreCase = true)
        }
        emit(results)
    }
    
    fun filterBarsByType(type: String): Flow<List<Bar>> = flow {
        delay(250)
        val filtered = when (type.lowercase()) {
            "rooftop" -> mockBars.filter { it.type.contains("Rooftop", ignoreCase = true) }
            "club" -> mockBars.filter { it.type.contains("Club", ignoreCase = true) }
            "lounge" -> mockBars.filter { it.type.contains("Lounge", ignoreCase = true) }
            "all" -> mockBars.filter { it.isActive }
            else -> mockBars
        }
        emit(filtered)
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