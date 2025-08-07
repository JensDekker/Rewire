package com.example.rewire.core

import java.time.*
import java.time.temporal.ChronoUnit

// --- Addiction Habit ---
data class AddictionHabit(
    var name: String,
    var startDate: LocalDate,
    var useLog: MutableMap<LocalDate, Int> = mutableMapOf(),
    var usagePlan: List<AbstinenceGoal> = emptyList(),
    var useTimeLog: MutableMap<LocalDate, MutableList<java.time.LocalTime?>> = mutableMapOf()
) {
    fun logUse(date: LocalDate = LocalDate.now(), time: java.time.LocalTime? = null) {
        useLog[date] = (useLog[date] ?: 0) + 1
        if (time != null) {
            useTimeLog.getOrPut(date) { mutableListOf() }.add(time)
        }
    }
    // Note management is now handled by AddictionManager using the shared Note class
    fun getRecurrenceIndex(current: LocalDate, goal: AbstinenceGoal): Int =
        when (goal.recurrence) {
            RecurrenceType.WEEKLY, RecurrenceType.WEEKDAYS, RecurrenceType.WEEKENDS, RecurrenceType.CUSTOM_WEEKLY -> ChronoUnit.WEEKS.between(startDate, current).toInt()
            RecurrenceType.MONTHLY -> ChronoUnit.MONTHS.between(startDate.withDayOfMonth(1), current.withDayOfMonth(1)).toInt()
            RecurrenceType.DAILY -> ChronoUnit.DAYS.between(startDate, current).toInt()
            RecurrenceType.QUARTERLY -> ChronoUnit.MONTHS.between(startDate.withDayOfMonth(1), current.withDayOfMonth(1)).toInt() / 3
        }
    fun getCurrentGoal(current: LocalDate): AbstinenceGoal? {
        var index = 0
        for (goal in usagePlan) {
            val repeat = goal.repeatCount
            if (getRecurrenceIndex(current, goal) < index + repeat) return goal
            index += repeat
        }
        return usagePlan.lastOrNull()
    }

    /**
     * Returns the number of uses in the current period for the given goal.
     */
    fun countUsesInRecurrence(current: LocalDate, goal: AbstinenceGoal): Int {
        // For each recurrence type, determine the window and sum uses in that window
        val dates = when (goal.recurrence) {
            RecurrenceType.DAILY -> listOf(current)
            RecurrenceType.WEEKLY, RecurrenceType.WEEKDAYS, RecurrenceType.WEEKENDS, RecurrenceType.CUSTOM_WEEKLY -> {
                val start = current.with(java.time.DayOfWeek.MONDAY)
                val end = start.plusDays(6)
                (0L..ChronoUnit.DAYS.between(start, end)).map { start.plusDays(it) }
            }
            RecurrenceType.MONTHLY -> {
                val start = current.withDayOfMonth(1)
                val end = start.withDayOfMonth(start.lengthOfMonth())
                (0L..ChronoUnit.DAYS.between(start, end)).map { start.plusDays(it) }
            }
            RecurrenceType.QUARTERLY -> {
                val start = current.withDayOfMonth(1).withMonth(((current.monthValue - 1) / 3) * 3 + 1)
                val end = start.plusMonths(2).withDayOfMonth(start.plusMonths(2).lengthOfMonth())
                (0L..ChronoUnit.DAYS.between(start, end)).map { start.plusDays(it) }
            }
        }
        return dates.sumOf { useLog[it] ?: 0 }
    }

    /**
     * Returns true if the number of uses in the period is less than or equal to the permitted value.
     */
    fun isGoalMet(current: LocalDate): Boolean {
        val goal = getCurrentGoal(current) ?: return false
        return countUsesInRecurrence(current, goal) <= goal.value
    }
    fun getDailyUseSummary(): List<Pair<LocalDate, Int>> = useLog.entries.sortedBy { it.key }.map { it.toPair() }
    fun getTimeLogForDate(date: LocalDate): List<java.time.LocalTime?> = useTimeLog[date] ?: emptyList()

    fun addUsagePlanItem(goal: AbstinenceGoal) {
        usagePlan = usagePlan + goal
    }

    fun editUsagePlanItem(index: Int, updatedGoal: AbstinenceGoal) {
        if (index in usagePlan.indices) {
            usagePlan = usagePlan.toMutableList().apply { this[index] = updatedGoal }
        }
    }

    fun deleteUsagePlanItem(index: Int) {
        if (index in usagePlan.indices) {
            usagePlan = usagePlan.toMutableList().apply { removeAt(index) }
        }
    }

    // Note management is now handled by AddictionManager using the shared Note class
}
