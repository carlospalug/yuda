package com.nightroll.app.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nightroll.app.data.models.Reel
import com.nightroll.app.data.models.User
import java.util.Date

class ProfileViewModel : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user
    
    private val _vibeRecapReels = MutableLiveData<List<Reel>>()
    val vibeRecapReels: LiveData<List<Reel>> = _vibeRecapReels
    
    private val _selectedTab = MutableLiveData<String>()
    val selectedTab: LiveData<String> = _selectedTab

    init {
        loadUserProfile()
        loadVibeRecapReels()
        _selectedTab.value = "NIGHTLIST"
    }

    fun shareProfile() {
        // TODO: Implement profile sharing
    }
    
    fun navigateToSettings() {
        // TODO: Implement navigation to settings
    }
    
    fun editProfile() {
        // TODO: Implement profile editing
    }
    
    fun setSelectedTab(tab: String) {
        _selectedTab.value = tab
    }
    
    fun playReel(reelId: String) {
        // TODO: Implement reel player
    }
    
    private fun loadUserProfile() {
        // Mock data - replace with actual API call
        val mockUser = User(
            userId = "user1",
            username = "nightroller_23",
            email = "user@example.com",
            passwordHash = "hashed_password",
            birthday = Date(),
            mobileNumber = "+1234567890",
            profilePictureUrl = "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
            bio = "Living for the nightlife 🌃 Always rolling to the best spots in the city",
            followingBars = listOf("bar1", "bar2", "bar3"),
            vibeRecapReels = listOf("reel1", "reel2", "reel3", "reel4"),
            comments = listOf("comment1", "comment2")
        )
        _user.value = mockUser
    }
    
    private fun loadVibeRecapReels() {
        // Mock data - replace with actual API call
        val mockReels = listOf(
            Reel("reel1", "https://example.com/reel1.mp4", "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg", "user1", Date()),
            Reel("reel2", "https://example.com/reel2.mp4", "https://images.pexels.com/photos/2747449/pexels-photo-2747449.jpeg", "user1", Date()),
            Reel("reel3", "https://example.com/reel3.mp4", "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg", "user1", Date()),
            Reel("reel4", "https://example.com/reel4.mp4", "https://images.pexels.com/photos/2747449/pexels-photo-2747449.jpeg", "user1", Date())
        )
        _vibeRecapReels.value = mockReels
    }
}