package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MicroStep
import kotlinx.coroutines.flow.Flow

@Dao
interface MicroStepDao {
    @Query("SELECT * FROM micro_steps WHERE taskId = :taskId ORDER BY orderIndex ASC, id ASC")
    fun getStepsForTask(taskId: Long): Flow<List<MicroStep>>

    @Query("SELECT * FROM micro_steps WHERE taskId = :taskId ORDER BY orderIndex ASC, id ASC")
    suspend fun getStepsForTaskList(taskId: Long): List<MicroStep>

    @Query("SELECT * FROM micro_steps ORDER BY orderIndex ASC")
    fun getAllMicroSteps(): Flow<List<MicroStep>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: MicroStep): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<MicroStep>)

    @Update
    suspend fun updateStep(step: MicroStep)

    @Delete
    suspend fun deleteStep(step: MicroStep)

    @Query("DELETE FROM micro_steps WHERE taskId = :taskId")
    suspend fun deleteStepsForTask(taskId: Long)
}
