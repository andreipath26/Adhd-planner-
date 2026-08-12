package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.AchievementBadge
import com.example.data.model.BrainDumpItem
import com.example.data.model.BrainDumpStatus
import com.example.data.model.DailyCheckIn
import com.example.data.model.DailyMicroGoal
import com.example.data.model.DailyMood
import com.example.data.model.DashboardWidgetConfig
import com.example.data.model.DashboardWidgetType
import com.example.data.model.EnergyLevel
import com.example.data.model.FocusAudioTrack
import com.example.data.model.FocusSession
import com.example.data.model.LeaderboardUser
import com.example.data.model.MicroStep
import com.example.data.model.PlannerTask
import com.example.data.model.TaskTimeChunk
import com.example.data.model.UserProgress
import com.example.data.model.WhitelistApp
import com.example.data.model.WhitelistContact
import com.example.data.repository.PlannerRepository
import com.example.util.GentleNotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class NavTab {
    DASHBOARD,
    PLANNER,
    FOCUS,
    SOCIAL,
    PROGRESS
}

class PlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = PlannerRepository(db)
    private val context: Context get() = getApplication<Application>().applicationContext

    // Navigation Tab
    private val _currentNavTab = MutableStateFlow(NavTab.DASHBOARD)
    val currentNavTab: StateFlow<NavTab> = _currentNavTab.asStateFlow()

    fun setNavTab(tab: NavTab) {
        _currentNavTab.value = tab
    }

    // Selected Day (1 = Mon, ..., 7 = Sun)
    private val _selectedDay = MutableStateFlow(getCurrentDayOfWeekIndex())
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    fun selectDay(dayIndex: Int) {
        _selectedDay.value = dayIndex
    }

    // Tasks for selected day
    val tasksForSelectedDay: StateFlow<List<PlannerTask>> = _selectedDay
        .flatMapLatest { day -> repository.getTasksForDay(day) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Tasks (for weekly stats / overview)
    val allTasks: StateFlow<List<PlannerTask>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Spotlight / Current Top Priority Task
    val spotlightTask: StateFlow<PlannerTask?> = repository.getSpotlightTask()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All micro-steps
    val allMicroSteps: StateFlow<List<MicroStep>> = repository.getAllMicroSteps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Progress & Gamification
    val userProgress: StateFlow<UserProgress> = repository.getUserProgress()
        .combine(_selectedDay) { progress, _ ->
            progress ?: UserProgress()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProgress())

    // Leaderboard Users
    val leaderboardUsers: StateFlow<List<LeaderboardUser>> = repository.getLeaderboardUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Focus Sessions History
    val focusSessions: StateFlow<List<FocusSession>> = repository.getFocusSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Decomposing status
    private val _decomposingTaskId = MutableStateFlow<Long?>(null)
    val decomposingTaskId: StateFlow<Long?> = _decomposingTaskId.asStateFlow()

    // Dialog / Sheet states
    private val _taskForDecomposition = MutableStateFlow<PlannerTask?>(null)
    val taskForDecomposition: StateFlow<PlannerTask?> = _taskForDecomposition.asStateFlow()

    private val _showAddTaskDialog = MutableStateFlow(false)
    val showAddTaskDialog: StateFlow<Boolean> = _showAddTaskDialog.asStateFlow()

    private val _showSyncDialog = MutableStateFlow(false)
    val showSyncDialog: StateFlow<Boolean> = _showSyncDialog.asStateFlow()

    private val _gentleAffirmation = MutableStateFlow<String?>(null)
    val gentleAffirmation: StateFlow<String?> = _gentleAffirmation.asStateFlow()

    // Sync State
    private val _syncStatus = MutableStateFlow("All devices synchronized")
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    // Visual Timer State
    private val _timerDurationTotal = MutableStateFlow(25 * 60) // in seconds
    val timerDurationTotal: StateFlow<Int> = _timerDurationTotal.asStateFlow()

    private val _timerSecondsRemaining = MutableStateFlow(18 * 60 + 42) // in seconds
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _showTimerCompletionCue = MutableStateFlow(false)
    val showTimerCompletionCue: StateFlow<Boolean> = _showTimerCompletionCue.asStateFlow()

    private val _isBreakMode = MutableStateFlow(false)
    val isBreakMode: StateFlow<Boolean> = _isBreakMode.asStateFlow()

    private val _activeFocusTask = MutableStateFlow<PlannerTask?>(null)
    val activeFocusTask: StateFlow<PlannerTask?> = _activeFocusTask.asStateFlow()

    private val _activeFocusStep = MutableStateFlow<MicroStep?>(null)
    val activeFocusStep: StateFlow<MicroStep?> = _activeFocusStep.asStateFlow()

    // Focus Mode (Full Distraction-Free Immersion)
    private val _isFocusModeActive = MutableStateFlow(false)
    val isFocusModeActive: StateFlow<Boolean> = _isFocusModeActive.asStateFlow()

    // Notification Shield / Whitelist
    private val _isNotificationShieldActive = MutableStateFlow(true)
    val isNotificationShieldActive: StateFlow<Boolean> = _isNotificationShieldActive.asStateFlow()

    private val _showFocusWhitelistDialog = MutableStateFlow(false)
    val showFocusWhitelistDialog: StateFlow<Boolean> = _showFocusWhitelistDialog.asStateFlow()

    private val _whitelistContacts = MutableStateFlow<List<WhitelistContact>>(
        listOf(
            WhitelistContact("c_1", "Mom / Emergency", "Family", isAllowed = true),
            WhitelistContact("c_2", "Alex (Work Lead)", "Boss / Team", isAllowed = true),
            WhitelistContact("c_3", "Dr. Miller", "Doctor", isAllowed = false),
            WhitelistContact("c_4", "Sarah", "Friend", isAllowed = false)
        )
    )
    val whitelistContacts: StateFlow<List<WhitelistContact>> = _whitelistContacts.asStateFlow()

    private val _whitelistApps = MutableStateFlow<List<WhitelistApp>>(
        listOf(
            WhitelistApp("a_1", "Phone Calls", "Emergency Alerts", isAllowed = true),
            WhitelistApp("a_2", "Slack Mentions", "Work Critical", isAllowed = false),
            WhitelistApp("a_3", "Calendar Reminders", "Schedule", isAllowed = true),
            WhitelistApp("a_4", "Messages", "Personal", isAllowed = false)
        )
    )
    val whitelistApps: StateFlow<List<WhitelistApp>> = _whitelistApps.asStateFlow()

    private val _focusAudioTrack = MutableStateFlow(FocusAudioTrack.WHITE_NOISE)
    val focusAudioTrack: StateFlow<FocusAudioTrack> = _focusAudioTrack.asStateFlow()

    private val _focusAudioVolume = MutableStateFlow(0.6f)
    val focusAudioVolume: StateFlow<Float> = _focusAudioVolume.asStateFlow()

    // --- Customizable Dashboard Widgets State ---
    private val _dashboardWidgets = MutableStateFlow<List<DashboardWidgetConfig>>(
        listOf(
            DashboardWidgetConfig(DashboardWidgetType.DAILY_INTENTION, isVisible = true, orderIndex = 0),
            DashboardWidgetConfig(DashboardWidgetType.VISUAL_TIMER, isVisible = true, orderIndex = 1),
            DashboardWidgetConfig(DashboardWidgetType.WEEKLY_OVERVIEW, isVisible = true, orderIndex = 2),
            DashboardWidgetConfig(DashboardWidgetType.TASK_DECOMPOSITION, isVisible = true, orderIndex = 3),
            DashboardWidgetConfig(DashboardWidgetType.PROGRESS_SNAPSHOT, isVisible = true, orderIndex = 4),
            DashboardWidgetConfig(DashboardWidgetType.BRAIN_DUMP_INBOX, isVisible = true, orderIndex = 5),
            DashboardWidgetConfig(DashboardWidgetType.ENERGY_SCHEDULE, isVisible = true, orderIndex = 6)
        )
    )
    val dashboardWidgets: StateFlow<List<DashboardWidgetConfig>> = _dashboardWidgets.asStateFlow()

    private val _showCustomizeDashboard = MutableStateFlow(false)
    val showCustomizeDashboard: StateFlow<Boolean> = _showCustomizeDashboard.asStateFlow()

    // --- Daily Mood & Energy Check-in State ---
    val todayCheckIn: StateFlow<DailyCheckIn?> = repository.getTodayCheckIn(getTodayDateString())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _showDailyCheckInScreen = MutableStateFlow(false)
    val showDailyCheckInScreen: StateFlow<Boolean> = _showDailyCheckInScreen.asStateFlow()

    // --- Brain Dump State ---
    val allBrainDumpItems: StateFlow<List<BrainDumpItem>> = repository.getAllBrainDumpItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingBrainDumpItems: StateFlow<List<BrainDumpItem>> = repository.getPendingBrainDumpItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingBrainDumpCount: StateFlow<Int> = repository.getPendingBrainDumpCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _showBrainDumpQuickSheet = MutableStateFlow(false)
    val showBrainDumpQuickSheet: StateFlow<Boolean> = _showBrainDumpQuickSheet.asStateFlow()

    private val _showBrainDumpTriageSheet = MutableStateFlow(false)
    val showBrainDumpTriageSheet: StateFlow<Boolean> = _showBrainDumpTriageSheet.asStateFlow()

    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _recordedVoiceSeconds = MutableStateFlow(0)
    val recordedVoiceSeconds: StateFlow<Int> = _recordedVoiceSeconds.asStateFlow()

    // --- Welcome & Donation Landing Page State ---
    private val prefs by lazy {
        getApplication<Application>().getSharedPreferences("focusflow_app_prefs", Context.MODE_PRIVATE)
    }

    private val _hasDonated = MutableStateFlow(false)
    val hasDonated: StateFlow<Boolean> = _hasDonated.asStateFlow()

    private val _showWelcomeLandingScreen = MutableStateFlow(false)
    val showWelcomeLandingScreen: StateFlow<Boolean> = _showWelcomeLandingScreen.asStateFlow()

    private var voiceTimerJob: Job? = null
    private var timerJob: Job? = null

    init {
        // Ensure database has seed data if empty
        viewModelScope.launch {
            try {
                if (repository.getUserProgressSync() == null) {
                    db.populateInitialSeedData()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Check Welcome / Donation Landing page condition (3-day interval unless donated)
        checkWelcomeLandingCondition()

        // Trigger initial gentle reminder check
        viewModelScope.launch {
            try {
                delay(1500)
                _gentleAffirmation.value = GentleNotificationHelper.getRandomAffirmation()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun checkWelcomeLandingCondition() {
        val donated = prefs.getBoolean("has_donated", false)
        _hasDonated.value = donated
        if (donated) {
            _showWelcomeLandingScreen.value = false
            return
        }

        val lastShowTime = prefs.getLong("last_welcome_show_timestamp", 0L)
        val currentTime = System.currentTimeMillis()
        val threeDaysMs = 3L * 24 * 60 * 60 * 1000L // 3 days in ms

        if (lastShowTime == 0L || (currentTime - lastShowTime) >= threeDaysMs) {
            _showWelcomeLandingScreen.value = true
            prefs.edit().putLong("last_welcome_show_timestamp", currentTime).apply()
        } else {
            _showWelcomeLandingScreen.value = false
        }
    }

    fun handleUserDonation(amount: String) {
        prefs.edit().putBoolean("has_donated", true).apply()
        _hasDonated.value = true
        _showWelcomeLandingScreen.value = false
    }

    fun dismissWelcomeLandingScreen() {
        _showWelcomeLandingScreen.value = false
    }

    fun openWelcomeLandingScreen() {
        _showWelcomeLandingScreen.value = true
    }

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun openDailyCheckIn() {
        _showDailyCheckInScreen.value = true
    }

    fun dismissDailyCheckIn() {
        _showDailyCheckInScreen.value = false
    }

    fun saveDailyCheckIn(
        mood: DailyMood,
        energyLevel: Int,
        intention: String,
        theme: String,
        affirmation: String,
        strategy: String,
        microGoals: List<DailyMicroGoal> = emptyList()
    ) {
        viewModelScope.launch {
            val checkIn = DailyCheckIn(
                date = getTodayDateString(),
                mood = mood,
                energyLevel = energyLevel,
                intention = intention,
                focusTheme = theme,
                mindfulAffirmation = affirmation,
                recommendedStrategy = strategy,
                microGoals = microGoals,
                completedAt = System.currentTimeMillis()
            )
            repository.saveDailyCheckIn(checkIn)
            _showDailyCheckInScreen.value = false
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
            GentleNotificationHelper.sendFocusNudge(
                context,
                "Intention Locked In 🌟",
                "Today's Mindset: ${mood.title} • $intention"
            )
        }
    }

    fun toggleDailyMicroGoal(goalId: String) {
        viewModelScope.launch {
            val isDone = repository.toggleDailyMicroGoal(goalId, getTodayDateString())
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = isDone)
            if (isDone) {
                _gentleAffirmation.value = "Micro-goal achieved! Mindful habits build focus & joy."
            }
        }
    }

    private fun getCurrentDayOfWeekIndex(): Int {
        val cal = Calendar.getInstance()
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }
    }

    // --- Dashboard Customization Actions ---

    fun openCustomizeDashboard() {
        _showCustomizeDashboard.value = true
    }

    fun closeCustomizeDashboard() {
        _showCustomizeDashboard.value = false
    }

    fun toggleWidgetVisibility(type: DashboardWidgetType) {
        val current = _dashboardWidgets.value.toMutableList()
        val index = current.indexOfFirst { it.type == type }
        if (index != -1) {
            val item = current[index]
            current[index] = item.copy(isVisible = !item.isVisible)
            _dashboardWidgets.value = current
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
        }
    }

    fun moveWidgetUp(type: DashboardWidgetType) {
        val current = _dashboardWidgets.value.sortedBy { it.orderIndex }.toMutableList()
        val index = current.indexOfFirst { it.type == type }
        if (index > 0) {
            val item = current[index]
            val prev = current[index - 1]
            current[index] = item.copy(orderIndex = prev.orderIndex)
            current[index - 1] = prev.copy(orderIndex = item.orderIndex)
            _dashboardWidgets.value = current.sortedBy { it.orderIndex }
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = false)
        }
    }

    fun moveWidgetDown(type: DashboardWidgetType) {
        val current = _dashboardWidgets.value.sortedBy { it.orderIndex }.toMutableList()
        val index = current.indexOfFirst { it.type == type }
        if (index != -1 && index < current.size - 1) {
            val item = current[index]
            val next = current[index + 1]
            current[index] = item.copy(orderIndex = next.orderIndex)
            current[index + 1] = next.copy(orderIndex = item.orderIndex)
            _dashboardWidgets.value = current.sortedBy { it.orderIndex }
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = false)
        }
    }

    fun resetDashboardWidgets() {
        _dashboardWidgets.value = listOf(
            DashboardWidgetConfig(DashboardWidgetType.DAILY_INTENTION, isVisible = true, orderIndex = 0),
            DashboardWidgetConfig(DashboardWidgetType.VISUAL_TIMER, isVisible = true, orderIndex = 1),
            DashboardWidgetConfig(DashboardWidgetType.WEEKLY_OVERVIEW, isVisible = true, orderIndex = 2),
            DashboardWidgetConfig(DashboardWidgetType.TASK_DECOMPOSITION, isVisible = true, orderIndex = 3),
            DashboardWidgetConfig(DashboardWidgetType.PROGRESS_SNAPSHOT, isVisible = true, orderIndex = 4),
            DashboardWidgetConfig(DashboardWidgetType.BRAIN_DUMP_INBOX, isVisible = true, orderIndex = 5),
            DashboardWidgetConfig(DashboardWidgetType.ENERGY_SCHEDULE, isVisible = true, orderIndex = 6)
        )
        GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
    }

    // --- Brain Dump Actions ---

    fun openBrainDumpQuickSheet() {
        _showBrainDumpQuickSheet.value = true
    }

    fun closeBrainDumpQuickSheet() {
        _showBrainDumpQuickSheet.value = false
        stopVoiceRecording()
    }

    fun openBrainDumpTriageSheet() {
        _showBrainDumpTriageSheet.value = true
    }

    fun closeBrainDumpTriageSheet() {
        _showBrainDumpTriageSheet.value = false
    }

    fun addBrainDump(
        content: String,
        category: String = "Idea",
        isAudio: Boolean = false,
        durationSeconds: Int = 0,
        energyHint: EnergyLevel = EnergyLevel.MEDIUM
    ) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val item = BrainDumpItem(
                content = content.trim(),
                categoryTag = category,
                isAudioDictation = isAudio,
                audioDurationSeconds = durationSeconds,
                energyHint = energyHint,
                status = BrainDumpStatus.PENDING
            )
            repository.insertBrainDumpItem(item)
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
            GentleNotificationHelper.sendFocusNudge(
                context,
                "Thought Captured! 🧠",
                "Stored in your Brain Dump inbox. Ready whenever you want to triage."
            )
        }
    }

    fun convertBrainDumpToTask(
        dumpId: Long,
        title: String,
        description: String = "",
        dayOfWeek: Int = selectedDay.value,
        timeSlot: TaskTimeChunk = TaskTimeChunk.MORNING,
        energyLevel: EnergyLevel = EnergyLevel.MEDIUM,
        estimatedMinutes: Int = 25,
        autoDecompose: Boolean = false
    ) {
        viewModelScope.launch {
            val colorHex = when (energyLevel) {
                EnergyLevel.LOW -> "#86EFAC"
                EnergyLevel.MEDIUM -> "#D0BCFF"
                EnergyLevel.HIGH -> "#EFB8C8"
            }
            val newTask = PlannerTask(
                title = title.trim(),
                description = description.trim(),
                dayOfWeek = dayOfWeek,
                timeSlot = timeSlot,
                category = "Triaged Task",
                energyRequired = energyLevel,
                estimatedMinutes = estimatedMinutes,
                colorHex = colorHex
            )
            repository.convertBrainDumpToTask(dumpId, newTask, autoDecompose)
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
            GentleNotificationHelper.sendFocusNudge(
                context,
                "Converted to Task! 📋",
                "Added to your schedule. +25 XP for clearing mental clutter!"
            )
        }
    }

    fun saveBrainDumpAsProject(dumpId: Long) {
        viewModelScope.launch {
            repository.saveBrainDumpAsProject(dumpId)
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
        }
    }

    fun archiveBrainDump(dumpId: Long) {
        viewModelScope.launch {
            repository.archiveBrainDumpItem(dumpId)
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = false)
        }
    }

    fun deleteBrainDump(item: BrainDumpItem) {
        viewModelScope.launch {
            repository.deleteBrainDumpItem(item)
        }
    }

    fun startVoiceRecording() {
        _isRecordingVoice.value = true
        _recordedVoiceSeconds.value = 0
        voiceTimerJob?.cancel()
        voiceTimerJob = viewModelScope.launch {
            while (_isRecordingVoice.value) {
                delay(1000)
                _recordedVoiceSeconds.value += 1
            }
        }
        GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
    }

    fun stopVoiceRecording(): Int {
        _isRecordingVoice.value = false
        voiceTimerJob?.cancel()
        val duration = _recordedVoiceSeconds.value
        _recordedVoiceSeconds.value = 0
        return duration
    }

    // --- Focus Mode & Whitelist Actions ---

    fun enterFocusMode(task: PlannerTask? = null, step: MicroStep? = null) {
        if (task != null) {
            _activeFocusTask.value = task
            _activeFocusStep.value = step
            val mins = step?.durationMinutes ?: task.estimatedMinutes
            _timerDurationTotal.value = mins * 60
            _timerSecondsRemaining.value = mins * 60
        }
        _isFocusModeActive.value = true
        startTimer()
        GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
    }

    fun exitFocusMode() {
        _isFocusModeActive.value = false
    }

    fun toggleFocusMode() {
        if (_isFocusModeActive.value) {
            exitFocusMode()
        } else {
            enterFocusMode(spotlightTask.value ?: tasksForSelectedDay.value.firstOrNull())
        }
    }

    fun openFocusWhitelistDialog() {
        _showFocusWhitelistDialog.value = true
    }

    fun closeFocusWhitelistDialog() {
        _showFocusWhitelistDialog.value = false
    }

    fun toggleWhitelistContact(id: String) {
        val current = _whitelistContacts.value.toMutableList()
        val idx = current.indexOfFirst { it.id == id }
        if (idx != -1) {
            current[idx] = current[idx].copy(isAllowed = !current[idx].isAllowed)
            _whitelistContacts.value = current
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
        }
    }

    fun toggleWhitelistApp(id: String) {
        val current = _whitelistApps.value.toMutableList()
        val idx = current.indexOfFirst { it.id == id }
        if (idx != -1) {
            current[idx] = current[idx].copy(isAllowed = !current[idx].isAllowed)
            _whitelistApps.value = current
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
        }
    }

    fun addCustomWhitelistContact(name: String, relation: String) {
        if (name.isBlank()) return
        val current = _whitelistContacts.value.toMutableList()
        val id = "contact_${System.currentTimeMillis()}"
        current.add(WhitelistContact(id, name.trim(), relation.trim().ifBlank { "Personal" }, isAllowed = true))
        _whitelistContacts.value = current
        GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
    }

    fun addCustomWhitelistApp(appName: String, category: String) {
        if (appName.isBlank()) return
        val current = _whitelistApps.value.toMutableList()
        val id = "app_${System.currentTimeMillis()}"
        current.add(WhitelistApp(id, appName.trim(), category.trim().ifBlank { "Utility" }, isAllowed = true))
        _whitelistApps.value = current
        GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
    }

    fun setFocusAudioTrack(track: FocusAudioTrack) {
        _focusAudioTrack.value = track
        GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = false)
    }

    fun setFocusAudioVolume(volume: Float) {
        _focusAudioVolume.value = volume.coerceIn(0f, 1f)
    }

    fun toggleNotificationShield() {
        _isNotificationShieldActive.value = !_isNotificationShieldActive.value
        GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = _isNotificationShieldActive.value)
    }

    // --- Task Actions ---

    fun addNewTask(
        title: String,
        description: String,
        dayOfWeek: Int,
        timeSlot: TaskTimeChunk,
        category: String,
        energyLevel: EnergyLevel,
        estimatedMinutes: Int,
        autoDecompose: Boolean
    ) {
        viewModelScope.launch {
            val colorHex = when (energyLevel) {
                EnergyLevel.LOW -> "#86EFAC"
                EnergyLevel.MEDIUM -> "#D0BCFF"
                EnergyLevel.HIGH -> "#EFB8C8"
            }
            val newTask = PlannerTask(
                title = title.trim(),
                description = description.trim(),
                dayOfWeek = dayOfWeek,
                timeSlot = timeSlot,
                category = category,
                energyRequired = energyLevel,
                estimatedMinutes = estimatedMinutes,
                colorHex = colorHex
            )
            val newId = repository.insertTask(newTask)
            if (autoDecompose) {
                _decomposingTaskId.value = newId
                repository.decomposeTaskWithAi(newTask.copy(id = newId))
                _decomposingTaskId.value = null
            }
            _showAddTaskDialog.value = false
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
        }
    }

    fun toggleTask(task: PlannerTask) {
        viewModelScope.launch {
            val wasCompleted = repository.toggleTaskCompleted(task)
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = wasCompleted)
            if (wasCompleted) {
                GentleNotificationHelper.sendFocusNudge(
                    context,
                    "Task Completed! 🎉",
                    "Great work finishing '${task.title}'. +50 XP gained!"
                )
            }
        }
    }

    fun deleteTask(task: PlannerTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun setSpotlightTask(task: PlannerTask) {
        viewModelScope.launch {
            repository.setSpotlightTask(task.id)
            _activeFocusTask.value = task
            setTimerPreset(task.estimatedMinutes)
        }
    }

    // --- Micro Step Actions ---

    fun openDecomposition(task: PlannerTask) {
        _taskForDecomposition.value = task
    }

    fun closeDecomposition() {
        _taskForDecomposition.value = null
    }

    fun decomposeTaskWithAi(task: PlannerTask) {
        viewModelScope.launch {
            _decomposingTaskId.value = task.id
            repository.decomposeTaskWithAi(task)
            _decomposingTaskId.value = null
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
        }
    }

    fun toggleMicroStep(step: MicroStep) {
        viewModelScope.launch {
            val isDone = repository.toggleMicroStep(step)
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = isDone)
            if (isDone) {
                _gentleAffirmation.value = "Micro-win unlocked! Tiny steps build great momentum."
            }
        }
    }

    fun addMicroStep(taskId: Long, title: String, duration: Int) {
        viewModelScope.launch {
            val count = allMicroSteps.value.count { it.taskId == taskId }
            repository.addMicroStep(
                MicroStep(
                    taskId = taskId,
                    title = title,
                    durationMinutes = duration,
                    orderIndex = count,
                    encouragementTip = "One small step at a time."
                )
            )
        }
    }

    fun deleteMicroStep(step: MicroStep) {
        viewModelScope.launch {
            repository.deleteMicroStep(step)
        }
    }

    // --- Timer Actions ---

    fun startTimerForTask(task: PlannerTask, microStep: MicroStep? = null) {
        _activeFocusTask.value = task
        _activeFocusStep.value = microStep
        _isBreakMode.value = false
        _showTimerCompletionCue.value = false
        val mins = microStep?.durationMinutes ?: task.estimatedMinutes
        _timerDurationTotal.value = mins * 60
        _timerSecondsRemaining.value = mins * 60
        _currentNavTab.value = NavTab.FOCUS
        startTimer()
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        _showTimerCompletionCue.value = false
        _isTimerRunning.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerSecondsRemaining.value > 0 && _isTimerRunning.value) {
                delay(1000)
                _timerSecondsRemaining.value -= 1
            }
            if (_timerSecondsRemaining.value <= 0 && _isTimerRunning.value) {
                _isTimerRunning.value = false
                _showTimerCompletionCue.value = true
                onTimerCompleted()
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun togglePlayPauseTimer() {
        if (_isTimerRunning.value) {
            pauseTimer()
        } else {
            if (_timerSecondsRemaining.value <= 0) {
                _timerSecondsRemaining.value = _timerDurationTotal.value
            }
            startTimer()
        }
    }

    fun resetTimer() {
        pauseTimer()
        _showTimerCompletionCue.value = false
        _timerSecondsRemaining.value = _timerDurationTotal.value
    }

    fun addMinutesToTimer(minutes: Int) {
        val extraSeconds = minutes * 60
        _timerDurationTotal.value += extraSeconds
        _timerSecondsRemaining.value += extraSeconds
        _showTimerCompletionCue.value = false
        GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = false)
    }

    fun setTimerPreset(minutes: Int, isBreak: Boolean = false) {
        pauseTimer()
        _isBreakMode.value = isBreak
        _showTimerCompletionCue.value = false
        _timerDurationTotal.value = minutes * 60
        _timerSecondsRemaining.value = minutes * 60
    }

    fun startBreakTimer(minutes: Int) {
        setTimerPreset(minutes, isBreak = true)
        startTimer()
    }

    fun startFocusPreset(minutes: Int) {
        setTimerPreset(minutes, isBreak = false)
        startTimer()
    }

    fun dismissTimerCompletion() {
        _showTimerCompletionCue.value = false
    }

    private fun onTimerCompleted() {
        val minutesLogged = (_timerDurationTotal.value / 60).coerceAtLeast(1)
        val task = _activeFocusTask.value
        val step = _activeFocusStep.value

        viewModelScope.launch {
            repository.recordFocusSession(
                taskId = task?.id,
                taskTitle = task?.title ?: "Freeform Focus Session",
                durationMinutes = minutesLogged,
                energyTag = task?.energyRequired?.label ?: "Medium Focus",
                microStepsCompleted = if (step != null) 1 else 0
            )

            if (step != null && !step.isDone) {
                repository.toggleMicroStep(step)
            }

            GentleNotificationHelper.sendFocusNudge(
                context,
                "Focus Session Complete! 🌿",
                "You focused for $minutesLogged mins on '${task?.title ?: "Deep Work"}'. Great job!"
            )
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
        }
    }

    // --- Community Leaderboard Actions ---

    fun cheerUser(user: LeaderboardUser) {
        viewModelScope.launch {
            if (!user.cheeredByMe) {
                repository.cheerLeaderboardUser(user.id)
                GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
            }
        }
    }

    // --- Cross-Device Sync Simulation ---

    fun triggerCloudSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncStatus.value = "Syncing with cloud devices..."
            delay(1200)
            _isSyncing.value = false
            _syncStatus.value = "Synced with all 3 devices (Phone, Tablet, Web)"
            GentleNotificationHelper.triggerGentleHaptic(context, isSuccess = true)
        }
    }

    fun openAddTaskDialog() {
        _showAddTaskDialog.value = true
    }

    fun closeAddTaskDialog() {
        _showAddTaskDialog.value = false
    }

    fun openSyncDialog() {
        _showSyncDialog.value = true
    }

    fun closeSyncDialog() {
        _showSyncDialog.value = false
    }

    fun dismissAffirmation() {
        _gentleAffirmation.value = null
    }

    fun getBadges(unlockedStr: String): List<AchievementBadge> {
        return repository.getAllBadges(unlockedStr)
    }
}

