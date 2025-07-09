package com.nightroll.app.ui.nightlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nightroll.app.data.models.Bar
import com.nightroll.app.data.models.HangoutList
import com.nightroll.app.data.models.Location
import com.nightroll.app.data.models.LocationHistory
import com.nightroll.app.data.models.WeeklyVibeEvent

class NightlistViewModel : ViewModel() {

    private val _hangoutLists = MutableLiveData<List<HangoutListItem>>()
    val hangoutLists: LiveData<List<HangoutListItem>> = _hangoutLists
    
    private val _locationHistory = MutableLiveData<List<LocationHistory>>()
    val locationHistory: LiveData<List<LocationHistory>> = _locationHistory
    
    private val _filteredHistory = MutableLiveData<List<LocationHistory>>()
    val filteredHistory: LiveData<List<LocationHistory>> = _filteredHistory
    
    private val _favouriteBars = MutableLiveData<List<Bar>>()
    val favouriteBars: LiveData<List<Bar>> = _favouriteBars
    
    private val _starredBars = MutableLiveData<List<Bar>>()
    val starredBars: LiveData<List<Bar>> = _starredBars
    
    private val _nextOutings = MutableLiveData<List<Bar>>()
    val nextOutings: LiveData<List<Bar>> = _nextOutings
    
    private val _labelledPlaces = MutableLiveData<List<Bar>>()
    val labelledPlaces: LiveData<List<Bar>> = _labelledPlaces
    
    private var allHistory: List<LocationHistory> = emptyList()

    init {
        loadHangoutLists()
        loadLocationHistory()
        loadFavouriteBars()
        loadStarredBars()
    }

    fun navigateToHangoutList(listType: String) {
        when (listType) {
            "Favourites" -> loadFavouriteBars()
            "Starred Places" -> loadStarredBars()
            "Next Outings" -> loadNextOutings()
            "Labelled" -> loadLabelledPlaces()
        }
    }
    
    fun navigateToBar(barId: String) {
        // TODO: Implement navigation to bar details
        println("Navigating to bar: $barId")
    }
    
    fun filterHistory(filter: String) {
        val filtered = when (filter) {
            "yesterday" -> filterByYesterday()
            "places" -> filterByPlaces()
            "month" -> filterByMonth()
            "cities" -> filterByCities()
            else -> allHistory
        }
        _filteredHistory.value = filtered
    }
    
    fun showAddToListDialog(listType: String) {
        // TODO: Show dialog to add places to list
        println("Show add dialog for: $listType")
    }
    
    fun loadNextOutings() {
        val mockOutings = listOf(
            Bar(
                barId = "next1",
                name = "Friday Night at Rooftop",
                type = "Planned Outing",
                address = "123 Main St, Downtown",
                location = Location(40.7128, -74.0060),
                imageUrl = "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
                weeklyVibe = emptyList(),
                followersCount = 0,
                isActive = false,
                vibe = "Planned",
                openHours = "Friday 8 PM"
            )
        )
        _nextOutings.value = mockOutings
    }
    
    fun loadLabelledPlaces() {
        val mockLabelled = listOf(
            Bar(
                barId = "label1",
                name = "Date Night Spot",
                type = "Romantic",
                address = "789 Love St, Midtown",
                location = Location(40.7589, -73.9851),
                imageUrl = "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
                weeklyVibe = emptyList(),
                followersCount = 0,
                isActive = false,
                vibe = "Romantic",
                openHours = "6 PM - 12 AM"
            )
        )
        _labelledPlaces.value = mockLabelled
    }
    
    private fun loadHangoutLists() {
        val mockLists = listOf(
            HangoutListItem("Favourites", "heart", 12),
            HangoutListItem("Next Outings", "calendar", 3),
            HangoutListItem("Labelled", "bookmark", 8),
            HangoutListItem("Starred Places", "star", 15)
        )
        _hangoutLists.value = mockLists
    }
    
    private fun loadLocationHistory() {
        val mockHistory = listOf(
            LocationHistory("1", "user1", "bar1", "New York", "2024-01-15"),
            LocationHistory("2", "user1", "bar2", "New York", "2024-01-14"),
            LocationHistory("3", "user1", "bar3", "Brooklyn", "2024-01-13"),
            LocationHistory("4", "user1", "bar4", "Manhattan", "2024-01-12"),
            LocationHistory("5", "user1", "bar5", "Queens", "2024-01-11"),
            LocationHistory("6", "user1", "bar1", "New York", "2024-01-10"),
            LocationHistory("7", "user1", "bar2", "Brooklyn", "2024-01-09"),
            LocationHistory("8", "user1", "bar3", "Manhattan", "2024-01-08")
        )
        allHistory = mockHistory
        _locationHistory.value = mockHistory
        _filteredHistory.value = mockHistory
    }
    
    private fun loadFavouriteBars() {
        val mockFavourites = listOf(
            Bar(
                barId = "fav1",
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
                barId = "fav2",
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
            )
        )
        _favouriteBars.value = mockFavourites
    }
    
    private fun loadStarredBars() {
        val mockStarred = listOf(
            Bar(
                barId = "star1",
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
                barId = "star2",
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
                barId = "star3",
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
        _starredBars.value = mockStarred
    }
    
    private fun filterByYesterday(): List<LocationHistory> {
        return allHistory.filter { it.visitDate == "2024-01-15" }
    }
    
    private fun filterByPlaces(): List<LocationHistory> {
        return allHistory.distinctBy { it.barId }
    }
    
    private fun filterByMonth(): List<LocationHistory> {
        return allHistory.filter { it.visitDate.startsWith("2024-01") }
    }
    
    private fun filterByCities(): List<LocationHistory> {
        return allHistory.distinctBy { it.city }
    }
}

data class HangoutListItem(
    val label: String,
    val icon: String,
    val count: Int
)