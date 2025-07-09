package com.nightroll.app.utils

object Constants {
    
    // Request codes
    const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    const val CAMERA_PERMISSION_REQUEST_CODE = 1002
    const val STORAGE_PERMISSION_REQUEST_CODE = 1003
    
    // Shared preferences keys
    const val PREFS_NAME = "nightroll_prefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_USERNAME = "username"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_FIRST_LAUNCH = "first_launch"
    const val KEY_LOCATION_ENABLED = "location_enabled"
    
    // Intent extras
    const val EXTRA_BAR_ID = "bar_id"
    const val EXTRA_BAR = "bar"
    const val EXTRA_REEL_ID = "reel_id"
    const val EXTRA_USER_ID = "user_id"
    
    // Map settings
    const val DEFAULT_ZOOM_LEVEL = 15f
    const val NEARBY_RADIUS_KM = 5.0
    const val MAP_ANIMATION_DURATION = 1000
    
    // UI settings
    const val SEARCH_DEBOUNCE_DELAY = 300L
    const val REFRESH_DELAY = 1000L
    const val ANIMATION_DURATION = 300L
    
    // Network settings
    const val NETWORK_TIMEOUT = 30L
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10MB
    
    // Image settings
    const val MAX_IMAGE_SIZE = 1024
    const val IMAGE_QUALITY = 85
    
    // Video settings
    const val MAX_VIDEO_DURATION = 30000L // 30 seconds
    
    // Notification settings
    const val LOCATION_NOTIFICATION_ID = 1001
    const val LOCATION_CHANNEL_ID = "location_updates"
    
    // Database settings
    const val DATABASE_NAME = "nightroll_database"
    const val DATABASE_VERSION = 1
    
    // Maps API Key
    const val GOOGLE_MAPS_API_KEY = "AIzaSyCOU93j67uGsk5_RbZ_PUhrOjj6gEYex-A"
    
    // API endpoints (for future backend integration)
    const val BASE_URL = "https://api.nightroll.com/"
    const val API_VERSION = "v1"
    
    // Error messages
    const val ERROR_NETWORK = "Network error. Please check your connection."
    const val ERROR_LOCATION = "Unable to get your location"
    const val ERROR_PERMISSION = "Permission required"
    const val ERROR_UNKNOWN = "Something went wrong. Please try again."
    
    // Success messages
    const val SUCCESS_PROFILE_UPDATED = "Profile updated successfully"
    const val SUCCESS_BAR_FOLLOWED = "Bar added to following"
    const val SUCCESS_BAR_UNFOLLOWED = "Bar removed from following"
}