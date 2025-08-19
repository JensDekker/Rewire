package com.example.rewire.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rewire.db.dao.HabitDao
import com.example.rewire.db.dao.AddictionHabitDao
import com.example.rewire.db.dao.AbstinenceGoalDao
import com.example.rewire.db.dao.HabitNoteDao
import com.example.rewire.db.dao.AddictionNoteDao
import com.example.rewire.db.entity.HabitEntity
import com.example.rewire.db.entity.AddictionHabitEntity
import com.example.rewire.db.entity.AbstinenceGoalEntity
import com.example.rewire.db.entity.HabitNoteEntity
import com.example.rewire.db.entity.AddictionNoteEntity

@Database(
    entities = [
        HabitEntity::class,
        AddictionHabitEntity::class,
        AbstinenceGoalEntity::class,
        HabitNoteEntity::class,
        AddictionNoteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RewireDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun addictionHabitDao(): AddictionHabitDao
    abstract fun abstinenceGoalDao(): AbstinenceGoalDao
    abstract fun habitNoteDao(): HabitNoteDao
    abstract fun addictionNoteDao(): AddictionNoteDao
    abstract fun habitCompletionDao(): com.example.rewire.db.dao.HabitCompletionDao
}
