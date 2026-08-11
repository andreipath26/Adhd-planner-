package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FocusAudioTrack
import com.example.data.model.LeaderboardUser
import com.example.data.model.MicroStep
import com.example.data.model.PlannerTask
import com.example.data.model.UserProgress
import com.example.ui.components.DecompositionView
import com.example.ui.components.GamificationCard
import com.example.ui.components.VisualTimerCard
import com.example.ui.components.VisualTimerDisk
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderSubtle
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
import com.example.ui.theme.EnergyHighColor
import com.example.ui.theme.EnergyLowColor

@Composable
fun FocusScreen(
    timerDurationTotal: Int,
    timerSecondsRemaining: Int,
    isTimerRunning: Boolean,
    activeTask: PlannerTask?,
    activeStep: MicroStep?,
    microSteps: List<MicroStep>,
    isDecomposingWithAi: Boolean,
    progress: UserProgress,
    leaderboardUsers: List<LeaderboardUser>,
    isDistractionFreeActive: Boolean,
    isNotificationShieldActive: Boolean,
    focusAudioTrack: FocusAudioTrack,
    onTogglePlayPause: () -> Unit,
    onResetTimer: () -> Unit,
    onAddFiveMinutes: () -> Unit,
    onPresetSelected: (Int) -> Unit,
    onToggleDistractionFree: () -> Unit,
    onToggleStep: (MicroStep) -> Unit,
    onStartStepTimer: (MicroStep) -> Unit,
    onDeleteStep: (MicroStep) -> Unit,
    onAddStep: (String, Int) -> Unit,
    onAiDecompose: () -> Unit,
    onOpenProgress: () -> Unit,
    onOpenShieldWhitelist: () -> Unit,
    onSelectAudioTrack: (FocusAudioTrack) -> Unit,
    onExitFocusMode: () -> Unit = onToggleDistractionFree,
    modifier: Modifier = Modifier
) {
    val displayTitle = activeTask?.title ?: "Deep Work Focus Block"
    val displayPhase = activeStep?.let { "Micro-step: ${it.title}" }
        ?: (activeTask?.description?.ifBlank { "Uninterrupted Execution" } ?: "Uninterrupted Execution")

    val effectiveSteps = microSteps

    // If Distraction-Free Immersion is active, show the ultra-minimalist focus space
    if (isDistractionFreeActive) {
        DistractionFreeImmersionView(
            displayTitle = displayTitle,
            displayPhase = displayPhase,
            totalSeconds = timerDurationTotal,
            secondsRemaining = timerSecondsRemaining,
            isRunning = isTimerRunning,
            microSteps = effectiveSteps,
            isNotificationShieldActive = isNotificationShieldActive,
            focusAudioTrack = focusAudioTrack,
            onTogglePlayPause = onTogglePlayPause,
            onReset = onResetTimer,
            onAddFiveMinutes = onAddFiveMinutes,
            onToggleStep = onToggleStep,
            onOpenShieldWhitelist = onOpenShieldWhitelist,
            onSelectAudioTrack = onSelectAudioTrack,
            onExitImmersion = onToggleDistractionFree,
            modifier = modifier
        )
    } else {
        // Standard Focus Screen with visual countdown, decomposition, shield settings, audio tracks
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ElegantDarkBackground)
                .testTag("focus_screen")
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Focus Mode Header with Shield & Fullscreen Immersion triggers
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Focus Studio",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantTextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                if (isNotificationShieldActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EnergyLowColor.copy(alpha = 0.2f))
                                            .border(1.dp, EnergyLowColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .clickable { onOpenShieldWhitelist() }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "🛡️ Shield Active",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EnergyLowColor
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Cognitive load reduction • Distraction suppression",
                                fontSize = 12.sp,
                                color = ElegantTextSecondary
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Shield Whitelist Settings button
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(ElegantDarkSurfaceVariant)
                                    .border(1.dp, ElegantDarkBorderSubtle, CircleShape)
                                    .clickable { onOpenShieldWhitelist() }
                                    .padding(8.dp)
                                    .testTag("focus_shield_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Shield Settings",
                                    tint = if (isNotificationShieldActive) ElegantLavender else ElegantTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Enter Fullscreen Immersion button
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(ElegantLavenderContainer)
                                    .clickable { onToggleDistractionFree() }
                                    .padding(8.dp)
                                    .testTag("enter_immersion_fullscreen_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = "Distraction Free Immersion",
                                    tint = ElegantLavenderDeepest,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // 1. Visual Countdown Timer Card
                item {
                    VisualTimerCard(
                        totalSeconds = timerDurationTotal,
                        secondsRemaining = timerSecondsRemaining,
                        isRunning = isTimerRunning,
                        taskTitle = displayTitle,
                        phaseSubtitle = displayPhase,
                        onTogglePlayPause = onTogglePlayPause,
                        onReset = onResetTimer,
                        onAddFiveMinutes = onAddFiveMinutes,
                        onPresetSelected = onPresetSelected,
                        onToggleDistractionFree = onToggleDistractionFree
                    )
                }

                // ADHD Ambient Audio Selector Bar
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ElegantDarkSurface)
                            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Audio Soundscape",
                                    tint = ElegantLavender,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ADHD Soundscape: ${focusAudioTrack.label}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ElegantTextPrimary
                                )
                            }

                            Text(
                                text = "Settings",
                                fontSize = 11.sp,
                                color = ElegantLavender,
                                modifier = Modifier.clickable { onOpenShieldWhitelist() }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FocusAudioTrack.entries.forEach { track ->
                                val isSelected = focusAudioTrack == track
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ElegantLavenderContainer else ElegantDarkSurfaceVariant)
                                        .border(1.dp, if (isSelected) ElegantLavender else ElegantDarkBorderSubtle, RoundedCornerShape(8.dp))
                                        .clickable { onSelectAudioTrack(track) }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${track.iconEmoji} ${track.label.take(6)}",
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) ElegantLavenderDeepest else ElegantTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Micro-Step Decomposition Section
                item {
                    DecompositionView(
                        task = activeTask,
                        steps = effectiveSteps,
                        isDecomposingWithAi = isDecomposingWithAi,
                        onToggleStep = onToggleStep,
                        onStartStepTimer = onStartStepTimer,
                        onDeleteStep = onDeleteStep,
                        onAddStep = onAddStep,
                        onAiDecompose = onAiDecompose
                    )
                }

                // 3. Gamification Level & Avatar Progress Bar
                item {
                    GamificationCard(
                        progress = progress,
                        leaderboardUsers = leaderboardUsers,
                        onCardClick = onOpenProgress
                    )
                }
            }
        }
    }
}

