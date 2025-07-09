package com.nightroll.app.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlacesResponse(
    val results: List<PlaceResult>,
    val status: String,
    val next_page_token: String? = null
) : Parcelable

@Parcelize
data class PlaceResult(
    val place_id: String,
    val name: String,
    val vicinity: String? = null,
    val formatted_address: String? = null,
    val geometry: PlaceGeometry,
    val rating: Double? = null,
    val price_level: Int? = null,
    val types: List<String>,
    val opening_hours: OpeningHours? = null,
    val photos: List<PlacePhoto>? = null,
    val plus_code: PlusCode? = null,
    val business_status: String? = null,
    val formatted_phone_number: String? = null,
    val website: String? = null,
    val reviews: List<PlaceReview>? = null
) : Parcelable

@Parcelize
data class PlaceGeometry(
    val location: PlaceLocation,
    val viewport: PlaceViewport? = null
) : Parcelable

@Parcelize
data class PlaceLocation(
    val lat: Double,
    val lng: Double
) : Parcelable

@Parcelize
data class PlaceViewport(
    val northeast: PlaceLocation,
    val southwest: PlaceLocation
) : Parcelable

@Parcelize
data class OpeningHours(
    val open_now: Boolean? = null,
    val periods: List<OpeningPeriod>? = null,
    val weekday_text: List<String>? = null
) : Parcelable

@Parcelize
data class OpeningPeriod(
    val close: OpeningTime? = null,
    val open: OpeningTime
) : Parcelable

@Parcelize
data class OpeningTime(
    val day: Int,
    val time: String
) : Parcelable

@Parcelize
data class PlacePhoto(
    val height: Int,
    val width: Int,
    val photo_reference: String,
    val html_attributions: List<String>
) : Parcelable

@Parcelize
data class PlusCode(
    val compound_code: String? = null,
    val global_code: String
) : Parcelable

@Parcelize
data class PlaceReview(
    val author_name: String,
    val author_url: String? = null,
    val language: String? = null,
    val profile_photo_url: String? = null,
    val rating: Int,
    val relative_time_description: String,
    val text: String,
    val time: Long
) : Parcelable

// Directions API Models
@Parcelize
data class DirectionsResponse(
    val routes: List<DirectionRoute>,
    val status: String,
    val geocoded_waypoints: List<GeocodedWaypoint>? = null
) : Parcelable

@Parcelize
data class DirectionRoute(
    val bounds: DirectionBounds,
    val copyrights: String,
    val legs: List<DirectionLeg>,
    val overview_polyline: DirectionPolyline,
    val summary: String,
    val warnings: List<String>? = null,
    val waypoint_order: List<Int>? = null
) : Parcelable

@Parcelize
data class DirectionBounds(
    val northeast: PlaceLocation,
    val southwest: PlaceLocation
) : Parcelable

@Parcelize
data class DirectionLeg(
    val distance: DirectionDistance,
    val duration: DirectionDuration,
    val end_address: String,
    val end_location: PlaceLocation,
    val start_address: String,
    val start_location: PlaceLocation,
    val steps: List<DirectionStep>,
    val traffic_speed_entry: List<String>? = null,
    val via_waypoint: List<String>? = null
) : Parcelable

@Parcelize
data class DirectionDistance(
    val text: String,
    val value: Int
) : Parcelable

@Parcelize
data class DirectionDuration(
    val text: String,
    val value: Int
) : Parcelable

@Parcelize
data class DirectionStep(
    val distance: DirectionDistance,
    val duration: DirectionDuration,
    val end_location: PlaceLocation,
    val html_instructions: String,
    val maneuver: String? = null,
    val polyline: DirectionPolyline,
    val start_location: PlaceLocation,
    val travel_mode: String
) : Parcelable

@Parcelize
data class DirectionPolyline(
    val points: String
) : Parcelable

@Parcelize
data class GeocodedWaypoint(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>
) : Parcelable