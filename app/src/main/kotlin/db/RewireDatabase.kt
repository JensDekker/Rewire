package com.example.rewire.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rewire.db.dao.AbstinenceGoalDao
import com.example.rewire.db.dao.AddictionHabitDao
import com.example.rewire.db.dao.AddictionNoteDao
import com.example.rewire.db.dao.HabitCompletionDao
import com.example.rewire.db.dao.HabitDao
import com.example.rewire.db.dao.HabitLabelDao
import com.example.rewire.db.dao.HabitNoteDao
import com.example.rewire.db.dao.LabelDao
import com.example.rewire.db.entity.AbstinenceGoalEntity
import com.example.rewire.db.entity.AddictionHabitEntity
import com.example.rewire.db.entity.AddictionNoteEntity
import com.example.rewire.db.entity.HabitCompletion
import com.example.rewire.db.entity.HabitEntity
import com.example.rewire.db.entity.HabitLabelCrossRef
import com.example.rewire.db.entity.HabitNoteEntity
import com.example.rewire.db.entity.LabelEntity

@Database(
    entities = [
        HabitEntity::class,
        AddictionHabitEntity::class,
        AbstinenceGoalEntity::class,
        HabitNoteEntity::class,
        AddictionNoteEntity::class,
        HabitCompletion::class,
        LabelEntity::class,
        HabitLabelCrossRef::class
    ],
    version = 2,
    exportSchema = true
)
abstract class RewireDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun addictionHabitDao(): AddictionHabitDao
    abstract fun abstinenceGoalDao(): AbstinenceGoalDao
    abstract fun habitNoteDao(): HabitNoteDao
    abstract fun addictionNoteDao(): AddictionNoteDao
    abstract fun habitCompletionDao(): HabitCompletionDao
    abstract fun labelDao(): LabelDao
    abstract fun habitLabelDao(): HabitLabelDao

    companion object {
        private const val DB_NAME = "rewire_database"

        @Volatile
        private var instance: RewireDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `labels` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `color` TEXT NOT NULL,
                        `createdAt` TEXT
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_labels_name` ON `labels` (`name`)"
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `habit_labels` (
                        `habitId` INTEGER NOT NULL,
                        `labelId` INTEGER NOT NULL,
                        PRIMARY KEY(`habitId`, `labelId`),
                        FOREIGN KEY(`habitId`) REFERENCES `habits`(`id`) ON DELETE CASCADE,
                        FOREIGN KEY(`labelId`) REFERENCES `labels`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_habit_labels_habitId` ON `habit_labels` (`habitId`)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_habit_labels_labelId` ON `habit_labels` (`labelId`)"
                )
            }
        }

        fun getInstance(context: Context): RewireDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    RewireDatabase::class.java,
                    DB_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
