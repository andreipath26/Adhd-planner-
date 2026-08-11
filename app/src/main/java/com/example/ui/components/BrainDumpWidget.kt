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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrainDumpItem
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

@Composable
fun BrainDumpWidget(
    pendingItems: List<BrainDumpItem>,
    pendingCount: Int,
    onQuickCapture: (String) -> Unit,
    onOpenVoiceCapture: () -> Unit,
    onOpenTriage: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ElegantDarkSurface)
            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(20.dp))
            .padding(18.dp)
            .testTag("brain_dump_widget")
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
                            .background(ElegantRose.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Brain Dump",
                            tint = ElegantRose,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Brain Dump Inbox",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElegantTextPrimary
                            )
                            if (pendingCount > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ElegantRose)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$pendingCount pending",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElegantDarkSurface
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Get thoughts out fast, categorize later",
                            fontSize = 11.sp,
                            color = ElegantTextSecondary
                        )
                    }
                }

                if (pendingCount > 0) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElegantLavenderContainer.copy(alpha = 0.3f))
                            .clickable { onOpenTriage() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("triage_inbox_btn"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Triage",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantLavender
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Triage",
                            tint = ElegantLavender,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Input Field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            text = "Quick thought or note...",
                            fontSize = 13.sp,
                            color = ElegantTextMuted
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("brain_dump_widget_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ElegantDarkSurfaceVariant,
                        unfocusedContainerColor = ElegantDarkSurfaceVariant,
                        focusedBorderColor = ElegantLavender,
                        unfocusedBorderColor = ElegantDarkBorderSubtle,
                        focusedTextColor = ElegantTextPrimary,
                        unfocusedTextColor = ElegantTextPrimary
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (textInput.isNotBlank()) {
                                onQuickCapture(textInput)
                                textInput = ""
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Voice Dictation Quick Button
                IconButton(
                    onClick = onOpenVoiceCapture,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElegantDarkSurfaceVariant)
                        .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(12.dp))
                        .testTag("brain_dump_voice_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Dictate note",
                        tint = ElegantRose,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (textInput.isNotBlank()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                onQuickCapture(textInput)
                                textInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElegantLavenderContainer)
                            .testTag("brain_dump_send_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Capture Thought",
                            tint = ElegantLavenderDeepest,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Preview recent pending thoughts if available
            if (pendingItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    pendingItems.take(2).forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ElegantDarkSurfaceVariant.copy(alpha = 0.5f))
                                .clickable { onOpenTriage() }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (item.isAudioDictation) "🎙️" else "💭",
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    text = item.content,
                                    fontSize = 12.sp,
                                    color = ElegantTextSecondary,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = item.categoryTag,
                                fontSize = 10.sp,
                                color = ElegantLavender,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ElegantLavenderDark.copy(alpha = 0.4f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
