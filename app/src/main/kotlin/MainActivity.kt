package com.example.rewire

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rewire.ui.navigation.AppNavHost
import com.example.rewire.ui.theme.RewireTheme
import com.example.rewire.manager.HabitManager
import com.example.rewire.repository.HabitRepository
import com.example.rewire.repository.HabitCompletionRepository
import com.example.rewire.repository.HabitNoteRepository
import com.example.rewire.repository.LabelRepository
import com.example.rewire.db.RewireDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create labels table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `labels` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `color` TEXT NOT NULL,
                `createdAt` TEXT
            )
        """)
        
        // Create unique index on label name
        db.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS `index_labels_name` 
            ON `labels` (`name`)
        """)
        
        // Create junction table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `habit_labels` (
                `habitId` INTEGER NOT NULL,
                `labelId` INTEGER NOT NULL,
                PRIMARY KEY(`habitId`, `labelId`),
                FOREIGN KEY(`habitId`) REFERENCES `habits`(`id`) ON DELETE CASCADE,
                FOREIGN KEY(`labelId`) REFERENCES `labels`(`id`) ON DELETE CASCADE
            )
        """)
        
        // Create indices on junction table
        db.execSQL("""
            CREATE INDEX IF NOT EXISTS `index_habit_labels_habitId` 
            ON `habit_labels` (`habitId`)
        """)
        
        db.execSQL("""
            CREATE INDEX IF NOT EXISTS `index_habit_labels_labelId` 
            ON `habit_labels` (`labelId`)
        """)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Initialize database and repositories
        val database = try {
            Room.databaseBuilder(
                applicationContext,
                RewireDatabase::class.java,
                "rewire_database"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
        } catch (e: IllegalStateException) {
            // Migration failed - this is a critical error
            Log.e("RewireDatabase", "Database migration failed: ${e.message}", e)
            Log.e("RewireDatabase", "This usually indicates a database schema issue. The app cannot continue safely.")
            // Re-throw to prevent app from continuing with corrupted database state
            // In production, you might want to show an error screen instead
            throw e
        } catch (e: Exception) {
            // Catch any other database initialization errors
            Log.e("RewireDatabase", "Database initialization failed: ${e.message}", e)
            throw e
        }
        
        // Initialize DAOs
        val habitDao = database.habitDao()
        val habitCompletionDao = database.habitCompletionDao()
        val habitNoteDao = database.habitNoteDao()
        val labelDao = database.labelDao()
        val habitLabelDao = database.habitLabelDao()
        
        // Initialize repositories
        val labelRepository = LabelRepository(labelDao, habitLabelDao)
        val habitRepository = HabitRepository(habitDao, labelRepository)
        val habitCompletionRepository = HabitCompletionRepository(habitCompletionDao)
        val habitNoteRepository = HabitNoteRepository(habitNoteDao)
        
        // Initialize managers
        val habitManager = HabitManager(
            habitRepository, 
            habitCompletionRepository, 
            habitNoteRepository,
            labelRepository
        )
        
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
}
