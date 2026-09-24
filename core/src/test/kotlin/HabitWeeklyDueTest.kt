package com.example.rewire.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class HabitWeeklyDueTest {

    @Test
    fun weekly_dueOnStartDateWeekdayOnly() {
        val habit = Habit(
            id = 1,
            name = "Wed",
            recurrence = RecurrenceType.Weekly,
            startDate = LocalDate.of(2025, 8, 6) // Wednesday
        )
        assertTrue(habit.isDueOn(LocalDate.of(2025, 8, 6)))
        assertTrue(habit.isDueOn(LocalDate.of(2025, 8, 13)))
        assertFalse(habit.isDueOn(LocalDate.of(2025, 8, 11))) // Monday
        assertFalse(habit.isDueOn(LocalDate.of(2025, 8, 7))) // Thursday
    }
}
