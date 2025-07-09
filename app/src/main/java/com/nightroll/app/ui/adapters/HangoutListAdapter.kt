package com.nightroll.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nightroll.app.databinding.ItemHangoutListBinding
import com.nightroll.app.ui.nightlist.HangoutListItem

class HangoutListAdapter(
    private val onItemClick: (String) -> Unit
) : ListAdapter<HangoutListItem, HangoutListAdapter.HangoutListViewHolder>(HangoutListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HangoutListViewHolder {
        val binding = ItemHangoutListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HangoutListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HangoutListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HangoutListViewHolder(
        private val binding: ItemHangoutListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HangoutListItem) {
            binding.apply {
                textLabel.text = item.label
                textCount.text = item.count.toString()
                
                // Set icon based on type
                when (item.icon) {
                    "heart" -> iconType.setImageResource(android.R.drawable.ic_menu_my_calendar)
                    "calendar" -> iconType.setImageResource(android.R.drawable.ic_menu_my_calendar)
                    "bookmark" -> iconType.setImageResource(android.R.drawable.ic_menu_save)
                    "star" -> iconType.setImageResource(android.R.drawable.btn_star)
                }
                
                root.setOnClickListener {
                    onItemClick(item.label)
                }
            }
        }
    }

    private class HangoutListDiffCallback : DiffUtil.ItemCallback<HangoutListItem>() {
        override fun areItemsTheSame(oldItem: HangoutListItem, newItem: HangoutListItem): Boolean {
            return oldItem.label == newItem.label
        }

        override fun areContentsTheSame(oldItem: HangoutListItem, newItem: HangoutListItem): Boolean {
            return oldItem == newItem
        }
    }
}