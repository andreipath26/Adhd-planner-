package com.example.data.repository

import com.example.data.AppDatabase
import com.example.data.ai.TaskDecomposerAi
import com.example.data.model.AchievementBadge
import com.example.data.model.BrainDumpItem
import com.example.data.model.BrainDumpStatus
import com.example.data.model.DailyCheckIn
import com.example.data.model.FocusSession
import com.example.data.model.LeaderboardUser
import com.example.data.model.MicroStep
import com.example.data.model.PlannerTask
import com.example.data.model.UserProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONArray
import org.json.JSONObject

class PlannerRepository(private val db: AppDatabase) {

    fun getTodayCheckIn(date: String): Flow<DailyCheckIn?> = db.dailyCheckInDao().getCheckInForDate(date)

    fun getLatestCheckIn(): Flow<DailyCheckIn?> = db.dailyCheckInDao().getLatestCheckIn()

    fun getAllCheckIns(): Flow<List<DailyCheckIn>> = db.dailyCheckInDao().getAllCheckIns()

    suspend fun getTodayCheckInSync(date: String): DailyCheckIn? = db.dailyCheckInDao().getCheckInForDateSync(date)

    suspend fun saveDailyCheckIn(checkIn: DailyCheckIn): Long {
        val existing = db.dailyCheckInDao().getCheckInForDateSync(checkIn.date)
        val id = db.dailyCheckInDao().insertOrUpdate(checkIn)
        if (existing == null) {
            // First check-in for the day grants +25 mindfulness XP!
            awardXp(25, 0)
        }
        return id
    }

    fun getAllTasks(): Flow<List<PlannerTask>> = db.taskDao().getAllTasks()

    fun getTasksForDay(dayOfWeek: Int): Flow<List<PlannerTask>> = db.taskDao().getTasksForDay(dayOfWeek)

    fun getSpotlightTask(): Flow<PlannerTask?> = db.taskDao().getSpotlightTask()

    fun getStepsForTask(taskId: Long): Flow<List<MicroStep>> = db.microStepDao().getStepsForTask(taskId)

    fun getAllMicroSteps(): Flow<List<MicroStep>> = db.microStepDao().getAllMicroSteps()

    fun getUserProgress(): Flow<UserProgress?> = db.userProgressDao().getProgress()

    fun getLeaderboardUsers(): Flow<List<LeaderboardUser>> = db.leaderboardDao().getAllLeaderboardUsers()

    fun getFocusSessions(): Flow<List<FocusSession>> = db.focusSessionDao().getAllSessions()

    // Brain Dump operations
    fun getAllBrainDumpItems(): Flow<List<BrainDumpItem>> = db.brainDumpDao().getAllItems()

    fun getPendingBrainDumpItems(): Flow<List<BrainDumpItem>> = db.brainDumpDao().getPendingItems()

    fun getPendingBrainDumpCount(): Flow<Int> = db.brainDumpDao().getPendingCount()

    suspend fun insertBrainDumpItem(item: BrainDumpItem): Long {
        awardXp(10, 0) // Quick reward for capturing mental clutter!
        return db.brainDumpDao().insertItem(item)
    }

    suspend fun updateBrainDumpItem(item: BrainDumpItem) {
        db.brainDumpDao().updateItem(item)
    }

    suspend fun deleteBrainDumpItem(item: BrainDumpItem) {
        db.brainDumpDao().deleteItem(item)
    }

    suspend fun convertBrainDumpToTask(
        dumpId: Long,
        task: PlannerTask,
        autoDecompose: Boolean
    ): Long {
        val newTaskId = db.taskDao().insertTask(task)
        db.brainDumpDao().updateStatus(dumpId, BrainDumpStatus.CONVERTED_TASK, newTaskId)
        if (autoDecompose) {
            decomposeTaskWithAi(task.copy(id = newTaskId))
        }
        awardXp(25, 0) // Reward for mental triage!
        return newTaskId
    }

    suspend fun saveBrainDumpAsProject(dumpId: Long) {
        db.brainDumpDao().updateStatus(dumpId, BrainDumpStatus.CONVERTED_PROJECT)
        awardXp(15, 0)
    }

    suspend fun archiveBrainDumpItem(dumpId: Long) {
        db.brainDumpDao().updateStatus(dumpId, BrainDumpStatus.ARCHIVED)
    }

    suspend fun clearArchivedBrainDumps() {
        db.brainDumpDao().clearArchived()
    }

    suspend fun insertTask(task: PlannerTask): Long {
        return db.taskDao().insertTask(task)
    }

    suspend fun updateTask(task: PlannerTask) {
        db.taskDao().updateTask(task)
    }

    suspend fun deleteTask(task: PlannerTask) {
        db.microStepDao().deleteStepsForTask(task.id)
        db.taskDao().deleteTask(task)
    }

    suspend fun setSpotlightTask(taskId: Long) {
        db.taskDao().setSpotlightTask(taskId)
    }

    suspend fun clearSpotlight() {
        db.taskDao().clearSpotlight()
    }

    suspend fun toggleTaskCompleted(task: PlannerTask): Boolean {
        val updatedCompleted = !task.isCompleted
        val updatedTask = task.copy(
            isCompleted = updatedCompleted,
            completedAt = if (updatedCompleted) System.currentTimeMillis() else null
        )
        db.taskDao().updateTask(updatedTask)

        // Award XP and update progress if completed
        if (updatedCompleted) {
            val xpGain = when (task.energyRequired) {
                com.example.data.model.EnergyLevel.LOW -> 30
                com.example.data.model.EnergyLevel.MEDIUM -> 50
                com.example.data.model.EnergyLevel.HIGH -> 80
            }
            awardXp(xpGain, task.estimatedMinutes)
        }
        return updatedCompleted
    }

