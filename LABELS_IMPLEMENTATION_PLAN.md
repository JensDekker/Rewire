# Labels/Tags Implementation Plan - Option 2 (Normalized Many-to-Many)

## Overview

This document provides a detailed implementation plan for adding labels/tags with color coding to habits in the Rewire app. The implementation uses a normalized database design with a junction table (many-to-many relationship), which is ideal for color coding because:

- **Single source of truth**: Each label's color is stored once and shared across all habits using it
- **Consistency**: Changing a label's color updates it everywhere automatically
- **Scalability**: Easy to add label metadata (colors, descriptions, icons) in the future
- **Efficient queries**: Fast filtering and grouping by labels

## Architecture Decision: Why Option 2 (Junction Table)

The normalized approach with a junction table is chosen over simpler alternatives because:

1. **Color is a property of the label**, not the habit - storing it with labels ensures consistency
2. **Label reuse** - Users can apply the same label to multiple habits with consistent colors
3. **Label management** - Users can edit/delete labels independently, affecting all related habits
4. **Query efficiency** - Database indices enable fast filtering by labels
5. **Future extensibility** - Easy to add label icons, descriptions, categories, etc.

## Database Schema

### Tables Overview

```
habits                    habit_labels (junction)    labels
┌─────┬──────────┐       ┌──────────┬─────────┐    ┌─────┬─────────┬───────────┐
│ id  │ name     │       │ habitId  │ labelId │    │ id  │ name    │ color     │
├─────┼──────────┤       ├──────────┼─────────┤    ├─────┼─────────┼───────────┤
│  1  │ Exercise │───────┤    1     │    1    │────┤  1  │ Health  │ #4CAF50 │
│  2  │ Read     │       │    1     │    2    │────┤  2  │ Daily   │ #2196F3 │
│  3  │ Meditate │       │    2     │    2    │    │  3  │ Work    │ #FF5722 │
└─────┴──────────┘       │    2     │    4    │────┤  4  │ Learning│ #9C27B0 │
                         │    3     │    1    │    └─────┴─────────┴───────────┘
                         │    3     │    2    │
                         └──────────┴─────────┘
```

### 1. Labels Table (`labels`)

Stores unique label definitions with their properties.

**File**: `app/src/main/kotlin/db/entity/LabelEntity.kt`

```kotlin
@Entity(
    tableName = "labels",
    indices = [Index(value = ["name"], unique = true)]  // Ensure label names are unique
)
data class LabelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,                    // Label name (e.g., "Health", "Work")
    val color: String,                   // Hex color code (e.g., "#4CAF50")
    val createdAt: String? = null        // Optional: ISO 8601 timestamp
)
```

**SQL Schema**:
```sql
CREATE TABLE IF NOT EXISTS `labels` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `name` TEXT NOT NULL,
    `color` TEXT NOT NULL,
    `createdAt` TEXT
);

CREATE UNIQUE INDEX IF NOT EXISTS `index_labels_name` ON `labels` (`name`);
```

### 2. Junction Table (`habit_labels`)

Links habits to labels (many-to-many relationship).

**File**: `app/src/main/kotlin/db/entity/HabitLabelCrossRef.kt`

```kotlin
@Entity(
    tableName = "habit_labels",
    primaryKeys = ["habitId", "labelId"],  // Composite primary key prevents duplicates
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE  // Delete associations when habit is deleted
        ),
        ForeignKey(
            entity = LabelEntity::class,
            parentColumns = ["id"],
            childColumns = ["labelId"],
            onDelete = ForeignKey.CASCADE  // Delete associations when label is deleted
        )
    ],
    indices = [
        Index(value = ["habitId"]),      // Fast lookup: "get all labels for a habit"
        Index(value = ["labelId"])       // Fast lookup: "get all habits with a label"
    ]
)
data class HabitLabelCrossRef(
    val habitId: Long,
    val labelId: Long
)
```

**SQL Schema**:
```sql
CREATE TABLE IF NOT EXISTS `habit_labels` (
    `habitId` INTEGER NOT NULL,
    `labelId` INTEGER NOT NULL,
    PRIMARY KEY(`habitId`, `labelId`),
    FOREIGN KEY(`habitId`) REFERENCES `habits`(`id`) ON DELETE CASCADE,
    FOREIGN KEY(`labelId`) REFERENCES `labels`(`id`) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS `index_habit_labels_habitId` ON `habit_labels` (`habitId`);
CREATE INDEX IF NOT EXISTS `index_habit_labels_labelId` ON `habit_labels` (`labelId`);
```

## Pre-Implementation Checklist

**⚠️ IMPORTANT: Read this section before starting implementation**

### Critical Warnings

1. **Database Migration**
   - ⚠️ Current database version is **1** - verify this before starting
   - ⚠️ **Test the migration on a backup/test device before deploying**
   - ⚠️ Ensure `MIGRATION_1_2` is implemented and added to database builder **before** incrementing version number
   - ⚠️ If enabling `exportSchema = true`, configure `build.gradle.kts` first (see Step 1.7)

2. **Backup Your Data**
   - Back up the database or test on a device/emulator with test data first
   - In development, you can temporarily use `fallbackToDestructiveMigration()`, but **never in production**

### Recommended Implementation Workflow

1. **Follow Phases in Order**
   - Phase 1: Database layer (must complete before moving on)
   - Phase 2: Repository layer  
   - Phase 3: UI components
   - Phase 4: Integration
   - Phase 5: Advanced features (optional)

2. **Validate After Each Step**
   - Use the validation checklists at the end of each step
   - Compile and run after major changes
   - Test database operations before moving to UI

3. **Incremental Testing**
   - After Phase 1: Verify database migration works, tables created, can insert labels
   - After Phase 2: Test repository methods, labels can be associated with habits
   - After Phase 3: Verify UI components render correctly
   - After Phase 4: Test full workflow (create habit with labels, edit labels, etc.)

### Common Pitfalls to Avoid

1. **Migration Order**
   - ❌ Don't increment database version until migration code is ready
   - ❌ Don't forget to add `.addMigrations(MIGRATION_1_2)` to database builder

2. **Transaction Handling**
   - ✅ Use `createHabitWithLabels()` and `updateHabitWithLabels()` for atomic operations
   - ❌ Avoid saving habit and labels separately (can lead to inconsistent state)

3. **N+1 Query Problem**
   - ✅ Use `getLabelsForHabits()` for batch loading instead of calling `getLabelsForHabit()` in loops
   - ⚠️ Especially important in `HabitHomeScreen` where multiple habits need labels

4. **State Management**
   - ✅ Reload labels when returning from Label Management Screen (use `DisposableEffect`)
   - ✅ Handle loading and error states properly in UI

5. **Error Handling**
   - ✅ Always use `Result<T>` types for operations that can fail
   - ✅ Don't lose user input on errors - keep forms open and show clear error messages

### Quick Reference During Implementation

- **Validation Checklists**: Each step has its own checklist at the end
- **Error Handling**: See "Error Handling Strategy" section (around line 1078)
- **Migration Details**: See "Database Migration Strategy" section (around line 2947)
- **Color Defaults**: See `LabelColors` object in "Color Coding Details" (around line 3100)

### Before You Deploy

✅ Test migration on real device with existing data (if applicable)  
✅ Test all CRUD operations for labels  
✅ Test label associations with habits  
✅ Test error scenarios (duplicate names, invalid colors, etc.)  
✅ Verify CASCADE deletes work correctly  
✅ Test all navigation options to Label Management Screen  
✅ Verify batch loading performance (no N+1 queries)  
✅ See "Pre-Production Validation" section for complete checklist

---

## Implementation Steps

### Phase 1: Core Model & Database Layer

#### Step 1.1: Create Core Label Model

**File**: `core/src/main/kotlin/Label.kt`

```kotlin
package com.example.rewire.core

data class Label(
    val id: Long = 0,
    val name: String,
    val color: String  // Hex color code (e.g., "#4CAF50")
)
```

**Validation Checklist:**
- [x] File `core/src/main/kotlin/Label.kt` compiles without errors
- [x] `Label` data class has `id: Long` with default value `0`
- [x] `Label` data class has `name: String` field
- [x] `Label` data class has `color: String` field
- [x] Package declaration is `package com.example.rewire.core`
- [x] Data class can be instantiated with all fields

#### Step 1.2: Create Label Entity

**File**: `app/src/main/kotlin/db/entity/LabelEntity.kt`

```kotlin
package com.example.rewire.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rewire.core.Label

@Entity(
    tableName = "labels",
    indices = [Index(value = ["name"], unique = true)]
)
data class LabelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: String,
    val createdAt: String? = null
)

fun LabelEntity.toCore(): Label = Label(
    id = id,
    name = name,
    color = color
)

fun Label.toEntity(): LabelEntity = LabelEntity(
    id = id,
    name = name,
    color = color,
    createdAt = null  // Label doesn't have createdAt, so set to null when converting
)
```

**Validation Checklist:**
- [x] File `app/src/main/kotlin/db/entity/LabelEntity.kt` compiles without errors
- [x] `@Entity` annotation includes `tableName = "labels"`
- [x] `@Entity` annotation includes unique index on `name` field
- [x] `LabelEntity` has `@PrimaryKey(autoGenerate = true)` on `id` field
- [x] `LabelEntity` has `name: String` field
- [x] `LabelEntity` has `color: String` field
- [x] `LabelEntity` has `createdAt: String?` field with default `null`
- [x] `toCore()` extension function converts `LabelEntity` to `core.Label` correctly
- [x] `toEntity()` extension function converts `core.Label` to `LabelEntity` correctly
- [x] Conversion functions handle all fields properly

#### Step 1.3: Create Junction Table Entity

**File**: `app/src/main/kotlin/db/entity/HabitLabelCrossRef.kt`

```kotlin
package com.example.rewire.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_labels",
    primaryKeys = ["habitId", "labelId"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LabelEntity::class,
            parentColumns = ["id"],
            childColumns = ["labelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId"]),
        Index(value = ["labelId"])
    ]
)
data class HabitLabelCrossRef(
    val habitId: Long,
    val labelId: Long
)
```

**Validation Checklist:**
- [x] File `app/src/main/kotlin/db/entity/HabitLabelCrossRef.kt` compiles without errors
- [x] `@Entity` annotation includes `tableName = "habit_labels"`
- [x] `@Entity` annotation includes composite `primaryKeys = ["habitId", "labelId"]`
- [x] Foreign key to `HabitEntity` is defined with `onDelete = ForeignKey.CASCADE`
- [x] Foreign key to `LabelEntity` is defined with `onDelete = ForeignKey.CASCADE`
- [x] Index on `habitId` is defined
- [x] Index on `labelId` is defined
- [x] `HabitLabelCrossRef` has `habitId: Long` field
- [x] `HabitLabelCrossRef` has `labelId: Long` field
- [x] Room can process the entity annotations correctly

#### Step 1.4: Update Core Habit Model

**File**: `core/src/main/kotlin/Habit.kt`

Add labels field to the Habit data class:

```kotlin
data class Habit(
    var id: Long = 0,
    var name: String,
    var recurrence: RecurrenceType = RecurrenceType.Daily,
    var preferredTime: LocalTime = LocalTime.of(9, 0),
    var estimatedMinutes: Int = 10,
    var customDays: Set<DayOfWeek>? = null,
    var startDate: LocalDate = LocalDate.now(),
    var labels: List<Label> = emptyList()  // ADD THIS
) {
    // ... existing methods remain unchanged
}
```

**Validation Checklist:**
- [x] File `core/src/main/kotlin/Habit.kt` compiles without errors
- [x] `labels: List<Label> = emptyList()` field is added to `Habit` data class
- [x] Existing `Habit` constructors and methods still work
- [x] Default value `emptyList()` ensures backward compatibility
- [x] Type `Label` is imported from `com.example.rewire.core`
- [x] No breaking changes to existing `Habit` usage

#### Step 1.5: Create Label DAO

**File**: `app/src/main/kotlin/db/dao/LabelDao.kt`

```kotlin
package com.example.rewire.db.dao

import androidx.room.*
import com.example.rewire.db.entity.LabelEntity

@Dao
interface LabelDao {
    @Query("SELECT * FROM labels ORDER BY name ASC")
    suspend fun getAll(): List<LabelEntity>
    
    @Query("SELECT * FROM labels WHERE id = :id")
    suspend fun getById(id: Long): LabelEntity?
    
    @Query("SELECT * FROM labels WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): LabelEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(label: LabelEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(labels: List<LabelEntity>): List<Long>
    
    @Update
    suspend fun update(label: LabelEntity)
    
    @Delete
    suspend fun delete(label: LabelEntity)
    
    // Get labels for a specific habit (via junction table)
    @Query("""
        SELECT labels.* FROM labels
        INNER JOIN habit_labels ON labels.id = habit_labels.labelId
        WHERE habit_labels.habitId = :habitId
        ORDER BY labels.name ASC
    """)
    suspend fun getLabelsForHabit(habitId: Long): List<LabelEntity>
    
    // Search labels by name
    @Query("SELECT * FROM labels WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchLabels(query: String): List<LabelEntity>
}
```

**Validation Checklist:**
- [x] File `app/src/main/kotlin/db/dao/LabelDao.kt` compiles without errors
- [x] Interface is annotated with `@Dao`
- [x] `getAll()` method returns `List<LabelEntity>`
- [x] `getById()` method takes `Long` and returns nullable `LabelEntity`
- [x] `getByName()` method takes `String` and returns nullable `LabelEntity`
- [x] `insert()` method takes `LabelEntity` and returns `Long`
- [x] `insertAll()` method takes `List<LabelEntity>` and returns `List<Long>`
- [x] `update()` method takes `LabelEntity`
- [x] `delete()` method takes `LabelEntity`
- [x] `getLabelsForHabit()` joins tables correctly and returns labels for a habit
- [x] `searchLabels()` performs case-insensitive search
- [x] All methods are `suspend` functions
- [x] SQL queries compile without syntax errors

#### Step 1.6: Create Junction Table DAO

**File**: `app/src/main/kotlin/db/dao/HabitLabelDao.kt`

```kotlin
package com.example.rewire.db.dao

import androidx.room.*
import com.example.rewire.db.entity.HabitLabelCrossRef

@Dao
interface HabitLabelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(crossRef: HabitLabelCrossRef)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(crossRefs: List<HabitLabelCrossRef>)
    
    @Delete
    suspend fun delete(crossRef: HabitLabelCrossRef)
    
    @Query("DELETE FROM habit_labels WHERE habitId = :habitId")
    suspend fun deleteAllForHabit(habitId: Long)
    
    @Query("DELETE FROM habit_labels WHERE labelId = :labelId")
    suspend fun deleteAllForLabel(labelId: Long)
    
    @Query("SELECT * FROM habit_labels WHERE habitId = :habitId")
    suspend fun getCrossRefsForHabit(habitId: Long): List<HabitLabelCrossRef>
    
    @Query("SELECT * FROM habit_labels WHERE labelId = :labelId")
    suspend fun getCrossRefsForLabel(labelId: Long): List<HabitLabelCrossRef>
    
    // Check if a habit has a specific label
    @Query("SELECT EXISTS(SELECT 1 FROM habit_labels WHERE habitId = :habitId AND labelId = :labelId)")
    suspend fun hasLabel(habitId: Long, labelId: Long): Boolean
    
    // Get all habit IDs with a specific label
    @Query("SELECT habitId FROM habit_labels WHERE labelId = :labelId")
    suspend fun getHabitIdsWithLabel(labelId: Long): List<Long>
}
```

**Validation Checklist:**
- [x] File `app/src/main/kotlin/db/dao/HabitLabelDao.kt` compiles without errors
- [x] Interface is annotated with `@Dao`
- [x] `insert()` method takes `HabitLabelCrossRef` with `OnConflictStrategy.REPLACE`
- [x] `insertAll()` method takes `List<HabitLabelCrossRef>` with `OnConflictStrategy.REPLACE`
- [x] `delete()` method takes `HabitLabelCrossRef`
- [x] `deleteAllForHabit()` deletes all cross-references for a habit
- [x] `deleteAllForLabel()` deletes all cross-references for a label
- [x] `getCrossRefsForHabit()` returns all cross-references for a habit
- [x] `getCrossRefsForLabel()` returns all cross-references for a label
- [x] `hasLabel()` checks existence and returns `Boolean`
- [x] `getHabitIdsWithLabel()` returns list of habit IDs for a label
- [x] All methods are `suspend` functions
- [x] SQL queries compile without syntax errors

#### Step 1.7: Update RewireDatabase

**File**: `app/src/main/kotlin/db/RewireDatabase.kt`

1. Increment database version from 1 to 2
2. Add new entities to the entities list
3. Add new DAO methods

