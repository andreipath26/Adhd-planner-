package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DailyMood(
    val emoji: String,
    val title: String,
    val description: String,
    val colorHex: String,
    val tip: String
) {
    ENERGIZED(
        emoji = "⚡",
        title = "Energized",
        description = "High drive & mental clarity",
        colorHex = "#FDE047",
        tip = "Capitalize on high momentum! Tackle your top spotlight task early."
    ),
    CALM(
        emoji = "🌿",
        title = "Calm & Grounded",
        description = "Centered, steady & balanced",
        colorHex = "#86EFAC",
        tip = "Great state for consistent, uninterrupted progress across planned blocks."
    ),
    FOCUSED(
        emoji = "🎯",
        title = "Locked In",
        description = "Ready for deep single-tasking",
        colorHex = "#D0BCFF",
        tip = "Activate Distraction-Free Immersion and protect your focus blocks."
    ),
    SCATTERED(
        emoji = "🌪️",
        title = "Scattered",
        description = "Racing thoughts or restless",
        colorHex = "#93C5FD",
        tip = "Break big goals into 5-minute micro-steps. Do not multitask today."
    ),
    FATIGUED(
        emoji = "🔋",
        title = "Low Battery",
        description = "Tired or low physical energy",
        colorHex = "#FCA5A5",
        tip = "Keep today gentle: 1-2 small wins, plenty of water, and frequent breaks."
    ),
    OVERWHELMED(
        emoji = "🌧️",
        title = "Overwhelmed",
        description = "Heavy mental load / anxious",
        colorHex = "#EFB8C8",
        tip = "Dump your mind into the Brain Dump inbox first. Pick only ONE tiny action."
    )
}

@Entity(tableName = "daily_check_ins")
data class DailyCheckIn(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // e.g. "2026-08-11"
    val mood: DailyMood = DailyMood.CALM,
    val energyLevel: Int = 3, // 1 (Lowest) to 5 (Peak Hyperfocus)
    val intention: String = "Protect my focus and work on what matters most",
    val focusTheme: String = "Single-tasking",
    val mindfulAffirmation: String = "One step at a time is all it takes.",
    val recommendedStrategy: String = "Schedule your main priority in the morning block.",
    val microGoals: List<DailyMicroGoal> = emptyList(),
    val completedAt: Long = System.currentTimeMillis()
) {
    val energyPercentage: Int
        get() = (energyLevel * 20).coerceIn(20, 100)

    val energyLabel: String
        get() = when (energyLevel) {
            1 -> "Low Battery (Gentle Pacing)"
            2 -> "Modest Energy (Light Load)"
            3 -> "Steady Flow (Normal Focus)"
            4 -> "High Capacity (Deep Work)"
            5 -> "Peak Hyperfocus (Major Sprint)"
            else -> "Steady Energy"
        }

    val completedMicroGoalsCount: Int
        get() = microGoals.count { it.isCompleted }

    val allMicroGoalsCompleted: Boolean
        get() = microGoals.isNotEmpty() && microGoals.all { it.isCompleted }
}

data class DailyMicroGoal(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val emoji: String = "✨",
    val durationMinutes: Int = 5,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)

object DefaultMicroGoals {
    val presets = listOf(
        DailyMicroGoal(title = "Meditate for 5 mins", emoji = "🧘", durationMinutes = 5),
        DailyMicroGoal(title = "Drink a large glass of water", emoji = "💧", durationMinutes = 2),
        DailyMicroGoal(title = "10-minute fresh air walk", emoji = "🚶", durationMinutes = 10),
        DailyMicroGoal(title = "3 deep physiological sigh breaths", emoji = "🫁", durationMinutes = 2),
        DailyMicroGoal(title = "10 mins of morning sunlight", emoji = "☀️", durationMinutes = 10),
        DailyMicroGoal(title = "30-min phone-free morning block", emoji = "📵", durationMinutes = 30),
        DailyMicroGoal(title = "5-min neck & shoulder desk stretch", emoji = "🙆", durationMinutes = 5),
        DailyMicroGoal(title = "Savor tea/coffee screen-free", emoji = "☕", durationMinutes = 5),
        DailyMicroGoal(title = "Quick 2-min brain release dump", emoji = "📝", durationMinutes = 2)
    )

    val allPresets = presets

    fun forMood(mood: DailyMood): List<DailyMicroGoal> = when (mood) {
        DailyMood.ENERGIZED -> listOf(
            DailyMicroGoal(title = "15-minute brisk walk outside", emoji = "🚶", durationMinutes = 15),
            DailyMicroGoal(title = "30-min phone-free morning block", emoji = "📵", durationMinutes = 30),
            DailyMicroGoal(title = "Drink 1L of water before noon", emoji = "💧", durationMinutes = 2)
        )
        DailyMood.CALM -> listOf(
            DailyMicroGoal(title = "Meditate for 5 mins", emoji = "🧘", durationMinutes = 5),
            DailyMicroGoal(title = "Hydrate with a tall glass of water", emoji = "💧", durationMinutes = 2),
            DailyMicroGoal(title = "Savor tea/coffee screen-free", emoji = "☕", durationMinutes = 5)
        )
        DailyMood.FOCUSED -> listOf(
            DailyMicroGoal(title = "Put phone in another room during focus", emoji = "📵", durationMinutes = 25),
            DailyMicroGoal(title = "2-min box breathing reset", emoji = "🫁", durationMinutes = 2),
            DailyMicroGoal(title = "Keep water bottle on desk", emoji = "💧", durationMinutes = 2)
        )
        DailyMood.SCATTERED -> listOf(
            DailyMicroGoal(title = "3 deep physiological sighs", emoji = "🫁", durationMinutes = 2),
            DailyMicroGoal(title = "Meditate for 5 mins", emoji = "🧘", durationMinutes = 5),
            DailyMicroGoal(title = "5-min desk stretch & posture reset", emoji = "🙆", durationMinutes = 5)
        )
        DailyMood.FATIGUED -> listOf(
            DailyMicroGoal(title = "Drink a tall cold glass of water", emoji = "💧", durationMinutes = 2),
            DailyMicroGoal(title = "10 mins of morning sunlight", emoji = "☀️", durationMinutes = 10),
            DailyMicroGoal(title = "10-minute quiet eye rest break", emoji = "🛋️", durationMinutes = 10)
        )
        DailyMood.OVERWHELMED -> listOf(
            DailyMicroGoal(title = "3 deep physiological sigh breaths", emoji = "🫁", durationMinutes = 2),
            DailyMicroGoal(title = "Meditate for 5 mins", emoji = "🧘", durationMinutes = 5),
            DailyMicroGoal(title = "Quick 2-min brain release dump", emoji = "📝", durationMinutes = 2)
        )
    }
}
