package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FocusSession
import com.example.data.model.LeaderboardUser
import com.example.data.model.UserProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getProgress(): Flow<UserProgress?>

    @Query("SELECT * FROM user_progress WHERE id = 1")
    suspend fun getProgressSync(): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: UserProgress)

    @Query("UPDATE user_progress SET totalXp = totalXp + :xpDelta, todayFocusMinutes = todayFocusMinutes + :minuteDelta, totalFocusMinutes = totalFocusMinutes + :minuteDelta WHERE id = 1")
    suspend fun addXpAndMinutes(xpDelta: Int, minuteDelta: Int)
}

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard_users ORDER BY rank ASC")
    fun getAllLeaderboardUsers(): Flow<List<LeaderboardUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<LeaderboardUser>)

    @Update
    suspend fun updateUser(user: LeaderboardUser)

    @Query("UPDATE leaderboard_users SET cheersReceived = cheersReceived + 1, cheeredByMe = 1 WHERE id = :userId")
    suspend fun cheerUser(userId: String)
}

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY completedAt DESC")
    fun getAllSessions(): Flow<List<FocusSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSession): Long
}
