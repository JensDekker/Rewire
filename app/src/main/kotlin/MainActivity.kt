package com.example.rewire

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.rewire.manager.HabitManagerFactory
import com.example.rewire.ui.navigation.AppNavHost
import com.example.rewire.ui.theme.RewireTheme
import com.example.rewire.util.NotificationConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        createNotificationChannel()

        val habitManager = try {
            HabitManagerFactory.create(applicationContext, withScheduler = true)
        } catch (e: Exception) {
            Log.e("RewireDatabase", "Database initialization failed: ${e.message}", e)
            throw e
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                habitManager.notificationScheduler?.rescheduleAllNotifications(habitManager)
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to reseed habit notifications", e)
            }
        }

        setContent {
            RewireTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AppNavHost(habitManager = habitManager)
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationConstants.CHANNEL_ID,
                NotificationConstants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = NotificationConstants.CHANNEL_DESCRIPTION
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
