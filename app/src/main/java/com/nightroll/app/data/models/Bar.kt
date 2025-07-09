package com.nightroll.app.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Bar(
    val barId: String,
    val name: String,
    val type: String,
    val address: String,
    val location: Location,
    val imageUrl: String,
    val weeklyVibe: List<WeeklyVibeEvent>,
    val followersCount: Int,
    val reels: List<String> = emptyList(),
    val isActive: Boolean = false,
    val vibe: String = "",
    val openHours: String = ""
) : Parcelable

@Parcelize
data class Location(
    val lat: Double,
    val lng: Double
) : Parcelable

@Parcelize
data class WeeklyVibeEvent(
    val day: String,
    val eventName: String
) : Parcelable