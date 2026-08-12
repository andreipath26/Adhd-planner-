package com.example.ui.components

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EnergyLevel
import com.example.data.model.PlannerTask
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderSubtle
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantRose
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import com.example.ui.theme.EnergyHighColor
import com.example.ui.theme.EnergyLowColor
import com.example.ui.theme.EnergyMediumColor

@Composable
fun TaskCard(
    task: PlannerTask,
    substepCount: Int,
    substepDoneCount: Int,
    onToggleCompleted: () -> Unit,
    onStartTimer: () -> Unit,
    onOpenDecomposition: () -> Unit,
    onSetSpotlight: () -> Unit,
    onDelete: () -> Unit,
    onAiBreakdown: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val energyColor = when (task.energyRequired) {
        EnergyLevel.LOW -> EnergyLowColor
        EnergyLevel.MEDIUM -> EnergyMediumColor
        EnergyLevel.HIGH -> EnergyHighColor
    }

    val cardBg = if (task.isFocusSpotlight) ElegantDarkSurfaceVariant else ElegantDarkSurface
    val cardBorderColor = if (task.isFocusSpotlight) ElegantLavender.copy(alpha = 0.5f) else ElegantDarkBorderSubtle

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .border(1.dp, cardBorderColor, RoundedCornerShape(18.dp))
            .clickable { onOpenDecomposition() }
            .padding(14.dp)
            .alpha(if (task.isCompleted) 0.55f else 1.0f)
            .testTag("task_card_${task.id}")
    ) {
        // Top Row: Category, Energy Level, Spotlight Star
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Energy badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ElegantDarkBorder)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(energyColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = task.energyRequired.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = ElegantTextSecondary
                        )
                    }
                }

                // Estimated time tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ElegantDarkBorder)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${task.estimatedMinutes}m",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElegantLavender
                    )
                }

                // Category tag
                if (task.category.isNotBlank()) {
                    Text(
                        text = task.category,
                        fontSize = 10.sp,
                        color = ElegantTextMuted
                    )
                }
            }

            // Spotlight Star Button
            IconButton(
                onClick = onSetSpotlight,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("spotlight_button_${task.id}")
            ) {
                Icon(
                    imageVector = if (task.isFocusSpotlight) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "Pin as Current Spotlight Priority",
                    tint = if (task.isFocusSpotlight) ElegantLavender else ElegantTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Title & Checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleCompleted,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("task_checkbox_${task.id}")
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle Complete",
                    tint = if (task.isCompleted) ElegantLavender else ElegantTextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.isCompleted) ElegantTextSecondary else ElegantTextPrimary
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = ElegantTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom Action Bar: Sub-step breakdown count, Play Timer, Decompose Button, Delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Substeps / AI Breakdown badge
            if (substepCount > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElegantDarkBorder)
                        .clickable { onOpenDecomposition() }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                        .testTag("task_substeps_badge_${task.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$substepDoneCount/$substepCount micro-steps",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (substepDoneCount == substepCount) ElegantLavender else ElegantRose
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElegantLavender.copy(alpha = 0.15f))
                        .border(1.dp, ElegantLavender.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable {
                            if (onAiBreakdown != null) {
                                onAiBreakdown.invoke()
                            } else {
                                onOpenDecomposition()
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("ai_breakdown_button_${task.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Breakdown",
                            tint = ElegantLavender,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI Breakdown",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElegantLavender
                        )
                    }
                }
            }

            // Quick Actions: Start Timer, Delete
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onStartTimer,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("start_task_timer_${task.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Focus Timer",
                        tint = ElegantLavender,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Task",
                        tint = ElegantTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
