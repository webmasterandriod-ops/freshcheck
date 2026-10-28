package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PantryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryDao {
    @Query("SELECT * FROM pantry_items ORDER BY expiryEpochDay ASC, id ASC")
    fun observeAllItems(): Flow<List<PantryItemEntity>>

    @Query("SELECT COUNT(*) FROM pantry_items")
    suspend fun getTotalItemCount(): Int

    @Query("SELECT * FROM pantry_items ORDER BY expiryEpochDay ASC, id ASC")
    suspend fun getAllItemsOnce(): List<PantryItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: PantryItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<PantryItemEntity>)

    @Update
    suspend fun updateItem(item: PantryItemEntity)

    @Query("DELETE FROM pantry_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("DELETE FROM pantry_items")
    suspend fun deleteAllItems()
}
