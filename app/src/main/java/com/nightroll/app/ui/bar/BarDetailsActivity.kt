package com.nightroll.app.ui.bar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.nightroll.app.data.models.Bar
import com.nightroll.app.databinding.ActivityBarDetailsBinding
import com.nightroll.app.ui.adapters.WeeklyVibeAdapter

class BarDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBarDetailsBinding
    private lateinit var bar: Bar
    private lateinit var weeklyVibeAdapter: WeeklyVibeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bar = intent.getParcelableExtra("bar") ?: return
        
        setupViews()
        setupData()
    }

    private fun setupViews() {
        binding.apply {
            // Setup toolbar
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
            
            // Setup directions button
            btnDirections.setOnClickListener {
                openMaps()
            }
            
            // Setup weekly vibe adapter
            weeklyVibeAdapter = WeeklyVibeAdapter()
            recyclerWeeklyVibe.apply {
                layoutManager = GridLayoutManager(this@BarDetailsActivity, 1)
                adapter = weeklyVibeAdapter
            }
        }
    }

    private fun setupData() {
        binding.apply {
            // Load bar image
            Glide.with(this@BarDetailsActivity)
                .load(bar.imageUrl)
                .centerCrop()
                .into(imageBar)
            
            // Set bar details
            textBarName.text = bar.name
            textBarType.text = bar.type
            textBarAddress.text = "${calculateDistance()} - ${bar.address}"
            
            // Load weekly vibe data
            weeklyVibeAdapter.submitList(bar.weeklyVibe)
        }
    }

    private fun openMaps() {
        val uri = Uri.parse("geo:${bar.location.lat},${bar.location.lng}?q=${bar.location.lat},${bar.location.lng}(${bar.name})")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // Fallback to web maps
            val webIntent = Intent(Intent.ACTION_VIEW, 
                Uri.parse("https://maps.google.com/?q=${bar.location.lat},${bar.location.lng}"))
            startActivity(webIntent)
        }
    }

    private fun calculateDistance(): String {
        // TODO: Implement actual distance calculation based on user location
        return "0.5 km"
    }
}