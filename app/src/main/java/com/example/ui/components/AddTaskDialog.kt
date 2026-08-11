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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.EnergyLevel
import com.example.data.model.TaskTimeChunk
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderSubtle
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantLavenderContainer
import com.example.ui.theme.ElegantLavenderDark
import com.example.ui.theme.ElegantLavenderDeepest
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import com.example.ui.theme.EnergyHighColor
import com.example.ui.theme.EnergyLowColor
import com.example.ui.theme.EnergyMediumColor

@Composable
fun AddTaskDialog(
    initialDayOfWeek: Int,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        description: String,
        dayOfWeek: Int,
        timeSlot: TaskTimeChunk,
        category: String,
        energyLevel: EnergyLevel,
        estimatedMinutes: Int,
        autoDecompose: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDay by remember { mutableIntStateOf(initialDayOfWeek) }
    var selectedTimeSlot by remember { mutableStateOf(TaskTimeChunk.MORNING) }
    var selectedCategory by remember { mutableStateOf("Focus") }
    var selectedEnergy by remember { mutableStateOf(EnergyLevel.MEDIUM) }
    var selectedDuration by remember { mutableIntStateOf(25) }
    var autoDecompose by remember { mutableStateOf(true) }

    val daysLetters = listOf(
        Pair(1, "Mon"),
        Pair(2, "Tue"),
        Pair(3, "Wed"),
        Pair(4, "Thu"),
        Pair(5, "Fri"),
        Pair(6, "Sat"),
        Pair(7, "Sun")
    )

    val categories = listOf("Focus", "Admin", "Creative", "Self-Care", "Health")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(24.dp))
                .testTag("add_task_dialog"),
            color = ElegantDarkSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Focus Task",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElegantTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ElegantTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title Input
                Text(
                    text = "TASK TITLE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ElegantTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("e.g. Write executive summary", color = ElegantTextMuted, fontSize = 14.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ElegantTextPrimary,
                        unfocusedTextColor = ElegantTextPrimary,
                        focusedBorderColor = ElegantLavender,
                        unfocusedBorderColor = ElegantDarkBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_task_title_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description Input
                Text(
                    text = "DETAILS (OPTIONAL)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ElegantTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Add any notes, links or objectives...", color = ElegantTextMuted, fontSize = 13.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ElegantTextPrimary,
                        unfocusedTextColor = ElegantTextPrimary,
                        focusedBorderColor = ElegantLavender,
                        unfocusedBorderColor = ElegantDarkBorder
                    ),
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_task_desc_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Day Selector
                Text(
                    text = "DAY OF THE WEEK",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ElegantTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    daysLetters.forEach { (dayIndex, label) ->
                        val isSelected = selectedDay == dayIndex
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ElegantLavender else ElegantDarkBorder.copy(alpha = 0.5f))
                                .clickable { selectedDay = dayIndex }
                                .padding(horizontal = 6.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ElegantLavenderDark else ElegantTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Time Chunk Selector
                Text(
                    text = "TIME BLOCK",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ElegantTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaskTimeChunk.values().forEach { chunk ->
                        val isSelected = selectedTimeSlot == chunk
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ElegantLavenderContainer else ElegantDarkBorder.copy(alpha = 0.5f))
                                .border(1.dp, if (isSelected) ElegantLavender else ElegantDarkBorderSubtle, RoundedCornerShape(8.dp))
                                .clickable { selectedTimeSlot = chunk }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chunk.name.lowercase().replaceFirstChar { it.uppercase() },
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ElegantLavenderDeepest else ElegantTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Energy Level Required
                Text(
                    text = "ENERGY REQUIRED",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ElegantTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EnergyLevel.values().forEach { energy ->
                        val isSelected = selectedEnergy == energy
                        val dotColor = when (energy) {
                            EnergyLevel.LOW -> EnergyLowColor
                            EnergyLevel.MEDIUM -> EnergyMediumColor
                            EnergyLevel.HIGH -> EnergyHighColor
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ElegantDarkBorder else ElegantDarkBorder.copy(alpha = 0.3f))
                                .border(1.dp, if (isSelected) ElegantLavender else ElegantDarkBorderSubtle, RoundedCornerShape(10.dp))
                                .clickable { selectedEnergy = energy }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(dotColor)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = energy.label.substringBefore(" "),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ElegantTextPrimary else ElegantTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Duration Selector
                Text(
                    text = "ESTIMATED DURATION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ElegantTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(10, 15, 25, 45, 60).forEach { mins ->
                        val isSelected = selectedDuration == mins
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ElegantLavender else ElegantDarkBorder)
                                .clickable { selectedDuration = mins }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${mins}m",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ElegantLavenderDark else ElegantTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // AI Auto Decompose Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElegantDarkBorder.copy(alpha = 0.4f))
                        .clickable { autoDecompose = !autoDecompose }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = autoDecompose,
                        onCheckedChange = { autoDecompose = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = ElegantLavender,
                            checkmarkColor = ElegantLavenderDark,
                            uncheckedColor = ElegantTextSecondary
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ElegantLavender,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Auto-Breakdown with AI",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElegantLavender
                            )
                        }
                        Text(
                            text = "Splits task into 3-5 manageable micro-steps",
                            fontSize = 10.sp,
                            color = ElegantTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onConfirm(
                                title,
                                description,
                                selectedDay,
                                selectedTimeSlot,
                                selectedCategory,
                                selectedEnergy,
                                selectedDuration,
                                autoDecompose
                            )
                        }
                    },
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElegantLavenderContainer,
                        contentColor = ElegantLavenderDeepest
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_task_button")
                ) {
                    Text(
                        text = if (autoDecompose) "Create & Break Down ✨" else "Add Task",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
