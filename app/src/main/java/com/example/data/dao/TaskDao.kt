package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PlannerTask
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM planner_tasks ORDER BY dayOfWeek ASC, isCompleted ASC, id ASC")
    fun getAllTasks(): Flow<List<PlannerTask>>

    @Query("SELECT * FROM planner_tasks WHERE dayOfWeek = :dayOfWeek ORDER BY isCompleted ASC, id ASC")
    fun getTasksForDay(dayOfWeek: Int): Flow<List<PlannerTask>>

    @Query("SELECT * FROM planner_tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): PlannerTask?

    @Query("SELECT * FROM planner_tasks WHERE isFocusSpotlight = 1 LIMIT 1")
    fun getSpotlightTask(): Flow<PlannerTask?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: PlannerTask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<PlannerTask>)

    @Update
    suspend fun updateTask(task: PlannerTask)

    @Delete
    suspend fun deleteTask(task: PlannerTask)

    @Query("DELETE FROM planner_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("UPDATE planner_tasks SET isFocusSpotlight = CASE WHEN id = :taskId THEN 1 ELSE 0 END")
    suspend fun setSpotlightTask(taskId: Long)

    @Query("UPDATE planner_tasks SET isFocusSpotlight = 0")
    suspend fun clearSpotlight()
}
