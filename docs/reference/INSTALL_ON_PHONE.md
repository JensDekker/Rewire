# How to Install the App on Your Phone

Your APK file is located at: `app/build/outputs/apk/debug/app-debug.apk`

## Method 1: Using USB Cable (Recommended)

### Step 1: Enable Developer Options on Your Phone
1. Go to **Settings** → **About Phone**
2. Find **Build Number** and tap it **7 times** until you see "You are now a developer!"

### Step 2: Enable USB Debugging
1. Go to **Settings** → **Developer Options** (or **System** → **Developer Options**)
2. Enable **USB Debugging**
3. Enable **Install via USB** (if available)

### Step 3: Connect Your Phone
1. Connect your phone to your computer via USB cable
2. On your phone, when prompted, allow USB debugging and check "Always allow from this computer"

### Step 4: Install the APK
Open PowerShell in the project directory and run:
```powershell
adb install app\build\outputs\apk\debug\app-debug.apk
```

**Note:** If `adb` is not found, you need to install Android SDK Platform Tools. You can download it from: https://developer.android.com/tools/releases/platform-tools

## Method 2: Transfer APK to Phone and Install Manually

### Step 1: Transfer the APK
1. Copy `app/build/outputs/apk/debug/app-debug.apk` to your phone using:
   - **Email**: Email the APK to yourself and open it on your phone
   - **Cloud Storage**: Upload to Google Drive/Dropbox and download on your phone
   - **USB File Transfer**: Connect phone via USB, enable file transfer mode, and copy the APK
   - **Bluetooth**: Send the file via Bluetooth

### Step 2: Enable Unknown Sources
1. On your phone, go to **Settings** → **Security** (or **Apps** → **Special Access**)
2. Enable **Install Unknown Apps** or **Unknown Sources**
3. If prompted, select the app you'll use to install (e.g., Files, Chrome, Email)

### Step 3: Install the APK
1. Open the APK file on your phone (from Downloads, email attachment, etc.)
2. Tap **Install** when prompted
3. If you see a security warning, tap **Install Anyway** or **OK**
4. Wait for installation to complete
5. Tap **Open** or find the app in your app drawer

## Method 3: Using Android Studio
1. Open your project in Android Studio
2. Connect your phone via USB (with USB debugging enabled)
3. Click the **Run** button (green play icon) or press `Shift + F10`
4. Select your phone from the device list
5. The app will be built and installed automatically

## Troubleshooting

### "Installation blocked" or "For security, your phone is set to block installation"
- Go to Settings → Security → Install Unknown Apps
- Enable installation for the app you're using (Files, Chrome, etc.)

### "App not installed" error
- Make sure you uninstall any previous version first
- Check that your phone meets the minimum SDK requirement (API 24 / Android 7.0)
- Try building a release APK instead (see below)

### ADB not found
- Install Android SDK Platform Tools from: https://developer.android.com/tools/releases/platform-tools
- Add the platform-tools directory to your system PATH

## Building a Release APK (Optional)

For a more optimized version, you can build a release APK:

```powershell
.\gradlew.bat assembleRelease
```

The release APK will be at: `app/build/outputs/apk/release/app-release.apk`

**Note:** Release APKs need to be signed. If you haven't set up a signing key, you can use the debug signing (which Android Studio does automatically) or create a keystore for production builds.

## Updating the App (Installing New Versions)

### How Updates Work

When you install a new version of the app over an existing installation, Android automatically:
- ✅ **Preserves all your data** (habits, completions, notes, labels, etc.)
- ✅ **Runs database migrations** if the schema changed
- ✅ **Keeps app settings** and preferences
- ✅ **Updates the app** without losing anything

### Important Requirements for Updates

For updates to work properly, you must:

