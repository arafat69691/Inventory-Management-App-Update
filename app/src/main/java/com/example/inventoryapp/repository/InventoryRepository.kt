package com.example.inventoryapp.repository
import androidx.lifecycle.LiveData
import com.example.inventoryapp.data.InventoryDao
import com.example.inventoryapp.data.InventoryItem


class InventoryRepository(private val dao: InventoryDao) {
    val allItems: LiveData<List<InventoryItem>> = dao.getAllItems()
    val lowStockItems: LiveData<List<InventoryItem>> = dao.getLowStockItems()
    val categories: LiveData<List<String>> = dao.getAllCategories()

    suspend fun insert(item: InventoryItem) = dao.insert(item)
    suspend fun update(item: InventoryItem) = dao.update(item)
    suspend fun delete(item: InventoryItem) = dao.delete(item)
    suspend fun getItemById(id: Int) = dao.getItemById(id)
}
