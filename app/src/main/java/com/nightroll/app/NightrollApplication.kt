package com.nightroll.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NightrollApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize any global configurations here
        setupGlobalConfigurations()
    }
    
    private fun setupGlobalConfigurations() {
        // Setup crash reporting, analytics, etc.
        // This will be expanded when backend is added
    }
}