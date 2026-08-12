package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderSubtle
import com.example.ui.theme.ElegantDarkNavBackground
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantLavenderDark
import com.example.ui.theme.ElegantRose
import com.example.ui.theme.ElegantRoseDark
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import com.example.ui.theme.EnergyLowColor
import com.example.ui.theme.StreakFlameColor

enum class DonationTier(
    val amountDisplay: String,
    val amountValue: Double,
    val title: String,
    val description: String,
    val emoji: String
) {
    COFFEE("$3", 3.0, "Coffee Boost", "A warm coffee to fuel late-night features", "☕"),
    SUPPORTER("$5", 5.0, "App Supporter", "Keeps FocusFlow independent & ad-free", "❤️"),
    PATRON("$10", 10.0, "Executive Patron", "Unlocks permanent Supporter Badge", "👑"),
    CUSTOM("Custom", 0.0, "Custom Pledge", "Specify your own contribution amount", "✨")
}

@Composable
fun WelcomeDonationScreen(
    onDonateSuccess: (amount: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTier by remember { mutableStateOf(DonationTier.SUPPORTER) }
    var customAmountText by remember { mutableStateOf("15") }
    var showThankYouDialog by remember { mutableStateOf(false) }

    val finalAmountDisplay = if (selectedTier == DonationTier.CUSTOM) {
        "$$customAmountText"
    } else {
        selectedTier.amountDisplay
    }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF131118),
                            ElegantDarkBackground,
                            Color(0xFF1E1B26)
                        )
                    )
                )
                .testTag("welcome_donation_screen")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Close Button (Maybe Later)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { onDismiss() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ElegantDarkSurfaceVariant)
                            .testTag("welcome_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ElegantTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hero Decorative Heart Badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    ElegantLavenderDark,
                                    ElegantRoseDark
                                )
                            )
                        )
                        .border(
                            2.dp,
                            Brush.linearGradient(
                                colors = listOf(
                                    ElegantLavender,
                                    ElegantRose
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = "Welcome Support",
                        tint = ElegantRose,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title & Message
                Text(
                    text = "Welcome to FocusFlow! 🌟",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Thank you for using our distraction-free executive planner.",
                    fontSize = 14.sp,
                    color = ElegantLavender,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Appreciation Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(ElegantDarkSurface)
                        .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = ElegantRose,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "A Note from the Developer",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElegantTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "FocusFlow is 100% ad-free, privacy-first, and stores all your data safely on your device. We rely on community donations to keep development independent and thriving.\n\nIf FocusFlow helps you structure your day and achieve flow, consider supporting us with a small voluntary donation.",
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = ElegantTextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(EnergyLowColor.copy(alpha = 0.12f))
                                .border(1.dp, EnergyLowColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Permanent Hide Guarantee",
                                tint = EnergyLowColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "One donation permanently disables this welcome landing prompt for as long as FocusFlow is installed.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EnergyLowColor,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Select Contribution Tier",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantTextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Donation Tier Selection Cards
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DonationTier.values().forEach { tier ->
                        val isSelected = selectedTier == tier

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) ElegantLavenderDark.copy(alpha = 0.4f)
                                    else ElegantDarkSurfaceVariant
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) ElegantLavender else ElegantDarkBorderSubtle,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedTier = tier }
                                .padding(14.dp)
                                .testTag("donation_tier_${tier.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = tier.emoji,
                                        fontSize = 24.sp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = tier.title,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) ElegantLavender else ElegantTextPrimary
                                            )
                                            if (tier == DonationTier.SUPPORTER) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(StreakFlameColor)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "POPULAR",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = Color.Black
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = tier.description,
                                            fontSize = 11.sp,
                                            color = ElegantTextSecondary
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) ElegantLavender
                                            else ElegantDarkSurface
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tier.amountDisplay,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) ElegantDarkBackground else ElegantTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Custom amount field if CUSTOM selected
                AnimatedVisibility(visible = selectedTier == DonationTier.CUSTOM) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        OutlinedTextField(
                            value = customAmountText,
                            onValueChange = { customAmountText = it.filter { char -> char.isDigit() } },
                            label = { Text("Enter Amount ($)", color = ElegantTextMuted) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = ElegantTextPrimary,
                                unfocusedTextColor = ElegantTextPrimary,
                                focusedBorderColor = ElegantLavender,
                                unfocusedBorderColor = ElegantDarkBorder,
                                focusedContainerColor = ElegantDarkSurface,
                                unfocusedContainerColor = ElegantDarkSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_donation_input")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Donate Button
                Button(
                    onClick = { showThankYouDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("donate_confirm_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElegantLavender,
                        contentColor = ElegantDarkBackground
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Donate $finalAmountDisplay & Remove Prompt",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Maybe Later Button
                TextButton(
                    onClick = { onDismiss() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("maybe_later_button")
                ) {
                    Text(
                        text = "Maybe Later (Continue to App)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElegantTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Thank You Confirmation Overlay Dialog
    if (showThankYouDialog) {
        Dialog(
            onDismissRequest = {
                showThankYouDialog = false
                onDonateSuccess(finalAmountDisplay)
            }
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(ElegantDarkSurface)
                    .border(2.dp, ElegantLavender, RoundedCornerShape(24.dp))
                    .padding(24.dp)
                    .testTag("donation_thank_you_dialog")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(EnergyLowColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = EnergyLowColor,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Thank You So Much! ❤️",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your donation of $finalAmountDisplay makes a huge difference. You've unlocked Supporter status, and this welcome landing prompt will never be shown again.",
                        fontSize = 13.sp,
                        color = ElegantTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            showThankYouDialog = false
                            onDonateSuccess(finalAmountDisplay)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("thank_you_continue_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EnergyLowColor,
                            contentColor = ElegantDarkBackground
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Enjoy FocusFlow",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
