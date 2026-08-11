package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey
    val id: Int = 1,
    val currentStreak: Int = 12,
    val bestStreak: Int = 14,
    val totalXp: Int = 820,
    val currentLevel: Int = 4,
    val levelTitle: String = "Momentum Builder",
    val todayFocusMinutes: Int = 45,
    val totalFocusMinutes: Int = 380,
    val completedTasksCount: Int = 18,
    val lastActiveDate: String = "", // e.g. "2026-08-11"
    val unlockedBadges: String = "FIRST_STEP,MOMENTUM_3,DEEP_DIVE,MICRO_MASTER" // comma-separated
) {
    val xpForNextLevel: Int
        get() = currentLevel * 250

    val currentLevelProgress: Float
        get() {
            val levelBaseXp = (currentLevel - 1) * 250
            val xpInCurrentLevel = (totalXp - levelBaseXp).coerceAtLeast(0)
            val neededInCurrentLevel = 250
            return (xpInCurrentLevel.toFloat() / neededInCurrentLevel).coerceIn(0f, 1f)
        }

    val xpInCurrentLevelDisplay: Int
        get() {
            val levelBaseXp = (currentLevel - 1) * 250
            return (totalXp - levelBaseXp).coerceAtLeast(0)
        }
}

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean
)
