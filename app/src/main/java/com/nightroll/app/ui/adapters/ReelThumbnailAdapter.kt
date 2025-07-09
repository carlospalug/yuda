package com.nightroll.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nightroll.app.data.models.Reel
import com.nightroll.app.databinding.ItemReelThumbnailBinding

class ReelThumbnailAdapter(
    private val onReelClick: (Reel) -> Unit
) : ListAdapter<Reel, ReelThumbnailAdapter.ReelViewHolder>(ReelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val binding = ItemReelThumbnailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReelViewHolder(
        private val binding: ItemReelThumbnailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reel: Reel) {
            binding.apply {
                Glide.with(imageReel.context)
                    .load(reel.thumbnailUrl)
                    .centerCrop()
                    .into(imageReel)
                
                root.setOnClickListener {
                    onReelClick(reel)
                }
            }
        }
    }

    private class ReelDiffCallback : DiffUtil.ItemCallback<Reel>() {
        override fun areItemsTheSame(oldItem: Reel, newItem: Reel): Boolean {
            return oldItem.reelId == newItem.reelId
        }

        override fun areContentsTheSame(oldItem: Reel, newItem: Reel): Boolean {
            return oldItem == newItem
        }
    }
}