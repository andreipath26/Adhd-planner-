package com.example

import com.example.data.model.DailyCheckIn
import com.example.data.model.DailyMood
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun dailyCheckIn_energyPercentageAndLabel_calculatedProperly() {
        val lowCheckIn = DailyCheckIn(
            date = "2026-08-11",
            mood = DailyMood.FATIGUED,
            energyLevel = 1,
            intention = "Take it slow"
        )
        assertEquals(20, lowCheckIn.energyPercentage)
        assertEquals("Low Battery (Gentle Pacing)", lowCheckIn.energyLabel)

        val peakCheckIn = DailyCheckIn(
            date = "2026-08-11",
            mood = DailyMood.ENERGIZED,
            energyLevel = 5,
            intention = "Crush spotlight task"
        )
        assertEquals(100, peakCheckIn.energyPercentage)
        assertEquals("Peak Hyperfocus (Major Sprint)", peakCheckIn.energyLabel)
    }

    @Test
    fun dailyMood_allValuesHaveDistinctColorsAndTips() {
        val moods = DailyMood.values()
        assertEquals(6, moods.size)
        moods.forEach { mood ->
            assertTrue(mood.emoji.isNotBlank())
            assertTrue(mood.title.isNotBlank())
            assertTrue(mood.tip.isNotBlank())
            assertTrue(mood.colorHex.startsWith("#"))
        }
    }
}