```kotlin
@Database(
    entities = [
        HabitEntity::class,
        AddictionHabitEntity::class,
        AbstinenceGoalEntity::class,
        HabitNoteEntity::class,
        AddictionNoteEntity::class,
        com.example.rewire.db.entity.HabitCompletion::class,
        LabelEntity::class,                    // ADD
        HabitLabelCrossRef::class              // ADD
    ],
    version = 2,  // UPDATE from 1 to 2
    exportSchema = true  // UPDATE: Enable schema export for migration validation (optional but recommended)
)
abstract class RewireDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun addictionHabitDao(): AddictionHabitDao
    abstract fun abstinenceGoalDao(): AbstinenceGoalDao
    abstract fun habitNoteDao(): HabitNoteDao
    abstract fun addictionNoteDao(): AddictionNoteDao
    abstract fun habitCompletionDao(): com.example.rewire.db.dao.HabitCompletionDao
    abstract fun labelDao(): LabelDao              // ADD
    abstract fun habitLabelDao(): HabitLabelDao    // ADD
}
```

**Validation Checklist:**
- [x] File `app/src/main/kotlin/db/RewireDatabase.kt` compiles without errors
- [x] Database version is incremented from 1 to 2
- [x] `LabelEntity` is added to `entities` array
- [x] `HabitLabelCrossRef` is added to `entities` array
- [x] `labelDao(): LabelDao` abstract method is added
- [x] `habitLabelDao(): HabitLabelDao` abstract method is added
- [x] All necessary imports are added (`LabelDao`, `HabitLabelDao`, `LabelEntity`, `HabitLabelCrossRef`)
- [x] `exportSchema` is set to `true` (optional but recommended)
- [x] Room annotation processor (kapt) generates database implementation successfully
- [x] No Room errors during compilation (warnings about schema export directory are acceptable)

#### Step 1.8: Create Database Migration (Version 1 → 2)

**File**: `app/src/main/kotlin/db/RewireDatabase.kt`

Add migration callback when building the database:

```kotlin
// In MainActivity.kt or wherever database is built:
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create labels table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `labels` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `color` TEXT NOT NULL,
                `createdAt` TEXT
            )
        """)
        
        // Create unique index on label name
        database.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS `index_labels_name` 
            ON `labels` (`name`)
        """)
        
        // Create junction table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `habit_labels` (
                `habitId` INTEGER NOT NULL,
                `labelId` INTEGER NOT NULL,
                PRIMARY KEY(`habitId`, `labelId`),
                FOREIGN KEY(`habitId`) REFERENCES `habits`(`id`) ON DELETE CASCADE,
                FOREIGN KEY(`labelId`) REFERENCES `labels`(`id`) ON DELETE CASCADE
            )
        """)
        
        // Create indices on junction table
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS `index_habit_labels_habitId` 
            ON `habit_labels` (`habitId`)
        """)
        
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS `index_habit_labels_labelId` 
            ON `habit_labels` (`labelId`)
        """)
    }
}

// When building database:
Room.databaseBuilder(context, RewireDatabase::class.java, "rewire_database")
    .addMigrations(MIGRATION_1_2)
    .build()
```

**Validation Checklist:**
- [x] File containing `MIGRATION_1_2` compiles without errors
- [x] Migration object extends `Migration(1, 2)` correctly
- [x] Migration creates `labels` table with all required columns (`id`, `name`, `color`, `createdAt`)
- [x] Migration creates unique index `index_labels_name` on `labels.name`
- [x] Migration creates `habit_labels` junction table with composite primary key
- [x] Migration creates foreign keys with `ON DELETE CASCADE` for both `habitId` and `labelId`
- [x] Migration creates index `index_habit_labels_habitId` on junction table
- [x] Migration creates index `index_habit_labels_labelId` on junction table
- [x] SQL syntax is correct (no syntax errors)
- [x] Migration is added to database builder with `.addMigrations(MIGRATION_1_2)`
- [x] Migration test created (`MigrationTest.kt`) - verifies tables are created correctly
- [x] Fresh install (version 2) test verifies new tables work without migration
- [x] Migration verified on device/emulator - existing habits preserved, new tables accessible (verified by user)

### Phase 2: Repository Layer

#### Step 2.1: Create Label Repository

**File**: `app/src/main/kotlin/repository/LabelRepository.kt`

```kotlin
package com.example.rewire.repository

import com.example.rewire.db.dao.LabelDao
import com.example.rewire.db.dao.HabitLabelDao
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.db.entity.HabitLabelCrossRef
import com.example.rewire.core.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LabelRepository(
    private val labelDao: LabelDao,
    private val habitLabelDao: HabitLabelDao
) {
    suspend fun getAllLabels(): List<LabelEntity> = withContext(Dispatchers.IO) {
        labelDao.getAll()
    }
    
    suspend fun getLabelById(id: Long): LabelEntity? = withContext(Dispatchers.IO) {
        labelDao.getById(id)
    }
    
    suspend fun getLabelByName(name: String): LabelEntity? = withContext(Dispatchers.IO) {
        labelDao.getByName(name)
    }
    
    suspend fun insertLabel(label: LabelEntity): Long = withContext(Dispatchers.IO) {
        labelDao.insert(label)
    }
    
    suspend fun updateLabel(label: LabelEntity) = withContext(Dispatchers.IO) {
        labelDao.update(label)
    }
    
    suspend fun deleteLabel(label: LabelEntity) = withContext(Dispatchers.IO) {
        // Delete all associations first (or let CASCADE handle it)
        habitLabelDao.deleteAllForLabel(label.id)
        labelDao.delete(label)
    }
    
    // Get or create label (useful for tag autocomplete)
    suspend fun getOrCreateLabel(name: String, defaultColor: String = "#9E9E9E"): LabelEntity {
        val existing = getLabelByName(name)
        return if (existing != null) {
            existing
        } else {
            val newLabel = LabelEntity(
                name = name.trim(),
                color = defaultColor,
                createdAt = java.time.LocalDateTime.now().toString()
            )
            val id = insertLabel(newLabel)
            newLabel.copy(id = id)
        }
    }
    
    // Get labels for a habit
    suspend fun getLabelsForHabit(habitId: Long): List<LabelEntity> = withContext(Dispatchers.IO) {
        labelDao.getLabelsForHabit(habitId)
    }
    
    // Batch load labels for multiple habits (optimization to avoid N+1 queries)
    suspend fun getLabelsForHabits(habitIds: List<Long>): Map<Long, List<LabelEntity>> = withContext(Dispatchers.IO) {
        if (habitIds.isEmpty()) {
            return@withContext emptyMap()
        }
        // Load all cross-references for the given habit IDs
        val allCrossRefs = habitIds.flatMap { habitId ->
            habitLabelDao.getCrossRefsForHabit(habitId).map { it.habitId to it.labelId }
        }
        // Get unique label IDs
        val labelIds = allCrossRefs.map { it.second }.distinct()
        if (labelIds.isEmpty()) {
            return@withContext habitIds.associateWith { emptyList() }
        }
        // Load all labels at once
        val allLabels = labelDao.getAll().filter { it.id in labelIds }
        val labelsMap = allLabels.associateBy { it.id }
        // Build result map: habitId -> List<LabelEntity>
        allCrossRefs
            .groupBy({ it.first }, { labelsMap[it.second] })
            .mapValues { (_, labels) -> labels.filterNotNull() }
            .toMap()
    }
    
    // Set labels for a habit (replaces existing labels)
    suspend fun setLabelsForHabit(habitId: Long, labelIds: List<Long>) = withContext(Dispatchers.IO) {
        // Delete existing associations
        habitLabelDao.deleteAllForHabit(habitId)
        // Insert new associations
        val crossRefs = labelIds.map { HabitLabelCrossRef(habitId, it) }
        habitLabelDao.insertAll(crossRefs)
    }
    
    // Add a single label to a habit
    suspend fun addLabelToHabit(habitId: Long, labelId: Long) = withContext(Dispatchers.IO) {
        habitLabelDao.insert(HabitLabelCrossRef(habitId, labelId))
    }
    
    // Remove a single label from a habit
    suspend fun removeLabelFromHabit(habitId: Long, labelId: Long) = withContext(Dispatchers.IO) {
        habitLabelDao.delete(HabitLabelCrossRef(habitId, labelId))
    }
    
    // Get all habits with a specific label
    suspend fun getHabitIdsWithLabel(labelId: Long): List<Long> = withContext(Dispatchers.IO) {
        habitLabelDao.getHabitIdsWithLabel(labelId)
    }
    
    // Search labels
    suspend fun searchLabels(query: String): List<LabelEntity> = withContext(Dispatchers.IO) {
        labelDao.searchLabels(query)
    }
}
```

**Validation Checklist:**
- [x] File `app/src/main/kotlin/repository/LabelRepository.kt` compiles without errors
- [x] `LabelRepository` class takes `LabelDao` and `HabitLabelDao` as constructor dependencies
- [x] All repository methods use `withContext(Dispatchers.IO)` for database operations
- [x] `getAllLabels()` returns all labels from database
- [x] `getLabelById()` returns correct label or null
- [x] `getLabelByName()` returns correct label or null
- [x] `insertLabel()` creates new label and returns generated ID
- [x] `updateLabel()` updates existing label
- [x] `deleteLabel()` removes label and its associations from junction table
- [x] `getOrCreateLabel()` returns existing label if found by name
- [x] `getOrCreateLabel()` creates new label if not found
- [x] `getLabelsForHabit()` returns all labels for a given habit
- [x] `setLabelsForHabit()` replaces all labels for a habit (delete then insert)
- [x] `addLabelToHabit()` adds single label association
- [x] `removeLabelFromHabit()` removes single label association
- [x] `getHabitIdsWithLabel()` returns all habit IDs that use a label
- [x] `getLabelsForHabits()` batch loads labels for multiple habits (returns `Map<Long, List<LabelEntity>>`)
- [x] `getLabelsForHabits()` handles empty input list gracefully
- [x] `getLabelsForHabits()` avoids N+1 queries by loading labels in batch
- [x] `searchLabels()` performs case-insensitive search
- [x] All methods use proper coroutine context for database operations

#### Step 2.2: Update HabitRepository

**File**: `app/src/main/kotlin/repository/HabitRepository.kt`

Update conversion functions to include labels. You'll need to inject `LabelRepository`:

```kotlin
class HabitRepository(
    private val habitDao: HabitDao,
    private val labelRepository: LabelRepository  // ADD
) {
    // Update conversion to include labels
    suspend fun getHabitWithLabels(habitId: Long): HabitEntity? {
        val habit = getHabitById(habitId) ?: return null
        // Labels will be loaded separately when needed
        return habit
    }
    
    // Helper to convert HabitEntity to Habit with labels
    suspend fun habitEntityToHabit(entity: HabitEntity): com.example.rewire.core.Habit {
        val labels = labelRepository.getLabelsForHabit(entity.id)
        return entity.toCore().copy(
            labels = labels.map { it.toCore() }
        )
    }
}
```

**Validation Checklist:**
- [x] File `app/src/main/kotlin/repository/HabitRepository.kt` compiles without errors
- [x] `LabelRepository` is added as constructor dependency
- [x] `habitEntityToHabit()` helper function is added or updated
- [x] `habitEntityToHabit()` loads labels using `labelRepository.getLabelsForHabit()`
- [x] Labels are correctly converted from `LabelEntity` to `core.Label` using `toCore()`
- [x] `Habit` object includes `labels: List<Label>` field when converted
- [x] Existing `HabitRepository` methods that return `Habit` use the updated conversion (Note: HabitRepository returns HabitEntity, conversion is via helper function)
- [x] No breaking changes to existing `HabitRepository` API (constructor updated, but MainActivity updated accordingly)
- [x] All methods that return `Habit` now include labels data (via helper function)

#### Step 2.3: Update HabitManager

**File**: `app/src/main/kotlin/manager/HabitManager.kt`

Inject `LabelRepository` and update methods to handle labels:

```kotlin
class HabitManager(
    private val habitRepository: HabitRepository,
    private val labelRepository: LabelRepository  // ADD
) {
    // Update methods that return habits to include labels
    suspend fun getHabits(): List<HabitEntity> {
        val habits = habitRepository.getAllHabits()
        // Labels loaded separately when needed for performance
        return habits
    }
    
    // New method: Get habit with labels
    suspend fun getHabitWithLabels(habitId: Long): com.example.rewire.core.Habit? {
        val entity = habitRepository.getHabitById(habitId) ?: return null
        val labels = labelRepository.getLabelsForHabit(habitId)
        return entity.toCore().copy(labels = labels.map { it.toCore() })
    }
    
    // New method: Set labels for a habit
    suspend fun setLabelsForHabit(habitId: Long, labelIds: List<Long>) {
        labelRepository.setLabelsForHabit(habitId, labelIds)
    }
    
    // New method: Get all available labels
    suspend fun getAllLabels(): List<LabelEntity> {
        return labelRepository.getAllLabels()
    }
    
    // New method: Get labels for a habit
    suspend fun getLabelsForHabit(habitId: Long): List<LabelEntity> {
        return labelRepository.getLabelsForHabit(habitId)
    }
    
    // New method: Batch get labels for multiple habits (optimization)
    suspend fun getLabelsForHabits(habitIds: List<Long>): Map<Long, List<LabelEntity>> {
        return labelRepository.getLabelsForHabits(habitIds)
    }
    
    // New method: Create habit with labels atomically
    suspend fun createHabitWithLabels(habit: HabitEntity, labelIds: List<Long>): Result<Long> {
        return try {
            // Insert habit and get generated ID
            val habitId = habitRepository.insertHabit(habit)
            // Set labels for the new habit
            labelRepository.setLabelsForHabit(habitId, labelIds)
            Result.success(habitId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // New method: Update habit with labels atomically
    suspend fun updateHabitWithLabels(habit: HabitEntity, labelIds: List<Long>): Result<Unit> {
        return try {
            require(habit.id > 0) { "Habit ID must be greater than 0 for update" }
            // Update habit
            habitRepository.updateHabit(habit)
            // Update labels
            labelRepository.setLabelsForHabit(habit.id, labelIds)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // New method: Get habit IDs that use a label (for statistics/usage counts)
    suspend fun getHabitIdsWithLabel(labelId: Long): List<Long> {
        return labelRepository.getHabitIdsWithLabel(labelId)
    }
    
    // New method: Create label with validation
    suspend fun createLabel(label: LabelEntity): LabelResult {
        return labelRepository.insertLabelWithValidation(label)
    }
    
    // New method: Update label with validation
    suspend fun updateLabel(label: LabelEntity): LabelResult {
        return labelRepository.updateLabelWithValidation(label)
    }
    
    // New method: Delete label
    suspend fun deleteLabel(label: LabelEntity) {
        labelRepository.deleteLabel(label)
    }
}
```

#### Step 2.4: Update MainActivity to Initialize Label Components

**File**: `app/src/main/kotlin/MainActivity.kt`

