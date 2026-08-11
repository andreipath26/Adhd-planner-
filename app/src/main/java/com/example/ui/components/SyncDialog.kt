package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
fun SyncDialog(
    syncStatus: String,
    isSyncing: Boolean,
    onTriggerSync: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(24.dp))
                .testTag("sync_devices_dialog"),
            color = ElegantDarkSurface
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(ElegantDarkBorder),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = null,
                        tint = ElegantLavender,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Cross-Device Continuity",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElegantTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Your tasks, micro-steps, and focus streak sync automatically between your active devices.",
                    fontSize = 12.sp,
                    color = ElegantTextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Connected Devices List
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ElegantDarkBorder.copy(alpha = 0.35f))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DeviceRowItem(
                        icon = Icons.Default.Smartphone,
                        deviceName = "Pixel 8 Pro (This Device)",
                        status = "Active & Synced"
                    )
                    DeviceRowItem(
                        icon = Icons.Default.Tablet,
                        deviceName = "Pixel Tablet",
                        status = "Synced 2m ago"
                    )
                    DeviceRowItem(
                        icon = Icons.Default.Devices,
                        deviceName = "Web Desktop Dashboard",
                        status = "Synced 5m ago"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = syncStatus,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = ElegantLavender
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Close", color = ElegantTextSecondary)
                    }

                    Button(
                        onClick = onTriggerSync,
                        enabled = !isSyncing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElegantLavenderContainer,
                            contentColor = ElegantLavenderDeepest
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("force_sync_button")
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = ElegantLavenderDeepest,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Syncing...", fontSize = 12.sp)
                        } else {
                            Text("Sync Now", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    deviceName: String,
    status: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ElegantLavender,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = deviceName, fontSize = 12.sp, color = ElegantTextPrimary, fontWeight = FontWeight.Medium)
            Text(text = status, fontSize = 10.sp, color = ElegantTextMuted)
        }
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Synced",
            tint = ElegantLavender,
            modifier = Modifier.size(14.dp)
        )
    }
}
