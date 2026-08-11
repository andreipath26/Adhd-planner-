package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard_users")
data class LeaderboardUser(
    @PrimaryKey
    val id: String,
    val name: String,
    val initials: String,
    val avatarBgHex: String,
    val avatarTextHex: String,
    val weeklyFocusMinutes: Int,
    val streakDays: Int,
    val rank: Int,
    val currentActivity: String,
    val isCurrentUser: Boolean = false,
    val cheersReceived: Int = 0,
    val cheeredByMe: Boolean = false
)

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long?,
    val taskTitle: String,
    val durationMinutes: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val energyTag: String = "Medium",
    val microStepsCompleted: Int = 0
)
