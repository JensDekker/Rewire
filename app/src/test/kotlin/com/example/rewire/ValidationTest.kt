package com.example.rewire

import com.example.rewire.core.DayOfWeek
import com.example.rewire.ui.screens.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for validation system
 */
class ValidationTest {

    @Test
    fun `validateRecurrenceParameters - Daily is always valid`() {
        val result = validateRecurrenceParameters("Daily")
        assertTrue(result.isValid)
        assertNull(result.message)
    }

    @Test
    fun `validateRecurrenceParameters - Weekly with no days shows default message`() {
        val result = validateRecurrenceParameters("Weekly", selectedDaysOfWeek = emptySet())
        assertTrue(result.isValid)
        assertEquals("No days selected - will default to Monday", result.message)
    }

    @Test
    fun `validateRecurrenceParameters - Weekly with selected days is valid`() {
        val selectedDays = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)
        val result = validateRecurrenceParameters("Weekly", selectedDaysOfWeek = selectedDays)
        assertTrue(result.isValid)
        assertNull(result.message)
    }

    @Test
    fun `validateRecurrenceParameters - Monthly by date with valid day is valid`() {
        val result = validateRecurrenceParameters(
            category = "Monthly",
            monthlySubType = "day",
            dayOfMonth = 15
        )
        assertTrue(result.isValid)
        assertNull(result.message)
    }

    @Test
    fun `validateRecurrenceParameters - Monthly by date with invalid day is invalid`() {
        val result = validateRecurrenceParameters(
            category = "Monthly",
            monthlySubType = "day",
            dayOfMonth = 32
        )
        assertFalse(result.isValid)
        assertEquals("Day must be at most 31", result.message)
    }

    @Test
    fun `validateRecurrenceParameters - Monthly by date with day 0 is invalid`() {
        val result = validateRecurrenceParameters(
            category = "Monthly",
            monthlySubType = "day",
            dayOfMonth = 0
        )
        assertFalse(result.isValid)
        assertEquals("Day must be at least 1", result.message)
    }

    @Test
    fun `validateRecurrenceParameters - Monthly by weekday with valid parameters is valid`() {
        val result = validateRecurrenceParameters(
            category = "Monthly",
            monthlySubType = "weekday",
            weekOfMonth = 3,
            selectedDayOfWeek = DayOfWeek.FRIDAY
        )
        assertTrue(result.isValid)
        assertNull(result.message)
    }

    @Test
    fun `validateRecurrenceParameters - Monthly by weekday with invalid week is invalid`() {
        val result = validateRecurrenceParameters(
            category = "Monthly",
            monthlySubType = "weekday",
            weekOfMonth = 5,
            selectedDayOfWeek = DayOfWeek.FRIDAY
        )
        assertFalse(result.isValid)
        assertEquals("Week must be at most 4th", result.message)
    }

    @Test
    fun `validateRecurrenceParameters - Monthly by weekday with week 0 is invalid`() {
        val result = validateRecurrenceParameters(
            category = "Monthly",
            monthlySubType = "weekday",
            weekOfMonth = 0,
            selectedDayOfWeek = DayOfWeek.FRIDAY
        )
        assertFalse(result.isValid)
        assertEquals("Week must be at least 1st", result.message)
    }

    @Test
    fun `validateRecurrenceParameters - Quarterly by date with valid parameters is valid`() {
        val result = validateRecurrenceParameters(
            category = "Quarterly",
            quarterlySubType = "day",
            dayOfMonth = 15,
            monthOffset = 1
        )
        assertTrue(result.isValid)
        assertNull(result.message)
    }

    @Test
    fun `validateRecurrenceParameters - Quarterly by date with invalid day is invalid`() {
        val result = validateRecurrenceParameters(
            category = "Quarterly",
            quarterlySubType = "day",
            dayOfMonth = 35,
            monthOffset = 1
        )
        assertFalse(result.isValid)
        assertEquals("Day must be at most 31", result.message)
    }

    @Test
    fun `validateRecurrenceParameters - Quarterly by date with invalid month offset is invalid`() {
        val result = validateRecurrenceParameters(
            category = "Quarterly",
            quarterlySubType = "day",
            dayOfMonth = 15,
            monthOffset = 3
        )
        assertFalse(result.isValid)
        assertEquals("Month offset must be at most 2", result.message)
    }

    @Test
    fun `validateRecurrenceParameters - Quarterly by weekday with valid parameters is valid`() {
        val result = validateRecurrenceParameters(
            category = "Quarterly",
            quarterlySubType = "weekday",
            weekOfMonth = 2,
            selectedDayOfWeek = DayOfWeek.TUESDAY,
            monthOffset = 0
        )
        assertTrue(result.isValid)
        assertNull(result.message)
    }

    @Test
    fun `validateRecurrenceParameters - Invalid monthly subtype is invalid`() {
        val result = validateRecurrenceParameters(
            category = "Monthly",
            monthlySubType = "invalid",
            dayOfMonth = 15
        )
        assertFalse(result.isValid)
        assertEquals("Invalid monthly subtype", result.message)
    }

    @Test
    fun `validateRecurrenceParameters - Invalid quarterly subtype is invalid`() {
        val result = validateRecurrenceParameters(
            category = "Quarterly",
            quarterlySubType = "invalid",
            dayOfMonth = 15,
            monthOffset = 1
        )
        assertFalse(result.isValid)
        assertEquals("Invalid quarterly subtype", result.message)
    }

    @Test
    fun `validateRecurrenceParameters - Invalid category is invalid`() {
        val result = validateRecurrenceParameters("InvalidCategory")
        assertFalse(result.isValid)
        assertEquals("Invalid recurrence category", result.message)
    }

    @Test
    fun `getValidationMessage - Returns correct message for valid weekly`() {
        val message = getValidationMessage("Weekly", selectedDaysOfWeek = emptySet())
        assertEquals("No days selected - will default to Monday", message)
    }

    @Test
    fun `getValidationMessage - Returns null for valid configuration`() {
        val message = getValidationMessage(
            "Monthly",
            monthlySubType = "day",
            dayOfMonth = 15
        )
        assertNull(message)
    }

    @Test
    fun `getValidationMessage - Returns error message for invalid configuration`() {
        val message = getValidationMessage(
            "Monthly",
            monthlySubType = "day",
            dayOfMonth = 35
        )
        assertEquals("Day must be at most 31", message)
    }
}
