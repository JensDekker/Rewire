package com.example.rewire.manager

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class HabitNotificationSchedulerTest {

    @Test
    fun selectScheduleDates_respectsFiveDayHorizonAndSkipsCompleted() {
        val preferred = LocalTime.of(9, 0)
        val now = LocalDateTime.of(2025, 8, 4, 8, 0) // Monday 08:00
        val due = (0L..10L).map { LocalDate.of(2025, 8, 4).plusDays(it) }.toSet()
        val completed = setOf(LocalDate.of(2025, 8, 4))

        val selected = HabitNotificationScheduler.selectScheduleDates(
            dueDates = due,
            completedDates = completed,
            preferredTime = preferred,
            now = now,
            horizonDays = 5
        )

        // today 4th completed → skip; 5–9 within horizon (5 days ahead from 4 = through 9)
        assertEquals(
            listOf(
                LocalDate.of(2025, 8, 5),
                LocalDate.of(2025, 8, 6),
                LocalDate.of(2025, 8, 7),
                LocalDate.of(2025, 8, 8),
                LocalDate.of(2025, 8, 9)
            ),
            selected
        )
        assertTrue(LocalDate.of(2025, 8, 10) !in selected)
    }

    @Test
    fun selectScheduleDates_skipsPastPreferredTimeToday() {
        val preferred = LocalTime.of(9, 0)
        val now = LocalDateTime.of(2025, 8, 4, 10, 0) // after 09:00
        val due = setOf(LocalDate.of(2025, 8, 4), LocalDate.of(2025, 8, 5))

        val selected = HabitNotificationScheduler.selectScheduleDates(
            dueDates = due,
            completedDates = emptySet(),
            preferredTime = preferred,
            now = now,
            horizonDays = 5
        )

        assertEquals(listOf(LocalDate.of(2025, 8, 5)), selected)
    }
}
