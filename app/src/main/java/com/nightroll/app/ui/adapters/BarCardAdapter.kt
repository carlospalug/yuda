package com.nightroll.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nightroll.app.data.models.Bar
import com.nightroll.app.databinding.ItemBarCardBinding

class BarCardAdapter(
    private val onBarClick: (Bar) -> Unit
) : ListAdapter<Bar, BarCardAdapter.BarViewHolder>(BarDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarViewHolder {
        val binding = ItemBarCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BarViewHolder(
        private val binding: ItemBarCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bar: Bar) {
            binding.apply {
                textBarName.text = bar.name
                textBarVibe.text = bar.vibe
                textBarHours.text = bar.openHours
                
                Glide.with(imageBar.context)
                    .load(bar.imageUrl)
                    .centerCrop()
                    .into(imageBar)
                
                root.setOnClickListener {
                    onBarClick(bar)
                }
            }
        }
    }

    private class BarDiffCallback : DiffUtil.ItemCallback<Bar>() {
        override fun areItemsTheSame(oldItem: Bar, newItem: Bar): Boolean {
            return oldItem.barId == newItem.barId
        }

        override fun areContentsTheSame(oldItem: Bar, newItem: Bar): Boolean {
            return oldItem == newItem
        }
    }
}