package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrainDumpItem
import com.example.data.model.DailyCheckIn
import com.example.data.model.DashboardWidgetConfig
import com.example.data.model.DashboardWidgetType
import com.example.data.model.LeaderboardUser
import com.example.data.model.MicroStep
import com.example.data.model.PlannerTask
import com.example.data.model.UserProgress
import com.example.ui.components.BrainDumpWidget
import com.example.ui.components.DailyIntentionWidget
import com.example.ui.components.DecompositionView
import com.example.ui.components.EnergyScheduleWidget
import com.example.ui.components.GamificationCard
import com.example.ui.components.VisualTimerCard
import com.example.ui.components.WeeklyOverviewWidget
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderSubtle
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantLavenderContainer
import com.example.ui.theme.ElegantLavenderDeepest
import com.example.ui.theme.ElegantRose
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import com.example.ui.theme.EnergyHighColor
import com.example.ui.theme.EnergyLowColor

@Composable
fun DashboardScreen(
    widgets: List<DashboardWidgetConfig>,
    userProgress: UserProgress,
    todayCheckIn: DailyCheckIn? = null,
    todayTasks: List<PlannerTask>,
    allTasks: List<PlannerTask>,
    spotlightTask: PlannerTask?,
    activeFocusTask: PlannerTask?,
    activeFocusStep: MicroStep?,
    allMicroSteps: List<MicroStep> = emptyList(),
    leaderboardUsers: List<LeaderboardUser> = emptyList(),
    timerDurationTotal: Int,
    timerSecondsRemaining: Int,
    isTimerRunning: Boolean,
    selectedDay: Int,
    pendingBrainDumps: List<BrainDumpItem>,
    pendingBrainDumpCount: Int,
    onSelectDay: (Int) -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onAddFiveMinutes: () -> Unit,
    onTimerPresetSelected: (Int) -> Unit,
    onEnterFocusMode: () -> Unit,
    onOpenCustomizeDashboard: () -> Unit,
    onOpenDailyCheckIn: () -> Unit = {},
    onQuickBrainDump: (String) -> Unit,
    onOpenVoiceBrainDump: () -> Unit,
    onOpenBrainDumpTriage: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToSocial: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onMicroStepToggle: (MicroStep) -> Unit,
    onGenerateSubsteps: (PlannerTask) -> Unit,
    onAddMicroStep: (taskId: Long, title: String, mins: Int) -> Unit,
    onDeleteMicroStep: (MicroStep) -> Unit,
    onFocusStepSelected: (MicroStep) -> Unit,
    onSelectTask: (PlannerTask) -> Unit,
    modifier: Modifier = Modifier
) {
    val visibleWidgets = widgets.filter { it.isVisible }.sortedBy { it.orderIndex }
    val completedTodayCount = todayTasks.count { it.isCompleted }
    val totalTodayCount = todayTasks.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBackground)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dashboard Header with Executive Status & Customize Trigger
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Executive Hub",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantTextPrimary
                    )
                    Text(
                        text = "$completedTodayCount of $totalTodayCount objectives complete today",
                        fontSize = 12.sp,
                        color = ElegantTextSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Daily Check-in Status Quick Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (todayCheckIn != null) ElegantLavenderContainer.copy(alpha = 0.2f)
                                else ElegantDarkSurfaceVariant
                            )
                            .border(
                                1.dp,
                                if (todayCheckIn != null) ElegantLavender.copy(alpha = 0.4f)
                                else ElegantDarkBorderSubtle,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onOpenDailyCheckIn() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("dashboard_header_checkin_chip"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = todayCheckIn?.mood?.emoji ?: "☀️",
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (todayCheckIn != null) todayCheckIn.mood.title else "Check-in",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (todayCheckIn != null) ElegantLavender else ElegantTextPrimary
                            )
                        }
                    }

                    // Customize Dashboard Layout Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElegantDarkSurfaceVariant)
                            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(12.dp))
                            .clickable { onOpenCustomizeDashboard() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("customize_dashboard_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Customize Dashboard",
                                tint = ElegantLavender,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Layout",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElegantLavender
                            )
                        }
                    }
                }
            }
        }

        // Executive Brain Dump Quick Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(ElegantDarkSurface)
                    .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(18.dp))
                    .padding(14.dp)
                    .testTag("dashboard_brain_dump_banner")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ElegantRose.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Brain Dump",
                                tint = ElegantRose,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Brain Dump Inbox",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElegantTextPrimary
                            )
                            Text(
                                text = if (pendingBrainDumpCount == 0) "Mind clear • 0 pending thoughts" else "$pendingBrainDumpCount thoughts awaiting triage",
                                fontSize = 11.sp,
                                color = if (pendingBrainDumpCount > 0) ElegantRose else ElegantTextSecondary
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Dictate / Capture Voice
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ElegantDarkSurfaceVariant)
                                .border(1.dp, ElegantDarkBorderSubtle, CircleShape)
                            .clickable { onOpenVoiceBrainDump() }
                            .padding(8.dp)
                            .testTag("dashboard_mic_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Dictate",
                            tint = ElegantLavender,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Triage Inbox
                    if (pendingBrainDumpCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(ElegantRose)
                                .clickable { onOpenBrainDumpTriage() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("dashboard_triage_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Triage",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElegantDarkSurface
                            )
                        }
                    }
                }
            }
        }
    }

    // Dynamically Render User-Arranged Widgets
    items(visibleWidgets.size, key = { visibleWidgets[it].type.id }) { index ->
        when (visibleWidgets[index].type) {
            DashboardWidgetType.DAILY_INTENTION -> {
                DailyIntentionWidget(
                    todayCheckIn = todayCheckIn,
                    onOpenCheckIn = onOpenDailyCheckIn
                )
            }

            DashboardWidgetType.VISUAL_TIMER -> {
                val targetTask = activeFocusTask ?: spotlightTask ?: todayTasks.firstOrNull()
                val taskName = targetTask?.title ?: "ADHD Focus Block"
                val taskSteps = allMicroSteps.filter { it.taskId == targetTask?.id }
                val phaseName = activeFocusStep?.title
                    ?: (taskSteps.firstOrNull { !it.isDone }?.title ?: "Warm-up & Flow")

                VisualTimerCard(
                    totalSeconds = timerDurationTotal,
                    secondsRemaining = timerSecondsRemaining,
                    isRunning = isTimerRunning,
                    taskTitle = taskName,
                    phaseSubtitle = phaseName,
                    onTogglePlayPause = onToggleTimer,
                    onReset = onResetTimer,
                    onAddFiveMinutes = onAddFiveMinutes,
                    onPresetSelected = onTimerPresetSelected,
                    onToggleDistractionFree = onEnterFocusMode
                )
            }

            DashboardWidgetType.WEEKLY_OVERVIEW -> {
                WeeklyOverviewWidget(
                    allTasks = allTasks,
                    selectedDay = selectedDay,
                    onSelectDay = onSelectDay,
                    onNavigateToPlanner = onNavigateToPlanner
                )
            }

            DashboardWidgetType.TASK_DECOMPOSITION -> {
                val targetTask = spotlightTask ?: todayTasks.firstOrNull()
                val targetSteps = allMicroSteps.filter { it.taskId == targetTask?.id }
                DecompositionView(
                    task = targetTask,
                    steps = targetSteps,
                    onSubstepToggle = onMicroStepToggle,
                    onAddSubstep = { taskId, title, mins -> onAddMicroStep(taskId, title, mins) },
                    onDeleteSubstep = onDeleteMicroStep,
                    onGenerateAiSubsteps = { targetTask?.let { onGenerateSubsteps(it) } },
                    onStartFocus = { step -> onFocusStepSelected(step) }
                )
            }

            DashboardWidgetType.PROGRESS_SNAPSHOT -> {
                GamificationCard(
                    progress = userProgress,
                    leaderboardUsers = leaderboardUsers,
                    onCardClick = onNavigateToProgress
                )
            }

            DashboardWidgetType.BRAIN_DUMP_INBOX -> {
                BrainDumpWidget(
                    pendingItems = pendingBrainDumps,
                    pendingCount = pendingBrainDumpCount,
                    onQuickCapture = onQuickBrainDump,
                    onOpenVoiceCapture = onOpenVoiceBrainDump,
                    onOpenTriage = onOpenBrainDumpTriage
                )
            }

            DashboardWidgetType.ENERGY_SCHEDULE -> {
                EnergyScheduleWidget(
                    tasks = todayTasks,
                    onTaskClick = onSelectTask
                )
            }
        }
    }

    // Bottom Quick Action / Focus Mode CTA
    item {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(ElegantDarkSurface)
                .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(18.dp))
                .clickable { onEnterFocusMode() }
                .padding(16.dp)
                .testTag("dashboard_enter_immersion_cta"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ElegantLavenderContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CenterFocusStrong,
                            contentDescription = "Focus Immersion",
                            tint = ElegantLavenderDeepest,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Launch Distraction-Free Space",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantTextPrimary
                        )
                        Text(
                            text = "Full immersion • Suppress notifications",
                            fontSize = 11.sp,
                            color = ElegantTextSecondary
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Launch",
                    tint = ElegantLavender,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
}
