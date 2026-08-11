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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyCheckIn
import com.example.data.model.DailyMood
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderSubtle
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantLavenderContainer
import com.example.ui.theme.ElegantLavenderDeepest
import com.example.ui.theme.ElegantRose
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import com.example.ui.theme.EnergyHighColor
import com.example.ui.theme.EnergyLowColor
import com.example.ui.theme.EnergyMediumColor

@Composable
fun DailyIntentionWidget(
    todayCheckIn: DailyCheckIn?,
    onOpenCheckIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (todayCheckIn != null) {
        // Active Daily Intention & Mood Overview
        val mood = todayCheckIn.mood
        val moodColor = try {
            Color(android.graphics.Color.parseColor(mood.colorHex))
        } catch (e: Exception) {
            ElegantLavender
        }

        val energyColor = when (todayCheckIn.energyLevel) {
            1 -> EnergyHighColor // Low battery - red/coral alert
            2 -> ElegantRose
            3 -> EnergyMediumColor
            4 -> EnergyLowColor // High energy - green
            5 -> ElegantLavender // Peak hyperfocus
            else -> EnergyMediumColor
        }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(ElegantDarkSurface)
                .border(1.dp, moodColor.copy(alpha = 0.35f), RoundedCornerShape(22.dp))
                .padding(18.dp)
                .testTag("daily_intention_widget")
        ) {
            // Header Row: Mood Badge, Energy Battery, Edit Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mood Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(moodColor.copy(alpha = 0.18f))
                            .border(1.dp, moodColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("widget_mood_chip"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = mood.emoji, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = mood.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = moodColor
                            )
                        }
                    }

                    // Battery Energy Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(energyColor.copy(alpha = 0.15f))
                            .border(1.dp, energyColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("widget_energy_chip"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BatteryChargingFull,
                                contentDescription = "Energy Battery",
                                tint = energyColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${todayCheckIn.energyPercentage}% Energy",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = energyColor
                            )
                        }
                    }
                }

                // Edit Check-in Button
                IconButton(
                    onClick = onOpenCheckIn,
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("edit_daily_checkin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Intention",
                        tint = ElegantTextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Intention Quote Block
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = moodColor.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = todayCheckIn.intention.ifBlank { "Protect my focus and flow smoothly." },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic,
                        color = ElegantTextPrimary,
                        lineHeight = 21.sp,
                        modifier = Modifier.testTag("widget_intention_text")
                    )

                    if (todayCheckIn.focusTheme.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Focus Theme: ",
                                fontSize = 11.sp,
                                color = ElegantTextSecondary
                            )
                            Text(
                                text = todayCheckIn.focusTheme,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = moodColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ADHD Tailored Strategy & Coping Tip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElegantDarkSurfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "ADHD Strategy",
                        tint = moodColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = todayCheckIn.recommendedStrategy.ifBlank { mood.tip },
                        fontSize = 11.sp,
                        color = ElegantTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    } else {
        // Morning Check-in Prompt Card (Not yet completed today)
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(ElegantDarkSurface)
                .border(1.dp, ElegantLavender.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                .clickable { onOpenCheckIn() }
                .padding(18.dp)
                .testTag("daily_intention_prompt_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ElegantLavenderContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Daily Check-in",
                            tint = ElegantLavenderDeepest,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Daily Mood & Energy Check-in",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantTextPrimary
                        )
                        Text(
                            text = "Set today's focus intention • Earn +25 XP",
                            fontSize = 11.sp,
                            color = ElegantLavender
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElegantLavender)
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .testTag("start_checkin_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ElegantLavenderDeepest,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Check In",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantLavenderDeepest
                        )
                    }
                }
            }
        }
    }
}
