package com.example.inventoryapp.repository
import androidx.lifecycle.LiveData
import com.example.inventoryapp.data.InventoryDao
import com.example.inventoryapp.data.InventoryItem
import com.example.inventoryapp.data.ItemHistory


class InventoryRepository(private val dao: InventoryDao) {
    val allItems: LiveData<List<InventoryItem>> = dao.getAllItems()
    val lowStockItems: LiveData<List<InventoryItem>> = dao.getLowStockItems()
    val categories: LiveData<List<String>> = dao.getAllCategories()

    fun getItemById(id: Int): LiveData<InventoryItem?> = dao.getItemById(id)

    suspend fun insert(item: InventoryItem): Long {
        val id = dao.insert(item)
        dao.insertHistory(
            ItemHistory(
                itemId = id.toInt(),
                itemName = item.name,
                action = "CREATED",
                changeDescription = "Item created",
                newValue = "Qty: ${item.quantity}, Price: $${item.price}"
            )
        )
        return id
    }

    suspend fun update(oldItem: InventoryItem, newItem: InventoryItem) {
        dao.update(newItem.copy(updatedAt = System.currentTimeMillis()))

        // Track changes
        val changes = mutableListOf<String>()
        if (oldItem.name != newItem.name) changes.add("Name: ${oldItem.name} → ${newItem.name}")
        if (oldItem.category != newItem.category) changes.add("Category: ${oldItem.category} → ${newItem.category}")
        if (oldItem.quantity != newItem.quantity) changes.add("Quantity: ${oldItem.quantity} → ${newItem.quantity}")
        if (oldItem.price != newItem.price) changes.add("Price: $${oldItem.price} → $${newItem.price}")

        if (changes.isNotEmpty()) {
            dao.insertHistory(
                ItemHistory(
                    itemId = newItem.id,
                    itemName = newItem.name,
                    action = "UPDATED",
                    changeDescription = changes.joinToString(", ")
                )
            )
        }
    }

    suspend fun updateQuantity(item: InventoryItem, quantityChange: Int, isAddition: Boolean) {
        val newQuantity = if (isAddition) {
            item.quantity + quantityChange
        } else {
            (item.quantity - quantityChange).coerceAtLeast(0)
        }

        val updatedItem = item.copy(
            quantity = newQuantity,
            updatedAt = System.currentTimeMillis()
        )
        dao.update(updatedItem)

        dao.insertHistory(
            ItemHistory(
                itemId = item.id,
                itemName = item.name,
                action = if (isAddition) "QUANTITY_ADDED" else "QUANTITY_REMOVED",
                oldValue = item.quantity.toString(),
                newValue = newQuantity.toString(),
                changeDescription = if (isAddition) {
                    "Added $quantityChange units (${item.quantity} → $newQuantity)"
                } else {
                    "Removed $quantityChange units (${item.quantity} → $newQuantity)"
                }
            )
        )
    }

    suspend fun delete(item: InventoryItem) {
        dao.delete(item)
        dao.insertHistory(
            ItemHistory(
                itemId = item.id,
                itemName = item.name,
                action = "DELETED",
                changeDescription = "Item deleted from inventory"
            )
        )
    }

    fun getItemHistory(itemId: Int): LiveData<List<ItemHistory>> = dao.getItemHistory(itemId)

    fun getAllHistory(): LiveData<List<ItemHistory>> = dao.getAllHistory()
}
