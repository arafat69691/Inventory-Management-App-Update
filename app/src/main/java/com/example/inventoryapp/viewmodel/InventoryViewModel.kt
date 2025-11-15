package com.example.inventoryapp.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.inventoryapp.data.InventoryDatabase
import com.example.inventoryapp.data.InventoryItem
import com.example.inventoryapp.repository.InventoryRepository
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: InventoryRepository
    val allItems: LiveData<List<InventoryItem>>
    val lowStockItems: LiveData<List<InventoryItem>>
    val categories: LiveData<List<String>>

    init {
        val dao = InventoryDatabase.getDatabase(application).inventoryDao()
        repository = InventoryRepository(dao)
        allItems = repository.allItems
        lowStockItems = repository.lowStockItems
        categories = repository.categories
    }

    fun insert(item: InventoryItem) = viewModelScope.launch {
        repository.insert(item)
    }

    fun update(item: InventoryItem) = viewModelScope.launch {
        repository.update(item)
    }

    fun delete(item: InventoryItem) = viewModelScope.launch {
        repository.delete(item)
    }
}
