package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EnergyLevel
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
fun BrainDumpQuickSheet(
    pendingCount: Int,
    isRecordingVoice: Boolean,
    recordedVoiceSeconds: Int,
    onStartVoiceRecording: () -> Unit,
    onStopVoiceRecording: () -> Int,
    onSaveThought: (content: String, category: String, isAudio: Boolean, duration: Int, energy: EnergyLevel) -> Unit,
    onOpenTriage: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var textContent by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Idea") }
    var selectedEnergy by remember { mutableStateOf(EnergyLevel.MEDIUM) }
    var showCapturedFeedback by remember { mutableStateOf(false) }

    val categories = listOf(
        "Idea" to "💡 Idea",
        "Task" to "📋 Task",
        "Errand" to "🛒 Errand",
        "Work" to "💼 Work",
        "Personal" to "🌱 Personal",
        "Urgent" to "⚡ Urgent"
    )

    // Voice pulsating animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecordingVoice) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ElegantDarkSurface,
        contentColor = ElegantTextPrimary,
        modifier = Modifier.testTag("brain_dump_quick_sheet")
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
                            contentDescription = "Brain Dump",
                            tint = ElegantRose,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Brain Dump",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElegantTextPrimary
                        )
                        Text(
                            text = "Clear mental RAM — write or dictate freely",
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

            // Text Input Box
            OutlinedTextField(
                value = textContent,
                onValueChange = { textContent = it },
                placeholder = {
                    Text(
                        text = if (isRecordingVoice) "Listening... speak your mind..." else "What's occupying your head? Get it out...",
                        fontSize = 14.sp,
                        color = if (isRecordingVoice) ElegantRose else ElegantTextMuted
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("brain_dump_input_field"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ElegantDarkSurfaceVariant,
                    unfocusedContainerColor = ElegantDarkSurfaceVariant,
                    focusedBorderColor = ElegantRose,
                    unfocusedBorderColor = ElegantDarkBorderSubtle,
                    focusedTextColor = ElegantTextPrimary,
                    unfocusedTextColor = ElegantTextPrimary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
            )

            // Voice Dictation Bar
            if (isRecordingVoice) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElegantRose.copy(alpha = 0.15f))
                        .border(1.dp, ElegantRose.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(ElegantRose)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recording Dictation (${recordedVoiceSeconds}s)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = ElegantRose
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElegantRose)
                            .clickable {
                                val dur = onStopVoiceRecording()
                                if (textContent.isBlank()) {
                                    textContent = "Audio note ($dur seconds)"
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop recording",
                                tint = ElegantDarkSurface,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Done",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElegantDarkSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Category Tag Selector
            Text(
                text = "Category Hint",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = ElegantTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { (catKey, catLabel) ->
                    val isSelected = selectedCategory == catKey
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) ElegantLavenderContainer else ElegantDarkSurfaceVariant)
                            .border(1.dp, if (isSelected) ElegantLavender else ElegantDarkBorderSubtle, RoundedCornerShape(10.dp))
                            .clickable { selectedCategory = catKey }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = catLabel,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ElegantLavenderDeepest else ElegantTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Energy Level Hint
            Text(
                text = "Energy Requirement (Optional)",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = ElegantTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EnergyLevel.entries.forEach { level ->
                    val isSelected = selectedEnergy == level
                    val color = when (level) {
                        EnergyLevel.LOW -> EnergyLowColor
                        EnergyLevel.MEDIUM -> EnergyMediumColor
                        EnergyLevel.HIGH -> EnergyHighColor
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) color.copy(alpha = 0.25f) else ElegantDarkSurfaceVariant)
                            .border(1.dp, if (isSelected) color else ElegantDarkBorderSubtle, RoundedCornerShape(10.dp))
                            .clickable { selectedEnergy = level }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${level.icon} ${level.label}",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) color else ElegantTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Actions (Voice Mic, Dump Thought Button, Go to Triage)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Voice Dictation Toggle Button
                Box(
                    modifier = Modifier
                        .scale(if (isRecordingVoice) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(if (isRecordingVoice) ElegantRose else ElegantDarkSurfaceVariant)
                        .border(1.dp, if (isRecordingVoice) ElegantRose else ElegantDarkBorderSubtle, CircleShape)
                        .clickable {
                            if (isRecordingVoice) {
                                val dur = onStopVoiceRecording()
                                if (textContent.isBlank()) {
                                    textContent = "Audio note (${dur}s)"
                                }
                            } else {
                                onStartVoiceRecording()
                            }
                        }
                        .padding(12.dp)
                        .testTag("dictation_fab_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRecordingVoice) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Voice Dictation",
                        tint = if (isRecordingVoice) ElegantDarkSurface else ElegantRose,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Dump Thought Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (textContent.isNotBlank() || isRecordingVoice) ElegantRose else ElegantDarkSurfaceVariant)
                        .clickable(enabled = textContent.isNotBlank() || isRecordingVoice) {
                            val isAudio = isRecordingVoice
                            val dur = if (isRecordingVoice) onStopVoiceRecording() else 0
                            val finalContent = if (textContent.isBlank()) "Voice note (${dur}s)" else textContent
                            onSaveThought(finalContent, selectedCategory, isAudio, dur, selectedEnergy)
                            textContent = ""
                            showCapturedFeedback = true
                        }
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .testTag("dump_thought_submit_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Dump Thought 🧠",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (textContent.isNotBlank() || isRecordingVoice) ElegantDarkSurface else ElegantTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer link to Inbox Triage
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElegantDarkSurfaceVariant.copy(alpha = 0.5f))
                    .clickable {
                        onDismiss()
                        onOpenTriage()
                    }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("open_triage_from_sheet_btn"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📥", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Brain Dump Inbox ($pendingCount pending to triage)",
                        fontSize = 12.sp,
                        color = ElegantTextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open Triage",
                    tint = ElegantLavender,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
