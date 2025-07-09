package com.nightroll.app.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HangoutList(
    val listId: String,
    val userId: String,
    val listType: String, // 'Favourites', 'Starred Places', 'Next Outings'
    val barIds: List<String>
) : Parcelable

@Parcelize
data class LocationHistory(
    val historyId: String,
    val userId: String,
    val barId: String,
    val city: String,
    val visitDate: String
) : Parcelable