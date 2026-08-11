package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EnergyLevel(val label: String, val icon: String) {
    LOW("Low Energy", "🟢"),
    MEDIUM("Medium Focus", "🟡"),
    HIGH("Deep Focus", "🔴")
}

enum class TaskTimeChunk(val label: String, val timeHint: String) {
    MORNING("Morning Block", "08:00 - 12:00"),
    AFTERNOON("Afternoon Block", "12:00 - 17:00"),
    EVENING("Evening Wind-down", "17:00 - 21:00"),
    ANYTIME("Anytime Today", "Flexible")
}

@Entity(tableName = "planner_tasks")
data class PlannerTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dayOfWeek: Int = 1, // 1 = Monday, 7 = Sunday
    val timeSlot: TaskTimeChunk = TaskTimeChunk.MORNING,
    val category: String = "Focus", // Focus, Self-Care, Admin, Creative, Health
    val energyRequired: EnergyLevel = EnergyLevel.MEDIUM,
    val estimatedMinutes: Int = 25,
    val isCompleted: Boolean = false,
    val isDecomposed: Boolean = false,
    val isFocusSpotlight: Boolean = false,
    val colorHex: String = "#10B981",
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val syncedAt: Long = System.currentTimeMillis(),
    val originDeviceId: String = "Device-Main"
)
