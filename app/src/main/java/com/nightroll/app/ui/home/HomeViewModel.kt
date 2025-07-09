package com.nightroll.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nightroll.app.data.models.Bar
import com.nightroll.app.data.models.Location
import com.nightroll.app.data.models.Reel
import com.nightroll.app.data.models.WeeklyVibeEvent
import java.util.Date

class HomeViewModel : ViewModel() {

    private val _activeBars = MutableLiveData<List<Bar>>()
    val activeBars: LiveData<List<Bar>> = _activeBars
    
    private val _nearbyBars = MutableLiveData<List<Bar>>()
    val nearbyBars: LiveData<List<Bar>> = _nearbyBars
    
    private val _trendingBars = MutableLiveData<List<Bar>>()
    val trendingBars: LiveData<List<Bar>> = _trendingBars
    
    private val _followingReels = MutableLiveData<List<Reel>>()
    val followingReels: LiveData<List<Reel>> = _followingReels
    
    private val _userStatus = MutableLiveData<Boolean>()
    val userStatus: LiveData<Boolean> = _userStatus
    
    private val _searchResults = MutableLiveData<List<Bar>>()
    val searchResults: LiveData<List<Bar>> = _searchResults
    
    private var allBars: List<Bar> = emptyList()

    init {
        loadAllData()
    }

    fun setUserStatusActive() {
        _userStatus.value = true
        // Simulate API call delay
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            loadActiveBars() // Refresh active bars when user goes active
        }, 1000)
    }
    
    fun playReel(reelId: String) {
        // TODO: Implement reel player functionality
        // For now, just simulate playing
        println("Playing reel: $reelId")
    }
    
    fun searchBars(query: String) {
        val results = allBars.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.type.contains(query, ignoreCase = true) ||
            it.address.contains(query, ignoreCase = true)
        }
        _searchResults.value = results
    }
    
    fun clearSearch() {
        _searchResults.value = emptyList()
        loadActiveBars() // Reset to active bars
    }
    
    fun filterBars(filter: String) {
        val filtered = when (filter) {
            "rooftop" -> allBars.filter { it.type.contains("Rooftop", ignoreCase = true) }
            "club" -> allBars.filter { it.type.contains("Club", ignoreCase = true) }
            "lounge" -> allBars.filter { it.type.contains("Lounge", ignoreCase = true) }
            else -> allBars.filter { it.isActive }
        }
        _activeBars.value = filtered
    }
    
    fun refreshData() {
        loadAllData()
    }
    
    private fun loadAllData() {
        loadActiveBars()
        loadNearbyBars()
        loadTrendingBars()
        loadFollowingReels()
    }
    
    private fun loadActiveBars() {
        // Enhanced mock data with more variety
        val mockBars = listOf(
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
                isActive = true,
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
            )
        )
        allBars = mockBars
        _activeBars.value = mockBars.filter { it.isActive }
    }
    
    private fun loadNearbyBars() {
        // Simulate nearby bars (subset of all bars)
        val nearbyBars = allBars.take(3)
        _nearbyBars.value = nearbyBars
    }
    
    private fun loadTrendingBars() {
        // Sort by followers count for trending
        val trendingBars = allBars.sortedByDescending { it.followersCount }.take(3)
        _trendingBars.value = trendingBars
    }
    
    private fun loadFollowingReels() {
        // Enhanced mock reels data
        val mockReels = listOf(
            Reel(
                reelId = "1",
                videoUrl = "https://example.com/reel1.mp4",
                thumbnailUrl = "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
                creatorId = "bar1",
                timestamp = Date()
            ),
            Reel(
                reelId = "2",
                videoUrl = "https://example.com/reel2.mp4",
                thumbnailUrl = "https://images.pexels.com/photos/2747449/pexels-photo-2747449.jpeg",
                creatorId = "bar2",
                timestamp = Date()
            ),
            Reel(
                reelId = "3",
                videoUrl = "https://example.com/reel3.mp4",
                thumbnailUrl = "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
                creatorId = "bar3",
                timestamp = Date()
            ),
            Reel(
                reelId = "4",
                videoUrl = "https://example.com/reel4.mp4",
                thumbnailUrl = "https://images.pexels.com/photos/2747449/pexels-photo-2747449.jpeg",
                creatorId = "bar4",
                timestamp = Date()
            ),
            Reel(
                reelId = "5",
                videoUrl = "https://example.com/reel5.mp4",
                thumbnailUrl = "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
                creatorId = "bar5",
                timestamp = Date()
            )
        )
        _followingReels.value = mockReels
    }
}