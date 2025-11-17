package com.example.inventoryapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.R
import com.example.inventoryapp.data.ItemHistory
import com.example.inventoryapp.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : ListAdapter<ItemHistory, HistoryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(history: ItemHistory) {
            binding.apply {
                tvAction.text = history.action.replace("_", " ")
                tvDescription.text = history.changeDescription

                val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                tvTimestamp.text = dateFormat.format(Date(history.timestamp))

                // Set action icon and color
                val (icon, color) = when (history.action) {
                    "CREATED" -> "➕" to R.color.success
                    "UPDATED" -> "✏️" to R.color.primary
                    "QUANTITY_ADDED" -> "📈" to R.color.success
                    "QUANTITY_REMOVED" -> "📉" to R.color.warning
                    "DELETED" -> "🗑️" to R.color.error
                    else -> "📝" to R.color.text_secondary
                }

                tvActionIcon.text = icon
                tvAction.setTextColor(ContextCompat.getColor(root.context, color))
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ItemHistory>() {
        override fun areItemsTheSame(oldItem: ItemHistory, newItem: ItemHistory) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ItemHistory, newItem: ItemHistory) =
            oldItem == newItem
    }
}