package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.MicroStep
import com.example.data.model.PlannerTask
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderSubtle
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantLavenderContainer
import com.example.ui.theme.ElegantLavenderDark
import com.example.ui.theme.ElegantRose
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary

@Composable
fun DecompositionView(
    task: PlannerTask?,
    steps: List<MicroStep> = emptyList(),
    isDecomposingWithAi: Boolean = false,
    onToggleStep: (MicroStep) -> Unit = {},
    onStartStepTimer: (MicroStep) -> Unit = {},
    onDeleteStep: (MicroStep) -> Unit = {},
    onAddStep: (String, Int) -> Unit = { _, _ -> },
    onAiDecompose: () -> Unit = {},
    onSubstepToggle: ((MicroStep) -> Unit)? = null,
    onAddSubstep: ((taskId: Long, title: String, mins: Int) -> Unit)? = null,
    onDeleteSubstep: ((MicroStep) -> Unit)? = null,
    onGenerateAiSubsteps: (() -> Unit)? = null,
    onStartFocus: ((MicroStep) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val actualToggle = onSubstepToggle ?: onToggleStep
    val actualDelete = onDeleteSubstep ?: onDeleteStep
    val actualAi = onGenerateAiSubsteps ?: onAiDecompose
    val actualStartTimer = onStartFocus ?: onStartStepTimer

    val effectiveSteps = steps
    val completedCount = effectiveSteps.count { it.isDone }
    val totalCount = effectiveSteps.size
    var showAddStepInput by remember { mutableStateOf(false) }
    var newStepTitle by remember { mutableStateOf("") }
    var newStepDuration by remember { mutableStateOf(5) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("decomposition_section")
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TASK DECOMPOSITION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp,
                    color = ElegantTextSecondary
                )
                if (task != null) {
                    Text(
                        text = task.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantTextPrimary,
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (totalCount > 0) {
                    Text(
                        text = "$completedCount/$totalCount Done",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElegantLavender,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                if (isDecomposingWithAi) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = ElegantLavender
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElegantDarkBorder)
                            .clickable { actualAi() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("ai_decompose_button"),
                        contentAlignment = Alignment.Center
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
                                text = "Breakdown ✨",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElegantLavender
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (effectiveSteps.isEmpty() && !isDecomposingWithAi) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ElegantDarkSurface)
                    .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (task == null) "Select a task to view micro-steps" else "No micro-steps yet",
                        fontSize = 13.sp,
                        color = ElegantTextSecondary
                    )
                    if (task != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { actualAi() },
                            modifier = Modifier.testTag("empty_ai_decompose_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ElegantLavender,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Decompose with AI (5-Min Chunks)",
                                color = ElegantLavender,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                effectiveSteps.forEachIndexed { index, step ->
                    val isDone = step.isDone
                    val isFirstIncomplete = !isDone && effectiveSteps.take(index).all { it.isDone }
                    MicroStepItem(
                        step = step,
                        isFocusHighlight = isFirstIncomplete,
                        onToggle = { actualToggle(step) },
                        onStartTimer = { actualStartTimer(step) },
                        onDelete = { actualDelete(step) }
                    )
                }
            }
        }

        // Add Custom Step Toggle
        Spacer(modifier = Modifier.height(8.dp))
        if (!showAddStepInput) {
            TextButton(
                onClick = { showAddStepInput = true },
                modifier = Modifier.testTag("add_custom_microstep_toggle")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = ElegantTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Add micro-step",
                    fontSize = 12.sp,
                    color = ElegantTextSecondary
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ElegantDarkSurface)
                    .border(1.dp, ElegantDarkBorder, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                OutlinedTextField(
                    value = newStepTitle,
                    onValueChange = { newStepTitle = it },
                    placeholder = { Text("e.g. Open browser tabs (3 mins)", color = ElegantTextMuted, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ElegantTextPrimary,
                        unfocusedTextColor = ElegantTextPrimary,
                        focusedBorderColor = ElegantLavender,
                        unfocusedBorderColor = ElegantDarkBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_microstep_input")
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Est: ", fontSize = 12.sp, color = ElegantTextSecondary)
                        listOf(3, 5, 10).forEach { mins ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (newStepDuration == mins) ElegantLavender else ElegantDarkBorder)
                                    .clickable { newStepDuration = mins }
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${mins}m",
                                    fontSize = 11.sp,
                                    color = if (newStepDuration == mins) ElegantLavenderContainer else ElegantTextPrimary
                                )
                            }
                        }
                    }

                    Row {
                        TextButton(onClick = { showAddStepInput = false }) {
                            Text("Cancel", color = ElegantTextMuted, fontSize = 12.sp)
                        }
                        TextButton(
                            onClick = {
                                if (newStepTitle.isNotBlank()) {
                                    if (onAddSubstep != null && task != null) {
                                        onAddSubstep(task.id, newStepTitle, newStepDuration)
                                    } else {
                                        onAddStep(newStepTitle, newStepDuration)
                                    }
                                    newStepTitle = ""
                                    showAddStepInput = false
                                }
                            },
                            modifier = Modifier.testTag("save_custom_microstep_button")
                        ) {
                            Text("Add", color = ElegantLavender, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MicroStepItem(
    step: MicroStep,
    isFocusHighlight: Boolean,
    onToggle: () -> Unit,
    onStartTimer: () -> Unit,
    onDelete: () -> Unit
) {
    val isCompleted = step.isDone
    val borderColor = when {
        isCompleted -> ElegantLavender
        isFocusHighlight -> ElegantRose
        else -> Color.Transparent
    }

    val backgroundColor = when {
        isCompleted -> ElegantDarkSurface
        isFocusHighlight -> ElegantDarkSurfaceVariant
        else -> ElegantDarkSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = if (isFocusHighlight || isCompleted) 1.5.dp else 1.dp,
                color = if (isFocusHighlight) ElegantRose.copy(alpha = 0.4f) else ElegantDarkBorderSubtle,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onToggle() }
            .padding(14.dp)
            .alpha(if (isCompleted) 0.6f else 1.0f)
            .testTag("micro_step_item_${step.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Colored indicator left border bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(borderColor)
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Checkbox Icon
        Icon(
            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = if (isCompleted) "Step Complete" else "Step Incomplete",
            tint = if (isCompleted) ElegantLavender else (if (isFocusHighlight) ElegantRose else ElegantTextSecondary),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Step Title and Duration
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = step.title,
                fontSize = 13.sp,
                fontWeight = if (isFocusHighlight) FontWeight.Medium else FontWeight.Normal,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = if (isCompleted) ElegantTextSecondary else ElegantTextPrimary
            )
            if (step.encouragementTip.isNotBlank() && isFocusHighlight) {
                Text(
                    text = step.encouragementTip,
                    fontSize = 11.sp,
                    color = ElegantRose.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Action Tag or Focus Pill
        if (isFocusHighlight && !isCompleted) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(ElegantDarkBorder)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .testTag("focus_badge")
            ) {
                Text(
                    text = "FOCUS",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantRose,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        // Quick timer trigger for this micro-step
        if (!isCompleted) {
            IconButton(
                onClick = onStartTimer,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("start_microstep_timer_${step.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Focus on this step",
                    tint = ElegantLavender,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete step",
                    tint = ElegantTextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
