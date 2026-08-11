package com.example.data.model

enum class DashboardWidgetType(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String
) {
    DAILY_INTENTION("intention", "Daily Intention & Energy", "Today's mood, battery & executive intention", "☀️"),
    VISUAL_TIMER("timer", "Prominent Visual Timer", "Countdown ring with presets & active task", "⏱️"),
    WEEKLY_OVERVIEW("weekly", "Weekly Overview", "7-day schedule & completion heat map", "📅"),
    TASK_DECOMPOSITION("decomp", "Task Micro-Decomposition", "Today's spotlight micro-steps & breakdown", "🧩"),
    PROGRESS_SNAPSHOT("progress", "Gamified Progress Tracker", "XP level, streak flame & milestones", "🏆"),
    BRAIN_DUMP_INBOX("braindump", "Brain Dump Quick Capture", "Mental inbox & fast thought triage", "🧠"),
    ENERGY_SCHEDULE("energy", "Energy & Time Blocks", "Morning, Afternoon, Evening breakdown", "⚡")
}

data class DashboardWidgetConfig(
    val type: DashboardWidgetType,
    val isVisible: Boolean = true,
    val orderIndex: Int
)
