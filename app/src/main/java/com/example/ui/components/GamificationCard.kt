package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaderboardUser
import com.example.data.model.UserProgress
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantDarkBorder
import com.example.ui.theme.ElegantDarkBorderSubtle
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantLavenderDark
import com.example.ui.theme.ElegantPurpleGrey
import com.example.ui.theme.ElegantPurpleGreyDark
import com.example.ui.theme.ElegantRose
import com.example.ui.theme.ElegantRoseDark
import com.example.ui.theme.ElegantTextSecondary

@Composable
fun GamificationCard(
    progress: UserProgress,
    leaderboardUsers: List<LeaderboardUser>,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.currentLevelProgress,
        label = "xp_bar_progress"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ElegantDarkSurface)
            .border(1.dp, ElegantDarkBorderSubtle, RoundedCornerShape(20.dp))
            .clickable { onCardClick() }
            .padding(14.dp)
            .testTag("gamification_progress_card"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // XP Progress & Level Details
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LVL ${progress.currentLevel}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantLavender
                )
                Text(
                    text = "${progress.totalXp} / ${progress.currentLevel * 250} XP",
                    fontSize = 11.sp,
                    color = ElegantTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // XP Level Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ElegantDarkBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(4.dp))
                        .background(ElegantLavender)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Community Avatar Stack (-space-x-2)
        Row(
            modifier = Modifier.testTag("leaderboard_avatar_stack"),
            horizontalArrangement = Arrangement.spacedBy((-8).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // JD Avatar
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(ElegantRose)
                    .border(2.dp, ElegantDarkBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "JD",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantRoseDark
                )
            }

            // ME Avatar
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(ElegantLavender)
                    .border(2.dp, ElegantDarkBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ME",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantLavenderDark
                )
            }

            // +4 Avatar
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(ElegantPurpleGrey)
                    .border(2.dp, ElegantDarkBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+4",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantPurpleGreyDark
                )
            }
        }
    }
}
