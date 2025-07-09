package com.nightroll.app.data.repository

import com.nightroll.app.data.api.PlacesApiService
import com.nightroll.app.data.models.*
import com.nightroll.app.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesRepository @Inject constructor(
    private val placesApiService: PlacesApiService
) {
    
    fun getNearbyBars(
        latitude: Double,
        longitude: Double,
        radius: Int = 5000
    ): Flow<List<PlaceResult>> = flow {
        try {
            val location = "$latitude,$longitude"
            val response = placesApiService.getNearbyPlaces(
                location = location,
                radius = radius,
                type = "night_club|bar|restaurant",
                keyword = "bar nightclub lounge pub",
                apiKey = Constants.GOOGLE_MAPS_API_KEY
            )
            
            if (response.isSuccessful) {
                val places = response.body()?.results?.filter { place ->
                    // Filter for nightlife-related places
                    place.types.any { type ->
                        type.contains("night_club") ||
                        type.contains("bar") ||
                        type.contains("liquor_store") ||
                        place.name.contains("bar", ignoreCase = true) ||
                        place.name.contains("club", ignoreCase = true) ||
                        place.name.contains("lounge", ignoreCase = true) ||
                        place.name.contains("pub", ignoreCase = true)
                    }
                } ?: emptyList()
                
                emit(places)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    fun searchBars(
        query: String,
        latitude: Double? = null,
        longitude: Double? = null
    ): Flow<List<PlaceResult>> = flow {
        try {
            val location = if (latitude != null && longitude != null) {
                "$latitude,$longitude"
            } else null
            
            val searchQuery = "$query bar nightclub lounge"
            
            val response = placesApiService.searchPlaces(
                query = searchQuery,
                location = location,
                radius = 10000,
                type = "night_club|bar|restaurant",
                apiKey = Constants.GOOGLE_MAPS_API_KEY
            )
            
            if (response.isSuccessful) {
                val places = response.body()?.results ?: emptyList()
                emit(places)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    fun getDirections(
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double,
        mode: String = "driving"
    ): Flow<DirectionsResponse?> = flow {
        try {
            val origin = "$originLat,$originLng"
            val destination = "$destLat,$destLng"
            
            val response = placesApiService.getDirections(
                origin = origin,
                destination = destination,
                mode = mode,
                alternatives = true,
                apiKey = Constants.GOOGLE_MAPS_API_KEY
            )
            
            if (response.isSuccessful) {
                emit(response.body())
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }
    
    fun getPlaceDetails(placeId: String): Flow<PlaceResult?> = flow {
        try {
            val response = placesApiService.getPlaceDetails(
                placeId = placeId,
                fields = "name,rating,formatted_phone_number,opening_hours,website,photos,reviews,geometry,formatted_address,types",
                apiKey = Constants.GOOGLE_MAPS_API_KEY
            )
            
            if (response.isSuccessful) {
                emit(response.body())
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }
    
    fun getPhotoUrl(photoReference: String, maxWidth: Int = 400): String {
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=$maxWidth&photoreference=$photoReference&key=${Constants.GOOGLE_MAPS_API_KEY}"
    }
}