    suspend fun toggleMicroStep(step: MicroStep): Boolean {
        val updatedDone = !step.isDone
        val updatedStep = step.copy(isDone = updatedDone)
        db.microStepDao().updateStep(updatedStep)

        if (updatedDone) {
            awardXp(15, step.durationMinutes)
            // Check if all microsteps for the parent task are done
            val allSteps = db.microStepDao().getStepsForTaskList(step.taskId)
            if (allSteps.isNotEmpty() && allSteps.all { it.isDone }) {
                val parent = db.taskDao().getTaskById(step.taskId)
                if (parent != null && !parent.isCompleted) {
                    toggleTaskCompleted(parent)
                }
            }
        }
        return updatedDone
    }

    suspend fun addMicroStep(step: MicroStep): Long {
        val id = db.microStepDao().insertStep(step)
        val parent = db.taskDao().getTaskById(step.taskId)
        if (parent != null && !parent.isDecomposed) {
            db.taskDao().updateTask(parent.copy(isDecomposed = true))
        }
        return id
    }

    suspend fun deleteMicroStep(step: MicroStep) {
        db.microStepDao().deleteStep(step)
    }

    suspend fun decomposeTaskWithAi(task: PlannerTask): List<MicroStep> {
        // Clear previous if any
        db.microStepDao().deleteStepsForTask(task.id)
        val steps = TaskDecomposerAi.decomposeTask(task.id, task.title, task.description)
        db.microStepDao().insertSteps(steps)
        db.taskDao().updateTask(task.copy(isDecomposed = true))
        awardXp(20, 0) // Reward for breaking it down!
        return steps
    }

    suspend fun recordFocusSession(
        taskId: Long?,
        taskTitle: String,
        durationMinutes: Int,
        energyTag: String,
        microStepsCompleted: Int
    ) {
        db.focusSessionDao().insertSession(
            FocusSession(
                taskId = taskId,
                taskTitle = taskTitle,
                durationMinutes = durationMinutes,
                energyTag = energyTag,
                microStepsCompleted = microStepsCompleted
            )
        )
        // Award XP: 2 XP per focus minute
        awardXp(durationMinutes * 2, durationMinutes)
    }

    suspend fun awardXp(xpDelta: Int, minuteDelta: Int) {
        val currentProgress = db.userProgressDao().getProgressSync() ?: UserProgress()
        val newTotalXp = currentProgress.totalXp + xpDelta
        val newLevel = (newTotalXp / 250) + 1
        val levelTitle = when (newLevel) {
            1 -> "Focus Beginner"
            2 -> "Habit Explorer"
            3 -> "Routine Finder"
            4 -> "Momentum Builder"
            5 -> "Deep Flow Pioneer"
            6 -> "ADHD Mastery Champion"
            else -> "Zen Flow Master"
        }

        val updatedProgress = currentProgress.copy(
            totalXp = newTotalXp,
            currentLevel = newLevel,
            levelTitle = levelTitle,
            todayFocusMinutes = currentProgress.todayFocusMinutes + minuteDelta,
            totalFocusMinutes = currentProgress.totalFocusMinutes + minuteDelta,
            completedTasksCount = currentProgress.completedTasksCount + if (minuteDelta > 0 && xpDelta >= 30) 1 else 0
        )
        db.userProgressDao().insertOrUpdate(updatedProgress)
    }

    suspend fun cheerLeaderboardUser(userId: String) {
        db.leaderboardDao().cheerUser(userId)
        awardXp(10, 0) // Kindness bonus
    }

    fun getAllBadges(unlockedIdsStr: String): List<AchievementBadge> {
        val unlockedSet = unlockedIdsStr.split(",").map { it.trim() }.toSet()
        return listOf(
            AchievementBadge("FIRST_STEP", "First Micro-Step", "Broke down and completed your very first micro-step.", "🌱", unlockedSet.contains("FIRST_STEP")),
            AchievementBadge("MOMENTUM_3", "Momentum 3-Day", "Maintained focus streak for 3 consecutive days.", "🔥", unlockedSet.contains("MOMENTUM_3")),
            AchievementBadge("DEEP_DIVE", "Deep Work Flow", "Logged 25+ minutes of continuous visual timer focus.", "⚡", unlockedSet.contains("DEEP_DIVE")),
            AchievementBadge("MICRO_MASTER", "Micro-Master", "Decomposed 5 complex tasks into bite-sized chunks.", "🧩", unlockedSet.contains("MICRO_MASTER")),
            AchievementBadge("ZEN_WEEK", "7-Day Clarity", "Logged in every day this week without skipping.", "👑", unlockedSet.contains("ZEN_WEEK")),
            AchievementBadge("COMMUNITY_CHEER", "Supportive Friend", "Sent encouragement cheers to accountability peers.", "🤝", unlockedSet.contains("COMMUNITY_CHEER"))
        )
    }

    // Cross-Device Sync Simulation & Export/Import
    suspend fun exportDataJson(): String {
        val root = JSONObject()
        val tasks = db.taskDao().getAllTasks().firstOrNull() ?: emptyList()
        val tasksArray = JSONArray()
        tasks.forEach { t ->
            val obj = JSONObject().apply {
                put("id", t.id)
                put("title", t.title)
                put("description", t.description)
                put("dayOfWeek", t.dayOfWeek)
                put("category", t.category)
                put("estimatedMinutes", t.estimatedMinutes)
                put("isCompleted", t.isCompleted)
                put("energyRequired", t.energyRequired.name)
            }
            tasksArray.put(obj)
        }
        root.put("tasks", tasksArray)
        root.put("syncedAt", System.currentTimeMillis())
        root.put("deviceId", "Device-Android-Client")
        return root.toString(2)
    }
}
