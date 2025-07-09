package com.nightroll.app.ui.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nightroll.app.databinding.ActivityNavigationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavigationActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityNavigationBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get destination from intent
        val destinationLat = intent.getDoubleExtra("destination_lat", 0.0)
        val destinationLng = intent.getDoubleExtra("destination_lng", 0.0)
        val destinationName = intent.getStringExtra("destination_name") ?: "Destination"
        
        // Set up navigation fragment
        if (savedInstanceState == null) {
            val fragment = NavigationMapFragment().apply {
                arguments = Bundle().apply {
                    putDouble("destination_lat", destinationLat)
                    putDouble("destination_lng", destinationLng)
                    putString("destination_name", destinationName)
                }
            }
            
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, fragment)
                .commit()
        }
        
        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.title = "Navigate to $destinationName"
    }
}