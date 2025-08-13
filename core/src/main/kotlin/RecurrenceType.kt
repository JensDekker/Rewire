package com.example.rewire.core

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

sealed class RecurrenceType {
    object Daily : RecurrenceType()
    object Weekly : RecurrenceType()
    data class MonthlyByDate(val dayOfMonth: Int) : RecurrenceType() // e.g., 11th of every month
    data class MonthlyByWeekday(val weekOfMonth: Int, val dayOfWeek: DayOfWeek) : RecurrenceType() // e.g., 3rd Friday

    data class QuarterlyByDate(val dayOfMonth: Int, val monthOffset: Int) : RecurrenceType() // e.g., 11th of every 3rd month, monthOffset=0 for Jan/Apr/Jul/Oct
    data class QuarterlyByWeekday(val weekOfMonth: Int, val dayOfWeek: DayOfWeek, val monthOffset: Int) : RecurrenceType() // e.g., 3rd Friday of every 3rd month

    data class CustomWeekly(val daysOfWeek: List<DayOfWeek>) : RecurrenceType() // e.g., Mondays and Thursdays
}
