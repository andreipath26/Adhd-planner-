package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderStrong
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
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/**
 * Focus duration preset model for countdown timer configuration.
 */
data class FocusDurationPreset(
    val minutes: Int,
    val label: String,
    val emoji: String,
    val isBreak: Boolean = false
)

val StandardFocusPresets = listOf(
    FocusDurationPreset(5, "5m Reset", "⚡"),
    FocusDurationPreset(15, "15m Sprint", "🏃"),
    FocusDurationPreset(25, "25m Pomodoro", "🍅"),
    FocusDurationPreset(45, "45m Deep", "🧠"),
    FocusDurationPreset(50, "50m Block", "🎯"),
    FocusDurationPreset(5, "5m Break", "☕", isBreak = true)
)

/**
 * Reusable Circular Progress Visual Countdown Timer Component.
 * Supports smooth animated sweep, glowing indicator head, breathing state pulses,
 * and visual completion cue upon timer expiration.
 */
@Composable
fun VisualCountdownTimer(
    totalSeconds: Int,
    secondsRemaining: Int,
    isRunning: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 190.dp,
    strokeWidth: Dp = 10.dp,
    isBreakMode: Boolean = false,
    showCompletionCue: Boolean = (secondsRemaining <= 0 && totalSeconds > 0)
) {
    val progress = if (totalSeconds > 0) {
        (secondsRemaining.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "circular_timer_progress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "timer_pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isRunning) 1.025f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val completionGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "completion_glow"
    )

    // Dynamic Arc Color based on state
    val arcColor by animateColorAsState(
        targetValue = when {
            showCompletionCue -> EnergyLowColor
            isBreakMode -> EnergyLowColor
            secondsRemaining in 1..60 -> EnergyHighColor
            isRunning -> ElegantLavender
            else -> ElegantLavender.copy(alpha = 0.85f)
        },
        label = "arc_color"
    )

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    Box(
        modifier = modifier
            .size(size)
            .scale(if (isRunning && !showCompletionCue) pulseScale else 1.0f)
            .testTag("visual_countdown_timer_component"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val diameter = this.size.minDimension - strokePx
            val radius = diameter / 2f
            val centerOffset = Offset(this.size.width / 2f, this.size.height / 2f)

            // 1. Outer subtle track ring
            drawCircle(
                color = ElegantDarkBorder,
                radius = radius,
                center = centerOffset,
                style = Stroke(width = strokePx)
            )

            // 2. Visual Completion Cue Glow Ring
            if (showCompletionCue) {
                drawCircle(
                    color = EnergyLowColor.copy(alpha = completionGlowAlpha * 0.4f),
                    radius = radius + 6.dp.toPx(),
                    center = centerOffset,
                    style = Stroke(width = strokePx * 1.6f)
                )
            }

            // 3. Foreground Progress Arc with Round Caps
            val sweepAngle = 360f * animatedProgress
            if (sweepAngle > 0.5f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = if (isBreakMode) {
                            listOf(EnergyLowColor.copy(alpha = 0.7f), EnergyLowColor)
                        } else if (secondsRemaining in 1..60) {
                            listOf(EnergyHighColor.copy(alpha = 0.8f), EnergyHighColor)
                        } else {
                            listOf(ElegantLavender.copy(alpha = 0.65f), ElegantLavender, ElegantLavenderDark)
                        }
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(strokePx / 2f, strokePx / 2f),
                    size = androidx.compose.ui.geometry.Size(diameter, diameter),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )

                // 4. Glowing Head Bead at the tip of the circular progress indicator
                val angleRad = Math.toRadians((sweepAngle - 90.0)).toFloat()
                val beadX = centerOffset.x + radius * cos(angleRad)
                val beadY = centerOffset.y + radius * sin(angleRad)

                drawCircle(
                    color = arcColor,
                    radius = strokePx * 0.7f,
                    center = Offset(beadX, beadY)
                )
                drawCircle(
                    color = Color.White,
                    radius = strokePx * 0.35f,
                    center = Offset(beadX, beadY)
                )
            }
        }

        // Inner Digital Display & Contextual State Pill
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            if (showCompletionCue) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Session Complete",
                    tint = EnergyLowColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "TIME'S UP!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = EnergyLowColor
                )
                Text(
                    text = "Session Complete ✨",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElegantTextSecondary
                )
            } else {
                // Digital Countdown Time
                Text(
                    text = formattedTime,
                    fontSize = if (size > 200.dp) 44.sp else 34.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-1).sp,
                    color = ElegantTextPrimary,
                    modifier = Modifier.testTag("countdown_timer_text")
                )

                // State Indicator Pill
                val statusText = when {
                    isBreakMode -> "☕ REST BREAK"
                    isRunning -> "🔥 FLOW ACTIVE"
                    secondsRemaining < totalSeconds -> "⏸ PAUSED"
                    else -> "READY"
                }

                val statusColor = when {
                    isBreakMode -> EnergyLowColor
                    isRunning -> ElegantLavender
                    else -> ElegantTextMuted
                }

                Text(
                    text = statusText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = statusColor
                )
            }
        }
    }
}

/**
 * Interactive Visual Timer Card with Duration Selector Presets,
 * Controls (Play/Pause, Reset, +5m), and Expiration Visual Cue Banner.
 */
