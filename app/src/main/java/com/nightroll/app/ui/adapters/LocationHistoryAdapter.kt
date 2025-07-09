package com.nightroll.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nightroll.app.data.models.LocationHistory
import com.nightroll.app.databinding.ItemLocationHistoryBinding

class LocationHistoryAdapter(
    private val onItemClick: (LocationHistory) -> Unit
) : ListAdapter<LocationHistory, LocationHistoryAdapter.LocationHistoryViewHolder>(LocationHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHistoryViewHolder {
        val binding = ItemLocationHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LocationHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LocationHistoryViewHolder(
        private val binding: ItemLocationHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(history: LocationHistory) {
            binding.apply {
                textCity.text = history.city
                textDate.text = history.visitDate
                
                root.setOnClickListener {
                    onItemClick(history)
                }
            }
        }
    }

    private class LocationHistoryDiffCallback : DiffUtil.ItemCallback<LocationHistory>() {
        override fun areItemsTheSame(oldItem: LocationHistory, newItem: LocationHistory): Boolean {
            return oldItem.historyId == newItem.historyId
        }

        override fun areContentsTheSame(oldItem: LocationHistory, newItem: LocationHistory): Boolean {
            return oldItem == newItem
        }
    }
}