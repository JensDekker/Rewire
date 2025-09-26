package com.example.rewire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.rewire.ui.screens.HabitHomeScreen
import com.example.rewire.ui.theme.RewireTheme
import com.example.rewire.manager.HabitManager
import com.example.rewire.repository.HabitRepository
import com.example.rewire.repository.HabitCompletionRepository
import com.example.rewire.repository.HabitNoteRepository
import com.example.rewire.db.RewireDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database and repositories
        val database = Room.databaseBuilder(
            applicationContext,
            RewireDatabase::class.java,
            "rewire_database"
        ).build()
        
        val habitRepository = HabitRepository(database.habitDao())
        val habitCompletionRepository = HabitCompletionRepository(database.habitCompletionDao())
        val habitNoteRepository = HabitNoteRepository(database.habitNoteDao())
        val habitManager = HabitManager(habitRepository, habitCompletionRepository, habitNoteRepository)
        
        setContent {
            RewireTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    HabitHomeScreen(habitManager = habitManager)
                }
            }
        }
    }
}
