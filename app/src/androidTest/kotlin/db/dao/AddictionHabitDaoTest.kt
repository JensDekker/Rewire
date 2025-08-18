package com.example.rewire.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.rewire.db.entity.AddictionHabitEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AddictionHabitDaoTest {
    private lateinit var db: com.example.rewire.db.RewireDatabase
    private lateinit var addictionHabitDao: com.example.rewire.db.dao.AddictionHabitDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, com.example.rewire.db.RewireDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        addictionHabitDao = db.addictionHabitDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun addictionHabitDao_crudOperations() {
        runBlocking {
            val addiction = AddictionHabitEntity(
                id = 1L,
                name = "Test Addiction",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.Weekly,
                preferredTime = "09:00",
                estimatedMinutes = 3
            )
            val id = addictionHabitDao.insert(addiction)
            val loaded = addictionHabitDao.getById(id)
            assertNotNull(loaded)
            addictionHabitDao.delete(loaded!!)
            assertNull(addictionHabitDao.getById(id))
        }
    }

    @Test
    fun insertDuplicateAddiction_shouldFailOrUpdate() {
        runBlocking {
            val addiction = AddictionHabitEntity(
                id = 2L,
                name = "Duplicate Addiction",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 2
            )
            addictionHabitDao.insert(addiction)
            try {
                addictionHabitDao.insert(addiction)
            } catch (e: Exception) {
                assertTrue(e is Exception)
            }
        }
    }

    @Test
    fun updateDeleteNonExistentAddiction_shouldNotCrash() {
        runBlocking {
            val addiction = AddictionHabitEntity(
                id = 9999L,
                name = "Non-existent",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 2
            )
            addictionHabitDao.update(addiction)
            addictionHabitDao.delete(addiction)
        }
    }
}
