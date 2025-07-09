package com.nightroll.app.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.nightroll.app.R

object ImageUtils {
    
    fun loadImage(
        context: Context,
        imageView: ImageView,
        url: String?,
        placeholder: Int = R.drawable.ic_person
    ) {
        Glide.with(context)
            .load(url)
            .placeholder(placeholder)
            .error(placeholder)
            .into(imageView)
    }
    
    fun loadCircularImage(
        context: Context,
        imageView: ImageView,
        url: String?,
        placeholder: Int = R.drawable.ic_person
    ) {
        Glide.with(context)
            .load(url)
            .transform(CircleCrop())
            .placeholder(placeholder)
            .error(placeholder)
            .into(imageView)
    }
    
    fun loadRoundedImage(
        context: Context,
        imageView: ImageView,
        url: String?,
        cornerRadius: Int = 8,
        placeholder: Int = R.drawable.ic_person
    ) {
        Glide.with(context)
            .load(url)
            .transform(RoundedCorners(cornerRadius))
            .placeholder(placeholder)
            .error(placeholder)
            .into(imageView)
    }
    
    fun loadBarImage(
        context: Context,
        imageView: ImageView,
        url: String?
    ) {
        loadRoundedImage(
            context = context,
            imageView = imageView,
            url = url,
            cornerRadius = 8,
            placeholder = R.drawable.ic_home
        )
    }
    
    fun loadReelThumbnail(
        context: Context,
        imageView: ImageView,
        url: String?
    ) {
        loadRoundedImage(
            context = context,
            imageView = imageView,
            url = url,
            cornerRadius = 8,
            placeholder = R.drawable.ic_play
        )
    }
}