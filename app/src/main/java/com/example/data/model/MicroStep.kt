package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "micro_steps",
    foreignKeys = [
        ForeignKey(
            entity = PlannerTask::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["taskId"])]
)
data class MicroStep(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long,
    val title: String,
    val durationMinutes: Int = 5,
    val isDone: Boolean = false,
    val orderIndex: Int = 0,
    val encouragementTip: String = ""
)
