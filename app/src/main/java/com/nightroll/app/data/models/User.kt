package com.nightroll.app.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class User(
    val userId: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val birthday: Date,
    val mobileNumber: String,
    val profilePictureUrl: String,
    val bio: String,
    val followingBars: List<String> = emptyList(),
    val vibeRecapReels: List<String> = emptyList(),
    val comments: List<String> = emptyList()
) : Parcelable