@Composable
fun VisualTimerCard(
    totalSeconds: Int,
    secondsRemaining: Int,
    isRunning: Boolean,
    taskTitle: String,
    phaseSubtitle: String,
    onTogglePlayPause: () -> Unit,
    onReset: () -> Unit,
    onAddFiveMinutes: () -> Unit,
    onPresetSelected: (Int) -> Unit,
    onToggleDistractionFree: () -> Unit,
    modifier: Modifier = Modifier,
    isBreakMode: Boolean = false,
    showCompletionCue: Boolean = (secondsRemaining <= 0 && totalSeconds > 0),
    onStartBreak: ((Int) -> Unit)? = null,
    onDismissCompletion: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(ElegantDarkSurface)
            .border(
                1.dp,
                if (showCompletionCue) EnergyLowColor.copy(alpha = 0.6f) else ElegantDarkBorderSubtle,
                RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
            .testTag("visual_timer_card"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top row: Header badge & Distraction-Free Immersion toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isBreakMode) Icons.Default.Coffee else Icons.Default.Timer,
                    contentDescription = null,
                    tint = if (isBreakMode) EnergyLowColor else ElegantLavender,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBreakMode) "Recovery Break" else "Visual Focus Countdown",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElegantTextPrimary
                )
            }

            IconButton(
                onClick = onToggleDistractionFree,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("fullscreen_focus_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Distraction-Free Immersion",
                    tint = ElegantTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Center Circular Countdown Timer Component
        VisualCountdownTimer(
            totalSeconds = totalSeconds,
            secondsRemaining = secondsRemaining,
            isRunning = isRunning,
            isBreakMode = isBreakMode,
            showCompletionCue = showCompletionCue,
            size = 180.dp,
            strokeWidth = 9.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Task & Phase Description
        Text(
            text = if (isBreakMode) "Mindful Rest & Recharging" else taskTitle.ifBlank { "Deep Work Focus Block" },
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = ElegantTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Text(
            text = if (isBreakMode) "Hydrate, stretch, or take 3 deep breaths" else phaseSubtitle.ifBlank { "Uninterrupted Execution Flow" },
            fontSize = 12.sp,
            color = if (isBreakMode) EnergyLowColor else ElegantTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
        )

        // Visual Expiration Cue Banner
        AnimatedVisibility(
            visible = showCompletionCue,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(EnergyLowColor.copy(alpha = 0.15f))
                    .border(1.dp, EnergyLowColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
                    .testTag("timer_expiration_visual_cue"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Celebration",
                        tint = EnergyLowColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Focus Complete! +25 XP Earned ✨",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EnergyLowColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Start 5m Break Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EnergyLowColor)
                            .clickable {
                                onStartBreak?.invoke(5) ?: onPresetSelected(5)
                            }
                            .padding(vertical = 8.dp)
                            .testTag("cue_start_break_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "☕ Take 5m Break",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantDarkBackground
                        )
                    }

                    // Start Next 25m Pomodoro Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElegantLavenderContainer)
                            .clickable {
                                onPresetSelected(25)
                            }
                            .padding(vertical = 8.dp)
                            .testTag("cue_next_pomodoro_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🍅 Next 25m Focus",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantLavenderDeepest
                        )
                    }
                }
            }
        }

        // Control Action Buttons (Play/Pause, Reset, +5m)
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main Play/Pause Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isRunning) ElegantRose else ElegantLavenderContainer)
                    .clickable { onTogglePlayPause() }
                    .testTag("timer_play_pause_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause Timer" else "Start Timer",
                    tint = if (isRunning) Color.White else ElegantLavenderDeepest,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Reset Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, ElegantDarkBorderStrong, CircleShape)
                    .clickable { onReset() }
                    .testTag("timer_reset_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Timer",
                    tint = ElegantLavender,
                    modifier = Modifier.size(20.dp)
                )
            }

            // +5 Min Quick Bump
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, ElegantDarkBorder, CircleShape)
                    .clickable { onAddFiveMinutes() }
                    .testTag("timer_add_five_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add 5 Minutes",
                        tint = ElegantTextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "5m",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Focus Duration Preset Selector Chips (5m, 15m, 25m Pomodoro, 45m, 50m)
        Text(
            text = "SELECT FOCUS DURATION",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = ElegantTextMuted,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("focus_duration_preset_row"),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StandardFocusPresets.forEach { preset ->
                val isSelected = (totalSeconds == preset.minutes * 60) && (isBreakMode == preset.isBreak)
                val chipColor = if (preset.isBreak) EnergyLowColor else ElegantLavender

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) {
                                chipColor.copy(alpha = 0.2f)
                            } else {
                                ElegantDarkSurfaceVariant
                            }
                        )
                        .border(
                            1.dp,
                            if (isSelected) chipColor else ElegantDarkBorderSubtle,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            if (preset.isBreak) {
                                onStartBreak?.invoke(preset.minutes) ?: onPresetSelected(preset.minutes)
                            } else {
                                onPresetSelected(preset.minutes)
                            }
                        }
                        .padding(vertical = 6.dp)
                        .testTag("preset_chip_${preset.minutes}_${if (preset.isBreak) "break" else "focus"}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = preset.emoji,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${preset.minutes}m",
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) chipColor else ElegantTextSecondary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Legacy alias for backwards compatibility
 */
@Composable
fun VisualTimerDisk(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "timer_disk_progress"
    )

    Canvas(modifier = modifier) {
        val strokeWidth = 10.dp.toPx()

        drawCircle(
            color = ElegantDarkBorder,
            radius = (size.minDimension - strokeWidth) / 2f,
            style = Stroke(width = strokeWidth)
        )

        val sweepAngle = 360f * animatedProgress
        drawArc(
            color = ElegantLavender,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}
