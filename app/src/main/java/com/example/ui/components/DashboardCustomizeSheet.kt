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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DashboardWidgetConfig
import com.example.data.model.DashboardWidgetType
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderSubtle
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantLavenderContainer
import com.example.ui.theme.ElegantLavenderDark
import com.example.ui.theme.ElegantLavenderDeepest
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardCustomizeSheet(
    widgets: List<DashboardWidgetConfig>,
    onToggleVisibility: (DashboardWidgetType) -> Unit,
    onMoveUp: (DashboardWidgetType) -> Unit,
    onMoveDown: (DashboardWidgetType) -> Unit,
    onResetDefault: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sortedWidgets = widgets.sortedBy { it.orderIndex }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ElegantDarkSurface,
        contentColor = ElegantTextPrimary,
        modifier = Modifier.testTag("dashboard_customize_sheet")
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
                Column {
                    Text(
                        text = "Customize Dashboard",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElegantTextPrimary
                    )
                    Text(
                        text = "Toggle & reorder widgets to match your executive flow",
                        fontSize = 12.sp,
                        color = ElegantTextSecondary
                    )
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

            // Widget List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                sortedWidgets.forEachIndexed { index, config ->
                    WidgetReorderRow(
                        config = config,
                        isFirst = index == 0,
                        isLast = index == sortedWidgets.size - 1,
                        onToggleVisibility = { onToggleVisibility(config.type) },
                        onMoveUp = { onMoveUp(config.type) },
                        onMoveDown = { onMoveDown(config.type) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons (Reset Default & Done)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onResetDefault() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Layout",
                        tint = ElegantTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reset Layout",
                        fontSize = 13.sp,
                        color = ElegantTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElegantLavenderContainer)
                        .clickable { onDismiss() }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .testTag("save_dashboard_layout_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = ElegantLavenderDeepest,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save Layout",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantLavenderDeepest
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun WidgetReorderRow(
    config: DashboardWidgetConfig,
    isFirst: Boolean,
    isLast: Boolean,
    onToggleVisibility: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (config.isVisible) ElegantDarkSurfaceVariant else ElegantDarkSurface)
            .border(1.dp, if (config.isVisible) ElegantDarkBorder else ElegantDarkBorderSubtle, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon & Title
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = config.type.iconEmoji,
                fontSize = 20.sp,
                modifier = Modifier.padding(end = 12.dp)
            )

            Column {
                Text(
                    text = config.type.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (config.isVisible) ElegantTextPrimary else ElegantTextMuted
                )
                Text(
                    text = config.type.subtitle,
                    fontSize = 11.sp,
                    color = if (config.isVisible) ElegantTextSecondary else ElegantTextMuted
                )
            }
        }

        // Reordering & Visibility Controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Move Up
            IconButton(
                onClick = onMoveUp,
                enabled = !isFirst,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Move Up",
                    tint = if (!isFirst) ElegantLavender else ElegantTextMuted.copy(alpha = 0.3f),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Move Down
            IconButton(
                onClick = onMoveDown,
                enabled = !isLast,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Move Down",
                    tint = if (!isLast) ElegantLavender else ElegantTextMuted.copy(alpha = 0.3f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Visibility Switch
            Switch(
                checked = config.isVisible,
                onCheckedChange = { onToggleVisibility() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ElegantLavenderDeepest,
                    checkedTrackColor = ElegantLavender,
                    uncheckedThumbColor = ElegantTextMuted,
                    uncheckedTrackColor = ElegantDarkBorder
                ),
                modifier = Modifier.testTag("switch_${config.type.id}")
            )
        }
    }
}
