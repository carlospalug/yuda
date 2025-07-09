package com.nightroll.app.ui.reel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nightroll.app.databinding.ActivityReelPlayerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReelPlayerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityReelPlayerBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReelPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupVideoPlayer()
    }
    
    private fun setupVideoPlayer() {
        // TODO: Implement video player functionality with ExoPlayer
        // This will be expanded when backend is added
    }
}