package com.example.rewire.core


import com.example.rewire.core.RecurrenceType

/**
 * value = permitted uses per recurrence (e.g. 2 uses per WEEKLY, or 1 per WEEKENDS)
 * recurrence = when usage is allowed (see RecurrenceType)
 * repeatCount = how many recurrences this plan applies for
 */
data class AbstinenceGoal(
    val id: Long = 0,
    val addictionId: Long = 0,
    val recurrence: RecurrenceType,
    val value: Int,
    val repeatCount: Int = 1
)
