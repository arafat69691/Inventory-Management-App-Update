package com.example.inventoryapp.data
import androidx.lifecycle.LiveData
import androidx.room.*

//@Dao
//interface InventoryDao {
//    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
//    fun getAllItems(): LiveData<List<InventoryItem>>
//
//    @Query("SELECT * FROM inventory_items WHERE id = :id")
//    suspend fun getItemById(id: Int): InventoryItem?
//
//    @Query("SELECT * FROM inventory_items WHERE quantity <= lowStockThreshold")
//    fun getLowStockItems(): LiveData<List<InventoryItem>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(item: InventoryItem)
//
//    @Update
//    suspend fun update(item: InventoryItem)
//
//    @Delete
//    suspend fun delete(item: InventoryItem)
//
//    @Query("SELECT DISTINCT category FROM inventory_items ORDER BY category ASC")
//    fun getAllCategories(): LiveData<List<String>>
//}

@Dao
interface InventoryDao {

    // ---- Inventory Items ----

    @Query("SELECT * FROM inventory_items ORDER BY updatedAt DESC")
    fun getAllItems(): LiveData<List<InventoryItem>>

    // LiveData version (new)
    @Query("SELECT * FROM inventory_items WHERE id = :id")
    fun getItemLive(id: Int): LiveData<InventoryItem?>

    // Suspend version (old)
    @Query("SELECT * FROM inventory_items WHERE id = :id")
    suspend fun getItemSync(id: Int): InventoryItem?

    @Query("SELECT * FROM inventory_items WHERE quantity <= lowStockThreshold")
    fun getLowStockItems(): LiveData<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InventoryItem): Long   // new return type

    @Update
    suspend fun update(item: InventoryItem)

    @Delete
    suspend fun delete(item: InventoryItem)

    @Query("SELECT DISTINCT category FROM inventory_items ORDER BY category ASC")
    fun getAllCategories(): LiveData<List<String>>

    @Query("SELECT * FROM inventory_items WHERE id = :id")
    fun getItemById(id: Int): LiveData<InventoryItem?>
    // ---- Item History (NEW) ----

    @Insert
    suspend fun insertHistory(history: ItemHistory)

    @Query("SELECT * FROM item_history WHERE itemId = :itemId ORDER BY timestamp DESC")
    fun getItemHistory(itemId: Int): LiveData<List<ItemHistory>>

    @Query("SELECT * FROM item_history ORDER BY timestamp DESC LIMIT 50")
    fun getAllHistory(): LiveData<List<ItemHistory>>
}
