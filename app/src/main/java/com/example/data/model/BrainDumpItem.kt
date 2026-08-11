package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BrainDumpStatus {
    PENDING,
    CONVERTED_TASK,
    CONVERTED_PROJECT,
    ARCHIVED
}

@Entity(tableName = "brain_dump_items")
data class BrainDumpItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val categoryTag: String = "Idea", // Idea, Task, Errand, Work, Personal, Random
    val isAudioDictation: Boolean = false,
    val audioDurationSeconds: Int = 0,
    val status: BrainDumpStatus = BrainDumpStatus.PENDING,
    val convertedTaskId: Long? = null,
    val energyHint: EnergyLevel = EnergyLevel.MEDIUM
)
