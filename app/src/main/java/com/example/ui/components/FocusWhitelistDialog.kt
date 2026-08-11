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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FocusAudioTrack
import com.example.data.model.WhitelistApp
import com.example.data.model.WhitelistContact
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusWhitelistDialog(
    isNotificationShieldActive: Boolean,
    whitelistContacts: List<WhitelistContact>,
    whitelistApps: List<WhitelistApp>,
    focusAudioTrack: FocusAudioTrack,
    focusAudioVolume: Float,
    onToggleNotificationShield: () -> Unit,
    onToggleContact: (String) -> Unit,
    onToggleApp: (String) -> Unit,
    onAddContact: (String, String) -> Unit,
    onAddApp: (String, String) -> Unit,
    onSelectAudioTrack: (FocusAudioTrack) -> Unit,
    onSetAudioVolume: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    var isAddingContact by remember { mutableStateOf(false) }
    var newContactName by remember { mutableStateOf("") }
    var newContactRelation by remember { mutableStateOf("") }

    var isAddingApp by remember { mutableStateOf(false) }
    var newAppName by remember { mutableStateOf("") }
    var newAppCategory by remember { mutableStateOf("") }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .clip(RoundedCornerShape(24.dp))
            .background(ElegantDarkSurface)
            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(24.dp))
            .padding(20.dp)
            .testTag("focus_whitelist_dialog")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
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
                            .background(ElegantLavenderDark.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Shield Settings",
                            tint = ElegantLavender,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Focus Shield & Whitelist",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElegantTextPrimary
                        )
                        Text(
                            text = "Distraction suppression rules",
                            fontSize = 11.sp,
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: DND Shield Toggle
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ElegantDarkSurfaceVariant)
                            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Suppress Non-Critical Alerts",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElegantTextPrimary
                            )
                            Text(
                                text = "Blocks social & media notifications during focus mode",
                                fontSize = 11.sp,
                                color = ElegantTextSecondary
                            )
                        }

                        Switch(
                            checked = isNotificationShieldActive,
                            onCheckedChange = { onToggleNotificationShield() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ElegantLavenderDeepest,
                                checkedTrackColor = ElegantLavender
                            )
                        )
                    }
                }

                // Section 2: Ambient Audio Focus Soundtrack
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ElegantDarkSurfaceVariant)
                            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Soundscape",
                                tint = ElegantLavender,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ambient ADHD Soundscape",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElegantTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            FocusAudioTrack.entries.forEach { track ->
                                val isSelected = focusAudioTrack == track
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ElegantLavenderContainer else ElegantDarkSurface)
                                        .clickable { onSelectAudioTrack(track) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = track.iconEmoji, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = track.label,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) ElegantLavenderDeepest else ElegantTextPrimary
                                        )
                                    }
                                    Text(
                                        text = track.description,
                                        fontSize = 10.sp,
                                        color = if (isSelected) ElegantLavenderDark else ElegantTextMuted
                                    )
                                }
                            }
                        }

                        if (focusAudioTrack != FocusAudioTrack.OFF) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Volume",
                                    tint = ElegantTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Slider(
                                    value = focusAudioVolume,
                                    onValueChange = { onSetAudioVolume(it) },
                                    modifier = Modifier.weight(1f),
                                    colors = SliderDefaults.colors(
                                        thumbColor = ElegantLavender,
                                        activeTrackColor = ElegantLavender,
                                        inactiveTrackColor = ElegantDarkBorder
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${(focusAudioVolume * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    color = ElegantTextSecondary
                                )
                            }
                        }
                    }
                }

                // Section 3: Whitelisted VIP Contacts
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ElegantDarkSurfaceVariant)
                            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Contacts",
                                    tint = ElegantLavender,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Allowed VIP Contacts",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantTextPrimary
                                )
                            }

                            IconButton(
                                onClick = { isAddingContact = !isAddingContact },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Contact",
                                    tint = ElegantLavender,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        if (isAddingContact) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newContactName,
                                    onValueChange = { newContactName = it },
                                    placeholder = { Text("Contact Name (e.g. Boss)", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = ElegantTextPrimary,
                                        unfocusedTextColor = ElegantTextPrimary
                                    ),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ElegantLavenderContainer)
                                        .clickable {
                                            if (newContactName.isNotBlank()) {
                                                onAddContact(newContactName, newContactRelation.ifBlank { "Personal" })
                                                newContactName = ""
                                                newContactRelation = ""
                                                isAddingContact = false
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElegantLavenderDeepest)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            whitelistContacts.forEach { contact ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ElegantDarkSurface)
                                        .clickable { onToggleContact(contact.id) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = contact.name, fontSize = 12.sp, color = ElegantTextPrimary)
                                        Text(text = contact.relation, fontSize = 10.sp, color = ElegantTextMuted)
                                    }

                                    Switch(
                                        checked = contact.isAllowed,
                                        onCheckedChange = { onToggleContact(contact.id) },
                                        modifier = Modifier.size(28.dp),
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = ElegantLavenderDeepest,
                                            checkedTrackColor = ElegantLavender
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 4: Whitelisted Allowed Apps
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ElegantDarkSurfaceVariant)
                            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = "Apps",
                                    tint = ElegantLavender,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Allowed Urgent Apps",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantTextPrimary
                                )
                            }

                            IconButton(
                                onClick = { isAddingApp = !isAddingApp },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add App",
                                    tint = ElegantLavender,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        if (isAddingApp) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newAppName,
                                    onValueChange = { newAppName = it },
                                    placeholder = { Text("App Name (e.g. Phone)", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = ElegantTextPrimary,
                                        unfocusedTextColor = ElegantTextPrimary
                                    ),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ElegantLavenderContainer)
                                        .clickable {
                                            if (newAppName.isNotBlank()) {
                                                onAddApp(newAppName, newAppCategory.ifBlank { "Utility" })
                                                newAppName = ""
                                                newAppCategory = ""
                                                isAddingApp = false
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ElegantLavenderDeepest)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            whitelistApps.forEach { app ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ElegantDarkSurface)
                                        .clickable { onToggleApp(app.id) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = app.appName, fontSize = 12.sp, color = ElegantTextPrimary)
                                        Text(text = app.category, fontSize = 10.sp, color = ElegantTextMuted)
                                    }

                                    Switch(
                                        checked = app.isAllowed,
                                        onCheckedChange = { onToggleApp(app.id) },
                                        modifier = Modifier.size(28.dp),
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = ElegantLavenderDeepest,
                                            checkedTrackColor = ElegantLavender
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Done Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElegantLavenderContainer)
                    .clickable { onDismiss() }
                    .padding(vertical = 12.dp)
                    .testTag("close_whitelist_dialog_btn"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Apply Shield Rules",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantLavenderDeepest
                )
            }
        }
    }
}
