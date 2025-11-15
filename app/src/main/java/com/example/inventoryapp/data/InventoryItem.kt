package com.example.inventoryapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: String,
    val quantity: Int,
    val price: Double,
    val description: String,
    val lowStockThreshold: Int,
    val timestamp: Long = System.currentTimeMillis()
)