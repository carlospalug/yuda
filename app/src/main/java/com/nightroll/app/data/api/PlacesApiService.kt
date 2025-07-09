package com.nightroll.app.data.api

import com.nightroll.app.data.models.PlaceResult
import com.nightroll.app.data.models.PlacesResponse
import com.nightroll.app.data.models.DirectionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApiService {
    
    @GET("place/nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int = 5000,
        @Query("type") type: String = "night_club|bar|restaurant",
        @Query("keyword") keyword: String = "bar nightclub lounge",
        @Query("key") apiKey: String
    ): Response<PlacesResponse>
    
    @GET("place/textsearch/json")
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Query("location") location: String? = null,
        @Query("radius") radius: Int = 10000,
        @Query("type") type: String = "night_club|bar|restaurant",
        @Query("key") apiKey: String
    ): Response<PlacesResponse>
    
    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String = "driving",
        @Query("alternatives") alternatives: Boolean = true,
        @Query("key") apiKey: String
    ): Response<DirectionsResponse>
    
    @GET("place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String = "name,rating,formatted_phone_number,opening_hours,website,photos,reviews",
        @Query("key") apiKey: String
    ): Response<PlaceResult>
}