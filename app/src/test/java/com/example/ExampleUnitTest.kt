package com.example

import com.example.data.ai.TaskDecomposerAi
import com.example.data.model.DailyCheckIn
import com.example.data.model.DailyMicroGoal
import com.example.data.model.DailyMood
import com.example.data.model.DefaultMicroGoals
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    @Test
    fun dailyMicroGoals_completionTrackingAndXP() {
        val goals = listOf(
            DailyMicroGoal(id = "g1", title = "Meditate for 5 mins", emoji = "🧘", durationMinutes = 5, isCompleted = true),
            DailyMicroGoal(id = "g2", title = "Drink water", emoji = "💧", durationMinutes = 2, isCompleted = false)
        )
        val checkIn = DailyCheckIn(
            date = "2026-08-11",
            microGoals = goals
        )

        assertEquals(1, checkIn.completedMicroGoalsCount)
        assertFalse(checkIn.allMicroGoalsCompleted)

        val allCompletedCheckIn = checkIn.copy(
            microGoals = goals.map { it.copy(isCompleted = true) }
        )
        assertEquals(2, allCompletedCheckIn.completedMicroGoalsCount)
        assertTrue(allCompletedCheckIn.allMicroGoalsCompleted)
    }

    @Test
    fun defaultMicroGoals_presetsAvailableForMoods() {
        DailyMood.values().forEach { mood ->
            val presetList = DefaultMicroGoals.forMood(mood)
            assertTrue(presetList.isNotEmpty())
            presetList.forEach { goal ->
                assertTrue(goal.title.isNotBlank())
                assertTrue(goal.emoji.isNotBlank())
                assertTrue(goal.durationMinutes > 0)
            }
        }
    }

    @Test
    fun taskDecomposerAi_generatesValidBiteSizedSubsteps() = runBlocking {
        val result = TaskDecomposerAi.decomposeTask(
            taskId = 42L,
            taskTitle = "Write Quarterly Financial Report",
            taskDescription = "Consolidate all revenue and cost spreadsheets"
        )

        assertTrue(result.isNotEmpty())
        assertEquals(42L, result.first().taskId)
        result.forEach { step ->
            assertTrue(step.title.isNotBlank())
            assertTrue(step.durationMinutes in 2..20)
            assertNotNull(step.encouragementTip)
            assertFalse(step.isDone)
        }
    }

    @Test
    fun visualTimerPresets_containsStandardDurations() {
        val presets = com.example.ui.components.StandardFocusPresets
        assertTrue(presets.isNotEmpty())
        assertEquals(6, presets.size)
        
        // Assert 25m Pomodoro exists
        val pomodoro = presets.find { it.minutes == 25 && !it.isBreak }
        assertNotNull(pomodoro)
        assertTrue(pomodoro!!.label.contains("Pomodoro"))
        assertEquals("🍅", pomodoro.emoji)
        
        // Assert 5m Reset exists
        val sprint = presets.find { it.minutes == 5 && !it.isBreak }
        assertNotNull(sprint)
        assertTrue(sprint!!.label.contains("5m"))

        // Assert 5m Break exists
        val breakPreset = presets.find { it.isBreak }
        assertNotNull(breakPreset)
        assertEquals(5, breakPreset!!.minutes)
    }

    @Test
    fun visualTimer_progressCalculation() {
        val totalSeconds = 25 * 60 // 1500s
        val remainingHalf = 750
        val progress = remainingHalf.toFloat() / totalSeconds.toFloat()
        assertEquals(0.5f, progress, 0.001f)

        val remainingZero = 0
        val progressZero = remainingZero.toFloat() / totalSeconds.toFloat()
        assertEquals(0.0f, progressZero, 0.001f)
    }
}
