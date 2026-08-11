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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlannerTask
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
import com.example.ui.theme.EnergyLowColor

@Composable
fun WeeklyOverviewWidget(
    allTasks: List<PlannerTask>,
    selectedDay: Int,
    onSelectDay: (Int) -> Unit,
    onNavigateToPlanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val daysOfWeek = listOf(
        1 to "Mon",
        2 to "Tue",
        3 to "Wed",
        4 to "Thu",
        5 to "Fri",
        6 to "Sat",
        7 to "Sun"
    )

    val totalWeekTasks = allTasks.size
    val completedWeekTasks = allTasks.count { it.isCompleted }
    val weekCompletionRate = if (totalWeekTasks > 0) completedWeekTasks.toFloat() / totalWeekTasks else 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ElegantDarkSurface)
            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(20.dp))
            .padding(18.dp)
            .testTag("weekly_overview_widget")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Widget Title & Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ElegantLavenderDark.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Weekly Overview",
                            tint = ElegantLavender,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Weekly Overview",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElegantTextPrimary
                        )
                        Text(
                            text = "$completedWeekTasks of $totalWeekTasks tasks finished",
                            fontSize = 11.sp,
                            color = ElegantTextSecondary
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onNavigateToPlanner() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Full Plan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElegantLavender
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go to Planner",
                        tint = ElegantLavender,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Completion Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { weekCompletionRate },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ElegantLavender,
                    trackColor = ElegantDarkSurfaceVariant,
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${(weekCompletionRate * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantLavender
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7-Day Day Selector & Heat Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEach { (dayIndex, dayLabel) ->
                    val dayTasks = allTasks.filter { it.dayOfWeek == dayIndex }
                    val dayDone = dayTasks.count { it.isCompleted }
                    val isSelected = selectedDay == dayIndex

                    DayMiniPill(
                        dayName = dayLabel,
                        taskCount = dayTasks.size,
                        doneCount = dayDone,
                        isSelected = isSelected,
                        onClick = { onSelectDay(dayIndex) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayMiniPill(
    dayName: String,
    taskCount: Int,
    doneCount: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isAllDone = taskCount > 0 && doneCount == taskCount

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) ElegantLavenderContainer
                else if (isAllDone) ElegantDarkSurfaceVariant.copy(alpha = 0.8f)
                else ElegantDarkSurfaceVariant.copy(alpha = 0.4f)
            )
            .border(
                1.dp,
                if (isSelected) ElegantLavender
                else if (isAllDone) EnergyLowColor.copy(alpha = 0.5f)
                else ElegantDarkBorderSubtle,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(
            text = dayName,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) ElegantLavenderDeepest else ElegantTextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (taskCount == 0) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) ElegantLavenderDark.copy(alpha = 0.4f) else ElegantTextMuted.copy(alpha = 0.3f))
            )
        } else if (isAllDone) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = if (isSelected) ElegantLavenderDeepest else EnergyLowColor,
                modifier = Modifier.size(12.dp)
            )
        } else {
            Text(
                text = "$doneCount/$taskCount",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) ElegantLavenderDeepest else ElegantTextSecondary
            )
        }
    }
}
