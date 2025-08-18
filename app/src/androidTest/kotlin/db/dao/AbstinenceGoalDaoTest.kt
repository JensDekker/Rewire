package com.example.rewire.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.rewire.db.entity.AddictionHabitEntity
import com.example.rewire.db.entity.AbstinenceGoalEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AbstinenceGoalDaoTest {
    private lateinit var db: com.example.rewire.db.RewireDatabase
    private lateinit var addictionHabitDao: com.example.rewire.db.dao.AddictionHabitDao
    private lateinit var abstinenceGoalDao: com.example.rewire.db.dao.AbstinenceGoalDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, com.example.rewire.db.RewireDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        addictionHabitDao = db.addictionHabitDao()
        abstinenceGoalDao = db.abstinenceGoalDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun abstinenceGoalDao_crudOperations() {
        runBlocking {
            val addiction = AddictionHabitEntity(
                id = 1L,
                name = "Test Addiction",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.MonthlyByDate(1),
                preferredTime = "09:00",
                estimatedMinutes = 1
            )
            addictionHabitDao.insert(addiction)
            val goal = AbstinenceGoalEntity(id = 1L, addictionId = 1L, recurrence = com.example.rewire.core.RecurrenceType.MonthlyByDate(1), value = 1, repeatCount = 1)
            val id = abstinenceGoalDao.insert(goal)
            val loaded = abstinenceGoalDao.getById(id)
            assertNotNull(loaded)
            abstinenceGoalDao.delete(loaded!!)
            assertNull(abstinenceGoalDao.getById(id))
        }
    }

    @Test(expected = Exception::class)
    fun insertGoalForNonExistentAddiction_shouldThrow() {
        runBlocking {
            val goal = AbstinenceGoalEntity(id = 5001L, addictionId = 9999L, recurrence = com.example.rewire.core.RecurrenceType.Daily, value = 1, repeatCount = 1)
            abstinenceGoalDao.insert(goal)
        }
    }

    @Test
    fun updateDeleteNonExistentGoal_shouldNotCrash() {
        runBlocking {
            val goal = AbstinenceGoalEntity(id = 9999L, addictionId = 9999L, recurrence = com.example.rewire.core.RecurrenceType.Daily, value = 1, repeatCount = 1)
            abstinenceGoalDao.update(goal)
            abstinenceGoalDao.delete(goal)
        }
    }
}
