package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailyCheckIn
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyCheckInDao {
    @Query("SELECT * FROM daily_check_ins WHERE date = :date LIMIT 1")
    fun getCheckInForDate(date: String): Flow<DailyCheckIn?>

    @Query("SELECT * FROM daily_check_ins WHERE date = :date LIMIT 1")
    suspend fun getCheckInForDateSync(date: String): DailyCheckIn?

    @Query("SELECT * FROM daily_check_ins ORDER BY completedAt DESC LIMIT 1")
    fun getLatestCheckIn(): Flow<DailyCheckIn?>

    @Query("SELECT * FROM daily_check_ins ORDER BY date DESC")
    fun getAllCheckIns(): Flow<List<DailyCheckIn>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(checkIn: DailyCheckIn): Long

    @Update
    suspend fun updateCheckIn(checkIn: DailyCheckIn)

    @Query("DELETE FROM daily_check_ins WHERE date = :date")
    suspend fun deleteCheckInForDate(date: String)
}
