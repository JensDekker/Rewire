# Quick Guide: Updating App Version

When you want to release a new version of your app, follow these steps:

## Step 1: Update Version in build.gradle.kts

Edit `app/build.gradle.kts` and update these lines:

```kotlin
defaultConfig {
    applicationId = "com.example.rewire"
    minSdk = 24
    targetSdk = 36
    versionCode = 2        // ← Increment this (1 → 2 → 3 → 4...)
    versionName = "1.1"   // ← Update this for display
    // ...
}
```

**Current values:**
- `versionCode = 1`
- `versionName = "1.0"`

**Next update should be:**
- `versionCode = 2`
- `versionName = "1.1"` (or whatever makes sense for your changes)

## Step 2: Update Database Version (Only if Database Changed)

**Only do this if you modified database entities (tables, columns, etc.)**

### 2a. Update Database Version

Edit `app/src/main/kotlin/db/RewireDatabase.kt`:

```kotlin
@Database(
    entities = [...],
    version = 3,  // ← Increment from current version (currently 2)
    exportSchema = true
)
```

### 2b. Create Migration

Edit `app/src/main/kotlin/MainActivity.kt` and add a new migration:

```kotlin
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add your database changes here
        // Examples:
        // db.execSQL("ALTER TABLE habits ADD COLUMN newField TEXT")
        // db.execSQL("CREATE TABLE IF NOT EXISTS new_table (...)")
    }
}
```

### 2c. Register Migration

In the same file, update the database builder:

```kotlin
Room.databaseBuilder(
    applicationContext,
    RewireDatabase::class.java,
    "rewire_database"
)
    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)  // ← Add new migration here
    .build()
```

## Step 3: Build and Install

```powershell
# Build the new APK
.\gradlew.bat assembleDebug

# Install on phone (data will be preserved automatically)
adb install app\build\outputs\apk\debug\app-debug.apk
```

Or transfer the APK to your phone and install manually.

## Version Numbering Examples

| Change Type | versionCode | versionName | Database Version |
|------------|-------------|-------------|------------------|
| Bug fix | 2 | 1.0.1 | 2 (no change) |
| New feature | 3 | 1.1 | 2 (no change) |
| Database change | 4 | 1.2 | 3 (increment) |
| Major update | 5 | 2.0 | 3 (or increment if DB changed) |

## Checklist

Before releasing an update:
- [ ] Incremented `versionCode` in `build.gradle.kts`
- [ ] Updated `versionName` in `build.gradle.kts`
- [ ] If database changed: incremented database version
- [ ] If database changed: created and registered migration
- [ ] Tested the update on a device with existing data
- [ ] Built new APK successfully
- [ ] Verified data is preserved after update

