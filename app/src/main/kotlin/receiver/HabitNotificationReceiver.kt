package com.example.rewire.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.RemoteInput
import com.example.rewire.manager.HabitManagerFactory
import com.example.rewire.manager.HabitNotificationManager
import com.example.rewire.util.NotificationConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Handles notification actions: Complete (background) and Add note (RemoteInput).
 * Both persist without requiring the full app UI.
 */
class HabitNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra(NotificationConstants.EXTRA_HABIT_ID, -1L)
        val date = intent.getStringExtra(NotificationConstants.EXTRA_DATE) ?: return
        if (habitId < 0L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val habitManager = HabitManagerFactory.create(context, withScheduler = true)
                val notificationManager = HabitNotificationManager(context.applicationContext)

                when (intent.action) {
                    NotificationConstants.ACTION_COMPLETE_HABIT -> {
                        habitManager.completeHabit(habitId, date)
                        notificationManager.cancel(habitId, date)
                        // Cancel today's alarm if any residual, and avoid re-fire
                        habitManager.getHabits().find { it.id == habitId }?.let { habit ->
                            // Reschedule remaining horizon without this completed day
                            habitManager.notificationScheduler
                                ?.scheduleNotificationsForHabit(habit, habitManager)
                        }
                    }

                    NotificationConstants.ACTION_ADD_NOTE -> {
                        val results = RemoteInput.getResultsFromIntent(intent)
                        val noteText = results
                            ?.getCharSequence(NotificationConstants.REMOTE_INPUT_NOTE_KEY)
                            ?.toString()
                            ?.trim()
                            .orEmpty()
                        if (noteText.isNotEmpty()) {
                            habitManager.upsertNoteForDate(habitId, noteText, date)
                        }
                        notificationManager.cancel(habitId, date)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to handle notification action ${intent.action}", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val TAG = "HabitNotifReceiver"
    }
}
