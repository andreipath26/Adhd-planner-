package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaderboardUser
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderSubtle
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantRose
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import com.example.ui.theme.StreakFlameColor

@Composable
fun SocialLeaderboardScreen(
    users: List<LeaderboardUser>,
    onCheerUser: (LeaderboardUser) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBackground)
            .testTag("social_leaderboard_screen")
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Focus Accountability Tribe",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElegantTextPrimary
                    )
                    Text(
                        text = "Shared momentum without pressure or guilt",
                        fontSize = 12.sp,
                        color = ElegantTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ElegantDarkBorder),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = ElegantLavender,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Leaderboard List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(users, key = { it.id }) { user ->
                LeaderboardUserItem(
                    user = user,
                    onCheer = { onCheerUser(user) }
                )
            }
        }
    }
}

@Composable
private fun LeaderboardUserItem(
    user: LeaderboardUser,
    onCheer: () -> Unit
) {
    val isMe = user.isCurrentUser
    val itemBg = if (isMe) ElegantDarkSurfaceVariant else ElegantDarkSurface
    val itemBorder = if (isMe) ElegantLavender.copy(alpha = 0.5f) else ElegantDarkBorderSubtle

    val avatarBg = try {
        Color(android.graphics.Color.parseColor(user.avatarBgHex))
    } catch (e: Exception) {
        ElegantLavender
    }

    val avatarText = try {
        Color(android.graphics.Color.parseColor(user.avatarTextHex))
    } catch (e: Exception) {
        Color.Black
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(itemBg)
            .border(1.dp, itemBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
            .testTag("leaderboard_user_${user.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank Number
        Text(
            text = "#${user.rank}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (user.rank == 1) ElegantLavender else ElegantTextMuted,
            modifier = Modifier.width(28.dp)
        )

        // Avatar Initial
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(avatarBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.initials,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = avatarText
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // User Info & Status
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isMe) ElegantLavender else ElegantTextPrimary
                )
                if (isMe) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ElegantDarkBorder)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(text = "YOU", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = ElegantLavender)
                    }
                }
            }

            Text(
                text = user.currentActivity,
                fontSize = 11.sp,
                color = ElegantTextSecondary,
                modifier = Modifier.padding(top = 1.dp)
            )

            // Focus Minutes & Streak
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${user.weeklyFocusMinutes} mins focus",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = ElegantLavender
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = StreakFlameColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${user.streakDays}d",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StreakFlameColor
                    )
                }
            }
        }

        // Cheer Button
        if (!isMe) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (user.cheeredByMe) ElegantDarkBorder else ElegantDarkBorder.copy(alpha = 0.5f))
                    .clickable { onCheer() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("cheer_button_${user.id}"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Cheer",
                        tint = if (user.cheeredByMe) ElegantRose else ElegantTextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${user.cheersReceived}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (user.cheeredByMe) ElegantRose else ElegantTextSecondary
                    )
                }
            }
        }
    }
}
