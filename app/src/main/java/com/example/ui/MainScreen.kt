package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DashboardWidgetType
import com.example.ui.components.AddTaskDialog
import com.example.ui.components.BrainDumpQuickSheet
import com.example.ui.components.BrainDumpTriageSheet
import com.example.ui.components.DashboardCustomizeSheet
import com.example.ui.components.DecompositionView
import com.example.ui.components.FocusWhitelistDialog
import com.example.ui.components.SyncDialog
import com.example.ui.screens.DailyMoodCheckInScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FocusScreen
import com.example.ui.screens.PlannerScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SocialLeaderboardScreen
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderSubtle
import com.example.ui.theme.ElegantDarkNavBackground
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantLavenderContainer
import com.example.ui.theme.ElegantLavenderDark
import com.example.ui.theme.ElegantLavenderDeepest
import com.example.ui.theme.ElegantRose
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import com.example.ui.theme.EnergyLowColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: PlannerViewModel) {
    val currentTab by viewModel.currentNavTab.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    val tasksForDay by viewModel.tasksForSelectedDay.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val spotlightTask by viewModel.spotlightTask.collectAsState()
    val allMicroSteps by viewModel.allMicroSteps.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()
    val leaderboardUsers by viewModel.leaderboardUsers.collectAsState()
    val focusSessions by viewModel.focusSessions.collectAsState()
    val decomposingTaskId by viewModel.decomposingTaskId.collectAsState()

    // Timer & Focus States
    val timerDurationTotal by viewModel.timerDurationTotal.collectAsState()
    val timerSecondsRemaining by viewModel.timerSecondsRemaining.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val activeFocusTask by viewModel.activeFocusTask.collectAsState()
    val activeFocusStep by viewModel.activeFocusStep.collectAsState()
    val isFocusModeActive by viewModel.isFocusModeActive.collectAsState()

    // Focus Shield & Whitelist States
    val isNotificationShieldActive by viewModel.isNotificationShieldActive.collectAsState()
    val whitelistContacts by viewModel.whitelistContacts.collectAsState()
    val whitelistApps by viewModel.whitelistApps.collectAsState()
    val focusAudioTrack by viewModel.focusAudioTrack.collectAsState()
    val focusAudioVolume by viewModel.focusAudioVolume.collectAsState()
    val showFocusWhitelistDialog by viewModel.showFocusWhitelistDialog.collectAsState()

    // Dashboard Customization States
    val dashboardWidgets by viewModel.dashboardWidgets.collectAsState()
    val showCustomizeDashboard by viewModel.showCustomizeDashboard.collectAsState()

    // Daily Mood & Energy Check-in States
    val todayCheckIn by viewModel.todayCheckIn.collectAsState()
    val showDailyCheckInScreen by viewModel.showDailyCheckInScreen.collectAsState()

    // Brain Dump States
    val pendingBrainDumps by viewModel.pendingBrainDumpItems.collectAsState()
    val pendingBrainDumpCount by viewModel.pendingBrainDumpCount.collectAsState()
    val showBrainDumpQuick by viewModel.showBrainDumpQuickSheet.collectAsState()
    val showBrainDumpTriage by viewModel.showBrainDumpTriageSheet.collectAsState()
    val isRecordingVoice by viewModel.isRecordingVoice.collectAsState()
    val recordedVoiceSeconds by viewModel.recordedVoiceSeconds.collectAsState()

    // Dialog & Sheet States
    val showAddTask by viewModel.showAddTaskDialog.collectAsState()
    val showSync by viewModel.showSyncDialog.collectAsState()
    val taskForDecomposition by viewModel.taskForDecomposition.collectAsState()
    val gentleAffirmation by viewModel.gentleAffirmation.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentDateStr = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

    // Daily Mood and Energy Check-in screen on app startup / on demand
    if (showDailyCheckInScreen) {
        DailyMoodCheckInScreen(
            currentCheckIn = todayCheckIn,
            onSaveCheckIn = { mood, energy, intention, theme, affirmation, strategy ->
                viewModel.saveDailyCheckIn(mood, energy, intention, theme, affirmation, strategy)
            },
            onDismiss = { viewModel.dismissDailyCheckIn() }
        )
        return
    }

    // If Distraction-Free Immersion Mode is active: HIDE ALL other interface elements
    // (no top bar, no bottom bar, no affirmations, no tabs) - displaying ONLY the currently selected task and visual timer
    if (isFocusModeActive) {
        val activeOrSpotlight = activeFocusTask ?: spotlightTask ?: tasksForDay.firstOrNull()
        val activeSteps = if (activeOrSpotlight != null) {
            allMicroSteps.filter { it.taskId == activeOrSpotlight.id }
        } else emptyList()

        FocusScreen(
            timerDurationTotal = timerDurationTotal,
            timerSecondsRemaining = timerSecondsRemaining,
            isTimerRunning = isTimerRunning,
            activeTask = activeOrSpotlight,
            activeStep = activeFocusStep,
            microSteps = activeSteps,
            isDecomposingWithAi = decomposingTaskId == (activeOrSpotlight?.id ?: -1L),
            progress = userProgress,
            leaderboardUsers = leaderboardUsers,
            isDistractionFreeActive = true,
            isNotificationShieldActive = isNotificationShieldActive,
            focusAudioTrack = focusAudioTrack,
            onTogglePlayPause = { viewModel.togglePlayPauseTimer() },
            onResetTimer = { viewModel.resetTimer() },
            onAddFiveMinutes = { viewModel.addMinutesToTimer(5) },
            onPresetSelected = { viewModel.setTimerPreset(it) },
            onToggleDistractionFree = { viewModel.toggleFocusMode() },
            onToggleStep = { viewModel.toggleMicroStep(it) },
            onStartStepTimer = { step ->
                activeOrSpotlight?.let { t -> viewModel.startTimerForTask(t, step) }
            },
            onDeleteStep = { viewModel.deleteMicroStep(it) },
            onAddStep = { title, duration ->
                activeOrSpotlight?.let { t -> viewModel.addMicroStep(t.id, title, duration) }
            },
            onAiDecompose = {
                activeOrSpotlight?.let { t -> viewModel.decomposeTaskWithAi(t) }
            },
            onOpenProgress = { viewModel.setNavTab(NavTab.PROGRESS) },
            onOpenShieldWhitelist = { viewModel.openFocusWhitelistDialog() },
            onSelectAudioTrack = { viewModel.setFocusAudioTrack(it) },
            onExitFocusMode = { viewModel.exitFocusMode() }
        )

        // Shield whitelist modal if opened in immersion
        if (showFocusWhitelistDialog) {
            FocusWhitelistDialog(
                isNotificationShieldActive = isNotificationShieldActive,
                whitelistContacts = whitelistContacts,
                whitelistApps = whitelistApps,
                focusAudioTrack = focusAudioTrack,
                focusAudioVolume = focusAudioVolume,
                onToggleNotificationShield = { viewModel.toggleNotificationShield() },
                onToggleContact = { viewModel.toggleWhitelistContact(it) },
                onToggleApp = { viewModel.toggleWhitelistApp(it) },
                onAddContact = { name, rel -> viewModel.addCustomWhitelistContact(name, rel) },
                onAddApp = { name, cat -> viewModel.addCustomWhitelistApp(name, cat) },
                onSelectAudioTrack = { viewModel.setFocusAudioTrack(it) },
                onSetAudioVolume = { viewModel.setFocusAudioVolume(it) },
                onDismiss = { viewModel.closeFocusWhitelistDialog() }
            )
        }
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ElegantDarkBackground),
        topBar = {
            HeaderSection(
                dateString = currentDateStr,
                streakDays = userProgress.currentStreak,
                pendingBrainDumpCount = pendingBrainDumpCount,
                isShieldActive = isNotificationShieldActive,
                onStreakClick = { viewModel.setNavTab(NavTab.PROGRESS) },
                onOpenBrainDump = { viewModel.openBrainDumpQuickSheet() },
                onOpenShield = { viewModel.openFocusWhitelistDialog() }
            )
        },
        bottomBar = {
            BottomNavBar(
                currentTab = currentTab,
                pendingBrainDumpCount = pendingBrainDumpCount,
                onTabSelected = { viewModel.setNavTab(it) }
            )
        },
        floatingActionButton = {
            // Global Floating Brain Dump Quick Trigger accessible from any screen
            FloatingActionButton(
                onClick = { viewModel.openBrainDumpQuickSheet() },
                containerColor = ElegantRose,
                contentColor = ElegantDarkSurface,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .testTag("global_brain_dump_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Brain Dump Quick Capture",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Dump 🧠",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        containerColor = ElegantDarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Gentle Affirmation / Focus Nudge Banner
            AnimatedVisibility(
                visible = gentleAffirmation != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                gentleAffirmation?.let { affirmation ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ElegantDarkSurface)
                            .border(1.dp, ElegantLavender.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("gentle_affirmation_banner"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "💡 $affirmation",
                            fontSize = 11.sp,
                            color = ElegantLavender,
                            modifier = Modifier.weight(1f),
                            lineHeight = 15.sp
                        )
                        IconButton(
                            onClick = { viewModel.dismissAffirmation() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss Nudge",
                                tint = ElegantTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Tab View Router (Dashboard, Planner, Focus, Social, Progress)
            when (currentTab) {
                NavTab.DASHBOARD -> {
                    DashboardScreen(
                        widgets = dashboardWidgets,
                        userProgress = userProgress,
                        todayCheckIn = todayCheckIn,
                        todayTasks = tasksForDay,
                        allTasks = allTasks,
                        spotlightTask = spotlightTask,
                        activeFocusTask = activeFocusTask,
                        activeFocusStep = activeFocusStep,
                        allMicroSteps = allMicroSteps,
                        leaderboardUsers = leaderboardUsers,
                        timerDurationTotal = timerDurationTotal,
                        timerSecondsRemaining = timerSecondsRemaining,
                        isTimerRunning = isTimerRunning,
                        selectedDay = selectedDay,
                        pendingBrainDumps = pendingBrainDumps,
                        pendingBrainDumpCount = pendingBrainDumpCount,
                        onSelectDay = { viewModel.selectDay(it) },
                        onToggleTimer = { viewModel.togglePlayPauseTimer() },
                        onResetTimer = { viewModel.resetTimer() },
                        onAddFiveMinutes = { viewModel.addMinutesToTimer(5) },
                        onTimerPresetSelected = { viewModel.setTimerPreset(it) },
                        onEnterFocusMode = { viewModel.enterFocusMode() },
                        onOpenCustomizeDashboard = { viewModel.openCustomizeDashboard() },
                        onOpenDailyCheckIn = { viewModel.openDailyCheckIn() },
                        onQuickBrainDump = { text -> viewModel.addBrainDump(text) },
                        onOpenVoiceBrainDump = { viewModel.openBrainDumpQuickSheet() },
                        onOpenBrainDumpTriage = { viewModel.openBrainDumpTriageSheet() },
                        onNavigateToPlanner = { viewModel.setNavTab(NavTab.PLANNER) },
                        onNavigateToSocial = { viewModel.setNavTab(NavTab.SOCIAL) },
                        onNavigateToProgress = { viewModel.setNavTab(NavTab.PROGRESS) },
                        onMicroStepToggle = { viewModel.toggleMicroStep(it) },
                        onGenerateSubsteps = { viewModel.decomposeTaskWithAi(it) },
                        onAddMicroStep = { taskId, title, mins -> viewModel.addMicroStep(taskId, title, mins) },
                        onDeleteMicroStep = { viewModel.deleteMicroStep(it) },
                        onFocusStepSelected = { step ->
                            val task = spotlightTask ?: tasksForDay.firstOrNull()
                            task?.let { t -> viewModel.startTimerForTask(t, step) }
                        },
                        onSelectTask = { viewModel.setSpotlightTask(it) }
                    )
                }

                NavTab.PLANNER -> {
                    PlannerScreen(
                        selectedDayIndex = selectedDay,
                        tasks = tasksForDay,
                        allMicroSteps = allMicroSteps,
                        onDaySelected = { viewModel.selectDay(it) },
                        onToggleTask = { viewModel.toggleTask(it) },
                        onStartTimer = { viewModel.startTimerForTask(it) },
                        onOpenDecomposition = { viewModel.openDecomposition(it) },
                        onSetSpotlight = { viewModel.setSpotlightTask(it) },
                        onDeleteTask = { viewModel.deleteTask(it) },
                        onAddTaskClick = { viewModel.openAddTaskDialog() },
                        onOpenSyncClick = { viewModel.openSyncDialog() }
                    )
                }

                NavTab.FOCUS -> {
                    val activeOrSpotlight = activeFocusTask ?: spotlightTask ?: tasksForDay.firstOrNull()
                    val activeSteps = if (activeOrSpotlight != null) {
                        allMicroSteps.filter { it.taskId == activeOrSpotlight.id }
                    } else emptyList()

                    FocusScreen(
                        timerDurationTotal = timerDurationTotal,
                        timerSecondsRemaining = timerSecondsRemaining,
                        isTimerRunning = isTimerRunning,
                        activeTask = activeOrSpotlight,
                        activeStep = activeFocusStep,
                        microSteps = activeSteps,
                        isDecomposingWithAi = decomposingTaskId == (activeOrSpotlight?.id ?: -1L),
                        progress = userProgress,
                        leaderboardUsers = leaderboardUsers,
                        isDistractionFreeActive = false,
                        isNotificationShieldActive = isNotificationShieldActive,
                        focusAudioTrack = focusAudioTrack,
                        onTogglePlayPause = { viewModel.togglePlayPauseTimer() },
                        onResetTimer = { viewModel.resetTimer() },
                        onAddFiveMinutes = { viewModel.addMinutesToTimer(5) },
                        onPresetSelected = { viewModel.setTimerPreset(it) },
                        onToggleDistractionFree = { viewModel.enterFocusMode(activeOrSpotlight, activeFocusStep) },
                        onToggleStep = { viewModel.toggleMicroStep(it) },
                        onStartStepTimer = { step ->
                            activeOrSpotlight?.let { t -> viewModel.startTimerForTask(t, step) }
                        },
                        onDeleteStep = { viewModel.deleteMicroStep(it) },
                        onAddStep = { title, duration ->
                            activeOrSpotlight?.let { t -> viewModel.addMicroStep(t.id, title, duration) }
                        },
                        onAiDecompose = {
                            activeOrSpotlight?.let { t -> viewModel.decomposeTaskWithAi(t) }
                        },
                        onOpenProgress = { viewModel.setNavTab(NavTab.PROGRESS) },
                        onOpenShieldWhitelist = { viewModel.openFocusWhitelistDialog() },
                        onSelectAudioTrack = { viewModel.setFocusAudioTrack(it) },
                        onExitFocusMode = { viewModel.exitFocusMode() }
                    )
                }

                NavTab.SOCIAL -> {
                    SocialLeaderboardScreen(
                        users = leaderboardUsers,
                        onCheerUser = { viewModel.cheerUser(it) }
                    )
                }

                NavTab.PROGRESS -> {
                    ProgressScreen(
                        progress = userProgress,
                        badges = viewModel.getBadges(userProgress.unlockedBadges),
                        sessions = focusSessions,
                        onOpenSync = { viewModel.openSyncDialog() }
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet for Dashboard Customization (Widget Arrange & Toggle)
    if (showCustomizeDashboard) {
        DashboardCustomizeSheet(
            widgets = dashboardWidgets,
            onToggleVisibility = { widgetType -> viewModel.toggleWidgetVisibility(widgetType) },
            onMoveUp = { widgetType -> viewModel.moveWidgetUp(widgetType) },
            onMoveDown = { widgetType -> viewModel.moveWidgetDown(widgetType) },
            onResetDefault = { viewModel.resetDashboardWidgets() },
            onDismiss = { viewModel.closeCustomizeDashboard() }
        )
    }

    // Modal Bottom Sheet for Brain Dump Quick Capture (Text or Audio Dictation)
    if (showBrainDumpQuick) {
        BrainDumpQuickSheet(
            pendingCount = pendingBrainDumpCount,
            isRecordingVoice = isRecordingVoice,
            recordedVoiceSeconds = recordedVoiceSeconds,
            onStartVoiceRecording = { viewModel.startVoiceRecording() },
            onStopVoiceRecording = { viewModel.stopVoiceRecording() },
            onSaveThought = { content, category, isAudio, duration, energy ->
                viewModel.addBrainDump(content, category, isAudio, duration, energy)
            },
            onOpenTriage = { viewModel.openBrainDumpTriageSheet() },
            onDismiss = { viewModel.closeBrainDumpQuickSheet() }
        )
    }

    // Modal Bottom Sheet for Brain Dump Triage (Convert to Task, Project, Archive)
    if (showBrainDumpTriage) {
        BrainDumpTriageSheet(
            pendingItems = pendingBrainDumps,
            selectedDay = selectedDay,
            onConvertToTask = { dumpId, title, desc, day, slot, energy, mins, autoDecomp ->
                viewModel.convertBrainDumpToTask(dumpId, title, desc, day, slot, energy, mins, autoDecomp)
            },
            onSaveAsProject = { dumpId -> viewModel.saveBrainDumpAsProject(dumpId) },
            onArchiveItem = { dumpId -> viewModel.archiveBrainDump(dumpId) },
            onDeleteItem = { item -> viewModel.deleteBrainDump(item) },
            onDismiss = { viewModel.closeBrainDumpTriageSheet() }
        )
    }

    // Focus Whitelist & Notification Shield Dialog
    if (showFocusWhitelistDialog) {
        FocusWhitelistDialog(
            isNotificationShieldActive = isNotificationShieldActive,
            whitelistContacts = whitelistContacts,
            whitelistApps = whitelistApps,
            focusAudioTrack = focusAudioTrack,
            focusAudioVolume = focusAudioVolume,
            onToggleNotificationShield = { viewModel.toggleNotificationShield() },
            onToggleContact = { viewModel.toggleWhitelistContact(it) },
            onToggleApp = { viewModel.toggleWhitelistApp(it) },
            onAddContact = { name, rel -> viewModel.addCustomWhitelistContact(name, rel) },
            onAddApp = { name, cat -> viewModel.addCustomWhitelistApp(name, cat) },
            onSelectAudioTrack = { viewModel.setFocusAudioTrack(it) },
            onSetAudioVolume = { viewModel.setFocusAudioVolume(it) },
            onDismiss = { viewModel.closeFocusWhitelistDialog() }
        )
    }

    // Modal Bottom Sheet for Task Decomposition
    if (taskForDecomposition != null) {
        val task = taskForDecomposition!!
        val taskSteps = allMicroSteps.filter { it.taskId == task.id }
        val isDecomposing = decomposingTaskId == task.id

        ModalBottomSheet(
            onDismissRequest = { viewModel.closeDecomposition() },
            sheetState = sheetState,
            containerColor = ElegantDarkSurface,
            contentColor = ElegantTextPrimary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = task.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElegantTextPrimary
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        fontSize = 13.sp,
                        color = ElegantTextSecondary,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                DecompositionView(
                    task = task,
                    steps = taskSteps,
                    isDecomposingWithAi = isDecomposing,
                    onToggleStep = { viewModel.toggleMicroStep(it) },
                    onStartStepTimer = { step ->
                        viewModel.closeDecomposition()
                        viewModel.startTimerForTask(task, step)
                    },
                    onDeleteStep = { viewModel.deleteMicroStep(it) },
                    onAddStep = { title, duration ->
                        viewModel.addMicroStep(task.id, title, duration)
                    },
                    onAiDecompose = {
                        viewModel.decomposeTaskWithAi(task)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Add Task Dialog
    if (showAddTask) {
        AddTaskDialog(
            initialDayOfWeek = selectedDay,
            onDismiss = { viewModel.closeAddTaskDialog() },
            onConfirm = { title, desc, day, slot, cat, energy, mins, autoDecomp ->
                viewModel.addNewTask(title, desc, day, slot, cat, energy, mins, autoDecomp)
            }
        )
    }

    // Cross-Device Sync Dialog
    if (showSync) {
        SyncDialog(
            syncStatus = syncStatus,
            isSyncing = isSyncing,
            onTriggerSync = { viewModel.triggerCloudSync() },
            onDismiss = { viewModel.closeSyncDialog() }
        )
    }
}

@Composable
private fun HeaderSection(
    dateString: String,
    streakDays: Int,
    pendingBrainDumpCount: Int,
    isShieldActive: Boolean,
    onStreakClick: () -> Unit,
    onOpenBrainDump: () -> Unit,
    onOpenShield: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .testTag("app_header"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Date & Title
        Column {
            Text(
                text = dateString.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.6.sp,
                color = ElegantTextSecondary
            )
            Text(
                text = "FocusFlow",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ElegantTextPrimary
            )
        }

        // Header Actions: Shield Indicator, Brain Dump Counter, Streak Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Shield Status Toggle
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isShieldActive) EnergyLowColor.copy(alpha = 0.2f) else ElegantDarkBorder)
                    .border(1.dp, if (isShieldActive) EnergyLowColor else ElegantDarkBorderSubtle, CircleShape)
                    .clickable { onOpenShield() }
                    .padding(8.dp)
                    .testTag("header_shield_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Shield Active",
                    tint = if (isShieldActive) EnergyLowColor else ElegantTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Brain Dump Quick Inbox Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (pendingBrainDumpCount > 0) ElegantRose.copy(alpha = 0.2f) else ElegantDarkBorder)
                    .border(1.dp, if (pendingBrainDumpCount > 0) ElegantRose.copy(alpha = 0.5f) else ElegantDarkBorderSubtle, RoundedCornerShape(12.dp))
                    .clickable { onOpenBrainDump() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("header_brain_dump_btn"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🧠", fontSize = 12.sp)
                    if (pendingBrainDumpCount > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$pendingBrainDumpCount",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantRose
                        )
                    }
                }
            }

            // Streak Pill Badge
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(ElegantDarkBorder)
                    .border(1.dp, ElegantDarkBorderSubtle, CircleShape)
                    .clickable { onStreakClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("streak_badge"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak Fire",
                        tint = ElegantLavender,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$streakDays",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantTextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    currentTab: NavTab,
    pendingBrainDumpCount: Int,
    onTabSelected: (NavTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(ElegantDarkNavBackground)
            .border(0.5.dp, ElegantDarkBorderSubtle)
            .navigationBarsPadding()
            .testTag("bottom_nav_bar"),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavTabItem(
            icon = Icons.Default.Dashboard,
            label = "Dashboard",
            isSelected = currentTab == NavTab.DASHBOARD,
            onClick = { onTabSelected(NavTab.DASHBOARD) },
            tag = "tab_dashboard"
        )
        NavTabItem(
            icon = Icons.Default.CalendarToday,
            label = "Planner",
            isSelected = currentTab == NavTab.PLANNER,
            onClick = { onTabSelected(NavTab.PLANNER) },
            tag = "tab_planner"
        )
        NavTabItem(
            icon = Icons.Default.Timer,
            label = "Focus",
            isSelected = currentTab == NavTab.FOCUS,
            onClick = { onTabSelected(NavTab.FOCUS) },
            tag = "tab_focus"
        )
        NavTabItem(
            icon = Icons.Default.Leaderboard,
            label = "Social",
            isSelected = currentTab == NavTab.SOCIAL,
            onClick = { onTabSelected(NavTab.SOCIAL) },
            tag = "tab_social"
        )
        NavTabItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            isSelected = currentTab == NavTab.PROGRESS,
            onClick = { onTabSelected(NavTab.PROGRESS) },
            tag = "tab_progress"
        )
    }
}

@Composable
private fun NavTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) ElegantLavender else ElegantTextSecondary,
                modifier = Modifier.size(22.dp)
            )
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ElegantRose)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) ElegantLavender else ElegantTextSecondary
        )
    }
}