/**
 * Distraction-Free Immersion Mode Composable
 * When activated, hides all distracting interface chrome, displaying ONLY:
 * 1. The currently selected task & active micro-step
 * 2. An uninterrupted visual timer
 * 3. Notification suppression shield indicator & VIP whitelist settings trigger
 * 4. Ambient soundscape controls
 * 5. Immediate micro-step checklist
 */
@Composable
private fun DistractionFreeImmersionView(
    displayTitle: String,
    displayPhase: String,
    totalSeconds: Int,
    secondsRemaining: Int,
    isRunning: Boolean,
    microSteps: List<MicroStep>,
    isNotificationShieldActive: Boolean,
    focusAudioTrack: FocusAudioTrack,
    onTogglePlayPause: () -> Unit,
    onReset: () -> Unit,
    onAddFiveMinutes: () -> Unit,
    onToggleStep: (MicroStep) -> Unit,
    onOpenShieldWhitelist: () -> Unit,
    onSelectAudioTrack: (FocusAudioTrack) -> Unit,
    onExitImmersion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressRatio = if (totalSeconds > 0) {
        1.0f - (secondsRemaining.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .testTag("distraction_free_immersion_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar: Shield Badge, Audio Track, Exit Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shield & Whitelist Status
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isNotificationShieldActive) EnergyLowColor.copy(alpha = 0.15f) else ElegantDarkSurface)
                    .border(1.dp, if (isNotificationShieldActive) EnergyLowColor.copy(alpha = 0.4f) else ElegantDarkBorderSubtle, RoundedCornerShape(20.dp))
                    .clickable { onOpenShieldWhitelist() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Shield",
                    tint = if (isNotificationShieldActive) EnergyLowColor else ElegantTextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isNotificationShieldActive) "Shield Active (Alerts Suppressed)" else "Shield Disabled",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isNotificationShieldActive) EnergyLowColor else ElegantTextMuted
                )
            }

            // Exit Immersion
            IconButton(
                onClick = onExitImmersion,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ElegantDarkSurfaceVariant)
                    .testTag("exit_immersion_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.FullscreenExit,
                    contentDescription = "Exit Immersion",
                    tint = ElegantLavender,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Center Content: Task Title, Phase, Large Visual Timer Disk, Digital Countdown
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Task Label Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElegantLavenderContainer)
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🎯 CURRENT FOCUS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = ElegantLavenderDeepest
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = displayTitle,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ElegantTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Text(
                text = displayPhase,
                fontSize = 13.sp,
                color = ElegantRose,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Prominent Visual Timer Disk
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                VisualTimerDisk(
                    progress = progressRatio,
                    modifier = Modifier.size(240.dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formattedTime,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantTextPrimary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = if (isRunning) "FOCUSING" else "PAUSED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = if (isRunning) ElegantLavender else ElegantTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Uninterrupted Controls: Play/Pause, +5m, Reset
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onReset,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(ElegantDarkSurfaceVariant)
                        .testTag("immersion_reset_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Timer",
                        tint = ElegantTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (isRunning) ElegantRose else ElegantLavender)
                        .clickable { onTogglePlayPause() }
                        .testTag("immersion_play_pause_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Play",
                        tint = ElegantDarkSurface,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElegantDarkSurfaceVariant)
                        .clickable { onAddFiveMinutes() }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("immersion_add_5m_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+5m",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantLavender
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Strip: Ambient Soundscape quick selector & Active micro-step pill
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Soundscape Quick Bar
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(ElegantDarkSurface)
                    .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "Soundscape",
                    tint = ElegantLavender,
                    modifier = Modifier.size(14.dp)
                )

                FocusAudioTrack.entries.forEach { track ->
                    val isTrackSelected = focusAudioTrack == track
                    Text(
                        text = "${track.iconEmoji} ${track.label}",
                        fontSize = 11.sp,
                        fontWeight = if (isTrackSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isTrackSelected) ElegantLavender else ElegantTextMuted,
                        modifier = Modifier
                            .clickable { onSelectAudioTrack(track) }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Zero distractions • Take it one breath at a time",
                fontSize = 11.sp,
                color = ElegantTextMuted
            )
        }
    }
}
