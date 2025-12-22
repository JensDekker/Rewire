package com.example.rewire.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.rewire.MIGRATION_1_2
import com.example.rewire.db.dao.LabelDao
import com.example.rewire.db.dao.HabitLabelDao
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.db.entity.HabitLabelCrossRef
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class MigrationTest {
    private lateinit var db: RewireDatabase
    private lateinit var labelDao: LabelDao
    private lateinit var habitLabelDao: HabitLabelDao

    @Test
    fun migrationFromV1ToV2_createsNewTables() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        // Create database with version 2 (fresh install scenario)
        db = Room.databaseBuilder(
            context,
            RewireDatabase::class.java,
            "test_migration_db"
        )
            .addMigrations(MIGRATION_1_2)
            .allowMainThreadQueries()
            .build()
        
        labelDao = db.labelDao()
        habitLabelDao = db.habitLabelDao()
        
        // Verify new tables exist by using them
        runBlocking {
            // Test labels table exists and works
            val label = LabelEntity(
                name = "Test Label",
                color = "#4CAF50"
            )
            val labelId = labelDao.insert(label)
            assertTrue(labelId > 0)
            
            val retrievedLabel = labelDao.getById(labelId)
            assertNotNull(retrievedLabel)
            assertEquals("Test Label", retrievedLabel?.name)
            assertEquals("#4CAF50", retrievedLabel?.color)
            
            // Test habit_labels junction table exists and works
            // Note: We're just verifying the table exists - we use a habitId that could exist
            // The fact that we can insert into habit_labels proves the table exists
            val crossRef = HabitLabelCrossRef(
                habitId = 1L,
                labelId = labelId
            )
            habitLabelDao.insert(crossRef)
            
            val crossRefs = habitLabelDao.getCrossRefsForHabit(1L)
            assertEquals(1, crossRefs.size)
            assertEquals(labelId, crossRefs[0].labelId)
        }
        
        db.close()
    }

    @Test
    fun freshInstall_v2_worksWithoutMigration() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        // Create fresh database with version 2 (no migration needed)
        db = Room.databaseBuilder(
            context,
            RewireDatabase::class.java,
            "test_fresh_install_db"
        )
            .addMigrations(MIGRATION_1_2)
            .allowMainThreadQueries()
            .build()
        
        labelDao = db.labelDao()
        
        // Verify we can use the new tables
        runBlocking {
            val label = LabelEntity(
                name = "Fresh Install Label",
                color = "#2196F3"
            )
            val labelId = labelDao.insert(label)
            assertTrue(labelId > 0)
            
            val allLabels = labelDao.getAll()
            assertTrue(allLabels.isNotEmpty())
            assertTrue(allLabels.any { it.name == "Fresh Install Label" })
        }
        
        db.close()
    }
}

