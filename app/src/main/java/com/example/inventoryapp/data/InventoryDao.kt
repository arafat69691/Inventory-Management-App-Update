package com.example.inventoryapp.data
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun getAllItems(): LiveData<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE id = :id")
    suspend fun getItemById(id: Int): InventoryItem?

    @Query("SELECT * FROM inventory_items WHERE quantity <= lowStockThreshold")
    fun getLowStockItems(): LiveData<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InventoryItem)

    @Update
    suspend fun update(item: InventoryItem)

    @Delete
    suspend fun delete(item: InventoryItem)

    @Query("SELECT DISTINCT category FROM inventory_items ORDER BY category ASC")
    fun getAllCategories(): LiveData<List<String>>
}
