package com.example.inventoryapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_history")
data class ItemHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val itemId:Int,
    val itemName: String,
    val action : String,
    val oldValue : String? = null,
    val newValue : String? = null,
    val changeDescription : String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
