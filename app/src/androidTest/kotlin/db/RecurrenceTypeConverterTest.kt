package com.example.rewire.db.converter

import com.example.rewire.core.RecurrenceType
import com.example.rewire.core.DayOfWeek
import org.junit.Assert.*
import org.junit.Test

class RecurrenceTypeConverterTest {
    private val converter = RecurrenceTypeConverter()

    @Test
    fun testDailySerialization() {
        val type = RecurrenceType.Daily
        val str = converter.fromRecurrenceType(type)
        val result = converter.toRecurrenceType(str)
        assertTrue(result is RecurrenceType.Daily)
    }

    @Test
    fun testWeeklySerialization() {
        val type = RecurrenceType.Weekly
        val str = converter.fromRecurrenceType(type)
        val result = converter.toRecurrenceType(str)
        assertTrue(result is RecurrenceType.Weekly)
    }

    @Test
    fun testMonthlyByDateSerialization() {
        val type = RecurrenceType.MonthlyByDate(15)
        val str = converter.fromRecurrenceType(type)
        val result = converter.toRecurrenceType(str)
        assertTrue(result is RecurrenceType.MonthlyByDate)
        assertEquals(15, (result as RecurrenceType.MonthlyByDate).dayOfMonth)
    }

    @Test
    fun testMonthlyByWeekdaySerialization() {
        val type = RecurrenceType.MonthlyByWeekday(2, DayOfWeek.FRIDAY)
        val str = converter.fromRecurrenceType(type)
        val result = converter.toRecurrenceType(str)
        assertTrue(result is RecurrenceType.MonthlyByWeekday)
        val res = result as RecurrenceType.MonthlyByWeekday
        assertEquals(2, res.weekOfMonth)
        assertEquals(DayOfWeek.FRIDAY, res.dayOfWeek)
    }

    @Test
    fun testQuarterlyByDateSerialization() {
        val type = RecurrenceType.QuarterlyByDate(5, 1)
        val str = converter.fromRecurrenceType(type)
        val result = converter.toRecurrenceType(str)
        assertTrue(result is RecurrenceType.QuarterlyByDate)
        val res = result as RecurrenceType.QuarterlyByDate
        assertEquals(5, res.dayOfMonth)
        assertEquals(1, res.monthOffset)
    }

    @Test
    fun testQuarterlyByWeekdaySerialization() {
        val type = RecurrenceType.QuarterlyByWeekday(1, DayOfWeek.MONDAY, 2)
        val str = converter.fromRecurrenceType(type)
        val result = converter.toRecurrenceType(str)
        assertTrue(result is RecurrenceType.QuarterlyByWeekday)
        val res = result as RecurrenceType.QuarterlyByWeekday
        assertEquals(1, res.weekOfMonth)
        assertEquals(DayOfWeek.MONDAY, res.dayOfWeek)
        assertEquals(2, res.monthOffset)
    }

    @Test
    fun testCustomWeeklySerialization_empty() {
        val type = RecurrenceType.CustomWeekly(emptyList())
        val str = converter.fromRecurrenceType(type)
        val result = converter.toRecurrenceType(str)
        assertTrue(result is RecurrenceType.CustomWeekly)
        assertTrue((result as RecurrenceType.CustomWeekly).daysOfWeek.isEmpty() || result.daysOfWeek == listOf(DayOfWeek.MONDAY))
    }

    @Test
    fun testCustomWeeklySerialization_single() {
        val type = RecurrenceType.CustomWeekly(listOf(DayOfWeek.WEDNESDAY))
        val str = converter.fromRecurrenceType(type)
        val result = converter.toRecurrenceType(str)
        assertTrue(result is RecurrenceType.CustomWeekly)
        assertEquals(listOf(DayOfWeek.WEDNESDAY), (result as RecurrenceType.CustomWeekly).daysOfWeek)
    }

    @Test
    fun testCustomWeeklySerialization_allDays() {
        val allDays = DayOfWeek.values().toList()
        val type = RecurrenceType.CustomWeekly(allDays)
        val str = converter.fromRecurrenceType(type)
        val result = converter.toRecurrenceType(str)
        assertTrue(result is RecurrenceType.CustomWeekly)
        assertEquals(allDays, (result as RecurrenceType.CustomWeekly).daysOfWeek)
    }

    @Test
    fun testInvalidTypeDeserializationDefaultsToDaily() {
        val invalidJson = "{\"type\":\"InvalidType\"}"
        val result = converter.toRecurrenceType(invalidJson)
        assertTrue(result is RecurrenceType.Daily)
    }

    @Test
    fun testMonthlyByDateOutOfRange() {
        val type = RecurrenceType.MonthlyByDate(32)
        val str = converter.fromRecurrenceType(type)
        val result = converter.toRecurrenceType(str)
        assertTrue(result is RecurrenceType.MonthlyByDate)
        assertEquals(32, (result as RecurrenceType.MonthlyByDate).dayOfMonth)
    }

    @Test
    fun testMonthlyByWeekdayOutOfRange() {
        val type = RecurrenceType.MonthlyByWeekday(5, DayOfWeek.SUNDAY)
        val str = converter.fromRecurrenceType(type)
        val result = converter.toRecurrenceType(str)
        assertTrue(result is RecurrenceType.MonthlyByWeekday)
        assertEquals(5, (result as RecurrenceType.MonthlyByWeekday).weekOfMonth)
    }
}
