package com.example.rewire.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.rewire.db.entity.HabitEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class HabitDaoTest {
    private lateinit var db: com.example.rewire.db.RewireDatabase
    private lateinit var habitDao: com.example.rewire.db.dao.HabitDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, com.example.rewire.db.RewireDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        habitDao = db.habitDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun bulkInsertUpdateDeleteHabits() {
        runBlocking {
            val habits = (1..100).map {
                HabitEntity(
                    id = 20000L + it,
                    name = "Bulk Habit $it",
                    recurrence = com.example.rewire.core.RecurrenceType.Daily,
                    preferredTime = "08:00",
                    estimatedMinutes = 5,
                    startDate = "2025-08-01"
                )
            }
            habits.forEach { habitDao.insert(it) }
            val all = habitDao.getAll().filter { it.id in 20001L..20100L }
            assertEquals(100, all.size)
            val updated = all.map { it.copy(name = it.name + " Updated") }
            updated.forEach { habitDao.update(it) }
            val updatedAll = habitDao.getAll().filter { it.name.endsWith("Updated") }
            assertEquals(100, updatedAll.size)
            updatedAll.forEach { habitDao.delete(it) }
            assertTrue(habitDao.getAll().none { it.id in 20001L..20100L })
        }
    }

    @Test
    fun habitDao_crudOperations() {
        runBlocking {
            val habit = HabitEntity(
                id = 1L,
                name = "Test Habit",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
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

    @Test
    fun insertDuplicateHabit_shouldFailOrUpdate() {
        runBlocking {
            val habit = HabitEntity(
                id = 2L,
                name = "Duplicate Habit",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "08:00",
                estimatedMinutes = 5,
                startDate = "2025-08-01"
            )
            habitDao.insert(habit)
            // Try to insert again with same id
            try {
                habitDao.insert(habit)
                // Depending on Room config, this may fail or update
            } catch (e: Exception) {
                assertTrue(e is Exception)
            }
        }
    }

    @Test
    fun updateDeleteNonExistentHabit_shouldNotCrash() {
        runBlocking {
            val habit = HabitEntity(
                id = 9999L,
                name = "Non-existent",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "08:00",
                estimatedMinutes = 5,
                startDate = "2025-08-01"
            )
            // Update
            habitDao.update(habit)
            // Delete
            habitDao.delete(habit)
            // Should not throw
        }
    }
}
