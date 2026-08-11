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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EnergyLevel
import com.example.data.model.PlannerTask
import com.example.data.model.TaskTimeChunk
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
fun EnergyScheduleWidget(
    tasks: List<PlannerTask>,
    onTaskClick: (PlannerTask) -> Unit,
    modifier: Modifier = Modifier
) {
    val morningTasks = tasks.filter { it.timeSlot == TaskTimeChunk.MORNING }
    val afternoonTasks = tasks.filter { it.timeSlot == TaskTimeChunk.AFTERNOON }
    val eveningTasks = tasks.filter { it.timeSlot == TaskTimeChunk.EVENING }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ElegantDarkSurface)
            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(20.dp))
            .padding(18.dp)
            .testTag("energy_schedule_widget")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Widget Title & Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(EnergyMediumColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Energy Blocks",
                        tint = EnergyMediumColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Energy & Time Blocks",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElegantTextPrimary
                    )
                    Text(
                        text = "Align tasks with your natural dopamine rhythms",
                        fontSize = 11.sp,
                        color = ElegantTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Time Block Cards (Morning, Afternoon, Evening)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimeBlockMiniCard(
                    title = "Morning",
                    timeRange = "8AM - 12PM",
                    energyLabel = "High Focus",
                    energyColor = EnergyHighColor,
                    tasks = morningTasks,
                    modifier = Modifier.weight(1f)
                )

                TimeBlockMiniCard(
                    title = "Afternoon",
                    timeRange = "12PM - 5PM",
                    energyLabel = "Medium",
                    energyColor = EnergyMediumColor,
                    tasks = afternoonTasks,
                    modifier = Modifier.weight(1f)
                )

                TimeBlockMiniCard(
                    title = "Evening",
                    timeRange = "5PM - 9PM",
                    energyLabel = "Gentle",
                    energyColor = EnergyLowColor,
                    tasks = eveningTasks,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TimeBlockMiniCard(
    title: String,
    timeRange: String,
    energyLabel: String,
    energyColor: Color,
    tasks: List<PlannerTask>,
    modifier: Modifier = Modifier
) {
    val totalMins = tasks.sumOf { it.estimatedMinutes }
    val doneCount = tasks.count { it.isCompleted }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(ElegantDarkSurfaceVariant)
            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ElegantTextPrimary
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(energyColor)
            )
        }

        Text(
            text = timeRange,
            fontSize = 9.sp,
            color = ElegantTextMuted
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (tasks.isEmpty()) "Free block" else "$doneCount/${tasks.size} done",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (tasks.isEmpty()) ElegantTextMuted else ElegantTextSecondary
        )

        if (totalMins > 0) {
            Text(
                text = "${totalMins}m est.",
                fontSize = 10.sp,
                color = ElegantLavender
            )
        }
    }
}
