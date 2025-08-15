package com.example.rewire.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.rewire.db.dao.AddictionHabitDao
import com.example.rewire.db.entity.AddictionHabitEntity
import com.example.rewire.core.RecurrenceType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AddictionHabitDaoTest {
    private lateinit var db: RewireDatabase
    private lateinit var addictionHabitDao: AddictionHabitDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, RewireDatabase::class.java)
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
                recurrence = RecurrenceType.Weekly,
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
}
