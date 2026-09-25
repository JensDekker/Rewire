package com.example.rewire.manager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.rewire.db.entity.HabitEntity
import com.example.rewire.receiver.HabitAlarmReceiver
import com.example.rewire.util.NotificationConstants
import com.example.rewire.util.NotificationPermissionHelper
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Schedules per-habit reminder alarms with a [NotificationConstants.SCHEDULE_HORIZON_DAYS]-day horizon.
 * Prefers exact / alarm-clock delivery; falls back to inexact when exact alarms are blocked.
 */
class HabitNotificationScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    suspend fun scheduleNotificationsForHabit(habit: HabitEntity, habitManager: HabitManager) {
        cancelNotificationsForHabit(habit.id)

        val preferredTime = parsePreferredTime(habit.preferredTime) ?: return
        val today = LocalDate.now()
        val horizonEnd = today.plusDays(NotificationConstants.SCHEDULE_HORIZON_DAYS.toLong())
        var day = today
        while (!day.isAfter(horizonEnd)) {
            val dateStr = day.toString()
            val due = habitManager.getHabitsDueOn(dateStr).any { it.id == habit.id }
            if (due && !habitManager.isHabitCompletedForDate(habit.id, dateStr)) {
                val fireAt = LocalDateTime.of(day, preferredTime)
                if (fireAt.isAfter(LocalDateTime.now())) {
                    scheduleAlarm(habit, dateStr, fireAt)
                }
            }
            day = day.plusDays(1)
        }
    }

    fun cancelNotificationsForHabit(habitId: Long) {
        val today = LocalDate.now()
        val horizonEnd = today.plusDays(NotificationConstants.SCHEDULE_HORIZON_DAYS.toLong() + 2)
        var day = today.minusDays(1)
        while (!day.isAfter(horizonEnd)) {
            cancelAlarm(habitId, day.toString())
            day = day.plusDays(1)
        }
    }

    suspend fun rescheduleAllNotifications(habitManager: HabitManager) {
        val habits = habitManager.getHabits()
        for (habit in habits) {
            scheduleNotificationsForHabit(habit, habitManager)
        }
    }

    private fun scheduleAlarm(habit: HabitEntity, date: String, fireAt: LocalDateTime) {
        val am = alarmManager ?: return
        val triggerAtMillis = fireAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val operation = alarmPendingIntent(habit.id, habit.name, date)

        try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                    // AlarmClock is the most reliable user-visible exact path and does not
                    // require SCHEDULE_EXACT_ALARM on API 31+.
                    val showIntent = PendingIntent.getActivity(
                        context,
                        NotificationConstants.getNotificationId(habit.id, date) xor 0x5A17,
                        Intent(context, com.example.rewire.MainActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    am.setAlarmClock(
                        AlarmManager.AlarmClockInfo(triggerAtMillis, showIntent),
                        operation
                    )
                }
                NotificationPermissionHelper.canScheduleExactAlarms(context) &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, operation)
                }
                else -> {
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, operation)
                    Log.w(TAG, "Exact alarms unavailable; scheduled inexact for habit ${habit.id} on $date")
                }
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "Exact alarm blocked; falling back to inexact for habit ${habit.id}", e)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, operation)
                } else {
                    @Suppress("DEPRECATION")
                    am.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, operation)
                }
            } catch (fallback: Exception) {
                Log.e(TAG, "Failed to schedule alarm for habit ${habit.id}", fallback)
            }
        }
    }

    private fun cancelAlarm(habitId: Long, date: String) {
        val am = alarmManager ?: return
        am.cancel(alarmPendingIntent(habitId, "", date))
    }

    private fun alarmPendingIntent(habitId: Long, habitName: String, date: String): PendingIntent {
        val intent = Intent(context, HabitAlarmReceiver::class.java).apply {
            action = NotificationConstants.ACTION_SHOW_HABIT_REMINDER
            putExtra(NotificationConstants.EXTRA_HABIT_ID, habitId)
            putExtra(NotificationConstants.EXTRA_HABIT_NAME, habitName)
            putExtra(NotificationConstants.EXTRA_DATE, date)
        }
        return PendingIntent.getBroadcast(
            context,
            NotificationConstants.getNotificationId(habitId, date),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val TAG = "HabitNotifScheduler"

        fun parsePreferredTime(raw: String): LocalTime? = try {
            LocalTime.parse(raw)
        } catch (_: Exception) {
            Log.w(TAG, "Unparseable preferredTime: $raw")
            null
        }

        /**
         * Pure helper for tests: dates in [from, from+horizonDays] that belong in [dueDates]
         * and are not completed, with fire time still in the future relative to [now].
         */
        fun selectScheduleDates(
            dueDates: Set<LocalDate>,
            completedDates: Set<LocalDate>,
            preferredTime: LocalTime,
            now: LocalDateTime,
            horizonDays: Int = NotificationConstants.SCHEDULE_HORIZON_DAYS
        ): List<LocalDate> {
            val from = now.toLocalDate()
            val end = from.plusDays(horizonDays.toLong())
            val result = mutableListOf<LocalDate>()
            var day = from
            while (!day.isAfter(end)) {
                if (day in dueDates && day !in completedDates) {
                    val fireAt = LocalDateTime.of(day, preferredTime)
                    if (fireAt.isAfter(now)) {
                        result.add(day)
                    }
                }
                day = day.plusDays(1)
            }
            return result
        }
    }
}
