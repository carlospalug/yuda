package com.nightroll.app.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Reel(
    val reelId: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val creatorId: String,
    val timestamp: Date
) : Parcelable