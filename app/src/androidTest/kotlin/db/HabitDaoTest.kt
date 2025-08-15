package com.example.rewire.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.rewire.db.dao.HabitDao
import com.example.rewire.db.entity.HabitEntity
import com.example.rewire.core.RecurrenceType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class HabitDaoTest {
    private lateinit var db: RewireDatabase
    private lateinit var habitDao: HabitDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, RewireDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        habitDao = db.habitDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun habitDao_crudOperations() {
        runBlocking {
            val habit = HabitEntity(
                id = 1L,
                name = "Test Habit",
                recurrence = RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 10,
                startDate = "2025-08-01"
            )
            val id = habitDao.insert(habit)
            val loaded = habitDao.getById(id)
            assertNotNull(loaded)
            assertEquals("Test Habit", loaded?.name)
            val updated = loaded!!.copy(name = "Updated Habit")
            habitDao.update(updated)
            val loaded2 = habitDao.getById(id)
            assertEquals("Updated Habit", loaded2?.name)
            habitDao.delete(updated)
            assertNull(habitDao.getById(id))
        }
    }
}