1. **Keep the same package name** (currently `com.example.rewire` - don't change this)
2. **Use the same signing key** (debug builds use the same debug key automatically)
3. **Increment the version code** (see below)

### How to Create an Update

#### Step 1: Update Version Numbers

Before building a new version, update `app/build.gradle.kts`:

```kotlin
defaultConfig {
    applicationId = "com.example.rewire"  // Keep this the same!
    versionCode = 2  // Increment this (was 1, now 2, then 3, etc.)
    versionName = "1.1"  // Update this for display (e.g., "1.1", "1.2", "2.0")
    // ... rest of config
}
```

**Important:** Always increment `versionCode` by at least 1 for each update. This tells Android the new version is newer than the previous one.

#### Step 2: Handle Database Schema Changes (If Needed)

If you modify database entities (add/remove/change tables or columns), you need to:

1. **Increment the database version** in `app/src/main/kotlin/db/RewireDatabase.kt`:
   ```kotlin
   @Database(
       entities = [...],
       version = 3,  // Increment from 2 to 3
       exportSchema = true
   )
   ```

2. **Create a migration** in `app/src/main/kotlin/MainActivity.kt`:
   ```kotlin
   val MIGRATION_2_3 = object : Migration(2, 3) {
       override fun migrate(db: SupportSQLiteDatabase) {
           // Add your schema changes here
           // Example: db.execSQL("ALTER TABLE habits ADD COLUMN newField TEXT")
       }
   }
   ```

3. **Register the migration** when building the database:
   ```kotlin
   Room.databaseBuilder(...)
       .addMigrations(MIGRATION_1_2, MIGRATION_2_3)  // Add new migration
       .build()
   ```

**Current Status:** Your app is at database version 2 with `MIGRATION_1_2` already set up. If you don't change the database schema, you don't need to do anything here.

#### Step 3: Build the New APK

Build the new version:
```powershell
.\gradlew.bat assembleDebug
```

Or for release:
```powershell
.\gradlew.bat assembleRelease
```

#### Step 4: Install the Update

Install the new APK using any of the methods above (USB, file transfer, etc.). Android will:
- Detect it's an update (same package name, higher version code)
- Preserve all existing data
- Run any database migrations automatically
- Update the app seamlessly

**You do NOT need to uninstall the old version first!** Just install the new APK over it.

### What Data Gets Preserved?

All data stored in your Room database is automatically preserved:
- ✅ Habits and their settings
- ✅ Habit completions and history
- ✅ Notes (habit notes and addiction notes)
- ✅ Labels and label associations
- ✅ Addiction habits and abstinence goals

The database file is stored at: `/data/data/com.example.rewire/databases/rewire_database`

### Version Management Best Practices

1. **Version Code**: Always increment by 1 (or more) for each release
   - Version 1 → 2 → 3 → 4, etc.
   - This is what Android uses to determine if an update is available

2. **Version Name**: Use semantic versioning for display
   - `1.0` → `1.1` (minor update)
   - `1.1` → `2.0` (major update)
   - `2.0` → `2.0.1` (patch)

3. **Database Version**: Only increment when you change the database schema
   - If you just fix bugs or change UI, keep database version the same
   - If you add/remove/modify tables or columns, increment database version

4. **Keep a Changelog**: Document what changed in each version
   - Helps you remember what migrations you need
   - Useful for tracking what features were added

### Example Update Workflow

```powershell
# 1. Make your code changes
# 2. Update versionCode in build.gradle.kts (e.g., 1 → 2)
# 3. Update versionName (e.g., "1.0" → "1.1")
# 4. If database changed, increment database version and add migration
# 5. Build new APK
.\gradlew.bat assembleDebug

# 6. Install on phone (data will be preserved automatically)
adb install app\build\outputs\apk\debug\app-debug.apk
# OR transfer APK to phone and install manually
```

### Troubleshooting Updates

**"App not installed" when trying to update:**
- Make sure `versionCode` is higher than the installed version
- Make sure the package name is exactly the same
- Make sure you're using the same signing key (debug builds are fine)

**Data lost after update:**
- This shouldn't happen if you follow the steps above
- If it does, check that database migrations are correct
- Make sure you didn't change the package name

**Database migration errors:**
- Check that all migrations are registered in `MainActivity.kt`
- Verify migration SQL is correct
- Test migrations on a device with existing data before releasing

