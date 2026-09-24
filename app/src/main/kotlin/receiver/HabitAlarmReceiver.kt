package com.example.rewire.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.rewire.manager.HabitManagerFactory
import com.example.rewire.manager.HabitNotificationManager
import com.example.rewire.util.NotificationConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Fires when a scheduled habit reminder alarm goes off. Shows the notification
 * unless the habit is no longer due or already completed for that date.
 */
class HabitAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != NotificationConstants.ACTION_SHOW_HABIT_REMINDER) return

        val habitId = intent.getLongExtra(NotificationConstants.EXTRA_HABIT_ID, -1L)
        val date = intent.getStringExtra(NotificationConstants.EXTRA_DATE) ?: return
        if (habitId < 0L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val habitManager = HabitManagerFactory.create(context, withScheduler = false)
                val stillDue = habitManager.getHabitsDueOn(date).any { it.id == habitId }
                if (!stillDue) {
                    Log.d(TAG, "Skipping reminder: habit $habitId not due on $date")
                    return@launch
                }
                if (habitManager.isHabitCompletedForDate(habitId, date)) {
                    Log.d(TAG, "Skipping reminder: habit $habitId already completed on $date")
                    return@launch
                }
                val habit = habitManager.getHabits().find { it.id == habitId } ?: return@launch
                val labels = habitManager.getLabelsForHabit(habitId)
                HabitNotificationManager(context.applicationContext)
                    .showHabitReminder(habit, date, labels)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to show habit reminder", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val TAG = "HabitAlarmReceiver"
    }
}
