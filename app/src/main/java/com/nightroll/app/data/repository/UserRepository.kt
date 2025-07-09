package com.nightroll.app.data.repository

import com.nightroll.app.data.models.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    
    // Mock user data - will be replaced with actual API calls when backend is added
    private val mockUser = User(
        userId = "user1",
        username = "nightroller_23",
        email = "user@example.com",
        passwordHash = "hashed_password",
        birthday = Date(),
        mobileNumber = "+1234567890",
        profilePictureUrl = "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
        bio = "Living for the nightlife 🌃 Always rolling to the best spots in the city",
        followingBars = listOf("1", "3", "6"),
        vibeRecapReels = listOf("reel1", "reel2", "reel3", "reel4"),
        comments = listOf("comment1", "comment2")
    )
    
    fun getCurrentUser(): Flow<User> = flow {
        delay(300)
        emit(mockUser)
    }
    
    fun updateUserProfile(user: User): Flow<Boolean> = flow {
        delay(500)
        // Mock successful update
        emit(true)
    }
    
    fun followBar(barId: String): Flow<Boolean> = flow {
        delay(200)
        // Mock successful follow
        emit(true)
    }
    
    fun unfollowBar(barId: String): Flow<Boolean> = flow {
        delay(200)
        // Mock successful unfollow
        emit(true)
    }
    
    fun isBarFollowed(barId: String): Flow<Boolean> = flow {
        delay(100)
        emit(mockUser.followingBars.contains(barId))
    }
    
    fun getFollowingBars(): Flow<List<String>> = flow {
        delay(200)
        emit(mockUser.followingBars)
    }
}