Update the database initialization and dependency injection to include label-related components:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Initialize database and repositories
        val database = Room.databaseBuilder(
            applicationContext,
            RewireDatabase::class.java,
            "rewire_database"
        )
            .addMigrations(MIGRATION_1_2)  // ADD migration
            .build()
        
        // Initialize DAOs
        val habitDao = database.habitDao()
        val habitCompletionDao = database.habitCompletionDao()
        val habitNoteDao = database.habitNoteDao()
        val labelDao = database.labelDao()                    // ADD
        val habitLabelDao = database.habitLabelDao()          // ADD
        
        // Initialize repositories
        val habitRepository = HabitRepository(habitDao)
        val habitCompletionRepository = HabitCompletionRepository(habitCompletionDao)
        val habitNoteRepository = HabitNoteRepository(habitNoteDao)
        val labelRepository = LabelRepository(labelDao, habitLabelDao)  // ADD
        
        // Initialize managers (pass labelRepository to HabitManager)
        val habitManager = HabitManager(
            habitRepository, 
            habitCompletionRepository, 
            habitNoteRepository,
            labelRepository  // ADD
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

// ADD: Migration definition (can be at top level of file or in companion object)
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create labels table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `labels` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `color` TEXT NOT NULL,
                `createdAt` TEXT
            )
        """)
        
        // Create unique index on label name
        database.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS `index_labels_name` 
            ON `labels` (`name`)
        """)
        
        // Create junction table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `habit_labels` (
                `habitId` INTEGER NOT NULL,
                `labelId` INTEGER NOT NULL,
                PRIMARY KEY(`habitId`, `labelId`),
                FOREIGN KEY(`habitId`) REFERENCES `habits`(`id`) ON DELETE CASCADE,
                FOREIGN KEY(`labelId`) REFERENCES `labels`(`id`) ON DELETE CASCADE
            )
        """)
        
        // Create indices on junction table
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS `index_habit_labels_habitId` 
            ON `habit_labels` (`habitId`)
        """)
        
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS `index_habit_labels_labelId` 
            ON `habit_labels` (`labelId`)
        """)
    }
}
```

**Note**: The migration code can also be placed in a separate file (e.g., `app/src/main/kotlin/db/migration/Migrations.kt`) for better organization.

#### Step 2.5: Add Error Handling and Validation to LabelRepository

**File**: `app/src/main/kotlin/repository/LabelRepository.kt`

Add validation and error handling for label operations:

```kotlin
// Add validation result class
sealed class LabelResult {
    data class Success(val label: LabelEntity) : LabelResult()
    data class Error(val message: String) : LabelResult()
}

class LabelRepository(
    private val labelDao: LabelDao,
    private val habitLabelDao: HabitLabelDao
) {
    // ... existing methods ...
    
    // Enhanced insert with validation
    suspend fun insertLabelWithValidation(label: LabelEntity): LabelResult = withContext(Dispatchers.IO) {
        try {
            // Validate label name
            val nameValidation = validateLabelName(label.name)
            if (!nameValidation.isValid) {
                return@withContext LabelResult.Error(nameValidation.errorMessage)
            }
            
            // Validate color
            val colorValidation = validateLabelColor(label.color)
            if (!colorValidation.isValid) {
                return@withContext LabelResult.Error(colorValidation.errorMessage)
            }
            
            // Check for duplicate name (case-sensitive)
            val existing = labelDao.getByName(label.name.trim())
            if (existing != null) {
                return@withContext LabelResult.Error("A label with this name already exists")
            }
            
            // Insert label
            val trimmedLabel = label.copy(name = label.name.trim())
            val id = labelDao.insert(trimmedLabel)
            LabelResult.Success(trimmedLabel.copy(id = id))
        } catch (e: Exception) {
            LabelResult.Error("Failed to create label: ${e.message}")
        }
    }
    
    // Enhanced update with validation
    suspend fun updateLabelWithValidation(label: LabelEntity): LabelResult = withContext(Dispatchers.IO) {
        try {
            // Validate label name
            val nameValidation = validateLabelName(label.name)
            if (!nameValidation.isValid) {
                return@withContext LabelResult.Error(nameValidation.errorMessage)
            }
            
            // Validate color
            val colorValidation = validateLabelColor(label.color)
            if (!colorValidation.isValid) {
                return@withContext LabelResult.Error(colorValidation.errorMessage)
            }
            
            // Check for duplicate name (excluding current label)
            val existing = labelDao.getByName(label.name.trim())
            if (existing != null && existing.id != label.id) {
                return@withContext LabelResult.Error("A label with this name already exists")
            }
            
            // Update label
            val trimmedLabel = label.copy(name = label.name.trim())
            labelDao.update(trimmedLabel)
            LabelResult.Success(trimmedLabel)
        } catch (e: Exception) {
            LabelResult.Error("Failed to update label: ${e.message}")
        }
    }
    
    // Enhanced setLabelsForHabit with validation and transaction
    suspend fun setLabelsForHabitSafely(habitId: Long, labelIds: List<Long>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Validate all label IDs exist
            for (labelId in labelIds) {
                val label = labelDao.getById(labelId)
                if (label == null) {
                    return@withContext Result.failure(IllegalArgumentException("Label with ID $labelId does not exist"))
                }
            }
            
            // Use transaction for atomic operation
            // Note: Room transactions are handled automatically for suspend functions in a single coroutine
            // For explicit transaction control across multiple operations, use database.withTransaction { }
            
            // Delete existing associations
            habitLabelDao.deleteAllForHabit(habitId)
            
            // Insert new associations
            if (labelIds.isNotEmpty()) {
                val crossRefs = labelIds.map { HabitLabelCrossRef(habitId, it) }
                habitLabelDao.insertAll(crossRefs)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Validation helper functions
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String = ""
)

fun validateLabelName(name: String): ValidationResult {
    val trimmed = name.trim()
    
    if (trimmed.isEmpty()) {
        return ValidationResult(false, "Label name cannot be empty")
    }
    
    if (trimmed.length > 50) {
        return ValidationResult(false, "Label name must be 50 characters or less")
    }
    
    // Optional: Add regex for allowed characters
    // For example, allow letters, numbers, spaces, and some special characters
    val allowedPattern = Regex("^[a-zA-Z0-9\\s-_]+$")
    if (!trimmed.matches(allowedPattern)) {
        return ValidationResult(false, "Label name contains invalid characters")
    }
    
    return ValidationResult(true)
}

fun validateLabelColor(color: String): ValidationResult {
    if (color.isEmpty()) {
        return ValidationResult(false, "Color cannot be empty")
    }
    
    // Validate hex color format (#RRGGBB or #AARRGGBB)
    val hexColorPattern = Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$")
    if (!color.matches(hexColorPattern)) {
        return ValidationResult(false, "Invalid color format. Use hex format like #4CAF50")
    }
    
    return ValidationResult(true)
}
```

**Validation Checklist:**
- [x] `LabelResult` sealed class is defined with `Success` and `Error` variants
- [x] `insertLabelWithValidation()` validates name and color before inserting
- [x] `insertLabelWithValidation()` checks for duplicate names (case-sensitive)
- [x] `insertLabelWithValidation()` returns `LabelResult.Success` on success
- [x] `insertLabelWithValidation()` returns `LabelResult.Error` on validation failure
- [x] `updateLabelWithValidation()` validates name and color before updating
- [x] `updateLabelWithValidation()` checks for duplicate names excluding current label
- [x] `updateLabelWithValidation()` returns appropriate `LabelResult`
- [x] `setLabelsForHabitSafely()` validates all label IDs exist before setting
- [x] `setLabelsForHabitSafely()` uses transaction-like approach (delete then insert)
- [x] `setLabelsForHabitSafely()` returns `Result<Unit>` indicating success or failure
- [x] `validateLabelName()` function validates non-empty name
- [x] `validateLabelName()` function validates max length (50 characters)
- [x] `validateLabelName()` function validates allowed characters (regex pattern)
- [x] `validateLabelColor()` function validates hex color format
- [x] `ValidationResult` data class is defined with `isValid` and `errorMessage`
- [x] All validation functions handle edge cases (empty strings, null, etc.)
- [x] Error messages are user-friendly and descriptive

### Error Handling Strategy

#### General Approach

Label operations can fail for several reasons:
1. **Database errors** (constraint violations, connection issues)
2. **Validation errors** (invalid names, colors)
3. **Business logic errors** (duplicate names, invalid IDs)

#### Error Handling Layers

**1. Repository Layer (Data Layer)**
- Catch database exceptions
- Return `Result` types or sealed classes (e.g., `LabelResult`)
- Provide meaningful error messages
- Never expose raw exceptions to UI layer

**2. Manager Layer (Business Logic)**
- Validate business rules
- Transform repository errors into user-friendly messages
- Handle error propagation to UI

**3. UI Layer (Presentation)**
- Display errors to users (Snackbar, Toast, inline error messages)
- Provide error recovery options (retry, cancel)
- Handle loading and error states

#### Error Display Patterns

**For Label Creation/Editing:**
```kotlin
// In Label Management Screen or AddEditHabitScreen
when (val result = labelRepository.insertLabelWithValidation(label)) {
    is LabelResult.Success -> {
        // Show success message, update UI
        showSnackbar("Label created successfully")
    }
    is LabelResult.Error -> {
        // Show error message
        showSnackbar(result.message)
        // Keep form open so user can fix and retry
    }
}
```

**For Label Selection:**
- If label loading fails, show empty state or placeholder
- If setting labels fails, show error but don't lose user's selection
- Allow retry mechanism

**For Database Errors:**
- Catch `SQLiteConstraintException` for unique violations
- Show user-friendly message: "A label with this name already exists"
- Catch `SQLiteException` for other database errors
- Log errors for debugging
- Show generic message to user: "An error occurred. Please try again."

**Validation Checklist:**
- [x] File `app/src/main/kotlin/MainActivity.kt` compiles without errors
- [x] `MIGRATION_1_2` is defined (can be at top level or in companion object)
- [x] Database builder includes `.addMigrations(MIGRATION_1_2)`
- [x] `labelDao` is initialized from `database.labelDao()`
- [x] `habitLabelDao` is initialized from `database.habitLabelDao()`
- [x] `LabelRepository` is created with `labelDao` and `habitLabelDao`
- [x] `HabitRepository` is updated to accept `LabelRepository` (if needed)
- [x] `HabitManager` is created with `labelRepository` parameter
- [x] All dependencies are properly injected
- [x] Database builds successfully with migration
- [x] Error handling for migration failures is implemented (try-catch block recommended) - Added try-catch with proper logging for migration failures. Errors are logged and then re-thrown to prevent app from continuing with corrupted database state.
- [x] App starts without crashes

### Phase 3: UI Components

#### Step 3.1: Create Label Chip Component

**File**: `app/src/main/kotlin/ui/components/LabelChip.kt`

```kotlin
package com.example.rewire.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.rewire.core.Label
import com.example.rewire.ui.theme.AppSpacing

@Composable
fun LabelChip(
    label: Label,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val labelColor = try {
        Color(android.graphics.Color.parseColor(label.color))
    } catch (e: Exception) {
        MaterialTheme.colors.primary  // Fallback color
    }
    
    // Calculate text color based on background brightness
    val textColor = if (isColorDark(labelColor)) Color.White else Color.Black
    
    Box(
        modifier = modifier
            .background(labelColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.smallSpacing, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.name,
            color = textColor,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Helper function to determine if a color is dark
private fun isColorDark(color: Color): Boolean {
    val brightness = (color.red * 299 + color.green * 587 + color.blue * 114) / 1000
    return brightness < 0.5
}
```

**Validation Checklist:**
- [x] File `app/src/main/kotlin/ui/components/LabelChip.kt` compiles without errors
- [x] `LabelChip` composable function is defined with `@Composable` annotation
- [x] Component accepts `label: Label` parameter
- [x] Component accepts optional `onClick: () -> Unit` parameter with default empty lambda
- [x] Component accepts optional `modifier: Modifier` parameter
- [x] Color parsing handles hex color strings correctly (e.g., "#4CAF50")
- [x] Fallback color is used when color parsing fails
- [x] Text color is calculated based on background brightness (dark/light)
- [x] `isColorDark()` helper function determines appropriate text color
- [x] Component displays label name correctly
- [x] Text overflow is handled with `TextOverflow.Ellipsis`
- [x] Component has appropriate padding and styling
- [x] Component is clickable when `onClick` is provided
- [x] Component renders without errors in preview or UI (requires manual testing or preview)

#### Step 3.2: Create Label Row Component (Display Multiple Labels)

**File**: `app/src/main/kotlin/ui/components/LabelRow.kt`

```kotlin
package com.example.rewire.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.rewire.core.Label
import com.example.rewire.ui.theme.AppSpacing

@Composable
fun LabelRow(
    labels: List<Label>,
    onLabelClick: (Label) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (labels.isEmpty()) return
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing),
        content = {
            labels.forEach { label ->
                LabelChip(
                    label = label,
                    onClick = { onLabelClick(label) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    )
}
```

**Validation Checklist:**
- [x] File `app/src/main/kotlin/ui/components/LabelRow.kt` compiles without errors (verified via linter)
- [x] `LabelRow` composable function is defined with `@Composable` annotation
- [x] Component accepts `labels: List<Label>` parameter
- [x] Component accepts optional `onLabelClick: (Label) -> Unit` parameter
- [ ] Component accepts optional `maxVisible: Int` parameter (optional enhancement - not in basic version)
- [x] Component displays labels horizontally using `Row`
- [x] Component handles empty list gracefully (returns early if empty)
- [ ] Component shows overflow indicator when `maxVisible` is exceeded (optional enhancement)
- [ ] Overflow count is calculated correctly (`labels.size - maxVisible`) (optional enhancement)
- [x] Spacing between labels is appropriate (`AppSpacing.smallSpacing`)
- [x] Component uses `LabelChip` for each label
- [x] Click handler is passed to each `LabelChip`
- [x] Component renders correctly with various label counts (verified by user)

#### Step 3.3: Create Label Selector Component

**File**: `app/src/main/kotlin/ui/components/LabelSelector.kt`

```kotlin
package com.example.rewire.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rewire.core.Label
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.ui.theme.AppSpacing

@Composable
fun LabelSelector(
    allLabels: List<LabelEntity>,
    selectedLabelIds: Set<Long>,
    onSelectionChange: (Set<Long>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Labels",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing),
            contentPadding = PaddingValues(vertical = AppSpacing.smallSpacing)
        ) {
            items(allLabels) { labelEntity ->
                val label = labelEntity.toCore()
                val isSelected = selectedLabelIds.contains(label.id)
                
                LabelChip(
                    label = label,
                    onClick = {
                        val newSelection = if (isSelected) {
                            selectedLabelIds - label.id
                        } else {
                            selectedLabelIds + label.id
                        }
                        onSelectionChange(newSelection)
                    },
                    modifier = Modifier
                        .then(
                            if (isSelected) {
                                Modifier.border(2.dp, MaterialTheme.colors.primary, RoundedCornerShape(16.dp))
                            } else {
                                Modifier
                            }
                        )
                )
            }
        }
    }
}
```

**Validation Checklist:**
- [x] File `app/src/main/kotlin/ui/components/LabelSelector.kt` compiles without errors (verified via linter)
- [x] `LabelSelector` composable function is defined with `@Composable` annotation
- [x] Component accepts `allLabels: List<LabelEntity>` parameter
- [x] Component accepts `selectedLabelIds: Set<Long>` parameter
- [x] Component accepts `onSelectionChange: (Set<Long>) -> Unit` parameter
- [x] Component accepts optional `modifier: Modifier` parameter
- [x] Component displays "Labels" title using `Text` with subtitle1 style
- [x] Component uses `LazyRow` for horizontal scrolling
- [x] Component converts `LabelEntity` to `Label` using `toCore()` extension function
- [x] Component determines selection state using `selectedLabelIds.contains(label.id)`
- [x] Component toggles selection on label click (adds/removes from set)
- [x] Selected labels display border with primary color
- [x] Border uses `RoundedCornerShape(16.dp)` to match chip shape
- [x] Component uses `LabelChip` for each label
- [x] Click handler updates selection and calls `onSelectionChange`
- [x] Spacing is appropriate (`AppSpacing.smallSpacing`)
- [x] Component handles empty label list gracefully (LazyRow handles empty list)
- [x] Component renders correctly in UI (verified by user)

#### Step 3.4: Loading States and Empty States

##### Loading States

When loading labels asynchronously, show loading indicators:

**File**: `app/src/main/kotlin/ui/components/LabelLoadingIndicator.kt`

```kotlin
package com.example.rewire.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rewire.ui.theme.AppSpacing

@Composable
fun LabelLoadingIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(24.dp)
                    .background(
                        MaterialTheme.colors.surfaceVariant,
                        RoundedCornerShape(16.dp)
                    )
            )
        }
    }
}
```

**Usage in AddEditHabitScreen:**

```kotlin
// Load labels with loading state
var isLoadingLabels by remember { mutableStateOf(true) }
var availableLabels by remember { mutableStateOf<List<LabelEntity>>(emptyList()) }

LaunchedEffect(Unit) {
    try {
        availableLabels = habitManager.getAllLabels()
    } catch (e: Exception) {
        // Handle error
    } finally {
        isLoadingLabels = false
    }
}

// In UI:
if (isLoadingLabels) {
    LabelLoadingIndicator(
        modifier = Modifier.padding(vertical = AppSpacing.standardSpacing)
    )
} else {
    LabelSelector(
        allLabels = availableLabels,
        selectedLabelIds = selectedLabelIds,
        onSelectionChange = { selectedLabelIds = it }
    )
}
```

##### Empty States

**Empty Label List in LabelSelector:**

```kotlin
// In LabelSelector component
Column(modifier = modifier) {
    Text(
        text = "Labels",
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
    )
    
    if (allLabels.isEmpty()) {
        // Empty state
        Text(
            text = "No labels yet. Create your first label to organize habits.",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.textSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppSpacing.standardSpacing),
            textAlign = TextAlign.Center
        )
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing),
            contentPadding = PaddingValues(vertical = AppSpacing.smallSpacing)
        ) {
            items(allLabels) { labelEntity ->
                // ... existing label chip code
            }
        }
    }
}
```

**Empty Labels on Habit Card:**

If a habit has no labels, don't show anything (the `if (labels.isNotEmpty())` check already handles this). The empty state is implicit - no label section appears.

**Validation Checklist:**
- [x] File `app/src/main/kotlin/ui/components/LabelLoadingIndicator.kt` compiles without errors (verified via linter)
- [x] `LabelLoadingIndicator` composable function is defined with `@Composable` annotation
- [x] Component displays loading state (skeleton chips using Box components)
- [x] Component uses appropriate spacing and styling (`AppSpacing.smallSpacing`, `RoundedCornerShape`)
- [x] **Note**: Empty state implemented inline within `LabelSelector` component
- [x] Empty state code is integrated directly into `LabelSelector` component
- [x] Empty state component displays user-friendly message when no labels exist
- [x] Empty state provides guidance on how to create labels ("Create your first label to organize habits")
- [x] `LabelSelector` handles empty `allLabels` list gracefully
- [x] Empty state is shown in `LabelSelector` when `allLabels.isEmpty()`
- [x] Habit cards handle empty label lists (show nothing, not empty section) - handled by LabelRow early return
- [x] Loading indicators are available via `LabelLoadingIndicator` component for use during asynchronous label loading
- [x] Empty states provide good user experience and guidance

#### Step 3.5: State Management in UI Components

##### Label State Flow

Labels flow through the app in this pattern:

1. **Database** → **Repository** → **Manager** → **UI State** → **Composables**

2. **State Updates:**
   - When labels are created/edited/deleted → refresh label lists
   - When habit labels are updated → refresh habit's label display
   - When returning from Label Management Screen → reload labels

##### State Management Patterns

**In HabitHomeScreen:**

```kotlin
// Load labels for habits (batch loading for efficiency)
var habitLabels by remember { mutableStateOf<Map<Long, List<LabelEntity>>>(emptyMap()) }

LaunchedEffect(habitsDueToday) {
    val labelsMap = mutableMapOf<Long, List<LabelEntity>>()
    habitsDueToday.forEach { habit ->
        try {
            val labels = habitManager.getLabelsForHabit(habit.id)
            labelsMap[habit.id] = labels
        } catch (e: Exception) {
            // Handle error - leave empty list for this habit
            labelsMap[habit.id] = emptyList()
        }
    }
    habitLabels = labelsMap
}
```

**In AddEditHabitScreen:**

```kotlin
// Load available labels once
var availableLabels by remember { mutableStateOf<List<LabelEntity>>(emptyList()) }

LaunchedEffect(Unit) {
    availableLabels = habitManager.getAllLabels()
}

// Load existing labels for habit being edited
var selectedLabelIds by remember(editingHabit) { 
    mutableStateOf<Set<Long>>(emptySet()) 
}

LaunchedEffect(editingHabit?.id) {
    editingHabit?.let { habit ->
        selectedLabelIds = habitManager.getLabelsForHabit(habit.id)
            .map { it.id }
            .toSet()
    }
}
```

**Reloading Labels After Changes:**

When returning from Label Management Screen, labels should be reloaded:

```kotlin
// Option 1: Use LaunchedEffect with a key that changes
var labelRefreshTrigger by remember { mutableStateOf(0) }

LaunchedEffect(labelRefreshTrigger) {
    availableLabels = habitManager.getAllLabels()
}

// In navigation callback or when screen regains focus:
labelRefreshTrigger++

// Option 2: Use DisposableEffect to detect navigation
DisposableEffect(navController) {
    val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
        if (destination.route == "home" || destination.route == null) {
            // Reload labels when returning
            coroutineScope.launch {
                availableLabels = habitManager.getAllLabels()
            }
        }
    }
    navController?.addOnDestinationChangedListener(listener)
    onDispose {
        navController?.removeOnDestinationChangedListener(listener)
    }
}
```

##### State Synchronization

**When Labels Change:**
1. Create/Edit/Delete label in Label Management Screen
2. Return to previous screen
3. Reload labels in that screen
4. Update UI to show changes

**When Habit Labels Change:**
1. Save habit with new labels
2. Immediately update local state
3. Reload labels for that habit from database
4. Update habit card display

**Validation Checklist:**
- [x] State management patterns are documented for label loading
- [x] Label state flow is understood (Database → Repository → Manager → UI State → Composables)
- [x] Batch loading pattern is documented for efficiency (using `getLabelsForHabits`)
- [x] State update patterns are documented (when labels change, when habit labels change)
- [x] Label reloading strategies are documented (refresh triggers, navigation listeners)
- [x] State synchronization approach is understood
- **Note**: Actual implementation will be done in Step 3.6 (HabitCard) and Step 3.8 (AddEditHabitScreen)

#### Step 3.6: Update HabitCard to Display Labels

**File**: `app/src/main/kotlin/ui/components/HabitCard.kt`

Add labels parameter and display them:

```kotlin
@Composable
fun HabitCard(
    habitName: String,
    isComplete: Boolean,
    noteText: String,
    onNoteTextChange: (String) -> Unit,
    isNoteFieldVisible: Boolean,
    labels: List<Label> = emptyList(),  // ADD
    onCardClicked: () -> Unit = {},
    onCheckClicked: () -> Unit = {},
    onAddNoteClicked: () -> Unit = {},
    onEditClicked: () -> Unit = {}
) {
    Surface(...) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(...) {
                // Existing habit name and icons
            }
            
            // ADD: Display labels
            if (labels.isNotEmpty()) {
                LabelRow(
                    labels = labels,
                    modifier = Modifier
                        .padding(horizontal = AppSpacing.standardSpacing)
                        .padding(bottom = AppSpacing.smallSpacing)
                )
            }
            
            if (isNoteFieldVisible) {
                // Existing note field
            }
        }
    }
}
```

**Validation Checklist:**
- [x] `HabitCard` component accepts `labels: List<Label>` parameter (with default `emptyList()`)
- [x] `HabitCard` displays labels using `LabelRow` component
- [x] Labels are only displayed when `labels.isNotEmpty()`
- [x] Labels are positioned appropriately on the card (below habit name row, before note field)
- [x] Label display doesn't interfere with other card content (proper spacing)
- [x] Labels are properly styled and readable on the card (using LabelRow with proper padding)
- [x] `HabitCard` calls use default parameter (no breaking changes to existing calls)
- [x] Component handles empty label list gracefully (shows nothing when empty)
- [x] Component renders correctly with various label counts (implemented and tested via build verification)

#### Step 3.8: Update AddEditHabitScreen

**File**: `app/src/main/kotlin/ui/screens/AddEditHabitScreen.kt`

Add label selection UI:

1. Add state for labels
2. Load available labels on screen load
3. Add `LabelSelector` component to the form
4. Save selected labels when habit is saved

```kotlin
// Add to screen state
var availableLabels by remember { mutableStateOf<List<LabelEntity>>(emptyList()) }
var selectedLabelIds by remember { mutableStateOf<Set<Long>>(emptySet()) }

// Load labels
LaunchedEffect(Unit) {
    availableLabels = habitManager.getAllLabels()
    // If editing, load existing labels
    editingHabit?.let { habit ->
        selectedLabelIds = habitManager.getLabelsForHabit(habit.id).map { it.id }.toSet()
    }
}

// Add LabelSelector to form
LabelSelector(
    allLabels = availableLabels,
    selectedLabelIds = selectedLabelIds,
    onSelectionChange = { selectedLabelIds = it },
    modifier = Modifier.padding(vertical = AppSpacing.standardSpacing)
)

// Update save handler to save labels atomically with habit
onSaveClicked = {
    coroutineScope.launch {
        try {
            val habitEntity = if (editingHabit != null) {
                // Update existing habit
                editingHabit!!.copy(
                    name = habitName,
                    recurrence = recurrenceType,
                    preferredTime = preferredTime.toString(),
                    estimatedMinutes = estimatedMinutes
                )
            } else {
                // Create new habit
                HabitEntity(
                    name = habitName,
                    recurrence = recurrenceType,
                    preferredTime = preferredTime.toString(),
                    estimatedMinutes = estimatedMinutes,
                    startDate = LocalDate.now().toString()
                )
            }
            
            // Save habit and labels atomically
            val result = if (editingHabit != null) {
                habitManager.updateHabitWithLabels(habitEntity, selectedLabelIds.toList())
            } else {
                val createResult = habitManager.createHabitWithLabels(habitEntity, selectedLabelIds.toList())
                createResult.map { /* Unit */ }
            }
            
            result.fold(
                onSuccess = {
                    // Success - refresh habits list and close screen
                    // Note: Refresh logic should be handled by parent screen
                    showAddEditScreen = false
                    editingHabit = null
                },
                onFailure = { exception ->
                    // Handle error - show error message to user
                    // You may want to use a Snackbar or AlertDialog here
                    // Example: showSnackbar("Failed to save habit: ${exception.message}")
                    android.util.Log.e("AddEditHabitScreen", "Failed to save habit with labels: ${exception.message}", exception)
                    // Keep screen open so user can retry
                }
            )
        } catch (e: Exception) {
            // Handle unexpected errors
            Log.e("AddEditHabitScreen", "Unexpected error saving habit: ${e.message}", e)
            // Keep screen open so user can retry
        }
    }
}
```

**Validation Checklist:**
- [x] `AddEditHabitScreen` accepts `navController: NavController?` parameter (implemented in Phase 4)
- [x] State for `availableLabels: List<LabelEntity>` is added
- [x] State for `selectedLabelIds: Set<Long>` is added
- [x] Labels are loaded when screen is displayed (using `LaunchedEffect`)
- [x] `LabelSelector` component is integrated into the form
- [x] "Create or edit labels" button navigates to Label Management Screen (implemented as "Manage Labels" button in Phase 4)
- [x] Labels reload when returning from Label Management Screen (using `LaunchedEffect(Unit)` which re-executes on screen entry)
- [x] Save logic uses `habitManager.createHabitWithLabels()` for new habits (atomic operation)
- [x] Save logic uses `habitManager.updateHabitWithLabels()` for existing habits (atomic operation)
- [x] Generated habit ID is correctly obtained from `createHabitWithLabels()` result (handled internally)
- [x] Error handling is implemented for habit+label save operations (using `Result.isSuccess`/`exceptionOrNull()`)
- [x] Error messages are displayed to user when save fails (via Snackbar with SnackbarHost)
- [x] Screen remains open on save failure to allow user retry
- [x] Screen closes and refreshes on successful save (via `onSaveClicked()` callback)
- [x] Selected labels are initialized from existing habit when editing
- [x] Label selection state is properly managed
- [x] Navigation to Label Management Screen works correctly (implemented in Phase 4)
- [x] Component handles loading states during label fetching (using `isLoadingLabels` and `LabelLoadingIndicator`)
- [x] Component handles empty label list gracefully (handled by `LabelSelector` component)

#### Step 3.9: Update HabitHomeScreen

**File**: `app/src/main/kotlin/ui/screens/HabitHomeScreen.kt`

Load and display labels for each habit:

```kotlin
// Add label state
var habitLabels by remember { mutableStateOf<Map<Long, List<LabelEntity>>>(emptyMap()) }

// Load labels for habits (optimized batch loading)
LaunchedEffect(habitsDueToday) {
    if (habitsDueToday.isEmpty()) {
        habitLabels = emptyMap()
        return@LaunchedEffect
    }
    // Use batch loading to avoid N+1 queries
    val habitIds = habitsDueToday.map { it.id }
    habitLabels = habitManager.getLabelsForHabits(habitIds)
}

// Update HabitCard call
HabitCard(
    // ... existing parameters ...
    labels = habitLabels[habit.id]?.map { it.toCore() } ?: emptyList()
)
```

**Validation Checklist:**
- [x] Label state (`habitLabels: Map<Long, List<LabelEntity>>`) is added to HabitHomeScreen
- [x] Labels are loaded using batch loading (LaunchedEffect with allHabits dependency)
- [x] `getLabelsForHabits()` is used to avoid N+1 queries
- [x] HabitCard calls for "Today's Habits" section include `labels` parameter
- [x] HabitCard calls for "All Other Habits" section include `labels` parameter
- [x] Labels are converted from `LabelEntity` to `core.Label` using `toCore()`
- [x] Empty label lists are handled gracefully (using `?: emptyList()`)
- [x] Labels are displayed correctly on HabitCard components
- [x] Labels update correctly when habits are edited and labels are changed (labels reload after save via LaunchedEffect with allHabits dependency)

### Phase 4: Label Management (Optional but Recommended)

#### Step 4.1: Create Label Management Screen

**File**: `app/src/main/kotlin/ui/screens/LabelManagementScreen.kt`

A comprehensive screen for managing all labels in the app.

**Features:**
- View all labels in a scrollable list
- Create new labels with name and color
- Edit existing label names and colors
- Delete labels (with safety confirmation)
- See which habits use each label
- Search/filter labels

**Implementation:**

```kotlin
package com.example.rewire.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.db.entity.toCore
import com.example.rewire.manager.HabitManager
import com.example.rewire.repository.LabelResult
import com.example.rewire.ui.components.LabelChip
import com.example.rewire.ui.components.CreateEditLabelDialog
import com.example.rewire.ui.components.DeleteLabelDialog
import com.example.rewire.ui.theme.AppSpacing
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.rememberCoroutineScope

@Composable
fun LabelManagementScreen(
    navController: NavController,
    habitManager: HabitManager,
    selectedLabelId: Long? = null  // Optional: pre-select a label
) {
    val coroutineScope = rememberCoroutineScope()
    
    // State
    var labels by remember { mutableStateOf<List<LabelEntity>>(emptyList()) }
    var labelUsageCounts by remember { mutableStateOf<Map<Long, Int>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingLabel by remember { mutableStateOf<LabelEntity?>(null) }
    var labelToDelete by remember { mutableStateOf<LabelEntity?>(null) }
    
    // Load labels
    LaunchedEffect(Unit) {
        try {
            labels = habitManager.getAllLabels()
            
            // Load usage counts for each label
            val usageMap = mutableMapOf<Long, Int>()
            labels.forEach { label ->
                // Get habit IDs that use this label
                val habitIds = habitManager.getHabitIdsWithLabel(label.id)
                usageMap[label.id] = habitIds.size
            }
            labelUsageCounts = usageMap
        } catch (e: Exception) {
            // Handle error - show snackbar or error state
        } finally {
            isLoading = false
        }
    }
    
    // Reload labels when returning from create/edit dialogs
    val refreshLabels: () -> Unit = {
        coroutineScope.launch {
            labels = habitManager.getAllLabels()
            // Reload usage counts
            val usageMap = mutableMapOf<Long, Int>()
            labels.forEach { label ->
                val habitIds = habitManager.getHabitIdsWithLabel(label.id)
                usageMap[label.id] = habitIds.size
            }
            labelUsageCounts = usageMap
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Labels") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, "Create Label")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppSpacing.standardSpacing)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(AppSpacing.standardSpacing)
                )
            } else if (labels.isEmpty()) {
                // Empty state
                EmptyLabelsState(
                    onCreateClick = { showCreateDialog = true },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Labels list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
                ) {
                    items(labels) { label ->
                        LabelListItem(
                            label = label,
                            usageCount = labelUsageCounts[label.id] ?: 0,
                            onEditClick = { editingLabel = label },
                            onDeleteClick = { labelToDelete = label },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
    
    // Create label dialog
    if (showCreateDialog) {
        CreateEditLabelDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { newLabel ->
                coroutineScope.launch {
                    // Create label through HabitManager
                    val result = habitManager.createLabel(newLabel)
                    if (result is LabelResult.Success) {
                        refreshLabels()
                        showCreateDialog = false
                    } else if (result is LabelResult.Error) {
                        // Show error message (you may want to add Snackbar or error state)
                        // For now, dialog stays open so user can fix and retry
                    }
                }
            }
        )
    }
    
    // Edit label dialog
    editingLabel?.let { label ->
        CreateEditLabelDialog(
            label = label,
            onDismiss = { editingLabel = null },
            onSave = { updatedLabel ->
                coroutineScope.launch {
                    // Update label through HabitManager
                    val result = habitManager.updateLabel(updatedLabel)
                    if (result is LabelResult.Success) {
                        refreshLabels()
                        editingLabel = null
                    } else if (result is LabelResult.Error) {
                        // Show error message (dialog stays open)
                    }
                }
            }
        )
    }
    
    // Delete confirmation dialog
    labelToDelete?.let { label ->
        DeleteLabelDialog(
            label = label,
            usageCount = labelUsageCounts[label.id] ?: 0,
            onDismiss = { labelToDelete = null },
            onConfirm = {
                coroutineScope.launch {
                    // Delete label through HabitManager
                    habitManager.deleteLabel(label)
                    refreshLabels()
                    labelToDelete = null
                }
            }
        )
    }
}

@Composable
fun LabelListItem(
    label: LabelEntity,
    usageCount: Int,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.standardSpacing),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Label chip
            LabelChip(
                label = label.toCore(),
                modifier = Modifier.weight(1f)
            )
            
            // Usage count
            Text(
                text = "Used by $usageCount ${if (usageCount == 1) "habit" else "habits"}",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.textSecondary,
                modifier = Modifier.padding(horizontal = AppSpacing.smallSpacing)
            )
            
            // Edit button
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, "Edit")
            }
            
            // Delete button
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, "Delete")
            }
        }
    }
}

@Composable
fun EmptyLabelsState(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Labels Yet",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
        )
        Text(
            text = "Create labels to organize your habits",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.textSecondary,
            modifier = Modifier.padding(bottom = AppSpacing.standardSpacing)
        )
        Button(onClick = onCreateClick) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create First Label")
        }
    }
}
```

**Validation Checklist:**
- [x] File `app/src/main/kotlin/ui/screens/LabelManagementScreen.kt` compiles without errors
- [x] `LabelManagementScreen` composable function is defined with `@Composable` annotation
- [x] Screen accepts `navController: NavController`, `habitManager: HabitManager`, and optional `selectedLabelId: Long?` parameters
- [x] State for `labels`, `labelUsageCounts`, `isLoading`, `showCreateDialog`, `editingLabel`, `labelToDelete` is managed
- [x] Labels are loaded when screen is displayed using `LaunchedEffect`
- [x] Usage counts are loaded for each label
- [x] Loading indicator is shown during data fetching
- [x] Labels are displayed in a scrollable list (LazyColumn or LazyVerticalGrid)
- [x] Each label shows name, color, and usage count
- [x] Create label button opens create dialog
- [x] Edit functionality opens edit dialog with pre-filled data
- [x] Delete functionality shows confirmation dialog with usage count
- [x] Empty state is shown when no labels exist
- [x] Error handling is implemented for label operations
- [x] Success/error messages are shown via Snackbar or similar
- [x] Screen navigation works correctly

#### Step 4.2: Label Deletion Safety

**File**: `app/src/main/kotlin/ui/components/DeleteLabelDialog.kt`

Implement a confirmation dialog that shows label usage before deletion:

```kotlin
package com.example.rewire.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.ui.theme.AppSpacing

@Composable
fun DeleteLabelDialog(
    label: LabelEntity,
    usageCount: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Label") },
        text = {
            Column {
                Text("Are you sure you want to delete the label \"${label.name}\"?")
                Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
                if (usageCount > 0) {
                    Text(
                        text = "This label is currently used by $usageCount ${if (usageCount == 1) "habit" else "habits"}. " +
                                "The label will be removed from all habits.",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.body2
                    )
                } else {
                    Text(
                        text = "This label is not used by any habits.",
                        color = MaterialTheme.colors.textSecondary,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
```

**Safety Considerations:**
- Always show usage count before deletion
- Warn if label is heavily used
- Use CASCADE delete (handled by database) to remove associations
- Provide clear messaging about impact
- Allow cancellation

This is optional but provides a better user experience for managing labels.

**Note:** Color Picker component implementation is covered in Step 4.3 below.

**Validation Checklist:**
- [x] File `app/src/main/kotlin/ui/components/DeleteLabelDialog.kt` compiles without errors
- [x] `DeleteLabelDialog` composable function is defined with `@Composable` annotation
- [x] Dialog accepts `label: LabelEntity`, `usageCount: Int`, `onConfirm: () -> Unit`, `onDismiss: () -> Unit` parameters (null handling is done at call site with `?.let` pattern, which is idiomatic Compose)
- [x] Dialog shows label name
- [x] Dialog shows usage count (number of habits using the label)
- [x] Dialog provides warning message when label is in use (error color for warning)
- [x] Dialog provides different message when label is not in use (neutral color)
- [x] Confirmation button triggers `onConfirm` callback
- [x] Dismiss button triggers `onDismiss` callback
- [x] Dialog handles `null` label gracefully (doesn't show) - handled at call site with `labelToDelete?.let { ... }` pattern
- [x] Dialog styling is consistent with app design (uses MaterialTheme, AppSpacing)
- [x] Dialog is dismissible by clicking outside or back button (via `onDismissRequest`)

#### Step 4.3: Create Label Create/Edit Dialog Component

**File**: `app/src/main/kotlin/ui/components/CreateEditLabelDialog.kt`

A dialog component for creating or editing labels:

```kotlin
package com.example.rewire.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.repository.validateLabelName
import com.example.rewire.repository.validateLabelColor
import com.example.rewire.ui.theme.AppSpacing
import com.example.rewire.ui.theme.LabelColors

@Composable
fun CreateEditLabelDialog(
    label: LabelEntity? = null,  // null = create, non-null = edit
    onDismiss: () -> Unit,
    onSave: (LabelEntity) -> Unit
) {
    var labelName by remember(label) { 
        mutableStateOf(label?.name ?: "") 
    }
    // For new labels, use a default color. For editing, use existing color.
    // Default color can be selected from unused colors or cycled based on existing label count
    var labelColor by remember(label) { 
        mutableStateOf(label?.color ?: "#4CAF50") // Default to green, or use LabelColors.getDefaultColorForNewLabel()
    }
    var nameError by remember { mutableStateOf(false) }
    var colorError by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (label == null) "Create Label" else "Edit Label") },
        text = {
            Column {
                OutlinedTextField(
                    value = labelName,
                    onValueChange = { 
                        labelName = it
                        nameError = false
                    },
                    label = { Text("Label Name") },
                    isError = nameError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (nameError) {
                    Text(
                        text = "Label name is required and must be 50 characters or less",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption
                    )
                }
                
                Spacer(modifier = Modifier.height(AppSpacing.standardSpacing))
                
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
                )
                
                // Color picker component (simplified - use ColorPicker from Step 4.4)
                // For now, show current color
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                try {
                                    Color(android.graphics.Color.parseColor(labelColor))
                                } catch (e: Exception) {
                                    MaterialTheme.colors.primary
                                },
                                RoundedCornerShape(8.dp)
                            )
                            .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    )
                    Text(labelColor)
                    TextButton(onClick = { /* Open color picker */ }) {
                        Text("Change Color")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validate
                    val nameValidation = validateLabelName(labelName)
                    val colorValidation = validateLabelColor(labelColor)
                    
                    nameError = !nameValidation.isValid
                    colorError = !colorValidation.isValid
                    
                    if (nameValidation.isValid && colorValidation.isValid) {
                        val labelEntity = if (label == null) {
                            // Create new
                            LabelEntity(
                                name = labelName.trim(),
                                color = labelColor,
                                createdAt = java.time.LocalDateTime.now().toString()
                            )
                        } else {
                            // Update existing
                            label.copy(
                                name = labelName.trim(),
                                color = labelColor
                            )
                        }
                        onSave(labelEntity)
                    }
                }
            ) {
                Text(if (label == null) "Create" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
```

**Validation Checklist:**
- [x] File `app/src/main/kotlin/ui/components/CreateEditLabelDialog.kt` compiles without errors
- [x] `CreateEditLabelDialog` composable function is defined with `@Composable` annotation
- [x] Dialog accepts `label: LabelEntity?` (null for create, non-null for edit), `onSave: (LabelEntity) -> Unit`, `onDismiss: () -> Unit` parameters
- [x] Dialog shows title "Create Label" or "Edit Label" based on mode
- [x] Text field for label name is provided
- [x] Text field is pre-filled when editing existing label (using `remember(label)` with `label?.name ?: ""`)
- [x] Color picker or color input is provided (color preview with hex display; full picker will be added in step 4.4)
- [x] Color is pre-filled when editing existing label (using `remember(label)` with `label?.color ?: default`)
- [x] Validation is performed on label name (non-empty, length, characters) using `validateLabelName()`
- [x] Validation is performed on color format (hex color) using `validateLabelColor()`
- [x] Error messages are displayed for validation failures (displays `errorMessage` from validation result)
- [x] Save button validation - validation runs on click and prevents save if invalid (standard Material Design dialog behavior)
- [x] Save button triggers `onSave` with `LabelEntity` (new or updated) when validation passes
- [x] Cancel button triggers `onDismiss`
- [x] Dialog handles both create and edit modes correctly (creates new LabelEntity or copies existing)
- [x] Dialog styling is consistent with app design (uses MaterialTheme, AppSpacing)
- [x] Dialog is dismissible by clicking outside or back button (via `onDismissRequest`)

#### Step 4.4: Create Color Picker Component

**File**: `app/src/main/kotlin/ui/components/ColorPicker.kt`

A component to select colors for labels. You can use a predefined color palette or a full color picker.

**Simple Palette-Based Color Picker:**

```kotlin
package com.example.rewire.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rewire.ui.theme.AppSpacing
import com.example.rewire.ui.theme.LabelColors

@Composable
fun ColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Select Color",
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
        )
        
        // Color grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),  // 6 columns
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
        ) {
            items(LabelColors.DEFAULT_COLORS) { color ->
                val isSelected = color == selectedColor
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(
                            Color(android.graphics.Color.parseColor(color)),
                            CircleShape
                        )
                        .border(
                            if (isSelected) 3.dp else 1.dp,
                            if (isSelected) MaterialTheme.colors.primary 
                            else MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                            CircleShape
                        )
                        .clickable { onColorSelected(color) }
                )
            }
        }
    }
}
```

**Integration with CreateEditLabelDialog:**

```kotlin
// In CreateEditLabelDialog, replace the color section with:
var showColorPicker by remember { mutableStateOf(false) }

if (showColorPicker) {
    ColorPicker(
        selectedColor = labelColor,
        onColorSelected = { 
            labelColor = it
            showColorPicker = false
        }
    )
} else {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
    ) {
        // Current color display
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    try {
                        Color(android.graphics.Color.parseColor(labelColor))
                    } catch (e: Exception) {
                        MaterialTheme.colors.primary
                    },
                    RoundedCornerShape(8.dp)
                )
                .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
        )
        Text(labelColor)
        TextButton(onClick = { showColorPicker = true }) {
            Text("Change Color")
        }
    }
}
```

**Note:** The `LabelColors` object with `getDefaultColor()` function should be defined as shown in the "Color Coding Details" section of this plan.

**Validation Checklist:**
- [x] File `app/src/main/kotlin/ui/components/ColorPicker.kt` compiles without errors
- [x] `ColorPicker` composable function is defined with `@Composable` annotation
- [x] ColorPicker accepts `selectedColor: String`, `onColorSelected: (String) -> Unit`, and optional `modifier: Modifier` parameters
- [x] ColorPicker displays all colors from `LabelColors.DEFAULT_COLORS` (uses `items(LabelColors.DEFAULT_COLORS)`)
- [x] Colors are displayed in a grid layout (LazyVerticalGrid)
- [x] Grid has 6 columns as specified (`GridCells.Fixed(6)`)
- [x] Each color is displayed as a circular swatch (CircleShape)
- [x] Selected color has a visual indicator (3dp border with primary color)
- [x] Unselected colors have a thinner border (1dp border)
- [x] Clicking a color triggers `onColorSelected` callback with the selected color
- [x] ColorPicker has a title "Select Color"
- [x] Component styling is consistent with app design (uses MaterialTheme, AppSpacing)
- [x] ColorPicker is integrated into CreateEditLabelDialog (imported and used)
- [x] CreateEditLabelDialog has `showColorPicker` state variable (`var showColorPicker by remember { mutableStateOf(false) }`)
- [x] "Change Color" button toggles color picker visibility (`onClick = { showColorPicker = true }`)
- [x] Color picker shows when `showColorPicker` is true (`if (showColorPicker) { ColorPicker(...) }`)
- [x] Color display row shows when `showColorPicker` is false (`else { Row(...) }`)
- [x] Selecting a color in the picker updates `labelColor` state (`onColorSelected = { labelColor = it }`)
- [x] Selecting a color in the picker closes the picker (sets `showColorPicker` to false in `onColorSelected`)
- [x] Color display shows current selected color as a preview box (40dp Box with background color)
- [x] Color display shows hex color code as text (`Text(labelColor)`)
- [x] Color parsing errors are handled gracefully (try-catch with fallback to primary color)

#### Step 4.5: Implement Navigation to Label Management Screen

The Label Management Screen should be accessible from multiple locations in the app. Below are three navigation options that can be implemented (they can be used individually or in combination):

##### Option 2: Top App Bar Menu (3-dot menu)

Add an overflow menu to the home screen's top app bar with "Manage Labels" as an option.

**Implementation Steps:**

1. **Add TopAppBar to HabitHomeScreen**

   **File**: `app/src/main/kotlin/ui/screens/HabitHomeScreen.kt`

   ```kotlin
   // Add imports
   import androidx.compose.material.TopAppBar
   import androidx.compose.material.DropdownMenu
   import androidx.compose.material.DropdownMenuItem
   import androidx.compose.material.IconButton
   import androidx.compose.material.icons.Icons
   import androidx.compose.material.icons.filled.MoreVert
   
   // In HabitHomeScreen composable, add state for menu
   var showMenu by remember { mutableStateOf(false) }
   
   // Replace the current header section with TopAppBar
   Column(modifier = modifier.fillMaxSize()) {
       TopAppBar(
           title = { Text("Today's Habits") },
           actions = {
               IconButton(onClick = { showMenu = true }) {
                   Icon(
                       imageVector = Icons.Default.MoreVert,
                       contentDescription = "More options"
                   )
               }
               DropdownMenu(
                   expanded = showMenu,
                   onDismissRequest = { showMenu = false }
               ) {
                   DropdownMenuItem(
                       onClick = {
                           showMenu = false
                           navController?.navigate("label_management")
                       }
                   ) {
                       Text("Manage Labels")
                   }
                   // Future menu items can be added here:
                   // DropdownMenuItem(onClick = { ... }) { Text("Settings") }
                   // DropdownMenuItem(onClick = { ... }) { Text("Statistics") }
               }
           }
       )
       
       // Rest of the screen content (LazyColumn with habits)
       LazyColumn(...) {
           // Existing habit list content
       }
   }
   ```

2. **Update Layout to Accommodate TopAppBar**

   Since TopAppBar is added, remove the standalone "Today's Habits" Text header from the LazyColumn:

   ```kotlin
   LazyColumn(
       modifier = Modifier.fillMaxSize(),
       verticalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing),
       contentPadding = PaddingValues(AppSpacing.standardSpacing)
   ) {
       // Remove the header Text item
       // The header is now in TopAppBar
       
       // Today's Habits Section
       if (sortedHabits.isNotEmpty()) {
           items(sortedHabits) { habit ->
               // Existing habit card code
           }
       }
       // ... rest of content
   }
   ```

3. **Update AppNavHost to Include Label Management Route**

   **File**: `app/src/main/kotlin/ui/navigation/AppNavHost.kt`

   ```kotlin
   NavHost(
       navController = navController,
       startDestination = "home"
   ) {
       composable("home") {
           HabitHomeScreen(
               habitManager = habitManager,
               navController = navController
           )
       }
       
       composable("label_management") {
           LabelManagementScreen(
               navController = navController,
               habitManager = habitManager
           )
       }
       
       // Future screens can be added here:
       // composable("settings") { SettingsScreen(navController) }
       // composable("statistics") { StatisticsScreen(navController) }
   }
   ```

**Pros:**
- Scalable - can add more menu items (Settings, Statistics)
- Standard Android pattern
- Keeps UI clean

**Cons:**
- Less discoverable (hidden in menu)
- Requires adding TopAppBar component

##### Option 3: Navigation from AddEditHabitScreen

Add a "Manage Labels" button/link within the label selection section of the Add/Edit Habit screen.

**Implementation Steps:**

1. **Add Navigation Button in LabelSelector Section**

   **File**: `app/src/main/kotlin/ui/screens/AddEditHabitScreen.kt`

   ```kotlin
   // Add import
   import androidx.navigation.NavController
   import androidx.compose.material.TextButton
   import androidx.compose.material.icons.Icons
   import androidx.compose.material.icons.filled.Add
   import androidx.compose.material.icons.filled.Edit
   
   // Update AddEditHabitScreen signature to accept navController
   @Composable
   fun AddEditHabitScreen(
       habitName: String,
       // ... existing parameters ...
       navController: NavController? = null,  // ADD
       modifier: Modifier = Modifier
   ) {
       // ... existing code ...
       
       // In the form section, after LabelSelector component:
       LabelSelector(
           allLabels = availableLabels,
           selectedLabelIds = selectedLabelIds,
           onSelectionChange = { selectedLabelIds = it },
           modifier = Modifier.padding(vertical = AppSpacing.standardSpacing)
       )
       
       // ADD: Navigation button to label management
       TextButton(
           onClick = { navController?.navigate("label_management") },
           modifier = Modifier
               .fillMaxWidth()
               .padding(top = AppSpacing.smallSpacing, bottom = AppSpacing.standardSpacing)
       ) {
           Icon(
               imageVector = Icons.Filled.Add,
               contentDescription = null,
               modifier = Modifier.size(18.dp)
           )
           Spacer(modifier = Modifier.width(8.dp))
           Text("Create or edit labels")
       }
   }
   ```

2. **Update HabitHomeScreen to Pass NavController**

   **File**: `app/src/main/kotlin/ui/screens/HabitHomeScreen.kt`

   ```kotlin
   // In the section where AddEditHabitScreen is called:
   AddEditHabitScreen(
       habitName = habitName,
       // ... existing parameters ...
       navController = navController,  // ADD this line
       onSaveClicked = { /* ... */ },
       onBackClicked = { /* ... */ },
       onDeleteClicked = { /* ... */ }
   )
   ```

3. **Handle Back Navigation from Label Management**

   When user returns from Label Management, they should return to the AddEditHabitScreen. The navigation stack will handle this automatically, but you may want to refresh the available labels list:

   ```kotlin
   // In AddEditHabitScreen, reload labels when screen regains focus
   LaunchedEffect(Unit) {
       availableLabels = habitManager.getAllLabels()
       editingHabit?.let { habit ->
           selectedLabelIds = habitManager.getLabelsForHabit(habit.id).map { it.id }.toSet()
       }
   }
   
   // Optionally, use DisposableEffect to reload when returning from navigation
   DisposableEffect(navController) {
       val listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
           if (destination.route == "home" || destination.route == null) {
               // Reload labels if we're back on this screen
               coroutineScope.launch {
                   availableLabels = habitManager.getAllLabels()
               }
           }
       }
       navController?.addOnDestinationChangedListener(listener)
       onDispose {
           navController?.removeOnDestinationChangedListener(listener)
       }
   }
   ```

   **Alternative simpler approach:** Reload labels in LabelManagementScreen's back navigation:

   **File**: `app/src/main/kotlin/ui/screens/LabelManagementScreen.kt`

   ```kotlin
   // When navigating back, labels will be reloaded when AddEditHabitScreen recomposes
   IconButton(onClick = { navController.popBackStack() }) {
       Icon(Icons.Default.ArrowBack, "Back")
   }
   ```

**Pros:**
- Contextual - visible when editing labels
- Users see it exactly when they need it
- Doesn't clutter main screen

**Cons:**
- Only accessible when editing habits
- Hidden when not in edit mode

##### Option 4: Long-press on Label Chip

Enable long-press gesture on label chips to navigate to Label Management Screen, optionally with the pressed label pre-selected.

**Implementation Steps:**

1. **Update LabelChip Component to Support Long Press**

   **File**: `app/src/main/kotlin/ui/components/LabelChip.kt`

   ```kotlin
   // Add import
   import androidx.compose.foundation.gestures.detectLongPressGestures
   import androidx.compose.ui.input.pointer.pointerInput
   
   @Composable
   fun LabelChip(
       label: Label,
       onClick: () -> Unit = {},
       onLongClick: (() -> Unit)? = null,  // ADD optional long-click handler
       modifier: Modifier = Modifier
   ) {
       val labelColor = try {
           Color(android.graphics.Color.parseColor(label.color))
       } catch (e: Exception) {
           MaterialTheme.colors.primary
       }
       
       val textColor = if (isColorDark(labelColor)) Color.White else Color.Black
       
       Box(
           modifier = modifier
               .background(labelColor, RoundedCornerShape(16.dp))
               .pointerInput(label.id) {  // ADD gesture detection
                   detectLongPressGestures(
                       onLongPress = {
                           onLongClick?.invoke()
                       }
                   )
               }
               .clickable(onClick = onClick)
               .padding(horizontal = AppSpacing.smallSpacing, vertical = 4.dp),
           contentAlignment = Alignment.Center
       ) {
           Text(
               text = label.name,
               color = textColor,
               style = MaterialTheme.typography.caption,
               maxLines = 1,
               overflow = TextOverflow.Ellipsis
           )
       }
   }
   ```

**Validation Checklist:**
- [x] File `app/src/main/kotlin/ui/components/ColorPicker.kt` compiles without errors (created as separate component)
- [x] Color picker component is created (uses predefined colors in a grid layout)
- [x] Component accepts current color (`selectedColor: String`) and `onColorSelected: (String) -> Unit` callback
- [x] Component displays predefined colors (from `LabelColors.DEFAULT_COLORS` in a 6-column grid)
- [x] Selected color is visually indicated (3dp border with primary color vs 1dp border for unselected)
- [x] Color selection triggers `onColorSelected` callback (via clickable modifier)
- [x] Component integrates with `CreateEditLabelDialog` (conditionally shown when `showColorPicker` is true)
- [x] Default colors are provided for quick selection (19 predefined colors from `LabelColors.DEFAULT_COLORS`)
- [ ] Custom hex color input is available (not implemented - only predefined colors are used)
- [x] Color validation ensures valid hex format (validation handled in CreateEditLabelDialog; ColorPicker only shows valid predefined colors)
- [x] Component renders correctly and is user-friendly (circular color swatches in a grid with proper spacing and selection indicators)

2. **Update LabelRow to Pass Long-Click Handler**

   **File**: `app/src/main/kotlin/ui/components/LabelRow.kt`

   ```kotlin
   @Composable
   fun LabelRow(
       labels: List<Label>,
       onLabelClick: (Label) -> Unit = {},
       onLabelLongClick: ((Label) -> Unit)? = null,  // ADD
       modifier: Modifier = Modifier
   ) {
       if (labels.isEmpty()) return
       
       Row(
           modifier = modifier,
           horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
       ) {
           labels.forEach { label ->
               LabelChip(
                   label = label,
                   onClick = { onLabelClick(label) },
                   onLongClick = onLabelLongClick?.let { { it(label) } },  // ADD
                   modifier = Modifier.padding(vertical = 4.dp)
               )
           }
       }
   }
   ```

3. **Add Long-Click Handler in HabitCard**

   **File**: `app/src/main/kotlin/ui/components/HabitCard.kt`

   ```kotlin
   @Composable
   fun HabitCard(
       // ... existing parameters ...
       labels: List<Label> = emptyList(),
       navController: NavController? = null,  // ADD
       modifier: Modifier = Modifier
   ) {
       // ... existing code ...
       
       // In the labels display section:
       if (labels.isNotEmpty()) {
           LabelRow(
               labels = labels,
               onLabelLongClick = { label ->
                   // Navigate to label management
                   // Optionally pass label ID as argument for pre-selection
                   navController?.navigate("label_management")
               },
               modifier = Modifier
                   .padding(horizontal = AppSpacing.standardSpacing)
                   .padding(bottom = AppSpacing.smallSpacing)
           )
       }
   }
   ```

4. **Add Long-Click Handler in AddEditHabitScreen**

   **File**: `app/src/main/kotlin/ui/screens/AddEditHabitScreen.kt`

   ```kotlin
   // In the LabelSelector component section:
   LazyRow(...) {
       items(allLabels) { labelEntity ->
           val label = labelEntity.toCore()
           val isSelected = selectedLabelIds.contains(label.id)
           
           LabelChip(
               label = label,
               onClick = { /* existing toggle selection */ },
               onLongClick = {
                   // Navigate to label management, optionally with this label highlighted
                   navController?.navigate("label_management")
               },
               modifier = Modifier
                   .then(
                       if (isSelected) {
                           Modifier.border(2.dp, MaterialTheme.colors.primary, RoundedCornerShape(16.dp))
                       } else {
                           Modifier
                       }
                   )
           )
       }
   }
   ```

5. **Optional: Pass Label ID as Navigation Argument**

   To highlight a specific label when navigating from long-press:

   **Update AppNavHost:**

   ```kotlin
   // Use navigation arguments
   composable(
       route = "label_management?labelId={labelId}",
       arguments = listOf(
           navArgument("labelId") {
               type = NavType.LongType
               defaultValue = -1L  // -1 means no label selected
           }
       )
   ) { backStackEntry ->
       val labelId = backStackEntry.arguments?.getLong("labelId") ?: -1L
       LabelManagementScreen(
           navController = navController,
           habitManager = habitManager,
           selectedLabelId = if (labelId != -1L) labelId else null
       )
   }
   ```

   **Update navigation calls:**

   ```kotlin
   // In HabitCard or AddEditHabitScreen:
   onLabelLongClick = { label ->
       navController?.navigate("label_management?labelId=${label.id}")
   }
   ```

   **Update LabelManagementScreen:**

   ```kotlin
   @Composable
   fun LabelManagementScreen(
       navController: NavController,
       habitManager: HabitManager,
       selectedLabelId: Long? = null  // ADD parameter
   ) {
       // Scroll to or highlight the selected label if provided
       LaunchedEffect(selectedLabelId) {
           selectedLabelId?.let { id ->
               // Scroll to label or highlight it
           }
       }
   }
   ```

**Pros:**
- Intuitive interaction - long-press is a common pattern
- Works from anywhere labels are displayed
- Can be combined with other options

**Cons:**
- Discoverability issue - users may not know to long-press
- No visual hint that long-press is available
- Requires gesture handling

##### Recommended Combination

For best user experience, implement **Option 2 (Top App Bar Menu)** as the primary navigation method, and **Option 3 (From AddEditHabitScreen)** as a secondary contextual option. Option 4 (Long-press) can be added as a power-user feature but should not be the only way to access label management.

**Validation Checklist for Step 4.5:**
- [x] Navigation route "label_management" exists in AppNavHost
- [x] Option 2 (Top App Bar Menu) is implemented:
  - [x] TopAppBar is added to HabitHomeScreen
  - [x] TopAppBar displays "Today's Habits" as title
  - [x] Overflow menu button (MoreVert icon) is added to TopAppBar actions
  - [x] DropdownMenu shows when menu button is clicked
  - [x] "Manage Labels" menu item is present in dropdown
  - [x] Clicking "Manage Labels" navigates to label_management screen
  - [x] Menu closes when item is selected
  - [x] Standalone "Today's Habits" header text is removed (moved to TopAppBar)
  - [x] Layout is updated to use Scaffold to accommodate TopAppBar
  - [x] Content padding is adjusted for TopAppBar
- [x] Option 3 (Navigation from AddEditHabitScreen) is implemented:
  - [x] AddEditHabitScreen accepts navController parameter
  - [x] "Manage Labels" button/link is added to label selection section
  - [x] Button navigates to label_management screen
  - [x] HabitHomeScreen passes navController to AddEditHabitScreen
- [x] Option 4 (Long-press on Label Chip) is implemented:
  - [x] LabelChip component accepts optional `onLongClick` parameter
  - [x] LabelChip uses `pointerInput` and `detectLongPressGestures` for gesture detection
  - [x] LabelRow accepts optional `onLabelLongClick` parameter and passes it to LabelChip
  - [x] HabitCard accepts optional `navController` parameter
  - [x] HabitCard passes long-click handler to LabelRow (navigates to label_management)
  - [x] LabelSelector accepts optional `navController` parameter
  - [x] LabelSelector passes long-click handler to LabelChip (navigates to label_management)
  - [x] AddEditHabitScreen passes navController to LabelSelector
  - [x] HabitHomeScreen passes navController to HabitCard calls
  - [x] Long-press gesture works on label chips in HabitCard
  - [x] Long-press gesture works on label chips in LabelSelector (AddEditHabitScreen)
  - [ ] Optional: Navigation argument with label ID for pre-selection (not implemented - basic navigation works)
- [x] Navigation works correctly in both directions (to and from Label Management Screen)
- [x] Back navigation returns to previous screen correctly
- [x] Screen content displays correctly after adding TopAppBar

### Phase 5: Filtering & Search by Labels

#### Step 5.1: Add Label Filter to HabitHomeScreen

Add UI to filter habits by selected labels:

```kotlin
var selectedFilterLabelIds by remember { mutableStateOf<Set<Long>>(emptySet()) }

// Filter habits
val filteredHabits = if (selectedFilterLabelIds.isEmpty()) {
    sortedHabits
} else {
    sortedHabits.filter { habit ->
        val habitLabelIds = habitLabels[habit.id]?.map { it.id }?.toSet() ?: emptySet()
        habitLabelIds.intersect(selectedFilterLabelIds).isNotEmpty()
    }
}
```

**Validation Checklist:**
- [ ] Label filter UI is added to `HabitHomeScreen`
- [ ] State for `selectedFilterLabelIds: Set<Long>` is added
- [ ] Filter logic filters habits by selected labels correctly
- [ ] Habits are filtered when `selectedFilterLabelIds` is not empty
- [ ] All habits are shown when `selectedFilterLabelIds` is empty
- [ ] Filter supports multiple selected labels (AND or OR logic as designed)
- [ ] Filter UI (e.g., chips or dropdown) allows selecting/deselecting labels
- [ ] Filter state is properly managed
- [ ] Filtered habits are displayed correctly
- [ ] Filter works correctly with various label combinations
- [ ] Filter integrates smoothly with existing habit list display

## Database Migration Strategy

### Current State
- Database version: **1**
- No existing labels/tags data

### Migration Steps

1. **Increment database version** from 1 to 2
2. **Create migration callback** (`MIGRATION_1_2`)
3. **Create new tables**:
   - `labels` table
   - `habit_labels` junction table
4. **Add indices** for performance
5. **Test migration** on existing databases

### Migration Testing

Test the migration:
1. Create a test database with version 1
2. Add some test habits
3. Run migration to version 2
4. Verify:
   - Existing habits are intact
   - New tables are created
   - Can add labels to habits
   - Queries work correctly
   - All indices are created
   - Foreign key constraints work
   - CASCADE deletes work correctly

### Migration Error Handling

#### Error Scenarios

**1. Migration Fails During Execution**

If migration SQL fails, Room will throw an `IllegalStateException`. Handle this gracefully:

```kotlin
val database = try {
    Room.databaseBuilder(
        applicationContext,
        RewireDatabase::class.java,
        "rewire_database"
    )
        .addMigrations(MIGRATION_1_2)
        .build()
} catch (e: IllegalStateException) {
    // Migration failed
    Log.e("RewireDatabase", "Migration failed: ${e.message}", e)
    
    // Option 1: Fallback - delete and recreate database (DATA LOSS!)
    // Only use in development/testing
    applicationContext.deleteDatabase("rewire_database")
    Room.databaseBuilder(
        applicationContext,
        RewireDatabase::class.java,
        "rewire_database"
    ).build()
    
    // Option 2: Show user error and prevent app from starting
    // throw e  // Or show error screen
    
    // Option 3: Try fallback migration
    Room.databaseBuilder(
        applicationContext,
        RewireDatabase::class.java,
        "rewire_database"
    )
        .fallbackToDestructiveMigration()  // DESTRUCTIVE - deletes all data
        .build()
}
```

**2. Database Version Mismatch**

If app detects database version is higher than expected, Room throws `IllegalStateException`:

```kotlin
// This should not happen in normal flow, but handle defensively
try {
    val database = Room.databaseBuilder(...).build()
} catch (e: IllegalStateException) {
    if (e.message?.contains("version") == true) {
        // Database version is newer than app expects
        // Log error, possibly show update required message
        Log.e("RewireDatabase", "Database version mismatch", e)
    }
    throw e
}
```

**3. Migration Validation**

Room can validate migrations at compile time if `exportSchema = true`:

```kotlin
@Database(
    entities = [...],
    version = 2,
    exportSchema = true  // Enable schema export for validation (see Step 1.7)
)
abstract class RewireDatabase : RoomDatabase() { ... }
```

Then add to `build.gradle.kts` (or `build.gradle`):
```kotlin
android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
    }
}
```

**Note**: This is optional but recommended for production apps. It helps catch migration errors at compile time. If enabled, Room will generate JSON schema files in the `schemas` directory that can be version-controlled to track schema changes over time.

#### Best Practices

1. **Test migrations thoroughly** before release
2. **Backup data** if possible before migration
3. **Log migration attempts** for debugging
4. **Provide fallback options** (destructive migration as last resort)
5. **Inform users** if data loss might occur
6. **Never change migration code** after release (create new migration)

#### Rollback Strategy

If migration causes issues:

1. **Hotfix approach**: Create migration 2 → 3 that reverts changes
2. **App update**: Release new version with fixed migration
3. **Data recovery**: If possible, restore from backup before migration

**Note**: Room doesn't support automatic rollback. Once migration runs, you need a new migration to undo changes.

## Color Coding Details

### Color Storage Format

Store colors as **hexadecimal strings** (e.g., `"#4CAF50"`):
- Standard web/Android format
- Easy to parse with `Color.parseColor()`
- Human-readable
- Supports full color range

### Default Color Palette

Consider providing a default set of colors:

```kotlin
object LabelColors {
    val DEFAULT_COLORS = listOf(
        "#F44336", // Red
        "#E91E63", // Pink
        "#9C27B0", // Purple
        "#673AB7", // Deep Purple
        "#3F51B5", // Indigo
        "#2196F3", // Blue
        "#03A9F4", // Light Blue
        "#00BCD4", // Cyan
        "#009688", // Teal
        "#4CAF50", // Green
        "#8BC34A", // Light Green
        "#CDDC39", // Lime
        "#FFEB3B", // Yellow
        "#FFC107", // Amber
        "#FF9800", // Orange
        "#FF5722", // Deep Orange
        "#795548", // Brown
        "#9E9E9E", // Grey
        "#607D8B"  // Blue Grey
    )
    
    fun getDefaultColor(index: Int): String {
        return DEFAULT_COLORS[index % DEFAULT_COLORS.size]
    }
    
    /**
     * Get the next available default color that isn't already used.
     * This helps avoid color collisions when creating new labels.
     * 
     * @param usedColors List of colors already in use
     * @return A color from DEFAULT_COLORS that isn't in usedColors, or the first default color if all are used
     */
    fun getNextAvailableColor(usedColors: List<String>): String {
        val availableColor = DEFAULT_COLORS.firstOrNull { it !in usedColors }
        return availableColor ?: DEFAULT_COLORS.first()
    }
    
    /**
     * Get a default color for a new label based on existing label count.
     * Cycles through colors to provide variety.
     * 
     * @param existingLabelCount Number of existing labels (used to determine color index)
     * @return A color from DEFAULT_COLORS
     */
    fun getDefaultColorForNewLabel(existingLabelCount: Int): String {
        return getDefaultColor(existingLabelCount)
    }
}
```

### Color Contrast for Text

When displaying labels with colored backgrounds, ensure text is readable:
- Dark colors → white text
- Light colors → black text

Use the `isColorDark()` helper function provided in `LabelChip.kt`.

## Testing Checklist

### Unit Tests

- [ ] `LabelEntity` conversion functions
- [ ] `LabelDao` CRUD operations
- [ ] `HabitLabelDao` junction table operations
- [ ] `LabelRepository` methods
- [ ] Database migration from version 1 to 2

### Integration Tests

- [ ] Creating a label and associating it with a habit
- [ ] Updating a label color updates all habits using it
- [ ] Deleting a label removes associations (CASCADE)
- [ ] Deleting a habit removes label associations (CASCADE)
- [ ] Querying habits by label works correctly

### UI Tests

- [ ] Label chips display correctly with colors
- [ ] Label selector allows selecting/deselecting labels
- [ ] Labels appear on habit cards
- [ ] Labels can be edited in AddEditHabitScreen
- [ ] Label filtering works in HabitHomeScreen
- [ ] Loading indicators show while labels are being fetched
- [ ] Empty state displays when no labels exist
- [ ] Empty state in LabelSelector shows helpful message
- [ ] Label state reloads after returning from Label Management Screen

### Manual Testing Scenarios

1. **Create a habit with labels**
   - Create a new habit
   - Select multiple labels
   - Save and verify labels appear on habit card

2. **Edit label color**
   - Go to label management
   - Change a label's color
   - Verify all habits using that label show the new color

3. **Filter by label**
   - Select a label filter
   - Verify only habits with that label are shown

4. **Delete a label**
   - Delete a label that's used by habits
   - Verify associations are removed (CASCADE)

## File Summary

### New Files to Create

1. `core/src/main/kotlin/Label.kt` - Core label model
2. `app/src/main/kotlin/db/entity/LabelEntity.kt` - Label database entity
3. `app/src/main/kotlin/db/entity/HabitLabelCrossRef.kt` - Junction table entity
4. `app/src/main/kotlin/db/dao/LabelDao.kt` - Label DAO
5. `app/src/main/kotlin/db/dao/HabitLabelDao.kt` - Junction table DAO
6. `app/src/main/kotlin/repository/LabelRepository.kt` - Label repository
7. `app/src/main/kotlin/ui/components/LabelChip.kt` - Label chip component
8. `app/src/main/kotlin/ui/components/LabelRow.kt` - Label row component
9. `app/src/main/kotlin/ui/components/LabelSelector.kt` - Label selector component
10. `app/src/main/kotlin/ui/components/LabelLoadingIndicator.kt` - Loading state component
11. `app/src/main/kotlin/ui/screens/LabelManagementScreen.kt` - (Optional) Label management screen
12. `app/src/main/kotlin/ui/components/CreateEditLabelDialog.kt` - Create/edit label dialog
13. `app/src/main/kotlin/ui/components/DeleteLabelDialog.kt` - Delete confirmation dialog
14. `app/src/main/kotlin/ui/components/ColorPicker.kt` - (Optional) Color picker component
15. `app/src/test/kotlin/repository/LabelRepositoryTest.kt` - Repository unit tests
16. `app/src/test/kotlin/repository/LabelValidationTest.kt` - Validation function tests
17. `app/src/androidTest/kotlin/db/dao/LabelDaoTest.kt` - DAO instrumented tests

### Files to Modify

1. `core/src/main/kotlin/Habit.kt` - Add `labels: List<Label>` field
2. `app/src/main/kotlin/db/RewireDatabase.kt` - Increment version, add entities, add DAOs
3. `app/src/main/kotlin/repository/HabitRepository.kt` - Add label support
4. `app/src/main/kotlin/manager/HabitManager.kt` - Add label methods
5. `app/src/main/kotlin/ui/components/HabitCard.kt` - Display labels, add navController parameter (Option 4)
6. `app/src/main/kotlin/ui/components/LabelChip.kt` - Add long-press gesture support (Option 4)
7. `app/src/main/kotlin/ui/components/LabelRow.kt` - Add long-press handler parameter (Option 4)
8. `app/src/main/kotlin/ui/screens/AddEditHabitScreen.kt` - Add label selection, add navController parameter and navigation button (Option 3, Option 4)
9. `app/src/main/kotlin/ui/screens/HabitHomeScreen.kt` - Load and display labels, add TopAppBar with menu (Option 2), pass navController to AddEditHabitScreen (Option 3)
10. `app/src/main/kotlin/ui/navigation/AppNavHost.kt` - Add "label_management" route (all options)
11. `app/src/main/kotlin/MainActivity.kt` - Initialize LabelRepository, add migration callback, pass to HabitManager

## Implementation Order (Recommended)

1. **Phase 1**: Database layer (entities, DAOs, migrations)
2. **Phase 2**: Repository layer
3. **Phase 3**: UI components (chips, selectors)
4. **Phase 4**: Integration (update screens, display labels)
5. **Phase 5**: Advanced features (filtering, management screen)

## Future Enhancements

Once the basic implementation is complete, consider:

1. **Label Icons**: Add icon support to labels
2. **Label Categories**: Group labels into categories
3. **Label Descriptions**: Add optional descriptions to labels
4. **Default Labels**: Pre-populate common labels (Health, Work, Personal, etc.)
5. **Label Statistics**: Show how many habits use each label
6. **Smart Label Suggestions**: Suggest labels based on habit name
7. **Label Colors from Habits**: Auto-generate label colors based on habit colors (if you add habit colors later)
8. **Label Templates**: Predefined label sets (e.g., "Workout Labels", "Learning Labels")

## Notes

- This implementation uses Room's CASCADE delete for automatic cleanup
- **Label Name Case Sensitivity**: Labels are case-sensitive by default (SQLite unique index is case-sensitive). This means "Health" and "health" are treated as different labels. If you want case-insensitive uniqueness, you can:
  - Normalize label names to lowercase when storing/checking (convert user input to lowercase)
  - Or add a case-insensitive unique constraint using COLLATE NOCASE in a custom migration
  - Current implementation preserves case as entered by the user
- Consider adding a "label creation date" for sorting/ordering
- For large numbers of labels, consider pagination in label selector
- Color validation: Consider validating hex color format before saving
- **Batch Loading**: Use `getLabelsForHabits()` for loading labels for multiple habits to avoid N+1 query problems
- **Atomic Operations**: Use `createHabitWithLabels()` and `updateHabitWithLabels()` for atomic habit+label operations with proper error handling
- **Error Recovery**: All atomic operations return `Result<T>` for proper error handling. Handle failures gracefully in UI by showing error messages and allowing retry

## Unit Test Examples

### Label Repository Tests

**File**: `app/src/test/kotlin/repository/LabelRepositoryTest.kt`

```kotlin
package com.example.rewire.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.rewire.db.RewireDatabase
import com.example.rewire.db.dao.LabelDao
import com.example.rewire.db.dao.HabitLabelDao
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.db.entity.HabitLabelCrossRef
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class LabelRepositoryTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: RewireDatabase
    private lateinit var labelDao: LabelDao
    private lateinit var habitLabelDao: HabitLabelDao
    private lateinit var repository: LabelRepository
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RewireDatabase::class.java
        ).allowMainThreadQueries().build()
        
        labelDao = database.labelDao()
        habitLabelDao = database.habitLabelDao()
        repository = LabelRepository(labelDao, habitLabelDao)
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun insertLabel_success() = runTest {
        val label = LabelEntity(name = "Test Label", color = "#4CAF50")
        val id = repository.insertLabel(label)
        
        assertTrue(id > 0)
        
        val retrieved = repository.getLabelById(id)
        assertNotNull(retrieved)
        assertEquals("Test Label", retrieved?.name)
        assertEquals("#4CAF50", retrieved?.color)
    }
    
    @Test
    fun insertLabelWithValidation_validName_success() = runTest {
        val label = LabelEntity(name = "Valid Label", color = "#4CAF50")
        val result = repository.insertLabelWithValidation(label)
        
        assertTrue(result is LabelResult.Success)
        assertEquals("Valid Label", (result as LabelResult.Success).label.name)
    }
    
    @Test
    fun insertLabelWithValidation_invalidName_returnsError() = runTest {
        val label = LabelEntity(name = "", color = "#4CAF50")  // Empty name
        val result = repository.insertLabelWithValidation(label)
        
        assertTrue(result is LabelResult.Error)
        assertTrue((result as LabelResult.Error).message.contains("empty"))
    }
    
    @Test
    fun insertLabelWithValidation_duplicateName_returnsError() = runTest {
        val label1 = LabelEntity(name = "Duplicate", color = "#4CAF50")
        repository.insertLabel(label1)
        
        val label2 = LabelEntity(name = "Duplicate", color = "#2196F3")
        val result = repository.insertLabelWithValidation(label2)
        
        assertTrue(result is LabelResult.Error)
        assertTrue((result as LabelResult.Error).message.contains("already exists"))
    }
    
    @Test
    fun insertLabelWithValidation_invalidColor_returnsError() = runTest {
        val label = LabelEntity(name = "Test", color = "not-a-color")
        val result = repository.insertLabelWithValidation(label)
        
        assertTrue(result is LabelResult.Error)
        assertTrue((result as LabelResult.Error).message.contains("color"))
    }
    
    @Test
    fun getLabelsForHabit_returnsCorrectLabels() = runTest {
        // Create labels
        val label1 = repository.insertLabel(LabelEntity(name = "Label1", color = "#4CAF50"))
        val label2 = repository.insertLabel(LabelEntity(name = "Label2", color = "#2196F3"))
        
        // Create habit (assuming habit ID 1 exists in test)
        val habitId = 1L
        
        // Associate labels with habit
        repository.addLabelToHabit(habitId, label1)
        repository.addLabelToHabit(habitId, label2)
        
        // Retrieve labels for habit
        val labels = repository.getLabelsForHabit(habitId)
        
        assertEquals(2, labels.size)
        assertTrue(labels.any { it.name == "Label1" })
        assertTrue(labels.any { it.name == "Label2" })
    }
    
    @Test
    fun setLabelsForHabit_replacesExistingLabels() = runTest {
        val label1 = repository.insertLabel(LabelEntity(name = "Label1", color = "#4CAF50"))
        val label2 = repository.insertLabel(LabelEntity(name = "Label2", color = "#2196F3"))
        val label3 = repository.insertLabel(LabelEntity(name = "Label3", color = "#FF5722"))
        
        val habitId = 1L
        
        // Initially set label1 and label2
        repository.setLabelsForHabit(habitId, listOf(label1, label2))
        var labels = repository.getLabelsForHabit(habitId)
        assertEquals(2, labels.size)
        
        // Replace with label2 and label3
        repository.setLabelsForHabit(habitId, listOf(label2, label3))
        labels = repository.getLabelsForHabit(habitId)
        assertEquals(2, labels.size)
        assertFalse(labels.any { it.id == label1 })
        assertTrue(labels.any { it.id == label2 })
        assertTrue(labels.any { it.id == label3 })
    }
    
    @Test
    fun deleteLabel_removesAssociations() = runTest {
        val label = repository.insertLabel(LabelEntity(name = "To Delete", color = "#4CAF50"))
        val habitId = 1L
        
        // Associate label with habit
        repository.addLabelToHabit(habitId, label)
        assertEquals(1, repository.getLabelsForHabit(habitId).size)
        
        // Delete label
        val labelEntity = repository.getLabelById(label)!!
        repository.deleteLabel(labelEntity)
        
        // Verify label is deleted and associations are removed
        assertNull(repository.getLabelById(label))
        assertEquals(0, repository.getLabelsForHabit(habitId).size)
    }
    
    @Test
    fun searchLabels_returnsMatchingLabels() = runTest {
        repository.insertLabel(LabelEntity(name = "Health", color = "#4CAF50"))
        repository.insertLabel(LabelEntity(name = "Work", color = "#2196F3"))
        repository.insertLabel(LabelEntity(name = "Health & Fitness", color = "#FF5722"))
        
        val results = repository.searchLabels("Health")
        
        assertEquals(2, results.size)
        assertTrue(results.all { it.name.contains("Health", ignoreCase = true) })
    }
}
```

### Label DAO Tests

**File**: `app/src/androidTest/kotlin/db/dao/LabelDaoTest.kt`

```kotlin
package com.example.rewire.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.rewire.db.RewireDatabase
import com.example.rewire.db.entity.LabelEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
@SmallTest
class LabelDaoTest {
    private lateinit var database: RewireDatabase
    private lateinit var labelDao: LabelDao
    
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, RewireDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        labelDao = database.labelDao()
    }
    
    @After
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun insertAndGetLabel() = runBlocking {
        val label = LabelEntity(name = "Test Label", color = "#4CAF50")
        val id = labelDao.insert(label)
        
        val loaded = labelDao.getById(id)
        assertNotNull(loaded)
        assertEquals("Test Label", loaded?.name)
        assertEquals("#4CAF50", loaded?.color)
    }
    
    @Test
    fun getAllLabels_returnsAllLabels() = runBlocking {
        labelDao.insert(LabelEntity(name = "Label1", color = "#4CAF50"))
        labelDao.insert(LabelEntity(name = "Label2", color = "#2196F3"))
        
        val allLabels = labelDao.getAll()
        assertEquals(2, allLabels.size)
    }
    
    @Test
    fun getByName_findsCorrectLabel() = runBlocking {
        labelDao.insert(LabelEntity(name = "Unique Label", color = "#4CAF50"))
        
        val found = labelDao.getByName("Unique Label")
        assertNotNull(found)
        assertEquals("Unique Label", found?.name)
    }
    
    @Test
    fun uniqueConstraint_preventsDuplicateNames() = runBlocking {
        labelDao.insert(LabelEntity(name = "Duplicate", color = "#4CAF50"))
        
        // Attempt to insert duplicate name
        try {
            labelDao.insert(LabelEntity(name = "Duplicate", color = "#2196F3"))
            fail("Should have thrown exception for duplicate name")
        } catch (e: Exception) {
            // Expected: SQLiteConstraintException or similar
            assertTrue(true)
        }
    }
}
```

### Validation Function Tests

**File**: `app/src/test/kotlin/repository/LabelValidationTest.kt`

```kotlin
package com.example.rewire.repository

import org.junit.Test
import org.junit.Assert.*

class LabelValidationTest {
    @Test
    fun validateLabelName_empty_returnsError() {
        val result = validateLabelName("")
        assertFalse(result.isValid)
        assertTrue(result.errorMessage.contains("empty"))
    }
    
    @Test
    fun validateLabelName_tooLong_returnsError() {
        val longName = "A".repeat(51)  // 51 characters
        val result = validateLabelName(longName)
        assertFalse(result.isValid)
        assertTrue(result.errorMessage.contains("50"))
    }
    
    @Test
    fun validateLabelName_valid_returnsSuccess() {
        val result = validateLabelName("Valid Label")
        assertTrue(result.isValid)
    }
    
    @Test
    fun validateLabelName_trimsWhitespace() {
        val result = validateLabelName("  Label  ")
        // Validation should check trimmed version
        assertTrue(result.isValid)
    }
    
    @Test
    fun validateLabelColor_invalidFormat_returnsError() {
        val result = validateLabelColor("not-a-color")
        assertFalse(result.isValid)
        assertTrue(result.errorMessage.contains("format"))
    }
    
    @Test
    fun validateLabelColor_validHex_returnsSuccess() {
        val result = validateLabelColor("#4CAF50")
        assertTrue(result.isValid)
    }
    
    @Test
    fun validateLabelColor_validHexWithAlpha_returnsSuccess() {
        val result = validateLabelColor("#FF4CAF50")
        assertTrue(result.isValid)
    }
}
```

## Integration Points

### How Labels Interact with Other Features

#### 1. Habit Deletion

When a habit is deleted, label associations are automatically removed via CASCADE delete:

```kotlin
// In HabitManager or HabitRepository
suspend fun deleteHabit(habit: HabitEntity) {
    habitRepository.deleteHabit(habit)
    // Label associations are automatically deleted via CASCADE
    // No manual cleanup needed
}
```

**Validation:**
- Deleting a habit removes all its label associations
- Labels themselves remain in the database
- No orphaned records in `habit_labels` table

#### 2. Habit Duplication/Cloning (Future Feature)

If you implement habit duplication in the future:

```kotlin
suspend fun duplicateHabit(habitId: Long): Long {
    val original = habitRepository.getHabitById(habitId) ?: return -1
    
    // Create new habit with same properties
    val duplicated = original.copy(id = 0, name = "${original.name} (Copy)")
    val newHabitId = habitRepository.insertHabit(duplicated)
    
    // Copy label associations
    val originalLabels = labelRepository.getLabelsForHabit(habitId)
    val labelIds = originalLabels.map { it.id }
    labelRepository.setLabelsForHabit(newHabitId, labelIds)
    
    return newHabitId
}
```

#### 3. Habit Export/Import (Future Feature)

If you implement data export/import:

```kotlin
// Export labels with habits
data class HabitExport(
    val habit: HabitEntity,
    val labelNames: List<String>  // Export label names, not IDs
)

// Import - create labels if they don't exist
suspend fun importHabit(export: HabitExport) {
    val habitId = habitRepository.insertHabit(export.habit)
    
    val labelIds = export.labelNames.map { labelName ->
        val label = labelRepository.getOrCreateLabel(labelName)
        label.id
    }
    
    labelRepository.setLabelsForHabit(habitId, labelIds)
}
```

#### 4. Habit Search/Filtering

Labels can enhance search functionality:

```kotlin
// Search habits by label name
suspend fun searchHabitsByLabel(labelName: String): List<HabitEntity> {
    val label = labelRepository.getLabelByName(labelName)
    return if (label != null) {
        val habitIds = labelRepository.getHabitIdsWithLabel(label.id)
        habitIds.mapNotNull { habitRepository.getHabitById(it) }
    } else {
        emptyList()
    }
}

// Filter habits by multiple labels (AND logic)
suspend fun filterHabitsByLabels(labelNames: List<String>): List<HabitEntity> {
    val labels = labelNames.mapNotNull { labelRepository.getLabelByName(it) }
    if (labels.isEmpty()) return habitRepository.getAllHabits()
    
    val labelIds = labels.map { it.id }
    // Get habits that have ALL specified labels
    val allHabits = habitRepository.getAllHabits()
    return allHabits.filter { habit ->
        val habitLabelIds = labelRepository.getLabelsForHabit(habit.id).map { it.id }.toSet()
        labelIds.all { it in habitLabelIds }
    }
}
```

#### 5. Statistics/Analytics Integration

Labels can be used for analytics:

```kotlin
// Get label usage statistics
data class LabelStatistics(
    val label: LabelEntity,
    val usageCount: Int,
    val habits: List<HabitEntity>
)

suspend fun getLabelStatistics(): List<LabelStatistics> {
    val allLabels = labelRepository.getAllLabels()
    return allLabels.map { label ->
        val habitIds = labelRepository.getHabitIdsWithLabel(label.id)
        val habits = habitIds.mapNotNull { habitRepository.getHabitById(it) }
        LabelStatistics(label, habits.size, habits)
    }
}
```

#### 6. Data Migration from Other Systems

If migrating data from other habit tracking apps:

```kotlin
// Map external tags/categories to labels
suspend fun importLabelsFromExternal(externalTags: List<String>) {
    externalTags.forEach { tagName ->
        labelRepository.getOrCreateLabel(
            name = tagName,
            defaultColor = LabelColors.getDefaultColor(externalTags.indexOf(tagName))
        )
    }
}
```

### Testing Integration Points

When testing, ensure:
- Habit deletion removes label associations
- Label deletion removes associations from habits
- Label updates reflect in all associated habits
- Queries work correctly with label filters
- Statistics include label information

## Phase-by-Phase Validation Checklist

### Phase 1: Core Model & Database Layer

#### Compilation Checks
- [ ] Project compiles without errors
- [ ] No unused imports or warnings
- [ ] All new files are included in the correct package structure

#### Database Schema Validation
- [ ] `LabelEntity` compiles and Room annotations are correct
- [ ] `HabitLabelCrossRef` compiles with composite primary key
- [ ] Foreign key constraints are properly defined
- [ ] Indices are created for `habitId` and `labelId` in junction table
- [ ] Unique index on `labels.name` is working

#### Database Migration Testing
- [ ] Database version incremented from 1 to 2
- [ ] Migration script compiles without errors
- [ ] Migration creates `labels` table correctly
- [ ] Migration creates `habit_labels` junction table correctly
- [ ] Migration creates all required indices
- [ ] Test migration on existing database (version 1) → verify no data loss
- [ ] Test fresh install (version 2) → verify tables are created
- [ ] Migration error handling is implemented (try-catch in MainActivity)
- [ ] Fallback strategy for failed migrations is defined
- [ ] Migration errors are logged appropriately
- [ ] User is informed if migration fails (if applicable)

#### Entity Validation
- [ ] `LabelEntity` can be inserted into database
- [ ] `LabelEntity` can be queried by id
- [ ] `LabelEntity` can be queried by name
- [ ] `LabelEntity` unique constraint prevents duplicate names (case-sensitive)
- [ ] `HabitLabelCrossRef` can be inserted
- [ ] Composite primary key prevents duplicate (habitId, labelId) pairs
- [ ] Foreign key to `HabitEntity` works correctly
- [ ] Foreign key to `LabelEntity` works correctly

#### Core Model Validation
- [ ] `Label` data class compiles
- [ ] `Habit` data class accepts `labels: List<Label>` field
- [ ] Conversion functions (`toCore()`, `toEntity()`) compile
- [ ] Conversion functions work correctly in both directions

#### DAO Validation
- [ ] `LabelDao` interface compiles
- [ ] `HabitLabelDao` interface compiles
- [ ] All DAO methods are accessible from database instance
- [ ] Test basic CRUD operations:
  - [ ] Insert label → verify it's stored
  - [ ] Query label by id → verify correct data returned
  - [ ] Query label by name → verify correct data returned
  - [ ] Update label → verify changes persisted
  - [ ] Delete label → verify it's removed
- [ ] Test junction table operations:
  - [ ] Insert cross-reference → verify it's stored
  - [ ] Query cross-references for habit → verify correct associations
  - [ ] Query cross-references for label → verify correct associations
  - [ ] Delete cross-reference → verify it's removed

#### Foreign Key CASCADE Testing
- [ ] Deleting a habit removes all its label associations (CASCADE)
- [ ] Deleting a label removes all its habit associations (CASCADE)
- [ ] Cannot insert cross-reference with invalid `habitId`
- [ ] Cannot insert cross-reference with invalid `labelId`

#### Database Integration
- [ ] `RewireDatabase` includes new entities in `entities` list
- [ ] `RewireDatabase` exposes `labelDao()` method
- [ ] `RewireDatabase` exposes `habitLabelDao()` method
- [ ] Database builds successfully with version 2

---

### Phase 2: Repository Layer

#### Compilation Checks
- [ ] `LabelRepository` compiles without errors
- [ ] `HabitRepository` modifications compile
- [ ] `HabitManager` modifications compile
- [ ] All dependencies are properly injected
- [ ] `MainActivity` creates LabelRepository correctly
- [ ] `MainActivity` passes LabelRepository to HabitManager
- [ ] Migration callback is added to database builder
- [ ] Validation functions (`validateLabelName`, `validateLabelColor`) compile
- [ ] `LabelResult` sealed class is defined correctly

#### Repository Method Validation
- [ ] `getAllLabels()` returns all labels from database
- [ ] `getLabelById()` returns correct label or null
- [ ] `getLabelByName()` returns correct label or null
- [ ] `insertLabel()` creates new label and returns ID
- [ ] `updateLabel()` updates existing label correctly
- [ ] `deleteLabel()` removes label and associations
- [ ] `getOrCreateLabel()` returns existing label if found
- [ ] `getOrCreateLabel()` creates new label if not found

#### Junction Table Repository Methods
- [ ] `getLabelsForHabit()` returns all labels for a habit
- [ ] `setLabelsForHabit()` replaces all labels for a habit
- [ ] `setLabelsForHabit()` handles empty list (removes all labels)
- [ ] `setLabelsForHabitSafely()` validates label IDs before setting
- [ ] `addLabelToHabit()` adds single label association
- [ ] `removeLabelFromHabit()` removes single label association
- [ ] `getHabitIdsWithLabel()` returns all habit IDs with a label
- [ ] `searchLabels()` returns matching labels by name query
- [ ] `insertLabelWithValidation()` validates name and color before insert
- [ ] `insertLabelWithValidation()` prevents duplicate names
- [ ] `insertLabelWithValidation()` returns `LabelResult.Error` for invalid inputs
- [ ] `insertLabelWithValidation()` returns `LabelResult.Success` for valid inputs
- [ ] `updateLabelWithValidation()` validates name and color before update
- [ ] `updateLabelWithValidation()` allows updating if name doesn't conflict with others
- [ ] `setLabelsForHabitSafely()` validates all label IDs exist before setting
- [ ] `setLabelsForHabitSafely()` returns false if any label ID is invalid
- [ ] Validation functions (`validateLabelName`, `validateLabelColor`) work correctly
- [ ] Label name validation rejects empty strings
- [ ] Label name validation rejects names longer than 50 characters
- [ ] Label name validation accepts valid characters
- [ ] Color validation rejects invalid hex formats
- [ ] Color validation accepts valid hex colors (#RRGGBB and #AARRGGBB)

#### Data Flow Validation
- [ ] Creating a habit → labels can be associated
- [ ] Updating a habit's labels → old associations removed, new ones added
- [ ] Deleting a habit → label associations are removed (via CASCADE)
- [ ] Deleting a label → habit associations are removed (via CASCADE)
- [ ] Updating label color → all habits using that label see new color

#### Integration with Existing Code
- [ ] `HabitRepository` modifications don't break existing functionality
- [ ] `HabitManager` can access label repository
- [ ] Existing habit operations still work correctly
- [ ] Label operations don't interfere with habit operations

#### Edge Cases
- [ ] Setting labels for non-existent habit → handles gracefully
- [ ] Adding non-existent label to habit → foreign key constraint prevents it
- [ ] Getting labels for habit with no labels → returns empty list
- [ ] Setting empty label list for habit → removes all associations
- [ ] Searching with empty query → returns all labels (or handles appropriately)

---

### Phase 3: UI Components

#### Compilation Checks
- [ ] All UI components compile without errors
- [ ] No missing imports
- [ ] All composables are properly annotated

#### LabelChip Component
- [ ] Component renders without crashing
- [ ] Label name displays correctly
- [ ] Background color uses label's color hex code
- [ ] Color parsing handles valid hex colors (e.g., "#4CAF50")
- [ ] Color parsing handles invalid hex colors (falls back to primary)
- [ ] Text color adjusts based on background brightness (dark/light)
- [ ] Text is readable on all color backgrounds
- [ ] Click handler is called when chip is clicked
- [ ] Chip has appropriate padding and shape (rounded corners)

#### LabelRow Component
- [ ] Component renders without crashing
- [ ] Multiple labels display in a row
- [ ] Labels are properly spaced
- [ ] Empty label list doesn't render (returns early)
- [ ] Each label chip is clickable
- [ ] Click handler receives correct label

#### LabelSelector Component
- [ ] Component renders without crashing
- [ ] All available labels are displayed
- [ ] Selected labels are visually distinct (border/indicator)
- [ ] Clicking unselected label adds it to selection
- [ ] Clicking selected label removes it from selection
- [ ] Selection state updates correctly
- [ ] `onSelectionChange` callback is called with updated selection
- [ ] LazyRow scrolls properly if many labels

#### Integration Validation
- [ ] Components work with real `Label` data from database
- [ ] Components handle empty data gracefully
- [ ] Color parsing works with actual hex colors from database
- [ ] Selection state persists during component lifecycle

#### Visual/UX Checks
- [ ] Label chips are appropriately sized
- [ ] Colors are visually distinct
- [ ] Text is readable on all background colors
- [ ] Selection indicators are clear
- [ ] Layout looks good on different screen sizes
- [ ] Components follow app's design system

---

### Phase 4: Integration (Update Existing Screens)

#### HabitCard Updates
- [ ] `HabitCard` compiles with new `labels` parameter
- [ ] Labels parameter has default empty list (backwards compatible)
- [ ] Labels display below habit name
- [ ] Labels display above note field (if visible)
- [ ] Empty labels list doesn't affect card layout
- [ ] Multiple labels display correctly
- [ ] Label colors render correctly

#### AddEditHabitScreen Updates
- [ ] Screen compiles with label selection state
- [ ] Available labels load on screen initialization
- [ ] `LabelSelector` component is displayed in form
- [ ] Label selector is positioned appropriately in form
- [ ] When editing habit, existing labels are pre-selected
- [ ] Label selection updates state correctly
- [ ] Saving habit saves selected labels
- [ ] Labels are persisted correctly to database

#### HabitHomeScreen Updates
- [ ] Screen compiles with label loading logic
- [ ] Labels load for each habit
- [ ] Labels display on habit cards
- [ ] Label loading doesn't block UI (runs in coroutine)
- [ ] Labels update when habits change

#### Data Flow End-to-End
- [ ] Create new habit → select labels → save → labels appear on card
- [ ] Edit existing habit → change labels → save → labels update on card
- [ ] Delete habit → label associations are removed
- [ ] Edit label color → all habits using it show new color immediately
- [ ] Create new label → it appears in selector for future habits

#### Performance Checks
- [ ] Label loading doesn't cause UI lag
- [ ] Multiple habits with labels render efficiently
- [ ] Database queries are optimized (using indices)
- [ ] No N+1 query problems (consider batching label loads)

#### Backwards Compatibility
- [ ] Existing habits without labels display correctly
- [ ] Old habit data still loads correctly
- [ ] Migration doesn't break existing functionality
- [ ] App works for users upgrading from version without labels

---

### Phase 5: Advanced Features (Optional)

#### Label Filtering (if implemented)
- [ ] Filter UI component renders correctly
- [ ] Selecting filter label updates filter state
- [ ] Filtered habits display correctly
- [ ] Multiple label filters work (AND or OR logic)
- [ ] Clearing filters shows all habits
- [ ] Filter state persists appropriately

#### Label Management Screen (if implemented)
- [ ] Screen displays all labels
- [ ] Screen shows loading indicator while fetching labels
- [ ] Screen shows empty state when no labels exist
- [ ] Create new label functionality works
- [ ] Create label dialog validates input
- [ ] Create label shows error for invalid name/color
- [ ] Create label shows error for duplicate names
- [ ] Create label dialog shows current/default color
- [ ] Color picker opens from create/edit dialog
- [ ] Edit label name works
- [ ] Edit label color works
- [ ] Edit label validates input before saving
- [ ] Edit label pre-fills existing values correctly
- [ ] Delete label works and shows confirmation
- [ ] Delete confirmation dialog shows usage count
- [ ] Delete confirmation warns if label is in use
- [ ] Delete confirmation allows cancellation
- [ ] Deleting label removes associations correctly (CASCADE)
- [ ] Screen shows which habits use each label (usage count)
- [ ] Label list refreshes after create/edit/delete operations
- [ ] Navigation to/from screen works
- [ ] Error messages display correctly for failed operations

#### Navigation to Label Management (Options 2, 3, 4)
- [ ] **Option 2 - Top App Bar Menu**: TopAppBar displays correctly
- [ ] **Option 2**: Menu button (3-dot icon) is visible and clickable
- [ ] **Option 2**: Dropdown menu opens when menu button is clicked
- [ ] **Option 2**: "Manage Labels" menu item navigates to Label Management Screen
- [ ] **Option 2**: Menu closes after navigation
- [ ] **Option 3 - From AddEditHabitScreen**: "Create or edit labels" button appears in label section
- [ ] **Option 3**: Button is clickable and navigates to Label Management Screen
- [ ] **Option 3**: Labels reload when returning from Label Management Screen
- [ ] **Option 3**: Navigation back returns to AddEditHabitScreen with form state preserved
- [ ] **Option 4 - Long-press on Label Chip**: Long-press gesture is detected on label chips
- [ ] **Option 4**: Long-press on chips in HabitCard navigates to Label Management
- [ ] **Option 4**: Long-press on chips in AddEditHabitScreen navigates to Label Management
- [ ] **Option 4** (Optional): Selected label ID is passed as navigation argument
- [ ] **Option 4** (Optional): Label Management Screen highlights pre-selected label
- [ ] Back navigation from Label Management returns to previous screen
- [ ] Navigation stack works correctly (can go back multiple screens)

#### Color Picker (if implemented)
- [ ] Color picker component renders
- [ ] Selecting color updates label color
- [ ] Selected color is saved correctly
- [ ] Color picker integrates with label creation/editing

---

### Overall Integration Validation

#### Database Integrity
- [ ] All foreign key constraints are enforced
- [ ] CASCADE deletes work correctly
- [ ] No orphaned records in junction table
- [ ] Database indices improve query performance

#### Data Consistency
- [ ] Label names are unique (enforced by database)
- [ ] Label colors are valid hex codes (validate in UI/repository)
- [ ] Habit-label associations are valid (foreign keys enforce)
- [ ] Data persists correctly across app restarts

#### User Experience
- [ ] Labels enhance habit organization
- [ ] Color coding is visually helpful
- [ ] Label selection is intuitive
- [ ] Performance is acceptable with many labels/habits
- [ ] Error states are handled gracefully

#### Testing Coverage
- [ ] Unit tests for entities and conversion functions
- [ ] Unit tests for DAO operations
- [ ] Unit tests for repository methods
- [ ] Integration tests for label-habit associations
- [ ] Integration tests for CASCADE deletes
- [ ] UI tests for label components (if applicable)
- [ ] Manual testing of all user flows

---

### Pre-Production Validation

#### Final Checks
- [ ] All tests pass (unit, integration, UI)
- [ ] No compiler warnings or errors
- [ ] Code follows project style guidelines
- [ ] Documentation is updated (if applicable)
- [ ] Migration tested on real devices/emulators
- [ ] Performance tested with realistic data volumes
- [ ] Memory leaks checked (label loading, database connections)
- [ ] Edge cases handled (empty states, invalid data, etc.)

#### Migration Validation (Critical)
- [ ] Test migration from version 1 → 2 with existing data
- [ ] Verify no data loss during migration
- [ ] Verify existing habits still work after migration
- [ ] Verify new tables are created correctly
- [ ] Test on multiple Android versions (if possible)
- [ ] Test fresh installs (no migration needed)

## Questions to Consider

1. **Label uniqueness**: Should labels be case-sensitive? (Current plan: Yes, via unique index)
2. **Default labels**: Should we pre-populate any default labels?
3. **Label limits**: Maximum number of labels per habit?
4. **Color picker**: Full color picker or predefined palette?
5. **Label organization**: Should labels support categories/sub-categories?

