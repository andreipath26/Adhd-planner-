package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrainDumpItem
import com.example.data.model.EnergyLevel
import com.example.data.model.TaskTimeChunk
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
import com.example.ui.theme.EnergyMediumColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrainDumpTriageSheet(
    pendingItems: List<BrainDumpItem>,
    selectedDay: Int,
    onConvertToTask: (dumpId: Long, title: String, desc: String, dayOfWeek: Int, timeSlot: TaskTimeChunk, energy: EnergyLevel, minutes: Int, autoDecompose: Boolean) -> Unit,
    onSaveAsProject: (dumpId: Long) -> Unit,
    onArchiveItem: (dumpId: Long) -> Unit,
    onDeleteItem: (BrainDumpItem) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var convertingItem by remember { mutableStateOf<BrainDumpItem?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ElegantDarkSurface,
        contentColor = ElegantTextPrimary,
        modifier = Modifier.testTag("brain_dump_triage_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            // Header
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
                            .background(ElegantRose.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Triage",
                            tint = ElegantRose,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Brain Dump Triage",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElegantTextPrimary
                        )
                        Text(
                            text = "${pendingItems.size} thoughts awaiting decision",
                            fontSize = 12.sp,
                            color = ElegantTextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = ElegantTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (pendingItems.isEmpty()) {
                // Zero Inbox State
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🎉", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Mental RAM Cleared!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "All brain dump items have been triaged or archived. Enjoy the mental peace!",
                        fontSize = 13.sp,
                        color = ElegantTextSecondary,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pendingItems, key = { it.id }) { item ->
                        BrainDumpTriageCard(
                            item = item,
                            isConverting = convertingItem?.id == item.id,
                            selectedDay = selectedDay,
                            onStartConvert = { convertingItem = item },
                            onCancelConvert = { convertingItem = null },
                            onConfirmConvert = { title, desc, day, slot, energy, mins, autoDec ->
                                onConvertToTask(item.id, title, desc, day, slot, energy, mins, autoDec)
                                convertingItem = null
                            },
                            onSaveAsProject = { onSaveAsProject(item.id) },
                            onArchive = { onArchiveItem(item.id) },
                            onDelete = { onDeleteItem(item) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BrainDumpTriageCard(
    item: BrainDumpItem,
    isConverting: Boolean,
    selectedDay: Int,
    onStartConvert: () -> Unit,
    onCancelConvert: () -> Unit,
    onConfirmConvert: (title: String, desc: String, day: Int, slot: TaskTimeChunk, energy: EnergyLevel, mins: Int, autoDec: Boolean) -> Unit,
    onSaveAsProject: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit
) {
    var taskTitle by remember(item.id) { mutableStateOf(item.content) }
    var taskDesc by remember(item.id) { mutableStateOf("") }
    var chosenDay by remember(item.id) { mutableStateOf(selectedDay) }
    var chosenSlot by remember(item.id) { mutableStateOf(TaskTimeChunk.MORNING) }
    var chosenEnergy by remember(item.id) { mutableStateOf(item.energyHint) }
    var chosenMins by remember(item.id) { mutableStateOf(25) }
    var autoDecompose by remember(item.id) { mutableStateOf(true) }

    val daysOfWeek = listOf(1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu", 5 to "Fri", 6 to "Sat", 7 to "Sun")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ElegantDarkSurfaceVariant)
            .border(1.dp, if (isConverting) ElegantLavender else ElegantDarkBorderSubtle, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        // Thought Header & Content
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (item.isAudioDictation) "🎙️ Audio Note" else "💭 ${item.categoryTag}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElegantRose
                )
                if (item.audioDurationSeconds > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${item.audioDurationSeconds}s)",
                        fontSize = 11.sp,
                        color = ElegantTextMuted
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete thought",
                    tint = ElegantTextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.content,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = ElegantTextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Conversion Form if expanded
        AnimatedVisibility(visible = isConverting) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElegantDarkSurface)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Convert to Scheduled Task",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantLavender
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task Title", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ElegantTextPrimary,
                        unfocusedTextColor = ElegantTextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Day Selector
                Text(text = "Target Day", fontSize = 11.sp, color = ElegantTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    daysOfWeek.forEach { (dIndex, dLabel) ->
                        val isDaySelected = chosenDay == dIndex
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDaySelected) ElegantLavenderContainer else ElegantDarkSurfaceVariant)
                                .clickable { chosenDay = dIndex }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = dLabel,
                                fontSize = 11.sp,
                                fontWeight = if (isDaySelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isDaySelected) ElegantLavenderDeepest else ElegantTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Time Slot & Energy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskTimeChunk.entries.forEach { slot ->
                        val isSlotSelected = chosenSlot == slot
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSlotSelected) ElegantLavenderContainer else ElegantDarkSurfaceVariant)
                                .clickable { chosenSlot = slot }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = slot.label.take(8),
                                fontSize = 10.sp,
                                fontWeight = if (isSlotSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSlotSelected) ElegantLavenderDeepest else ElegantTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Auto Decompose with AI Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = autoDecompose,
                        onCheckedChange = { autoDecompose = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = ElegantLavender,
                            checkmarkColor = ElegantLavenderDeepest,
                            uncheckedColor = ElegantDarkBorder
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Auto-decompose into micro-steps ✨",
                        fontSize = 12.sp,
                        color = ElegantLavender
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Confirm & Cancel Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 12.sp,
                        color = ElegantTextMuted,
                        modifier = Modifier
                            .clickable { onCancelConvert() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElegantLavenderContainer)
                            .clickable {
                                if (taskTitle.isNotBlank()) {
                                    onConfirmConvert(taskTitle, taskDesc, chosenDay, chosenSlot, chosenEnergy, chosenMins, autoDecompose)
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Add to Plan (+25 XP)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantLavenderDeepest
                        )
                    }
                }
            }
        }

        // Triage Decision Action Buttons (Convert, Project, Archive)
        if (!isConverting) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Convert to Task
                Row(
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ElegantLavenderContainer)
                        .clickable { onStartConvert() }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatListBulleted,
                        contentDescription = "Task",
                        tint = ElegantLavenderDeepest,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Make Task",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantLavenderDeepest
                    )
                }

                // Save as Project / Idea
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ElegantDarkSurface)
                        .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(10.dp))
                        .clickable { onSaveAsProject() }
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Project",
                        tint = ElegantLavender,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Save Idea",
                        fontSize = 11.sp,
                        color = ElegantLavender
                    )
                }

                // Archive / Dismiss
                Row(
                    modifier = Modifier
                        .weight(0.9f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ElegantDarkSurface)
                        .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(10.dp))
                        .clickable { onArchive() }
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Archive,
                        contentDescription = "Archive",
                        tint = ElegantTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Archive",
                        fontSize = 11.sp,
                        color = ElegantTextMuted
                    )
                }
            }
        }
    }
}
