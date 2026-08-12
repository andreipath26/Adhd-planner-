package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MicroStep
import com.example.data.model.PlannerTask
import com.example.data.model.TaskTimeChunk
import com.example.ui.components.TaskCard
import com.example.ui.components.WeekDaySelector
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderSubtle
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantLavenderContainer
import com.example.ui.theme.ElegantLavenderDeepest
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary

@Composable
fun PlannerScreen(
    selectedDayIndex: Int,
    tasks: List<PlannerTask>,
    allMicroSteps: List<MicroStep>,
    onDaySelected: (Int) -> Unit,
    onToggleTask: (PlannerTask) -> Unit,
    onStartTimer: (PlannerTask) -> Unit,
    onOpenDecomposition: (PlannerTask) -> Unit,
    onSetSpotlight: (PlannerTask) -> Unit,
    onDeleteTask: (PlannerTask) -> Unit,
    onAddTaskClick: () -> Unit,
    onOpenSyncClick: () -> Unit,
    onAiBreakdown: ((PlannerTask) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val dayName = when (selectedDayIndex) {
        1 -> "Monday"
        2 -> "Tuesday"
        3 -> "Wednesday"
        4 -> "Thursday"
        5 -> "Friday"
        6 -> "Saturday"
        7 -> "Sunday"
        else -> "Today"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Weekday Strip
            WeekDaySelector(
                selectedDayIndex = selectedDayIndex,
                onDaySelected = onDaySelected
            )

            // Time Blocks & Task List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("planner_tasks_list"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Day Summary Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$dayName's Focus Flow",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElegantTextPrimary
                            )
                            val remaining = tasks.count { !it.isCompleted }
                            Text(
                                text = if (tasks.isEmpty()) "No tasks yet — enjoy calm headspace!" else "$remaining tasks left to conquer",
                                fontSize = 12.sp,
                                color = ElegantTextSecondary
                            )
                        }

                        IconButton(
                            onClick = onOpenSyncClick,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("planner_sync_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Cross-Device Sync",
                                tint = ElegantLavender,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                if (tasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(ElegantDarkSurface)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "🌿 Calm Clear Day",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ElegantTextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tap the '+' button to schedule gentle tasks or break down big goals.",
                                    fontSize = 13.sp,
                                    color = ElegantTextSecondary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // Chunk by Time Slot
                TaskTimeChunk.values().forEach { chunk ->
                    val chunkTasks = tasks.filter { it.timeSlot == chunk }
                    if (chunkTasks.isNotEmpty()) {
                        item(key = "header_${chunk.name}") {
                            TimeBlockHeader(chunk = chunk, count = chunkTasks.size)
                        }

                        items(chunkTasks, key = { it.id }) { task ->
                            val taskSteps = allMicroSteps.filter { it.taskId == task.id }
                            TaskCard(
                                task = task,
                                substepCount = taskSteps.size,
                                substepDoneCount = taskSteps.count { it.isDone },
                                onToggleCompleted = { onToggleTask(task) },
                                onStartTimer = { onStartTimer(task) },
                                onOpenDecomposition = { onOpenDecomposition(task) },
                                onSetSpotlight = { onSetSpotlight(task) },
                                onDelete = { onDeleteTask(task) },
                                onAiBreakdown = { onAiBreakdown?.invoke(task) ?: onOpenDecomposition(task) }
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button to Add Task
        FloatingActionButton(
            onClick = onAddTaskClick,
            containerColor = ElegantLavenderContainer,
            contentColor = ElegantLavenderDeepest,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 20.dp)
                .testTag("add_task_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add New Task",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun TimeBlockHeader(
    chunk: TaskTimeChunk,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = chunk.label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = ElegantLavender
        )
        Text(
            text = chunk.timeHint,
            fontSize = 11.sp,
            color = ElegantTextMuted
        )
    }
}
