package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderStrong
import com.example.ui.theme.ElegantDarkBorderSubtle
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantLavenderContainer
import com.example.ui.theme.ElegantLavenderDeepest
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import java.util.Locale

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

        // Background Track Ring
        drawCircle(
            color = ElegantDarkBorder,
            radius = (size.minDimension - strokeWidth) / 2f,
            style = Stroke(width = strokeWidth)
        )

        // Foreground Progress Arc
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
    modifier: Modifier = Modifier
) {
    val progress = if (totalSeconds > 0) {
        (secondsRemaining.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "timer_progress"
    )

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(ElegantDarkSurface)
            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(24.dp))
            .padding(20.dp)
            .testTag("visual_timer_card"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top row with distraction-free toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = onToggleDistractionFree,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("fullscreen_focus_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Distraction-Free Mode",
                    tint = ElegantTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Circular Timer Canvas
        Box(
            modifier = Modifier
                .size(164.dp)
                .padding(bottom = 12.dp)
                .testTag("timer_progress_ring"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(150.dp)) {
                val strokeWidth = 8.dp.toPx()

                // Background Track
                drawCircle(
                    color = ElegantDarkBorder,
                    radius = (size.minDimension - strokeWidth) / 2f,
                    style = Stroke(width = strokeWidth)
                )

                // Foreground Animated Progress Arc
                val sweepAngle = 360f * animatedProgress
                drawArc(
                    color = ElegantLavender,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = timeFormatted,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-1).sp,
                    color = ElegantTextPrimary
                )
                Text(
                    text = "REMAINING",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    color = ElegantTextSecondary
                )
            }
        }

        // Task & Phase Description
        Text(
            text = taskTitle.ifBlank { "Deep Work Focus" },
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = ElegantTextPrimary,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = phaseSubtitle.ifBlank { "Phase 1: Active Flow" },
            fontSize = 13.sp,
            color = ElegantTextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Control Action Buttons (Pause/Play, Reset, +5m)
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main Play/Pause Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ElegantLavenderContainer)
                    .clickable { onTogglePlayPause() }
                    .testTag("timer_play_pause_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause Timer" else "Start Timer",
                    tint = ElegantLavenderDeepest,
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
                        modifier = Modifier.size(14.dp)
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

        Spacer(modifier = Modifier.height(14.dp))

        // Preset Chips (5m, 15m, 25m, 45m)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            listOf(5, 15, 25, 45).forEach { mins ->
                val isCurrentPreset = totalSeconds == mins * 60
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isCurrentPreset) ElegantDarkBorder else Color.Transparent)
                        .border(
                            1.dp,
                            if (isCurrentPreset) ElegantLavender else ElegantDarkBorderSubtle,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onPresetSelected(mins) }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${mins}m",
                        fontSize = 11.sp,
                        fontWeight = if (isCurrentPreset) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCurrentPreset) ElegantLavender else ElegantTextSecondary
                    )
                }
            }
        }
    }
}
