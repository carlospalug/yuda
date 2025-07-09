package com.nightroll.app.data.repository

import com.nightroll.app.data.models.Reel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReelRepository @Inject constructor() {
    
    // Mock reel data - will be replaced with actual API calls when backend is added
    private val mockReels = listOf(
        Reel(
            reelId = "reel1",
            videoUrl = "https://example.com/reel1.mp4",
            thumbnailUrl = "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
            creatorId = "bar1",
            timestamp = Date()
        ),
        Reel(
            reelId = "reel2",
            videoUrl = "https://example.com/reel2.mp4",
            thumbnailUrl = "https://images.pexels.com/photos/2747449/pexels-photo-2747449.jpeg",
            creatorId = "bar2",
            timestamp = Date()
        ),
        Reel(
            reelId = "reel3",
            videoUrl = "https://example.com/reel3.mp4",
            thumbnailUrl = "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
            creatorId = "bar3",
            timestamp = Date()
        ),
        Reel(
            reelId = "reel4",
            videoUrl = "https://example.com/reel4.mp4",
            thumbnailUrl = "https://images.pexels.com/photos/2747449/pexels-photo-2747449.jpeg",
            creatorId = "bar4",
            timestamp = Date()
        ),
        Reel(
            reelId = "reel5",
            videoUrl = "https://example.com/reel5.mp4",
            thumbnailUrl = "https://images.pexels.com/photos/1267320/pexels-photo-1267320.jpeg",
            creatorId = "bar5",
            timestamp = Date()
        )
    )
    
    fun getFollowingReels(): Flow<List<Reel>> = flow {
        delay(400)
        emit(mockReels)
    }
    
    fun getReelsByBarId(barId: String): Flow<List<Reel>> = flow {
        delay(300)
        emit(mockReels.filter { it.creatorId == barId })
    }
    
    fun getReelById(reelId: String): Flow<Reel?> = flow {
        delay(200)
        emit(mockReels.find { it.reelId == reelId })
    }
    
    fun getUserVibeRecapReels(userId: String): Flow<List<Reel>> = flow {
        delay(350)
        // Return user's own reels (mock data)
        emit(mockReels.filter { it.creatorId == userId })
    }
}