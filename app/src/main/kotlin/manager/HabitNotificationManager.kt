package com.example.rewire.manager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.example.rewire.MainActivity
import com.example.rewire.R
import com.example.rewire.db.entity.HabitEntity
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.receiver.HabitNotificationReceiver
import com.example.rewire.util.NotificationConstants
import com.example.rewire.util.NotificationPermissionHelper

class HabitNotificationManager(private val context: Context) {
    private val notificationManager = NotificationManagerCompat.from(context)

    fun showHabitReminder(
        habit: HabitEntity,
        date: String,
        labels: List<LabelEntity> = emptyList()
    ) {
        if (!NotificationPermissionHelper.hasNotificationPermission(context)) return

        val notificationId = NotificationConstants.getNotificationId(habit.id, date)
        val builder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_add_notes)
            .setContentTitle(habit.name)
            .setContentText("Time for your habit: ${habit.name}")
            .setColor(resolveColor(labels))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(createOpenAppPendingIntent(habit.id, date, notificationId))
            .addAction(createCompleteAction(habit.id, date, notificationId))
            .addAction(createAddNoteAction(habit.id, habit.name, date, notificationId))

        notificationManager.notify(notificationId, builder.build())
    }

    fun cancel(habitId: Long, date: String) {
        notificationManager.cancel(NotificationConstants.getNotificationId(habitId, date))
    }

    private fun createCompleteAction(
        habitId: Long,
        date: String,
        notificationId: Int
    ): NotificationCompat.Action {
        val intent = Intent(context, HabitNotificationReceiver::class.java).apply {
            action = NotificationConstants.ACTION_COMPLETE_HABIT
            putExtra(NotificationConstants.EXTRA_HABIT_ID, habitId)
            putExtra(NotificationConstants.EXTRA_DATE, date)
            putExtra(NotificationConstants.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId xor 0xC0FFEE,
            intent,
            pendingIntentFlags(mutable = false)
        )
        return NotificationCompat.Action.Builder(
            R.drawable.ic_edit_square,
            "Complete",
            pendingIntent
        ).build()
    }

    private fun createAddNoteAction(
        habitId: Long,
        habitName: String,
        date: String,
        notificationId: Int
    ): NotificationCompat.Action {
        val remoteInput = RemoteInput.Builder(NotificationConstants.REMOTE_INPUT_NOTE_KEY)
            .setLabel("Add note")
            .build()

        val intent = Intent(context, HabitNotificationReceiver::class.java).apply {
            action = NotificationConstants.ACTION_ADD_NOTE
            putExtra(NotificationConstants.EXTRA_HABIT_ID, habitId)
            putExtra(NotificationConstants.EXTRA_HABIT_NAME, habitName)
            putExtra(NotificationConstants.EXTRA_DATE, date)
            putExtra(NotificationConstants.EXTRA_NOTIFICATION_ID, notificationId)
        }
        // RemoteInput requires a mutable PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId xor 0xADD70E,
            intent,
            pendingIntentFlags(mutable = true)
        )
        return NotificationCompat.Action.Builder(
            R.drawable.ic_add_notes,
            "Add note",
            pendingIntent
        )
            .addRemoteInput(remoteInput)
            .setAllowGeneratedReplies(true)
            .build()
    }

    private fun createOpenAppPendingIntent(
        habitId: Long,
        date: String,
        notificationId: Int
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(NotificationConstants.EXTRA_HABIT_ID, habitId)
            putExtra(NotificationConstants.EXTRA_DATE, date)
        }
        return PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            pendingIntentFlags(mutable = false)
        )
    }

    private fun pendingIntentFlags(mutable: Boolean): Int {
        val mutability = when {
            mutable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> PendingIntent.FLAG_MUTABLE
            mutable -> PendingIntent.FLAG_UPDATE_CURRENT
            else -> PendingIntent.FLAG_IMMUTABLE
        }
        return PendingIntent.FLAG_UPDATE_CURRENT or mutability
    }

    private fun resolveColor(labels: List<LabelEntity>): Int {
        if (labels.isEmpty()) return Color.parseColor("#5B8C5A")
        return try {
            Color.parseColor(labels.first().color)
        } catch (_: IllegalArgumentException) {
            Color.parseColor("#5B8C5A")
        }
    }
}
