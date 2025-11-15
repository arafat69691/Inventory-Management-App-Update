package com.example.inventoryapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventoryapp.R
import com.example.inventoryapp.data.InventoryItem
import com.example.inventoryapp.databinding.ItemInventoryBinding
import java.text.NumberFormat
import java.util.Locale

class InventoryAdapter(
    private val onItemClick: (InventoryItem) -> Unit,
    private val onDeleteClick: (InventoryItem) -> Unit
) : ListAdapter<InventoryItem, InventoryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInventoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemInventoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InventoryItem) {
            binding.apply {
                tvName.text = item.name
                tvCategory.text = item.category
                tvQuantity.text = item.quantity.toString()

                val formatter = NumberFormat.getCurrencyInstance(Locale.US)
                tvPrice.text = formatter.format(item.price)

                // Low stock indicator
                val isLowStock = item.quantity <= item.lowStockThreshold
                if (isLowStock) {
                    tvQuantity.setTextColor(
                        ContextCompat.getColor(root.context, R.color.error)
                    )
                    cardLowStock.visibility = View.VISIBLE
                } else {
                    tvQuantity.setTextColor(
                        ContextCompat.getColor(root.context, R.color.success)
                    )
                    cardLowStock.visibility = View.GONE
                }

                root.setOnClickListener { onItemClick(item) }
                btnDelete.setOnClickListener { onDeleteClick(item) }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<InventoryItem>() {
        override fun areItemsTheSame(oldItem: InventoryItem, newItem: InventoryItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: InventoryItem, newItem: InventoryItem) =
            oldItem == newItem
    }
}