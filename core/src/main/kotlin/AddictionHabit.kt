package com.example.rewire.core

import java.time.*
import java.time.temporal.ChronoUnit

// --- Addiction Habit ---
data class AddictionHabit(
    var name: String,
    var startDate: LocalDate,
    var useLog: MutableMap<LocalDate, Int> = mutableMapOf(),
    var usagePlan: List<AbstinenceGoal> = emptyList(),
    var dailyNotes: MutableMap<LocalDate, String> = mutableMapOf()
) {
    fun logUse(date: LocalDate = LocalDate.now()) { useLog[date] = (useLog[date] ?: 0) + 1 }
    fun addNote(date: LocalDate, note: String) { dailyNotes[date] = note }
    fun getPeriodIndex(current: LocalDate, goal: AbstinenceGoal): Int =
        when (goal.period) {
            GoalPeriod.WEEKLY -> ChronoUnit.WEEKS.between(startDate, current).toInt()
            GoalPeriod.MONTHLY -> ChronoUnit.MONTHS.between(startDate.withDayOfMonth(1), current.withDayOfMonth(1)).toInt()
        }
    fun getCurrentGoal(current: LocalDate): AbstinenceGoal? {
        var index = 0
        for (goal in usagePlan) {
            val repeat = goal.repeatCount
            if (getPeriodIndex(current, goal) < index + repeat) return goal
            index += repeat
        }
        return usagePlan.lastOrNull()
    }
    fun countRestDaysInPeriod(current: LocalDate, goal: AbstinenceGoal): Int {
        val start = when (goal.period) {
            GoalPeriod.WEEKLY -> current.with(DayOfWeek.MONDAY)
            GoalPeriod.MONTHLY -> current.withDayOfMonth(1)
        }
        val end = when (goal.period) {
            GoalPeriod.WEEKLY -> start.plusDays(6)
            GoalPeriod.MONTHLY -> start.withDayOfMonth(start.lengthOfMonth())
        }
        return (0L..ChronoUnit.DAYS.between(start, end)).count { i ->
            val date = start.plusDays(i)
            (useLog[date] ?: 0) == 0
        }
    }
    fun isGoalMet(current: LocalDate): Boolean {
        val goal = getCurrentGoal(current) ?: return false
        return countRestDaysInPeriod(current, goal) >= goal.value
    }
    fun getDailyUseSummary(): List<Pair<LocalDate, Int>> = useLog.entries.sortedBy { it.key }.map { it.toPair() }

    fun editNote(date: LocalDate, newNote: String) {
        if (dailyNotes.containsKey(date)) {
            dailyNotes[date] = newNote
        }
    }

    fun deleteNote(date: LocalDate) {
        dailyNotes.remove(date)
    }

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
}
