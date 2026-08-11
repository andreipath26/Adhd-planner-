package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyCheckIn
import com.example.data.model.DailyMood
import com.example.ui.theme.ElegantDarkBackground
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyMoodCheckInScreen(
    currentCheckIn: DailyCheckIn?,
    onSaveCheckIn: (DailyMood, Int, String, String, String, String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMood by remember { mutableStateOf(currentCheckIn?.mood ?: DailyMood.CALM) }
    var selectedEnergy by remember { mutableIntStateOf(currentCheckIn?.energyLevel ?: 3) }
    var intentionText by remember {
        mutableStateOf(currentCheckIn?.intention ?: "Protect my focus and single-task with ease")
    }
    var selectedTheme by remember {
        mutableStateOf(currentCheckIn?.focusTheme ?: "Single-tasking")
    }

    val selectedMoodColor = try {
        Color(android.graphics.Color.parseColor(selectedMood.colorHex))
    } catch (e: Exception) {
        ElegantLavender
    }

    val dateFormatted = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    val predefinedIntentions = listOf(
        "🎯 Complete my #1 priority spotlight task",
        "🌊 Single-task without jumping between tabs",
        "🕊️ Practice self-compassion & take regular breaks",
        "🧹 Clear 3 lingering items in Brain Dump",
        "⚡ Build momentum with 5-minute micro-steps",
        "🛡️ Protect focus blocks from outside distractions"
    )

    val themeChips = listOf(
        "Single-tasking",
        "Gentle Pacing",
        "Deep Focus",
        "Mental Declutter",
        "Momentum Building",
        "Rest & Recharge"
    )

    // Dynamic strategy recommendation based on mood & energy
    val dynamicStrategy = when {
        selectedMood == DailyMood.OVERWHELMED || selectedEnergy <= 2 ->
            "Keep today gentle. Choose only 1 small task and break it into 5-minute micro-steps. Take a 10-minute walk."
        selectedMood == DailyMood.SCATTERED ->
            "Ground your attention: turn on White Noise audio, close unneeded browser tabs, and work in 15-minute visual timer blocks."
        selectedMood == DailyMood.ENERGIZED && selectedEnergy >= 4 ->
            "Capitalize on high energy! Schedule your spotlight task in your morning block while mental capacity is peak."
        selectedMood == DailyMood.FOCUSED ->
            "Lock into Deep Work mode. Enable distraction-free immersion to protect your cognitive flow."
        else ->
            "Steady pacing: Align your high-energy tasks to your morning block and wind down with lighter admin later."
    }

    val mindfulAffirmation = when (selectedMood) {
        DailyMood.ENERGIZED -> "Channel your enthusiasm with calm intention."
        DailyMood.CALM -> "Smooth, steady waters carry you the farthest."
        DailyMood.FOCUSED -> "One clear goal at a time is your superpower."
        DailyMood.SCATTERED -> "You don't need to do everything—just the very next micro-step."
        DailyMood.FATIGUED -> "Resting is productive. Honor your energy without guilt."
        DailyMood.OVERWHELMED -> "Breathe. Let go of the mental pile and focus on right now."
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("daily_mood_checkin_screen"),
        color = ElegantDarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dateFormatted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElegantLavender
                    )
                    Text(
                        text = "Daily Mind & Energy Check-in",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantTextPrimary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_checkin_screen_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss Check-in",
                        tint = ElegantTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Section 1: Mood Selection
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(ElegantDarkSurface)
                            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "1", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElegantLavender)
                            Text(
                                text = "How is your headspace feeling?",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElegantTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Grid of 6 Moods (2 columns)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val moods = DailyMood.values()
                            for (i in moods.indices step 2) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    MoodCardItem(
                                        mood = moods[i],
                                        isSelected = selectedMood == moods[i],
                                        onClick = { selectedMood = moods[i] },
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (i + 1 < moods.size) {
                                        MoodCardItem(
                                            mood = moods[i + 1],
                                            isSelected = selectedMood == moods[i + 1],
                                            onClick = { selectedMood = moods[i + 1] },
                                            modifier = Modifier.weight(1f)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Selected Mood Empathetic Description
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(selectedMoodColor.copy(alpha = 0.12f))
                                .border(1.dp, selectedMoodColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = selectedMood.emoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${selectedMood.title}: ${selectedMood.description}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = selectedMoodColor
                                    )
                                    Text(
                                        text = selectedMood.tip,
                                        fontSize = 11.sp,
                                        color = ElegantTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 2: Energy Battery Selector
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(ElegantDarkSurface)
                            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "2", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElegantLavender)
                                Text(
                                    text = "Cognitive Battery & Energy Level",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ElegantTextPrimary
                                )
                            }

                            Text(
                                text = "${selectedEnergy * 20}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedMoodColor
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Visual Segmented Battery Graphic
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElegantDarkSurfaceVariant)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (level in 1..5) {
                                val isFilled = level <= selectedEnergy
                                val segmentColor = when (level) {
                                    1 -> EnergyHighColor
                                    2 -> ElegantRose
                                    3 -> EnergyMediumColor
                                    4 -> EnergyLowColor
                                    5 -> ElegantLavender
                                    else -> EnergyMediumColor
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isFilled) segmentColor else Color.Transparent)
                                        .clickable { selectedEnergy = level }
                                        .testTag("energy_segment_$level"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$level",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isFilled) ElegantLavenderDeepest else ElegantTextMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Energy description label
                        val energyDescription = when (selectedEnergy) {
                            1 -> "🔋 Level 1: Low Battery — Aim for 1 micro-win and restful pacing."
                            2 -> "🔋 Level 2: Gentle Load — 1-2 light tasks, protect energy reserves."
                            3 -> "🔋 Level 3: Steady Flow — Balanced capacity for standard focus blocks."
                            4 -> "⚡ Level 4: High Capacity — Great for deep work on your spotlight task."
                            5 -> "🚀 Level 5: Peak Hyperfocus — High drive for tackling complex projects."
                            else -> "Balanced Energy"
                        }

                        Text(
                            text = energyDescription,
                            fontSize = 12.sp,
                            color = ElegantTextSecondary,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                // Section 3: Today's Intention & Theme
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(ElegantDarkSurface)
                            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "3", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElegantLavender)
                            Text(
                                text = "Set Today's Focus Intention",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElegantTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Theme Chips
                        Text(
                            text = "Focus Theme:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ElegantTextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            themeChips.forEach { theme ->
                                val isSelected = selectedTheme == theme
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) ElegantLavender.copy(alpha = 0.2f)
                                            else ElegantDarkSurfaceVariant
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) ElegantLavender else Color.Transparent,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedTheme = theme }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .testTag("theme_chip_$theme"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = theme,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) ElegantLavender else ElegantTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick suggestion chips
                        Text(
                            text = "Quick Presets:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ElegantTextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            predefinedIntentions.take(3).forEach { suggestion ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(ElegantDarkSurfaceVariant)
                                        .clickable { intentionText = suggestion }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = suggestion,
                                        fontSize = 12.sp,
                                        color = ElegantTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Editable Intention Text Field
                        OutlinedTextField(
                            value = intentionText,
                            onValueChange = { intentionText = it },
                            label = { Text("My personal intention for today") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("intention_text_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElegantLavender,
                                unfocusedBorderColor = ElegantDarkBorder,
                                focusedTextColor = ElegantTextPrimary,
                                unfocusedTextColor = ElegantTextPrimary,
                                focusedContainerColor = ElegantDarkSurfaceVariant,
                                unfocusedContainerColor = ElegantDarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(14.dp),
                            minLines = 2,
                            maxLines = 3
                        )
                    }
                }

                // Section 4: Tailored ADHD Strategy & Affirmation Preview
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(ElegantDarkSurfaceVariant)
                            .border(1.dp, selectedMoodColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = selectedMoodColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tailored ADHD Workload Strategy",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedMoodColor
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = dynamicStrategy,
                            fontSize = 12.sp,
                            color = ElegantTextPrimary,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = ElegantRose,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "\"$mindfulAffirmation\"",
                                fontSize = 11.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = ElegantTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("skip_checkin_btn")
                ) {
                    Text(
                        text = "Skip for now",
                        fontSize = 13.sp,
                        color = ElegantTextMuted
                    )
                }

                Button(
                    onClick = {
                        onSaveCheckIn(
                            selectedMood,
                            selectedEnergy,
                            intentionText.ifBlank { "Protect my focus and flow smoothly" },
                            selectedTheme,
                            mindfulAffirmation,
                            dynamicStrategy
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("save_checkin_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElegantLavender,
                        contentColor = ElegantLavenderDeepest
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Set Intention (+25 XP)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodCardItem(
    mood: DailyMood,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val moodColor = try {
        Color(android.graphics.Color.parseColor(mood.colorHex))
    } catch (e: Exception) {
        ElegantLavender
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) moodColor else Color.Transparent,
        label = "moodBorderColor"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) moodColor.copy(alpha = 0.18f)
                else ElegantDarkSurfaceVariant
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) animatedBorderColor else ElegantDarkBorderSubtle,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(10.dp)
            .testTag("mood_card_${mood.name.lowercase()}"),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = mood.emoji,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mood.title,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) moodColor else ElegantTextPrimary
                )
                Text(
                    text = mood.description,
                    fontSize = 10.sp,
                    color = ElegantTextSecondary,
                    maxLines = 1
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = moodColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
