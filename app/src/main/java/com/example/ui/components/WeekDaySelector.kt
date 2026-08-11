package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElegantDarkBackground
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantLavenderDark
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary

data class DayItem(
    val dayOfWeekIndex: Int, // 1=Mon, ..., 7=Sun
    val letter: String,
    val dayOfMonth: Int
)

@Composable
fun WeekDaySelector(
    selectedDayIndex: Int,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Current week days (Sunday to Saturday or Monday to Sunday - let's do S M T W T F S)
    val days = listOf(
        DayItem(7, "S", 11),
        DayItem(1, "M", 12),
        DayItem(2, "T", 13),
        DayItem(3, "W", 14),
        DayItem(4, "T", 15),
        DayItem(5, "F", 16),
        DayItem(6, "S", 17)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("weekday_selector"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { item ->
            val isSelected = item.dayOfWeekIndex == selectedDayIndex
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onDaySelected(item.dayOfWeekIndex) }
                    .testTag("day_button_${item.dayOfWeekIndex}"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.letter,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) ElegantLavender else ElegantTextSecondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) ElegantLavender else ElegantDarkBackground
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${item.dayOfMonth}",
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) ElegantLavenderDark else ElegantTextPrimary
                    )
                }
            }
        }
    }
}
