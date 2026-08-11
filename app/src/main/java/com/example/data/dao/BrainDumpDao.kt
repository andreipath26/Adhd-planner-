package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BrainDumpItem
import com.example.data.model.BrainDumpStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BrainDumpDao {
    @Query("SELECT * FROM brain_dump_items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<BrainDumpItem>>

    @Query("SELECT * FROM brain_dump_items WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingItems(): Flow<List<BrainDumpItem>>

    @Query("SELECT COUNT(*) FROM brain_dump_items WHERE status = 'PENDING'")
    fun getPendingCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: BrainDumpItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<BrainDumpItem>)

    @Update
    suspend fun updateItem(item: BrainDumpItem)

    @Delete
    suspend fun deleteItem(item: BrainDumpItem)

    @Query("UPDATE brain_dump_items SET status = :status, convertedTaskId = :taskId WHERE id = :id")
    suspend fun updateStatus(id: Long, status: BrainDumpStatus, taskId: Long? = null)

    @Query("DELETE FROM brain_dump_items WHERE status = 'ARCHIVED'")
    suspend fun clearArchived()
}
