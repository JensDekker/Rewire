package com.example.rewire.util

object NotificationConstants {
    const val CHANNEL_ID = "habit_reminders"
    const val CHANNEL_NAME = "Habit Reminders"
    const val CHANNEL_DESCRIPTION = "Notifications for your habit reminders"

    const val ACTION_SHOW_HABIT_REMINDER = "com.example.rewire.ACTION_SHOW_HABIT_REMINDER"
    const val ACTION_COMPLETE_HABIT = "com.example.rewire.ACTION_COMPLETE_HABIT"
    const val ACTION_ADD_NOTE = "com.example.rewire.ACTION_ADD_NOTE"
    const val ACTION_RESEED_NOTIFICATIONS = "com.example.rewire.ACTION_RESEED_NOTIFICATIONS"

    const val EXTRA_HABIT_ID = "habit_id"
    const val EXTRA_HABIT_NAME = "habit_name"
    const val EXTRA_DATE = "date"
    const val EXTRA_NOTIFICATION_ID = "notification_id"

    const val REMOTE_INPUT_NOTE_KEY = "habit_note_reply"

    /** How many days ahead to schedule habit reminder alarms. */
    const val SCHEDULE_HORIZON_DAYS = 5

    fun getNotificationId(habitId: Long, date: String): Int =
        "habit_${habitId}_$date".hashCode()
}
