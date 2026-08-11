package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AchievementBadge
import com.example.data.model.FocusSession
import com.example.data.model.UserProgress
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderSubtle
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantLavenderContainer
import com.example.ui.theme.ElegantLavenderDark
import com.example.ui.theme.ElegantLavenderDeepest
import com.example.ui.theme.ElegantRose
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import com.example.ui.theme.StreakFlameColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressScreen(
    progress: UserProgress,
    badges: List<AchievementBadge>,
    sessions: List<FocusSession>,
    onOpenSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    val levelProgress by animateFloatAsState(
        targetValue = progress.currentLevelProgress,
        label = "progress_bar_anim"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBackground)
            .testTag("progress_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Level & XP Hero Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ElegantDarkSurface)
                    .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Level ${progress.currentLevel}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantLavender
                        )
                        Text(
                            text = progress.levelTitle,
                            fontSize = 13.sp,
                            color = ElegantTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElegantDarkBorder)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${progress.totalXp} Total XP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantLavender
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // XP Progress Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Next: Level ${progress.currentLevel + 1}",
                        fontSize = 11.sp,
                        color = ElegantTextSecondary
                    )
                    Text(
                        text = "${progress.xpInCurrentLevelDisplay} / 250 XP in level",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElegantLavender
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(ElegantDarkBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(levelProgress)
                            .clip(RoundedCornerShape(5.dp))
                            .background(ElegantLavender)
                    )
                }
            }
        }

        // Summary Metric Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Streak Card
                MetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = StreakFlameColor,
                    value = "${progress.currentStreak} Days",
                    label = "Current Streak",
                    subtitle = "Best: ${progress.bestStreak}d"
                )

                // Today's Focus Card
                MetricCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Timer,
                    iconTint = ElegantLavender,
                    value = "${progress.todayFocusMinutes}m",
                    label = "Today's Focus",
                    subtitle = "Total: ${progress.totalFocusMinutes}m"
                )
            }
        }

        // Achievement Badges Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(ElegantDarkSurface)
                    .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "ADHD MOMENTUM BADGES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = ElegantTextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    badges.forEach { badge ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (badge.isUnlocked) ElegantDarkBorder.copy(alpha = 0.4f) else ElegantDarkBorder.copy(alpha = 0.15f))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = badge.iconEmoji,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = badge.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (badge.isUnlocked) ElegantTextPrimary else ElegantTextMuted
                                )
                                Text(
                                    text = badge.description,
                                    fontSize = 11.sp,
                                    color = if (badge.isUnlocked) ElegantTextSecondary else ElegantTextMuted
                                )
                            }
                            if (badge.isUnlocked) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ElegantLavenderContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "UNLOCKED",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElegantLavenderDeepest
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Cross Device Sync Quick Trigger
        item {
            Button(
                onClick = onOpenSync,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElegantDarkSurface,
                    contentColor = ElegantLavender
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ElegantLavender.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(vertical = 4.dp)
                    .testTag("progress_sync_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cross-Device Sync & Continuity", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Focus Session History
        if (sessions.isNotEmpty()) {
            item {
                Text(
                    text = "RECENT FOCUS SESSIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = ElegantTextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(sessions.take(5)) { session ->
                val timeStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(session.completedAt))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ElegantDarkSurface)
                        .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = session.taskTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = ElegantTextPrimary
                        )
                        Text(
                            text = timeStr,
                            fontSize = 11.sp,
                            color = ElegantTextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElegantDarkBorder)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "+${session.durationMinutes}m focus",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantLavender
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    value: String,
    label: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ElegantDarkSurface)
            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ElegantTextPrimary
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = ElegantTextSecondary
        )
        Text(
            text = subtitle,
            fontSize = 10.sp,
            color = ElegantTextMuted,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
