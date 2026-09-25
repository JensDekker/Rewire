package com.example.rewire.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.rewire.manager.HabitManagerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Reschedules habit reminder alarms after device reboot.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            return
        }

        Log.d(TAG, "Boot completed — reseeding habit notifications")
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val habitManager = HabitManagerFactory.create(context, withScheduler = true)
                habitManager.notificationScheduler?.rescheduleAllNotifications(habitManager)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to reseed notifications after boot", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}
