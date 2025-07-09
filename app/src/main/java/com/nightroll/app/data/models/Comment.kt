package com.nightroll.app.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Comment(
    val commentId: String,
    val authorId: String,
    val text: String,
    val timestamp: Date
) : Parcelable