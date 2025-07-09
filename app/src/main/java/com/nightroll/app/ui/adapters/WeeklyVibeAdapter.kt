package com.nightroll.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nightroll.app.data.models.WeeklyVibeEvent
import com.nightroll.app.databinding.ItemWeeklyVibeBinding

class WeeklyVibeAdapter : ListAdapter<WeeklyVibeEvent, WeeklyVibeAdapter.WeeklyVibeViewHolder>(WeeklyVibeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyVibeViewHolder {
        val binding = ItemWeeklyVibeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeeklyVibeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeeklyVibeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WeeklyVibeViewHolder(
        private val binding: ItemWeeklyVibeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: WeeklyVibeEvent) {
            binding.apply {
                textDay.text = event.day
                textEvent.text = event.eventName
            }
        }
    }

    private class WeeklyVibeDiffCallback : DiffUtil.ItemCallback<WeeklyVibeEvent>() {
        override fun areItemsTheSame(oldItem: WeeklyVibeEvent, newItem: WeeklyVibeEvent): Boolean {
            return oldItem.day == newItem.day
        }

        override fun areContentsTheSame(oldItem: WeeklyVibeEvent, newItem: WeeklyVibeEvent): Boolean {
            return oldItem == newItem
        }
    }
}