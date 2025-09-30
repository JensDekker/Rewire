package com.example.rewire

import com.example.rewire.core.DayOfWeek
import com.example.rewire.core.RecurrenceType
import com.example.rewire.ui.screens.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for recurrence logic functions
 */
class RecurrenceLogicTest {

    @Test
    fun `createRecurrenceType - Daily returns Daily type`() {
        val result = createRecurrenceType("Daily")
        assertEquals(RecurrenceType.Daily, result)
    }

    @Test
    fun `createRecurrenceType - Weekly with no days returns Weekly type`() {
        val result = createRecurrenceType(
            category = "Weekly",
            selectedDaysOfWeek = emptySet()
        )
        assertEquals(RecurrenceType.Weekly, result)
    }

    @Test
    fun `createRecurrenceType - Weekly with selected days returns CustomWeekly type`() {
        val selectedDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        val result = createRecurrenceType(
            category = "Weekly",
            selectedDaysOfWeek = selectedDays
        )
        assertTrue(result is RecurrenceType.CustomWeekly)
        assertEquals(selectedDays.toList(), (result as RecurrenceType.CustomWeekly).daysOfWeek)
    }

    @Test
    fun `createRecurrenceType - Monthly by date returns MonthlyByDate type`() {
        val result = createRecurrenceType(
            category = "Monthly",
            monthlySubType = "day",
            dayOfMonth = 15
        )
        assertTrue(result is RecurrenceType.MonthlyByDate)
        assertEquals(15, (result as RecurrenceType.MonthlyByDate).dayOfMonth)
    }

    @Test
    fun `createRecurrenceType - Monthly by weekday returns MonthlyByWeekday type`() {
        val result = createRecurrenceType(
            category = "Monthly",
            monthlySubType = "weekday",
            weekOfMonth = 3,
            selectedDayOfWeek = DayOfWeek.FRIDAY
        )
        assertTrue(result is RecurrenceType.MonthlyByWeekday)
        val monthlyWeekday = result as RecurrenceType.MonthlyByWeekday
        assertEquals(3, monthlyWeekday.weekOfMonth)
        assertEquals(DayOfWeek.FRIDAY, monthlyWeekday.dayOfWeek)
    }

    @Test
    fun `createRecurrenceType - Quarterly by date returns QuarterlyByDate type`() {
        val result = createRecurrenceType(
            category = "Quarterly",
            quarterlySubType = "day",
            dayOfMonth = 10,
            monthOffset = 1
        )
        assertTrue(result is RecurrenceType.QuarterlyByDate)
        val quarterlyDate = result as RecurrenceType.QuarterlyByDate
        assertEquals(10, quarterlyDate.dayOfMonth)
        assertEquals(1, quarterlyDate.monthOffset)
    }

    @Test
    fun `createRecurrenceType - Quarterly by weekday returns QuarterlyByWeekday type`() {
        val result = createRecurrenceType(
            category = "Quarterly",
            quarterlySubType = "weekday",
            weekOfMonth = 2,
            selectedDayOfWeek = DayOfWeek.TUESDAY,
            monthOffset = 0
        )
        assertTrue(result is RecurrenceType.QuarterlyByWeekday)
        val quarterlyWeekday = result as RecurrenceType.QuarterlyByWeekday
        assertEquals(2, quarterlyWeekday.weekOfMonth)
        assertEquals(DayOfWeek.TUESDAY, quarterlyWeekday.dayOfWeek)
        assertEquals(0, quarterlyWeekday.monthOffset)
    }

    @Test
    fun `createRecurrenceType - Invalid category returns Daily as fallback`() {
        val result = createRecurrenceType("Invalid")
        assertEquals(RecurrenceType.Daily, result)
    }
}
