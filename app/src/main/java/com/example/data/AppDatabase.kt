package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.BrainDumpDao
import com.example.data.dao.DailyCheckInDao
import com.example.data.dao.FocusSessionDao
import com.example.data.dao.LeaderboardDao
import com.example.data.dao.MicroStepDao
import com.example.data.dao.TaskDao
import com.example.data.dao.UserProgressDao
import com.example.data.model.BrainDumpItem
import com.example.data.model.BrainDumpStatus
import com.example.data.model.DailyCheckIn
import com.example.data.model.DailyMicroGoal
import com.example.data.model.DailyMood
import com.example.data.model.EnergyLevel
import com.example.data.model.FocusSession
import com.example.data.model.LeaderboardUser
import com.example.data.model.MicroStep
import com.example.data.model.PlannerTask
import com.example.data.model.TaskTimeChunk
import com.example.data.model.UserProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun fromEnergyLevel(value: EnergyLevel?): String = value?.name ?: EnergyLevel.MEDIUM.name

    @TypeConverter
    fun toEnergyLevel(value: String?): EnergyLevel =
        try {
            value?.let { EnergyLevel.valueOf(it) } ?: EnergyLevel.MEDIUM
        } catch (e: Exception) {
            EnergyLevel.MEDIUM
        }

    @TypeConverter
    fun fromTaskTimeChunk(value: TaskTimeChunk?): String = value?.name ?: TaskTimeChunk.MORNING.name

    @TypeConverter
    fun toTaskTimeChunk(value: String?): TaskTimeChunk =
        try {
            value?.let { TaskTimeChunk.valueOf(it) } ?: TaskTimeChunk.MORNING
        } catch (e: Exception) {
            TaskTimeChunk.MORNING
        }

    @TypeConverter
    fun fromBrainDumpStatus(value: BrainDumpStatus?): String = value?.name ?: BrainDumpStatus.PENDING.name

    @TypeConverter
    fun toBrainDumpStatus(value: String?): BrainDumpStatus =
        try {
            value?.let { BrainDumpStatus.valueOf(it) } ?: BrainDumpStatus.PENDING
        } catch (e: Exception) {
            BrainDumpStatus.PENDING
        }

    @TypeConverter
    fun fromDailyMood(value: DailyMood?): String = value?.name ?: DailyMood.CALM.name

    @TypeConverter
    fun toDailyMood(value: String?): DailyMood =
        try {
            value?.let { DailyMood.valueOf(it) } ?: DailyMood.CALM
        } catch (e: Exception) {
            DailyMood.CALM
        }

    @TypeConverter
    fun fromMicroGoalList(goals: List<DailyMicroGoal>?): String {
        if (goals.isNullOrEmpty()) return ""
        val array = JSONArray()
        for (goal in goals) {
            val obj = JSONObject().apply {
                put("id", goal.id)
                put("title", goal.title)
                put("emoji", goal.emoji)
                put("isCompleted", goal.isCompleted)
                goal.completedAt?.let { put("completedAt", it) }
            }
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toMicroGoalList(jsonStr: String?): List<DailyMicroGoal> {
        if (jsonStr.isNullOrBlank()) return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<DailyMicroGoal>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    DailyMicroGoal(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        title = obj.getString("title"),
                        emoji = obj.optString("emoji", "✨"),
                        isCompleted = obj.optBoolean("isCompleted", false),
                        completedAt = if (obj.has("completedAt")) obj.getLong("completedAt") else null
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }
}

@Database(
    entities = [
        PlannerTask::class,
        MicroStep::class,
        UserProgress::class,
        LeaderboardUser::class,
        FocusSession::class,
        BrainDumpItem::class,
        DailyCheckIn::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun microStepDao(): MicroStepDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun leaderboardDao(): LeaderboardDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun brainDumpDao(): BrainDumpDao
    abstract fun dailyCheckInDao(): DailyCheckInDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "focusflow_planner_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate initial seed data
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).populateInitialSeedData()
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun populateInitialSeedData() {
        // Initial user progress
        userProgressDao().insertOrUpdate(
            UserProgress(
                id = 1,
                currentStreak = 12,
                bestStreak = 14,
                totalXp = 820,
                currentLevel = 4,
                levelTitle = "Momentum Builder",
                todayFocusMinutes = 45,
                totalFocusMinutes = 380,
                completedTasksCount = 18,
                lastActiveDate = "2026-08-11",
                unlockedBadges = "FIRST_STEP,MOMENTUM_3,DEEP_DIVE,MICRO_MASTER"
            )
        )

        // Initial tasks for Monday (dayOfWeek = 1) through Sunday (dayOfWeek = 7)
        val initialTasks = listOf(
            PlannerTask(
                id = 1,
                title = "Deep Work: Report",
                description = "Phase 1: Research Notes & Executive outline",
                dayOfWeek = 1, // Monday
                timeSlot = TaskTimeChunk.MORNING,
                category = "Focus",
                energyRequired = EnergyLevel.HIGH,
                estimatedMinutes = 25,
                isCompleted = false,
                isDecomposed = true,
                isFocusSpotlight = true,
                colorHex = "#D0BCFF"
            ),
            PlannerTask(
                id = 2,
                title = "Inbox Zero & Triage",
                description = "Quick 10-minute scan, flag high priority only",
                dayOfWeek = 1,
                timeSlot = TaskTimeChunk.MORNING,
                category = "Admin",
                energyRequired = EnergyLevel.LOW,
                estimatedMinutes = 10,
                isCompleted = true,
                isDecomposed = false,
                isFocusSpotlight = false,
                colorHex = "#86EFAC"
            ),
            PlannerTask(
                id = 3,
                title = "Team Sync & Status Update",
                description = "Share week milestones & blocker notes",
                dayOfWeek = 1,
                timeSlot = TaskTimeChunk.AFTERNOON,
                category = "Collaboration",
                energyRequired = EnergyLevel.MEDIUM,
                estimatedMinutes = 20,
                isCompleted = false,
                isDecomposed = true,
                colorHex = "#EFB8C8"
            ),
            PlannerTask(
                id = 4,
                title = "Hydration & Gentle Walk",
                description = "Step away from screens for 15 mins",
                dayOfWeek = 1,
                timeSlot = TaskTimeChunk.AFTERNOON,
                category = "Self-Care",
                energyRequired = EnergyLevel.LOW,
                estimatedMinutes = 15,
                isCompleted = false,
                isDecomposed = false,
                colorHex = "#86EFAC"
            ),
            PlannerTask(
                id = 5,
                title = "Weekly Review & Workspace Clear",
                description = "Tidy desktop and plan tomorrow's 3 wins",
                dayOfWeek = 1,
                timeSlot = TaskTimeChunk.EVENING,
                category = "Reflection",
                energyRequired = EnergyLevel.LOW,
                estimatedMinutes = 15,
                isCompleted = false,
                isDecomposed = false,
                colorHex = "#CCC2DC"
            ),
            // Tuesday
            PlannerTask(
                id = 6,
                title = "Draft Executive Summary",
                description = "Condense report research into 3 bullet points",
                dayOfWeek = 2,
                timeSlot = TaskTimeChunk.MORNING,
                category = "Focus",
                energyRequired = EnergyLevel.HIGH,
                estimatedMinutes = 30,
                isCompleted = false,
                colorHex = "#D0BCFF"
            ),
            PlannerTask(
                id = 7,
                title = "Organize Project Assets",
                description = "Sort downloads folder into folder archives",
                dayOfWeek = 2,
                timeSlot = TaskTimeChunk.AFTERNOON,
                category = "Admin",
                energyRequired = EnergyLevel.LOW,
                estimatedMinutes = 15,
                isCompleted = false,
                colorHex = "#86EFAC"
            ),
            // Wednesday
            PlannerTask(
                id = 8,
                title = "Design Prototype Wireframes",
                description = "Sketch 4 mobile viewport layouts",
                dayOfWeek = 3,
                timeSlot = TaskTimeChunk.MORNING,
                category = "Creative",
                energyRequired = EnergyLevel.HIGH,
                estimatedMinutes = 40,
                isCompleted = false,
                colorHex = "#D0BCFF"
            ),
            // Thursday
            PlannerTask(
                id = 9,
                title = "Code Review & Refactor",
                description = "Clean up ViewModel states and Room DAOs",
                dayOfWeek = 4,
                timeSlot = TaskTimeChunk.MORNING,
                category = "Focus",
                energyRequired = EnergyLevel.HIGH,
                estimatedMinutes = 35,
                isCompleted = false,
                colorHex = "#D0BCFF"
            ),
            // Friday
            PlannerTask(
                id = 10,
                title = "Weekly Victory Celebration & Retrospective",
                description = "Review achievements & reward yourself",
                dayOfWeek = 5,
                timeSlot = TaskTimeChunk.AFTERNOON,
                category = "Reflection",
                energyRequired = EnergyLevel.LOW,
                estimatedMinutes = 20,
                isCompleted = false,
                colorHex = "#EFB8C8"
            )
        )
        taskDao().insertTasks(initialTasks)

        // Micro-steps for Task 1 ("Deep Work: Report")
        val task1Steps = listOf(
            MicroStep(
                id = 1,
                taskId = 1,
                title = "Gather existing data sources",
                durationMinutes = 5,
                isDone = true,
                orderIndex = 0,
                encouragementTip = "Just opening the links is 80% of the battle!"
            ),
            MicroStep(
                id = 2,
                taskId = 1,
                title = "Structure initial outline",
                durationMinutes = 5,
                isDone = true,
                orderIndex = 1,
                encouragementTip = "Quick bullet points only, don't worry about prose."
            ),
            MicroStep(
                id = 3,
                taskId = 1,
                title = "Draft executive summary",
                durationMinutes = 10,
                isDone = false,
                orderIndex = 2,
                encouragementTip = "Focus mode active. You've got this!"
            ),
            MicroStep(
                id = 4,
                taskId = 1,
                title = "Proofread & polish formatting",
                durationMinutes = 5,
                isDone = false,
                orderIndex = 3,
                encouragementTip = "Final sprint! Almost across the finish line."
            )
        )
        microStepDao().insertSteps(task1Steps)

        // Seed Community Leaderboard Users (ADHD Accountability Tribe)
        val seedUsers = listOf(
            LeaderboardUser(
                id = "user_1",
                name = "Jessica D.",
                initials = "JD",
                avatarBgHex = "#EFB8C8",
                avatarTextHex = "#492532",
                weeklyFocusMinutes = 485,
                streakDays = 15,
                rank = 1,
                currentActivity = "Deep Work: Thesis",
                isCurrentUser = false,
                cheersReceived = 34,
                cheeredByMe = false
            ),
            LeaderboardUser(
                id = "user_me",
                name = "You (Me)",
                initials = "ME",
                avatarBgHex = "#D0BCFF",
                avatarTextHex = "#381E72",
                weeklyFocusMinutes = 420,
                streakDays = 12,
                rank = 2,
                currentActivity = "Deep Work: Report",
                isCurrentUser = true,
                cheersReceived = 28,
                cheeredByMe = false
            ),
            LeaderboardUser(
                id = "user_3",
                name = "Marcus L.",
                initials = "ML",
                avatarBgHex = "#CCC2DC",
                avatarTextHex = "#332D41",
                weeklyFocusMinutes = 390,
                streakDays = 9,
                rank = 3,
                currentActivity = "Pomodoro: UI Polish",
                isCurrentUser = false,
                cheersReceived = 19,
                cheeredByMe = false
            ),
            LeaderboardUser(
                id = "user_4",
                name = "Aisha K.",
                initials = "AK",
                avatarBgHex = "#86EFAC",
                avatarTextHex = "#14532D",
                weeklyFocusMinutes = 340,
                streakDays = 8,
                rank = 4,
                currentActivity = "Micro-step: Math Homework",
                isCurrentUser = false,
                cheersReceived = 12,
                cheeredByMe = false
            ),
            LeaderboardUser(
                id = "user_5",
                name = "Sam R.",
                initials = "SR",
                avatarBgHex = "#FDE047",
                avatarTextHex = "#713F12",
                weeklyFocusMinutes = 295,
                streakDays = 6,
                rank = 5,
                currentActivity = "Wind-down: Reading",
                isCurrentUser = false,
                cheersReceived = 8,
                cheeredByMe = false
            )
        )
        leaderboardDao().insertAll(seedUsers)

        // Seed Initial Brain Dump Items
        val seedBrainDumps = listOf(
            BrainDumpItem(
                id = 1,
                content = "Buy replacement noise-cancelling ear cushions",
                createdAt = System.currentTimeMillis() - 3600000 * 2,
                categoryTag = "Errand",
                energyHint = EnergyLevel.LOW,
                status = BrainDumpStatus.PENDING
            ),
            BrainDumpItem(
                id = 2,
                content = "Idea: Test 10-minute micro-break rule between deep work blocks",
                createdAt = System.currentTimeMillis() - 3600000 * 5,
                categoryTag = "Idea",
                energyHint = EnergyLevel.MEDIUM,
                status = BrainDumpStatus.PENDING
            ),
            BrainDumpItem(
                id = 3,
                content = "Ask team lead about Q3 documentation priority before Friday",
                createdAt = System.currentTimeMillis() - 3600000 * 8,
                categoryTag = "Work",
                energyHint = EnergyLevel.MEDIUM,
                status = BrainDumpStatus.PENDING
            )
        )
        brainDumpDao().insertAll(seedBrainDumps)
    }
}
