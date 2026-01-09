# Notifications Implementation Plan

## Overview

Implement a notification system to remind users of their habits at their estimated/preferred time. Notifications will provide quick actions to mark habits as complete or add a note. When the "add note" action is selected, a small dialog will open allowing the user to enter a note for the habit.

## Compatibility Check

**Overall Compatibility**: ✅ **Compatible with other features (e.g., Utilities Menu) with minor coordination needed**

This implementation has been checked for compatibility with other planned features. Key coordination points:

### 1. HabitManager Constructor Change ⚠️ **REQUIRES COORDINATION**

**Issue**: Adding optional `Context` parameter to `HabitManager` constructor for notification scheduling.

**Solution**: Make `Context` parameter **optional with default `null`** for backward compatibility.

**Affected Files**:
- `app/src/main/kotlin/MainActivity.kt` - Pass `applicationContext`
- `app/src/test/kotlin/manager/HabitManagerTest.kt` - Pass `null` (tests don't need notifications)

**Action Required**:
- [ ] Make `Context` parameter optional with default `null` in `HabitManager` constructor
- [ ] Update `MainActivity.kt` to pass `applicationContext` to `HabitManager`
- [ ] Update `HabitManagerTest.kt` to pass `null` for context
- [ ] Verify no other files instantiate `HabitManager` directly

### 2. HabitHomeScreen State Management ⚠️ **REQUIRES COORDINATION**

**Issue**: Both notifications and utilities menu add state variables to `HabitHomeScreen`.

**Solution**: Use clear, unique state variable names to avoid conflicts.

**State Variables to Add** (Notifications):
- `showNoteDialog: Boolean`
- `noteDialogHabitId: Long?`
- `noteDialogHabitName: String`
- `noteDialogInitialNote: String`

**Note**: Utilities Menu uses different state variables (`showSearchModal`, `searchQuery`, `showFilterUI`), so no conflicts.

**Action Required**:
- [ ] Use unique, descriptive names for all state variables
- [ ] Document state management in code comments
- [ ] Test that all state works together correctly

### 3. Intent Handling ✅ **NO CONFLICT**

**Issue**: Notifications add intent handling in `MainActivity`.

**Solution**: Intent actions are namespaced (`com.example.rewire.ACTION_ADD_NOTE`) and won't conflict.

**Action Required**:
- [ ] Ensure intent action constants are unique and namespaced
- [ ] Test that notification intents don't interfere with other app functionality

### 4. File Creation ✅ **NO CONFLICT**

**New Files Created**:
- `NotificationManager.kt`, `NotificationScheduler.kt`, `HabitNotificationWorker.kt`
- `BootReceiver.kt`, `HabitNotificationReceiver.kt`
- `HabitNoteDialog.kt`, `NotificationConstants.kt`
- `NotificationDateCalculator.kt`, `NotificationPermissionHelper.kt`

**Note**: No naming conflicts with other features (e.g., Utilities Menu creates `HabitSearchModal.kt`).

### 5. Dependencies ✅ **NO CONFLICT**

**New Dependency**: WorkManager (`androidx.work:work-runtime-ktx`)

**Note**: Standard AndroidX library, doesn't conflict with existing dependencies.

### 6. AndroidManifest Changes ⚠️ **REQUIRES COORDINATION**

**Changes Required**:
- Add `POST_NOTIFICATIONS` permission (Android 13+)
- Add `RECEIVE_BOOT_COMPLETED` permission
- Register `BootReceiver` and `HabitNotificationReceiver`

**Action Required**:
- [ ] Add all required permissions
- [ ] Register all receivers with correct intent filters
- [ ] Verify manifest compiles correctly

### Implementation Order Recommendation

**Recommended**: Implement Utilities Menu first, then Notifications. This allows:
1. Establishing state management patterns in `HabitHomeScreen`
2. Testing simpler feature first
3. Adding notifications with full awareness of existing state

### Testing Considerations

When implementing with other features, test:
- [ ] State independence (note dialog doesn't interfere with search modal)
- [ ] Intent handling (notification actions work correctly)
- [ ] HabitManager methods (all methods work together)
- [ ] UI interactions (all modals/dialogs work together)

---

## Current State

- Habits have a `preferredTime: LocalTime` field indicating when the user wants to be reminded
- Habits have an `estimatedMinutes: Int` field for duration
- Habits have a `recurrence: RecurrenceType` field for scheduling frequency
- **Habit Display Logic**: The home screen uses `HabitManager.getHabitsDueOn(today)` to determine which habits to display
  - This method filters all habits based on recurrence logic and `startDate`
  - Habits are sorted by `preferredTime` for display
  - **This same logic should be used to determine which habits need notifications**
- Completion tracking exists via `HabitCompletion` entity
  - **Missing**: Direct method to check if a habit is completed for a specific date
  - Current approach: Get all completions and check manually with `completions.any { it.date == today }`
  - **Need to add**: `isHabitCompletedForDate(habitId: Long, date: String): Boolean` at all levels (DAO, Repository, Manager)
- Note system exists via `HabitNoteEntity` with methods in `HabitManager`:
  - `insertNote(note: HabitNoteEntity)`
  - `editNote(note: HabitNoteEntity)`
  - `deleteNote(note: HabitNoteEntity)`
  - `getNotesForHabit(habitId: Long): List<HabitNoteEntity>`
  - `getNoteForHabitOnDate(habitId: Long, date: String): String`
- No notification system currently exists in the app
- No notification permissions are requested

## Proposed Changes

### 1. Notification Scheduling System

#### 1.1 Core Scheduling Logic
- **Habit Selection**: Use `HabitManager.getHabitsDueOn(date)` to determine which habits need notifications for each date
  - This reuses the existing recurrence logic already used by the home screen
  - Ensures consistency between displayed habits and scheduled notifications
- Schedule notifications based on habit `preferredTime` and `recurrence`
- Calculate notification times based on:
  - Daily habits: Every day at `preferredTime`
  - Weekly habits: On specified days at `preferredTime` (defaults to Monday)
  - Monthly habits: On specified dates/days at `preferredTime`
  - Custom weekly habits: On custom days at `preferredTime`
- **Before scheduling**: Check if habit is already completed for that date using `isHabitCompletedForDate()`
  - Skip notification if habit is already completed
  - This prevents annoying notifications for already-completed habits
- Recalculate schedules when:
  - Habits are created, updated, or deleted
  - App starts/restarts
  - Device reboots (see Boot Receiver section)

#### 1.2 Notification Channel
- Create a notification channel for habit reminders
- Channel name: "Habit Reminders"
- Channel description: "Notifications for your daily habit reminders"
- Channel importance: `NotificationManager.IMPORTANCE_HIGH`
- Enable vibration and sound

#### 1.3 Notification Content
- **Title**: Habit name (e.g., "Drink Water")
- **Text**: Customizable message (e.g., "Time for your habit: [Habit Name]")
- **Icon**: App icon or habit-specific icon (if labels have icons in future)
- **Color**: Use label color if habit has a label, otherwise default app color

### 2. Notification Actions

#### 2.1 "Mark as Complete" Action
- Action button/icon in the notification
- When tapped:
  - Marks the habit as complete for today's date
  - Calls `HabitManager.completeHabit(habitId, date)`
  - Updates notification to show completion status (or dismisses)
  - Optionally shows a confirmation message

#### 2.2 "Add Note" Action
- Action button/icon in the notification
- When tapped:
  - Opens the app (if closed) or brings it to foreground
  - Opens a dialog to add/edit a note for the habit
  - Dialog should:
    - Display habit name
    - Provide a text field for note input
    - Include "Cancel" and "Save" buttons
    - Pre-fill with existing note if one exists for today
    - Call `HabitManager.insertNote()` or `editNote()` on save
    - Handle both new notes and editing existing notes

### 3. Notification Dialog Components

#### 3.1 Note Dialog Component
- **File**: `app/src/main/kotlin/ui/components/HabitNoteDialog.kt` (new)
- **Design**:
  - Material Design Dialog
  - Title: "Add Note for [Habit Name]"
  - Multiline text field for note input
  - Character count (optional)
  - "Cancel" and "Save" buttons
  - Use app theme (AppColors, AppTypography, AppShapes)
- **Functionality**:
  - Accepts `habitId: Long` and optional `initialNote: String`
  - Emits save action with note content
  - Validates note is not empty before saving
  - Handles keyboard and focus management

#### 3.2 Deep Linking/Intent Handling
- Create intent filters for notification actions
- Handle notification taps to:
  - Open app to specific habit (if viewing habit detail)
  - Open note dialog directly from notification action
  - Handle "complete" action without opening app (background service)

### 4. Background Service/Worker

#### 4.1 Notification Service
- Use WorkManager for reliable notification scheduling
- **File**: `app/src/main/kotlin/service/HabitNotificationWorker.kt` (new)
- **Responsibilities**:
  - Schedule notifications for all active habits
  - Cancel notifications when habits are deleted
  - Reschedule notifications when habits are updated
  - Handle periodic scheduling for recurring habits

#### 4.2 Notification Receiver
- **File**: `app/src/main/kotlin/receiver/HabitNotificationReceiver.kt` (new)
- **Responsibilities**:
  - Handle notification action intents
  - Process "complete habit" action in background
  - Launch app with intent data for "add note" action
  - Update notification state after actions

## Implementation Notes

### Technical Considerations

1. **Permission Management**:
   - **Notification Permission (Android 13+ / API 33+)**:
     - Request `POST_NOTIFICATIONS` permission
     - **Important**: The app can function without notification permission, but notifications will not work
     - If permission is denied, the app should continue to work normally (habits can still be tracked)
     - Show a helpful message explaining why notifications are useful, but don't block app functionality
   - Use `NotificationManagerCompat` for compatibility with older Android versions
   - Handle permission denial gracefully - don't crash or block app usage
   - **Exact Alarm Permission (Android 12+ / API 31+)**:
     - Request `SCHEDULE_EXACT_ALARM` permission for precise timing
     - Check with `AlarmManager.canScheduleExactAlarms()`
     - Fallback to inexact alarms if denied (WorkManager handles this automatically)
     - **Note**: WorkManager uses inexact alarms by default, which is usually sufficient

2. **WorkManager vs AlarmManager**:
   - **WorkManager** (Recommended):
     - **What is WorkManager?**: WorkManager is an AndroidX library (part of Android Jetpack) that provides a unified API for background work
     - It's not part of the OS, but a library that interfaces with the OS to schedule background tasks
     - Better battery optimization
     - Handles doze mode and app standby
     - More reliable for recurring tasks
     - Better integration with Android system
     - **Note**: Periodic work has 15-minute minimum interval
     - **Solution**: Use `OneTimeWorkRequest` scheduled individually for exact timing
     - Each notification is scheduled as a separate one-time work request at the exact `preferredTime`
   - **AlarmManager**:
     - More precise timing
     - More complex implementation
     - Less battery friendly
     - May be needed if exact timing is critical (but WorkManager with OneTimeWorkRequest should be sufficient)

3. **Notification IDs**:
   - Use unique IDs per habit per day
   - **Recommended format**: Deterministic ID generation
     - Format: `"habit_${habitId}_${date}"` then hash, or
     - Use `habitId * 10000 + date.hashCode()` (ensure no collisions)
   - Store notification IDs for cancellation
   - Consider using WorkManager work IDs instead of manual ID management

4. **State Management**:
   - Track scheduled notifications in a repository or database
   - Store notification IDs for cancellation
   - Handle app updates and notification rescheduling
   - **Decision needed**: Database table vs WorkManager internal storage
     - Database: More control, queryable, debuggable
     - WorkManager: Simpler, less control

5. **Date Calculation Logic**:
   - Calculate next occurrence date for each recurrence type:
     - **Daily**: Next day at `preferredTime`
     - **Weekly**: Next occurrence of the weekday (default Monday) at `preferredTime`
     - **CustomWeekly**: Next occurrence of any selected day at `preferredTime`
     - **MonthlyByDate**: Next month's same date (handle month-end: 31st → 30th/28th)
     - **MonthlyByWeekday**: Next month's same weekday occurrence (e.g., 3rd Friday)
     - **QuarterlyByDate**: Next quarter's same date (handle month-offset)
     - **QuarterlyByWeekday**: Next quarter's same weekday occurrence
   - Schedule multiple future occurrences (recommend: 30-90 days ahead)
   - Handle edge cases: leap years, month-end dates, dates before `startDate`
   - Don't schedule notifications for past dates or dates before `habit.startDate`

6. **Completed Habit Check**:
   - Before showing notification, check if habit is already completed for that date
   - Skip notification if `HabitManager.getCompletionsForHabit(habitId)` contains the date
   - This prevents annoying notifications for already-completed habits
   - Should be basic functionality, not future enhancement

7. **Boot Receiver**:
   - **Purpose**: Reschedule all notifications when device reboots (notifications are cleared on reboot)
   - **File**: `app/src/main/kotlin/receiver/BootReceiver.kt`
   - **Implementation**:
     - Listen for `ACTION_BOOT_COMPLETED` broadcast
     - When received, reschedule all notifications for all habits
     - Use `HabitManager.getHabits()` to get all habits
     - For each habit, calculate next occurrence dates and schedule notifications
   - **Manifest Registration**:
     - Add `RECEIVE_BOOT_COMPLETED` permission
     - Register `BootReceiver` with intent filter for `ACTION_BOOT_COMPLETED`
   - **Note**: On Android 9+ (API 28+), boot completed broadcast may be delayed until user unlocks device
   - WorkManager also handles this automatically, but explicit receiver ensures immediate rescheduling on unlock

8. **Timezone Change Handling**:
   - Listen for `TIMEZONE_CHANGED` broadcast
   - Reschedule all notifications with new timezone
   - Can be handled in existing receiver or separate `TimezoneChangeReceiver`

9. **Notification Grouping**:
   - Multiple habits may have same `preferredTime`
   - **Decision needed**: Group notifications or show separately?
   - Grouping reduces clutter but may reduce visibility
   - Recommend: Separate notifications for Phase 1, grouping as future enhancement

10. **Label Color Extraction**:
    - Convert label hex color string to Android `Color` for notification
    - If habit has multiple labels, use first label or primary label
    - Default color if no labels: Use `AppColors.primary` or app theme color
    - Helper function: `getNotificationColor(habit: HabitEntity): Int`

11. **Handling Null PreferredTime**:
    - `AddictionHabit` has `preferredTime: LocalTime?` (nullable)
    - **Decision needed**: Skip notifications or use default time (e.g., 9:00 AM)?
    - Recommend: Skip notifications if `preferredTime` is null

12. **Testing Considerations**:
   - Test notification scheduling with various recurrence types
   - Test notification actions (complete, add note)
   - Test behavior when app is in foreground/background/killed
   - Test on different Android versions (especially permissions)
   - Test timezone changes
   - Test device reboots and app updates
    - Test edge cases: leap years, month-end dates, dates before startDate
    - Test with habits that have no preferredTime
    - Test notification cancellation and rescheduling

### UI/UX Considerations

1. **Notification Design**:
   - Keep notification text concise
   - Use clear action labels ("Complete", "Add Note")
   - Consider notification expansion for additional details
   - Match notification styling to app theme

2. **Note Dialog UX**:
   - Auto-focus text field when dialog opens
   - Support keyboard shortcuts (Enter to save, Esc to cancel)
   - Show loading state while saving
   - Provide feedback on save success/failure
   - Consider character limits if needed

3. **User Preferences** (Future Enhancement):
   - Allow users to enable/disable notifications per habit
   - Allow users to set custom notification times
   - Allow users to customize notification message
   - Allow users to set quiet hours

## Future Enhancements

1. **Notification Settings Screen**:
   - Toggle notifications on/off per habit
   - Custom notification times per habit
   - Custom notification messages
   - Quiet hours configuration
   - Notification sound/vibration preferences

2. **Smart Notifications**:
   - Skip notifications if habit is already completed
   - Adaptive scheduling based on completion patterns
   - Reminder notifications for missed habits

3. **Notification History**:
   - Track notification delivery
   - Show notification statistics
   - Analyze notification effectiveness

4. **Rich Notifications**:
   - Show habit progress in notification
   - Show streak information
   - Show label/chip information
   - Notification images or media

5. **Multiple Notification Types**:
   - Morning summary notifications
   - End-of-day review notifications
   - Weekly/monthly progress notifications

## Files to Create

1. **Notification Worker**:
   - `app/src/main/kotlin/service/HabitNotificationWorker.kt` - WorkManager worker for scheduling notifications

2. **Notification Receiver**:
   - `app/src/main/kotlin/receiver/HabitNotificationReceiver.kt` - BroadcastReceiver for notification actions

3. **Boot Receiver**:
   - `app/src/main/kotlin/receiver/BootReceiver.kt` - BroadcastReceiver to reschedule notifications on device reboot

4. **Notification Manager/Helper**:
   - `app/src/main/kotlin/manager/NotificationManager.kt` - Helper class for notification creation and scheduling
   - Include date calculation logic for next occurrence dates
   - Include label color extraction logic
   - Include completed habit check logic

5. **Note Dialog Component**:
   - `app/src/main/kotlin/ui/components/HabitNoteDialog.kt` - Dialog component for adding/editing notes

6. **Intent Extras/Constants**:
   - `app/src/main/kotlin/util/NotificationConstants.kt` - Constants for notification actions, intents, and extras

7. **Date Calculation Utilities** (optional, or include in NotificationManager):
   - `app/src/main/kotlin/util/NotificationDateCalculator.kt` - Helper functions for calculating next occurrence dates

8. **Repository/Database** (if needed):
   - Consider adding a table to track scheduled notifications
   - Or use WorkManager's internal storage
   - **Decision needed during implementation**

## Files to Update

1. **AndroidManifest.xml**:
   - Add notification permission (`POST_NOTIFICATIONS`) for Android 13+
   - Add exact alarm permission (`SCHEDULE_EXACT_ALARM`) for Android 12+ (optional, WorkManager may not need it)
   - Add boot completed permission (`RECEIVE_BOOT_COMPLETED`)
   - Register `HabitNotificationReceiver`
   - Register `BootReceiver` with `ACTION_BOOT_COMPLETED` intent filter
   - Add intent filters for notification actions
   - Register WorkManager constraints (if needed)

2. **HabitCompletionDao.kt**:
   - **Add method**: `isHabitCompletedForDate(habitId: Long, date: String): Boolean`
   - Query: `SELECT EXISTS(SELECT 1 FROM habit_completions WHERE habitId = :habitId AND date = :date)`

3. **HabitCompletionRepository.kt**:
   - **Add method**: `suspend fun isHabitCompletedForDate(habitId: Long, date: String): Boolean`
   - Call DAO method with proper coroutine context

4. **HabitManager.kt**:
   - **Add method**: `suspend fun isHabitCompletedForDate(habitId: Long, date: String): Boolean`
   - Call repository method
   - Add method to schedule/cancel notifications when habits are created/updated/deleted
   - Integrate notification scheduling with habit lifecycle
   - Use `getHabitsDueOn(date)` to determine which habits need notifications

5. **HabitHomeScreen.kt** (or relevant screen):
   - Integrate `HabitNoteDialog` for adding notes
   - Handle deep links from notifications
   - Update UI after notification actions
   - **Optional**: Replace manual completion check with new `isHabitCompletedForDate()` method

6. **MainActivity.kt**:
   - Handle notification action intents
   - Request notification permissions (non-blocking, app works without them)
   - Initialize notification scheduling on app start
   - Reschedule notifications on app start (in case they were cleared)

7. **Gradle Dependencies** (app/build.gradle.kts):
   - Add WorkManager dependency: `androidx.work:work-runtime-ktx` (latest version)
   - Ensure notification-compat library is included (usually via Material or core-ktx)
   - Verify version compatibility with current Android SDK

## Implementation Phases

### Phase 1: Core Infrastructure

#### Step 1.1: Add Completion Check Methods at All Levels

**Objective**: Add `isHabitCompletedForDate()` method at DAO, Repository, and Manager levels to efficiently check if a habit is completed for a specific date.

**File**: `app/src/main/kotlin/db/dao/HabitCompletionDao.kt`

**Changes Required**:
- Add query method: `isHabitCompletedForDate(habitId: Long, date: String): Boolean`
- Use Room's `@Query` annotation with EXISTS check for efficiency

**Implementation**:
```kotlin
@Query("SELECT EXISTS(SELECT 1 FROM habit_completions WHERE habitId = :habitId AND date = :date)")
suspend fun isHabitCompletedForDate(habitId: Long, date: String): Boolean
```

**Validation Checklist**:
- [ ] File `app/src/main/kotlin/db/dao/HabitCompletionDao.kt` compiles without errors
- [ ] Method `isHabitCompletedForDate()` is added with correct signature
- [ ] Query uses EXISTS for efficient boolean check
- [ ] Method is marked as `suspend` for coroutine support
- [ ] Room can process the query annotation correctly

---

#### Step 1.2: Add Completion Check to Repository

**Objective**: Add completion check method to `HabitCompletionRepository` that calls the DAO method.

**File**: `app/src/main/kotlin/repository/HabitCompletionRepository.kt`

**Changes Required**:
- Add method: `suspend fun isHabitCompletedForDate(habitId: Long, date: String): Boolean`
- Call DAO method with proper coroutine context (`Dispatchers.IO`)

**Implementation**:
```kotlin
suspend fun isHabitCompletedForDate(habitId: Long, date: String): Boolean = withContext(Dispatchers.IO) {
    habitCompletionDao.isHabitCompletedForDate(habitId, date)
}
```

**Validation Checklist**:
- [ ] File `app/src/main/kotlin/repository/HabitCompletionRepository.kt` compiles without errors
- [ ] Method `isHabitCompletedForDate()` is added with correct signature
- [ ] Method uses `withContext(Dispatchers.IO)` for proper threading
- [ ] Method calls `habitCompletionDao.isHabitCompletedForDate()`
- [ ] Method returns Boolean result correctly

---

#### Step 1.3: Add Completion Check to HabitManager

**Objective**: Add completion check method to `HabitManager` that calls the repository method.

**File**: `app/src/main/kotlin/manager/HabitManager.kt`

**Changes Required**:
- Add method: `suspend fun isHabitCompletedForDate(habitId: Long, date: String): Boolean`
- Call repository method

**Implementation**:
```kotlin
suspend fun isHabitCompletedForDate(habitId: Long, date: String): Boolean {
    return habitCompletionRepository.isHabitCompletedForDate(habitId, date)
}
```

**Validation Checklist**:
- [ ] File `app/src/main/kotlin/manager/HabitManager.kt` compiles without errors
- [ ] Method `isHabitCompletedForDate()` is added with correct signature
- [ ] Method calls `habitCompletionRepository.isHabitCompletedForDate()`
- [ ] Method returns Boolean result correctly
- [ ] Method can be called from UI layer (Compose screens)

---

#### Step 1.4: Create Notification Constants File

**Objective**: Create a constants file to centralize notification-related strings, action IDs, and intent extras.

**File**: `app/src/main/kotlin/util/NotificationConstants.kt` (new)

**Implementation**:
```kotlin
package com.example.rewire.util

object NotificationConstants {
    // Notification Channel
    const val CHANNEL_ID = "habit_reminders"
    const val CHANNEL_NAME = "Habit Reminders"
    const val CHANNEL_DESCRIPTION = "Notifications for your daily habit reminders"
    
    // Notification Actions
    const val ACTION_COMPLETE_HABIT = "com.example.rewire.ACTION_COMPLETE_HABIT"
    const val ACTION_ADD_NOTE = "com.example.rewire.ACTION_ADD_NOTE"
    
    // Intent Extras
    const val EXTRA_HABIT_ID = "habit_id"
    const val EXTRA_HABIT_NAME = "habit_name"
    const val EXTRA_DATE = "date"
    const val EXTRA_NOTIFICATION_ID = "notification_id"
    
    // Notification IDs
    fun getNotificationId(habitId: Long, date: String): Int {
        return "habit_${habitId}_${date}".hashCode()
    }
    
    // WorkManager Tags
    const val WORK_TAG_HABIT_NOTIFICATION = "habit_notification"
    const val WORK_TAG_HABIT_ID = "habit_id"
}
```

**Validation Checklist**:
- [ ] File `app/src/main/kotlin/util/NotificationConstants.kt` is created
- [ ] All constants are defined with appropriate values
- [ ] Channel ID, name, and description are set
- [ ] Action constants are defined for complete and add note
- [ ] Intent extra keys are defined
- [ ] `getNotificationId()` helper function is implemented
- [ ] WorkManager tags are defined
- [ ] File compiles without errors

---

#### Step 1.5: Set Up Notification Channel

**Objective**: Create notification channel for habit reminders with appropriate settings.

**File**: `app/src/main/kotlin/MainActivity.kt`

**Changes Required**:
- Create notification channel in `onCreate()` method
- Use `NotificationManagerCompat` for compatibility
- Set channel importance to `IMPORTANCE_HIGH`
- Enable vibration and sound

**Implementation**:
```kotlin
private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            NotificationConstants.CHANNEL_ID,
            NotificationConstants.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = NotificationConstants.CHANNEL_DESCRIPTION
            enableVibration(true)
            enableLights(true)
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
```

**Validation Checklist**:
- [ ] `createNotificationChannel()` method is added to `MainActivity`
- [ ] Method is called in `onCreate()`
- [ ] Channel is created with correct ID, name, and description
- [ ] Channel importance is set to `IMPORTANCE_HIGH`
- [ ] Vibration is enabled
- [ ] Sound is enabled (default system sound)
- [ ] Method checks Android version (API 26+)
- [ ] Channel is created successfully (test on device/emulator)

---

#### Step 1.6: Create Notification Permission Handler

**Objective**: Create helper class to request and check notification permissions (non-blocking).

**File**: `app/src/main/kotlin/util/NotificationPermissionHelper.kt` (new)

**Implementation**:
```kotlin
package com.example.rewire.util

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object NotificationPermissionHelper {
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            // Permission not required for Android 12 and below
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }
    
    fun shouldRequestPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }
}
```

**Validation Checklist**:
- [ ] File `app/src/main/kotlin/util/NotificationPermissionHelper.kt` is created
- [ ] `hasNotificationPermission()` method checks permission correctly
- [ ] Method handles Android 13+ (API 33+) correctly
- [ ] Method handles older Android versions correctly
- [ ] `shouldRequestPermission()` method returns true only for Android 13+
- [ ] File compiles without errors
- [ ] Permission check works correctly (test on different Android versions)

---

#### Step 1.7: Create Basic Notification Manager Helper

**Objective**: Create helper class for creating and showing notifications.

**File**: `app/src/main/kotlin/manager/NotificationManager.kt` (new)

**Implementation**:
```kotlin
package com.example.rewire.manager

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.rewire.R
import com.example.rewire.util.NotificationConstants
import com.example.rewire.db.entity.HabitEntity
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.ui.theme.AppColors
import android.graphics.Color

class HabitNotificationManager(private val context: Context) {
    private val notificationManager = NotificationManagerCompat.from(context)
    
    fun createNotification(
        habit: HabitEntity,
        labels: List<LabelEntity> = emptyList()
    ): NotificationCompat.Builder {
        val color = getNotificationColor(labels)
        
        return NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use app icon
            .setContentTitle(habit.name)
            .setContentText("Time for your habit: ${habit.name}")
            .setColor(color)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_check, // Use check icon
                "Complete",
                createCompleteActionPendingIntent(habit.id)
            )
            .addAction(
                R.drawable.ic_note, // Use note icon
                "Add Note",
                createAddNoteActionPendingIntent(habit.id, habit.name)
            )
    }
    
    fun showNotification(habit: HabitEntity, labels: List<LabelEntity> = emptyList()) {
        if (!NotificationPermissionHelper.hasNotificationPermission(context)) {
            return // Silently fail if no permission
        }
        
        val notification = createNotification(habit, labels).build()
        val notificationId = NotificationConstants.getNotificationId(habit.id, LocalDate.now().toString())
        notificationManager.notify(notificationId, notification)
    }
    
    private fun getNotificationColor(labels: List<LabelEntity>): Int {
        return if (labels.isNotEmpty()) {
            try {
                Color.parseColor(labels.first().color)
            } catch (e: IllegalArgumentException) {
                AppColors.primary.hashCode()
            }
        } else {
            AppColors.primary.hashCode()
        }
    }
    
    // TODO: Implement pending intents in Step 3.1
    private fun createCompleteActionPendingIntent(habitId: Long): PendingIntent? = null
    private fun createAddNoteActionPendingIntent(habitId: Long, habitName: String): PendingIntent? = null
}
```

**Validation Checklist**:
- [ ] File `app/src/main/kotlin/manager/NotificationManager.kt` is created
- [ ] Class `HabitNotificationManager` is created with `Context` parameter
- [ ] `createNotification()` method creates notification builder correctly
- [ ] Notification uses correct channel ID
- [ ] Notification title and text are set correctly
- [ ] Notification color is extracted from labels or uses default
- [ ] `showNotification()` method checks permission before showing
- [ ] `getNotificationColor()` handles label colors correctly
- [ ] Notification ID is generated using helper function
- [ ] File compiles without errors (pending intents can be null for now)

---

#### Step 1.8: Create Boot Receiver

**Objective**: Create broadcast receiver to reschedule notifications when device reboots.

**File**: `app/src/main/kotlin/receiver/BootReceiver.kt` (new)

**Implementation**:
```kotlin
package com.example.rewire.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device booted, rescheduling notifications")
            // TODO: Implement notification rescheduling in Phase 2
            // This will call HabitNotificationScheduler.rescheduleAllNotifications()
        }
    }
}
```

**Validation Checklist**:
- [ ] File `app/src/main/kotlin/receiver/BootReceiver.kt` is created
- [ ] Class extends `BroadcastReceiver`
- [ ] `onReceive()` method handles `ACTION_BOOT_COMPLETED` intent
- [ ] Logging is added for debugging
- [ ] File compiles without errors
- [ ] Receiver is registered in manifest (see Step 1.9)

---

#### Step 1.9: Update AndroidManifest.xml

**Objective**: Add permissions and register receivers in manifest.

**File**: `app/src/main/AndroidManifest.xml`

**Changes Required**:
- Add `POST_NOTIFICATIONS` permission (Android 13+)
- Add `RECEIVE_BOOT_COMPLETED` permission
- Register `BootReceiver` with intent filter

**Implementation**:
```xml
<manifest ...>
    <!-- Permissions -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    
    <application ...>
        <!-- Existing activity -->
        
        <!-- Boot Receiver -->
        <receiver
            android:name=".receiver.BootReceiver"
            android:enabled="true"
            android:exported="true"
            android:permission="android.permission.RECEIVE_BOOT_COMPLETED">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
    </application>
</manifest>
```

**Validation Checklist**:
- [ ] `POST_NOTIFICATIONS` permission is added
- [ ] `RECEIVE_BOOT_COMPLETED` permission is added
- [ ] `BootReceiver` is registered in manifest
- [ ] Receiver has correct intent filter for `BOOT_COMPLETED`
- [ ] Receiver has `enabled="true"` and `exported="true"`
- [ ] Manifest file compiles without errors

---

#### Step 1.10: Add WorkManager Dependency

**Objective**: Add WorkManager library to project dependencies.

**File**: `app/build.gradle.kts`

**Changes Required**:
- Add WorkManager dependency

**Implementation**:
```kotlin
dependencies {
    // ... existing dependencies ...
    
    // WorkManager for notification scheduling
    implementation("androidx.work:work-runtime-ktx:2.9.0")
}
```

**Validation Checklist**:
- [ ] WorkManager dependency is added to `app/build.gradle.kts`
- [ ] Version is compatible with current Android SDK (check latest version)
- [ ] Project syncs successfully
- [ ] No dependency conflicts
- [ ] WorkManager classes can be imported in code

---

### Phase 2: Notification Scheduling

#### Step 2.1: Create Date Calculation Utility

**Objective**: Create utility functions to calculate next occurrence dates for each recurrence type.

**File**: `app/src/main/kotlin/util/NotificationDateCalculator.kt` (new)

**Implementation**:
```kotlin
package com.example.rewire.util

import com.example.rewire.core.RecurrenceType
import com.example.rewire.core.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

object NotificationDateCalculator {
    /**
     * Calculate the next occurrence date for a habit based on its recurrence type.
     * Returns null if no valid date found (e.g., before startDate).
     */
    fun getNextOccurrenceDate(
        recurrence: RecurrenceType,
        startDate: LocalDate,
        preferredTime: LocalTime,
        fromDate: LocalDate = LocalDate.now()
    ): LocalDate? {
        // Don't schedule before start date
        if (fromDate.isBefore(startDate)) {
            return null
        }
        
        return when (recurrence) {
            is RecurrenceType.Daily -> {
                if (fromDate.isBefore(startDate)) startDate else fromDate.plusDays(1)
            }
            is RecurrenceType.Weekly -> {
                getNextWeeklyOccurrence(fromDate, startDate.dayOfWeek)
            }
            is RecurrenceType.CustomWeekly -> {
                getNextCustomWeeklyOccurrence(fromDate, recurrence.daysOfWeek)
            }
            is RecurrenceType.MonthlyByDate -> {
                getNextMonthlyByDateOccurrence(fromDate, recurrence.dayOfMonth, startDate)
            }
            is RecurrenceType.MonthlyByWeekday -> {
                getNextMonthlyByWeekdayOccurrence(fromDate, recurrence.weekOfMonth, recurrence.dayOfWeek, startDate)
            }
            is RecurrenceType.QuarterlyByDate -> {
                getNextQuarterlyByDateOccurrence(fromDate, recurrence.dayOfMonth, recurrence.monthOffset, startDate)
            }
            is RecurrenceType.QuarterlyByWeekday -> {
                getNextQuarterlyByWeekdayOccurrence(fromDate, recurrence.weekOfMonth, recurrence.dayOfWeek, recurrence.monthOffset, startDate)
            }
        }
    }
    
    /**
     * Get all occurrence dates for the next N days (default 30).
     */
    fun getOccurrenceDates(
        recurrence: RecurrenceType,
        startDate: LocalDate,
        preferredTime: LocalTime,
        daysAhead: Int = 30,
        fromDate: LocalDate = LocalDate.now()
    ): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var currentDate = fromDate
        var nextDate = getNextOccurrenceDate(recurrence, startDate, preferredTime, currentDate)
        
        while (nextDate != null && dates.size < daysAhead && 
               ChronoUnit.DAYS.between(fromDate, nextDate) <= daysAhead) {
            dates.add(nextDate)
            currentDate = nextDate.plusDays(1)
            nextDate = getNextOccurrenceDate(recurrence, startDate, preferredTime, currentDate)
        }
        
        return dates
    }
    
    // Helper methods for each recurrence type...
    private fun getNextWeeklyOccurrence(fromDate: LocalDate, startDayOfWeek: java.time.DayOfWeek): LocalDate {
        // Implementation for weekly
    }
    
    // ... other helper methods
}
```

**Validation Checklist**:
- [ ] File `app/src/main/kotlin/util/NotificationDateCalculator.kt` is created
- [ ] `getNextOccurrenceDate()` method handles all recurrence types
- [ ] Method respects `startDate` (returns null if before start)
- [ ] `getOccurrenceDates()` method calculates multiple future dates
- [ ] Helper methods are implemented for each recurrence type
- [ ] Edge cases are handled (month-end dates, leap years, etc.)
- [ ] File compiles without errors
- [ ] Unit tests can be written for date calculations

---

#### Step 2.2: Create Notification Scheduler

**Objective**: Create class to schedule notifications using WorkManager.

**File**: `app/src/main/kotlin/manager/HabitNotificationScheduler.kt` (new)

**Implementation**:
```kotlin
package com.example.rewire.manager

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data
import com.example.rewire.util.NotificationConstants
import com.example.rewire.util.NotificationDateCalculator
import com.example.rewire.db.entity.HabitEntity
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class HabitNotificationScheduler(private val context: Context) {
    private val workManager = WorkManager.getInstance(context)
    
    suspend fun scheduleNotificationsForHabit(
        habit: HabitEntity,
        habitManager: HabitManager
    ) {
        // Get next occurrence dates
        val preferredTime = LocalTime.parse(habit.preferredTime)
        val startDate = LocalDate.parse(habit.startDate)
        val occurrenceDates = NotificationDateCalculator.getOccurrenceDates(
            recurrence = habit.recurrence,
            startDate = startDate,
            preferredTime = preferredTime,
            daysAhead = 30
        )
        
        // Schedule notification for each occurrence
        for (date in occurrenceDates) {
            // Check if habit is already completed for this date
            val isCompleted = habitManager.isHabitCompletedForDate(habit.id, date.toString())
            if (isCompleted) {
                continue // Skip if already completed
            }
            
            // Calculate notification time
            val notificationDateTime = LocalDateTime.of(date, preferredTime)
            val now = LocalDateTime.now()
            
            // Only schedule if notification time is in the future
            if (notificationDateTime.isAfter(now)) {
                scheduleNotification(habit, date, notificationDateTime)
            }
        }
    }
    
    private fun scheduleNotification(
        habit: HabitEntity,
        date: LocalDate,
        notificationDateTime: LocalDateTime
    ) {
        val delay = java.time.Duration.between(LocalDateTime.now(), notificationDateTime).toMillis()
        
        val inputData = Data.Builder()
            .putLong(NotificationConstants.EXTRA_HABIT_ID, habit.id)
            .putString(NotificationConstants.EXTRA_DATE, date.toString())
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<HabitNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag(NotificationConstants.WORK_TAG_HABIT_NOTIFICATION)
            .addTag("${NotificationConstants.WORK_TAG_HABIT_ID}_${habit.id}")
            .build()
        
        workManager.enqueue(workRequest)
    }
    
    fun cancelNotificationsForHabit(habitId: Long) {
        workManager.cancelAllWorkByTag("${NotificationConstants.WORK_TAG_HABIT_ID}_${habitId}")
    }
    
    suspend fun rescheduleAllNotifications(habitManager: HabitManager) {
        val habits = habitManager.getHabits()
        for (habit in habits) {
            scheduleNotificationsForHabit(habit, habitManager)
        }
    }
}
```

**Validation Checklist**:
- [ ] File `app/src/main/kotlin/manager/HabitNotificationScheduler.kt` is created
- [ ] `scheduleNotificationsForHabit()` method schedules notifications correctly
- [ ] Method checks completion status before scheduling
- [ ] Method only schedules future notifications
- [ ] `scheduleNotification()` creates WorkManager request correctly
- [ ] `cancelNotificationsForHabit()` cancels all notifications for a habit
- [ ] `rescheduleAllNotifications()` reschedules all habits
- [ ] File compiles without errors

---

#### Step 2.3: Create WorkManager Worker

**Objective**: Create WorkManager worker that shows notifications when scheduled time arrives.

**File**: `app/src/main/kotlin/service/HabitNotificationWorker.kt` (new)

**Implementation**:
```kotlin
package com.example.rewire.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.rewire.manager.HabitManager
import com.example.rewire.manager.HabitNotificationManager
import com.example.rewire.repository.HabitRepository
import com.example.rewire.repository.HabitCompletionRepository
import com.example.rewire.repository.HabitNoteRepository
import com.example.rewire.repository.LabelRepository
import com.example.rewire.util.NotificationConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val habitId = inputData.getLong(NotificationConstants.EXTRA_HABIT_ID, -1)
            val date = inputData.getString(NotificationConstants.EXTRA_DATE) ?: return@withContext Result.failure()
            
            // Get habit from database (need to inject HabitManager or repositories)
            // For now, use application context to get database
            // TODO: Use dependency injection in future
            
            // Check if habit is still completed (may have been completed since scheduling)
            // If completed, don't show notification
            
            // Show notification
            val notificationManager = HabitNotificationManager(applicationContext)
            // Get habit and labels, then show notification
            
            Result.success()
        } catch (e: Exception) {
            Result.retry() // Retry on failure
        }
    }
}
```

**Validation Checklist**:
- [ ] File `app/src/main/kotlin/service/HabitNotificationWorker.kt` is created
- [ ] Class extends `CoroutineWorker`
- [ ] `doWork()` method extracts habit ID and date from input data
- [ ] Method checks if habit is still completed before showing
- [ ] Method shows notification using `HabitNotificationManager`
- [ ] Error handling is implemented (retry on failure)
- [ ] File compiles without errors
- [ ] Worker can be tested with WorkManager test utilities

---

#### Step 2.4: Integrate Scheduling with HabitManager

**Objective**: Add notification scheduling calls to HabitManager when habits are created/updated/deleted.

**File**: `app/src/main/kotlin/manager/HabitManager.kt`

**Changes Required**:
- Add optional `Context` parameter to constructor (default `null` for backward compatibility)
- Add `HabitNotificationScheduler` as dependency (or create instance when needed)
- Call scheduler when habits are created
- Call scheduler when habits are updated
- Call cancel when habits are deleted

**Important Compatibility Note**: 
- Making `Context` parameter **optional with default `null`** ensures backward compatibility
- This allows existing code (like tests) to continue working without changes
- Only code that needs notifications should pass context

**Implementation**:
```kotlin
class HabitManager(
    private val habitRepository: HabitRepository,
    private val habitCompletionRepository: HabitCompletionRepository,
    private val habitNoteRepository: HabitNoteRepository,
    private val labelRepository: LabelRepository,
    private val context: Context? = null // Optional, default null for backward compatibility
) {
    // Notification scheduler only created if context is provided
    private val notificationScheduler: HabitNotificationScheduler? = 
        context?.let { HabitNotificationScheduler(it) }
    
    suspend fun createHabit(habit: HabitEntity) {
        require(habit.name.isNotBlank()) { "Habit name cannot be blank" }
        habitRepository.insertHabit(habit)
        
        // Schedule notifications for new habit (only if scheduler is available)
        notificationScheduler?.scheduleNotificationsForHabit(habit, this)
    }
    
    suspend fun updateHabit(habit: HabitEntity) {
        require(habit.name.isNotBlank()) { "Habit name cannot be blank" }
        habitRepository.updateHabit(habit)
        
        // Reschedule notifications for updated habit (only if scheduler is available)
        notificationScheduler?.cancelNotificationsForHabit(habit.id)
        notificationScheduler?.scheduleNotificationsForHabit(habit, this)
    }
    
    suspend fun deleteHabit(habit: HabitEntity) {
        habitRepository.deleteHabit(habit)
        
        // Cancel notifications for deleted habit (only if scheduler is available)
        notificationScheduler?.cancelNotificationsForHabit(habit.id)
    }
}
```

**Validation Checklist**:
- [ ] `HabitManager` constructor accepts optional `Context` parameter with default `null`
- [ ] `notificationScheduler` is created only when context is available
- [ ] `createHabit()` schedules notifications after creating habit (if scheduler available)
- [ ] `updateHabit()` cancels and reschedules notifications (if scheduler available)
- [ ] `deleteHabit()` cancels notifications (if scheduler available)
- [ ] Methods handle null scheduler gracefully (when context is null)
- [ ] Existing code that doesn't pass context still works (backward compatible)
- [ ] File compiles without errors

---

#### Step 2.5: Initialize Scheduling in MainActivity

**Objective**: Initialize notification scheduling when app starts and pass context to HabitManager.

**File**: `app/src/main/kotlin/MainActivity.kt`

**Changes Required**:
- Pass application context to HabitManager (update existing instantiation)
- Reschedule all notifications on app start (in case they were cleared)
- Request notification permission (non-blocking)

**Important Compatibility Note**:
- This updates the existing `HabitManager` instantiation in `MainActivity`
- The change is backward compatible because `Context` parameter is optional
- Other features (like Utilities Menu) that don't need notifications can continue using `HabitManager` without context

**Implementation**:
```kotlin
// In onCreate(), update existing HabitManager instantiation:
val habitManager = HabitManager(
    habitRepository,
    habitCompletionRepository,
    habitNoteRepository,
    labelRepository,
    applicationContext // ADD: Pass context for notification scheduling
)

// Request notification permission (non-blocking)
if (NotificationPermissionHelper.shouldRequestPermission() &&
    !NotificationPermissionHelper.hasNotificationPermission(this)) {
    // Show permission request dialog (optional, non-blocking)
    // User can continue using app without notifications
}

// Reschedule all notifications on app start
lifecycleScope.launch {
    HabitNotificationScheduler(applicationContext)
        .rescheduleAllNotifications(habitManager)
}
```

**Validation Checklist**:
- [ ] Existing `HabitManager` instantiation is updated to pass `applicationContext`
- [ ] Notification permission is checked (non-blocking)
- [ ] All notifications are rescheduled on app start
- [ ] App continues to work if permission is denied
- [ ] No crashes occur when notifications can't be scheduled
- [ ] Other features that use `HabitManager` still work correctly
- [ ] File compiles without errors

---

#### Step 2.6: Update BootReceiver to Reschedule Notifications

**Objective**: Complete BootReceiver implementation to reschedule notifications on device reboot.

**File**: `app/src/main/kotlin/receiver/BootReceiver.kt`

**Changes Required**:
- Get HabitManager instance (or use application context)
- Call reschedule method

**Implementation**:
```kotlin
override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
        Log.d("BootReceiver", "Device booted, rescheduling notifications")
        
        // Use WorkManager's initialization to reschedule
        // Or get HabitManager from application class
        // For now, use a background service or WorkManager one-time work
        val scheduler = HabitNotificationScheduler(context)
        
        // Use WorkManager to schedule rescheduling work
        // This ensures database is ready
        val rescheduleWork = OneTimeWorkRequestBuilder<RescheduleNotificationsWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS) // Wait for system to be ready
            .build()
        WorkManager.getInstance(context).enqueue(rescheduleWork)
    }
}
```

**Validation Checklist**:
- [ ] BootReceiver calls rescheduling logic
- [ ] Rescheduling is delayed slightly to ensure system is ready
- [ ] WorkManager is used for reliable rescheduling
- [ ] File compiles without errors
- [ ] Receiver works correctly on device reboot (test on device)

---

### Phase 3: Notification Actions

#### Step 3.1: Create Notification Action Pending Intents

**Objective**: Create pending intents for notification actions (Complete and Add Note).

**File**: `app/src/main/kotlin/manager/NotificationManager.kt`

**Changes Required**:
- Implement `createCompleteActionPendingIntent()`
- Implement `createAddNoteActionPendingIntent()`

**Implementation**:
```kotlin
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import com.example.rewire.util.NotificationConstants

private fun createCompleteActionPendingIntent(habitId: Long): PendingIntent {
    val intent = Intent(context, HabitNotificationReceiver::class.java).apply {
        action = NotificationConstants.ACTION_COMPLETE_HABIT
        putExtra(NotificationConstants.EXTRA_HABIT_ID, habitId)
        putExtra(NotificationConstants.EXTRA_DATE, LocalDate.now().toString())
    }
    
    return PendingIntent.getBroadcast(
        context,
        NotificationConstants.getNotificationId(habitId, LocalDate.now().toString()),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

private fun createAddNoteActionPendingIntent(habitId: Long, habitName: String): PendingIntent {
    val intent = Intent(context, MainActivity::class.java).apply {
        action = NotificationConstants.ACTION_ADD_NOTE
        putExtra(NotificationConstants.EXTRA_HABIT_ID, habitId)
        putExtra(NotificationConstants.EXTRA_HABIT_NAME, habitName)
        putExtra(NotificationConstants.EXTRA_DATE, LocalDate.now().toString())
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    
    return TaskStackBuilder.create(context).run {
        addNextIntentWithParentStack(intent)
        getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
```

**Validation Checklist**:
- [ ] `createCompleteActionPendingIntent()` creates broadcast pending intent
- [ ] `createAddNoteActionPendingIntent()` creates activity pending intent
- [ ] Intents include correct extras (habit ID, date, name)
- [ ] Pending intents use `FLAG_IMMUTABLE` (required for Android 12+)
- [ ] Add Note intent uses TaskStackBuilder for proper navigation
- [ ] File compiles without errors

---

#### Step 3.2: Create Notification Receiver for Actions

**Objective**: Create BroadcastReceiver to handle "Complete" action from notifications.

**File**: `app/src/main/kotlin/receiver/HabitNotificationReceiver.kt` (new)

**Implementation**:
```kotlin
package com.example.rewire.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.rewire.util.NotificationConstants
import java.time.LocalDate

class HabitNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            NotificationConstants.ACTION_COMPLETE_HABIT -> {
                val habitId = intent.getLongExtra(NotificationConstants.EXTRA_HABIT_ID, -1)
                val date = intent.getStringExtra(NotificationConstants.EXTRA_DATE) 
                    ?: LocalDate.now().toString()
                
                if (habitId != -1L) {
                    // Complete habit in background
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            // Get HabitManager from application or use repository directly
                            // For now, use a helper method
                            completeHabitInBackground(context, habitId, date)
                            
                            // Dismiss notification
                            val notificationManager = android.app.NotificationManager
                                .from(context)
                            val notificationId = NotificationConstants.getNotificationId(habitId, date)
                            notificationManager.cancel(notificationId)
                        } catch (e: Exception) {
                            Log.e("HabitNotificationReceiver", "Error completing habit", e)
                        }
                    }
                }
            }
        }
    }
    
    private suspend fun completeHabitInBackground(
        context: Context,
        habitId: Long,
        date: String
    ) {
        // Get HabitManager instance and complete habit
        // Implementation depends on how HabitManager is accessed
    }
}
```

**Validation Checklist**:
- [ ] File `app/src/main/kotlin/receiver/HabitNotificationReceiver.kt` is created
- [ ] Receiver handles `ACTION_COMPLETE_HABIT` action
- [ ] Receiver extracts habit ID and date from intent
- [ ] Habit completion is done in background coroutine
- [ ] Notification is dismissed after completion
- [ ] Error handling is implemented
- [ ] Receiver is registered in manifest (see Step 3.3)
- [ ] File compiles without errors

---

#### Step 3.3: Register Notification Receiver in Manifest

**Objective**: Register HabitNotificationReceiver in AndroidManifest.xml.

**File**: `app/src/main/AndroidManifest.xml`

**Changes Required**:
- Register HabitNotificationReceiver with intent filters

**Implementation**:
```xml
<receiver
    android:name=".receiver.HabitNotificationReceiver"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
        <action android:name="com.example.rewire.ACTION_COMPLETE_HABIT" />
    </intent-filter>
</receiver>
```

**Validation Checklist**:
- [ ] `HabitNotificationReceiver` is registered in manifest
- [ ] Receiver has correct intent filter for complete action
- [ ] Receiver has `enabled="true"` and `exported="true"`
- [ ] Manifest file compiles without errors

---

#### Step 3.4: Handle Add Note Intent in MainActivity

**Objective**: Handle "Add Note" action intent in MainActivity to open note dialog.

**File**: `app/src/main/kotlin/MainActivity.kt`

**Changes Required**:
- Check for `ACTION_ADD_NOTE` intent in `onCreate()` and `onNewIntent()`
- Extract habit ID and name from intent
- Open note dialog

**Implementation**:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // ... existing code ...
    
    // Handle notification action intents
    handleNotificationIntent(intent)
}

override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    handleNotificationIntent(intent)
}

private fun handleNotificationIntent(intent: Intent) {
    if (intent.action == NotificationConstants.ACTION_ADD_NOTE) {
        val habitId = intent.getLongExtra(NotificationConstants.EXTRA_HABIT_ID, -1)
        val habitName = intent.getStringExtra(NotificationConstants.EXTRA_HABIT_NAME) ?: ""
        
        if (habitId != -1L) {
            // Open note dialog
            // This will be implemented in Phase 4
            // For now, just log
            Log.d("MainActivity", "Opening note dialog for habit: $habitName")
        }
    }
}
```

**Validation Checklist**:
- [ ] `handleNotificationIntent()` method is added
- [ ] Method is called in `onCreate()` and `onNewIntent()`
- [ ] Method extracts habit ID and name from intent
- [ ] Method handles `ACTION_ADD_NOTE` action
- [ ] File compiles without errors

---

### Phase 4: Note Dialog

#### Step 4.1: Create HabitNoteDialog Component

**Objective**: Create Material Design dialog component for adding/editing habit notes.

**File**: `app/src/main/kotlin/ui/components/HabitNoteDialog.kt` (new)

**Implementation**:
```kotlin
package com.example.rewire.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.example.rewire.ui.theme.AppColors
import com.example.rewire.ui.theme.AppTypography
import com.example.rewire.ui.theme.AppShapes

@Composable
fun HabitNoteDialog(
    habitName: String,
    initialNote: String = "",
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var noteText by remember { mutableStateOf(initialNote) }
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Note for $habitName",
                style = AppTypography.materialTypography.h6
            )
        },
        text = {
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = { Text("Enter your note...") },
                maxLines = 5,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = AppColors.primary,
                    unfocusedBorderColor = AppColors.borderMedium
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (noteText.isNotBlank()) {
                        onSave(noteText)
                    }
                },
                enabled = noteText.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = AppShapes.cardShape
    )
}
```

**Validation Checklist**:
- [ ] File `app/src/main/kotlin/ui/components/HabitNoteDialog.kt` is created
- [ ] Dialog uses Material Design `AlertDialog`
- [ ] Dialog title includes habit name
- [ ] Text field is multiline (5 lines)
- [ ] Text field auto-focuses when dialog opens
- [ ] Save button is disabled when note is empty
- [ ] Dialog uses app theme (AppColors, AppTypography, AppShapes)
- [ ] File compiles without errors
- [ ] Dialog can be previewed in Android Studio

---

#### Step 4.2: Integrate Note Dialog with HabitManager

**Objective**: Connect note dialog to HabitManager to save notes.

**File**: `app/src/main/kotlin/ui/screens/HabitHomeScreen.kt`

**Changes Required**:
- Add state for showing note dialog
- Add state for habit ID and name for note dialog
- Call HabitManager methods to save notes

**Important Compatibility Note**:
- This adds state variables to `HabitHomeScreen`
- Utilities Menu also adds state variables (search modal, filter visibility)
- Use clear, unique names to avoid conflicts:
  - Note dialog: `showNoteDialog`, `noteDialogHabitId`, `noteDialogHabitName`, `noteDialogInitialNote`
  - Search modal: `showSearchModal`, `searchQuery`, `searchResults`
  - Filter: `showFilterUI` (if not already exists)
- These state variables are independent and won't conflict

**Implementation**:
```kotlin
// Add state
var showNoteDialog by remember { mutableStateOf(false) }
var noteDialogHabitId by remember { mutableStateOf<Long?>(null) }
var noteDialogHabitName by remember { mutableStateOf("") }
var noteDialogInitialNote by remember { mutableStateOf("") }

// In composable, show dialog when state is true
if (showNoteDialog && noteDialogHabitId != null) {
    HabitNoteDialog(
        habitName = noteDialogHabitName,
        initialNote = noteDialogInitialNote,
        onDismiss = {
            showNoteDialog = false
            noteDialogHabitId = null
        },
        onSave = { noteText ->
            coroutineScope.launch {
                noteDialogHabitId?.let { habitId ->
                    val noteEntity = HabitNoteEntity(
                        habitId = habitId,
                        content = noteText,
                        timestamp = today
                    )
                    // Check if note exists for today
                    val existingNote = habitManager.getNoteForHabitOnDate(habitId, today)
                    if (existingNote.isNotBlank()) {
                        // Update existing note
                        val existingEntity = habitManager.getNotesForHabit(habitId)
                            .find { it.timestamp == today }
                        existingEntity?.let {
                            habitManager.editNote(it.copy(content = noteText))
                        }
                    } else {
                        // Insert new note
                        habitManager.insertNote(noteEntity)
                    }
                    // Refresh notes
                    habitNotes = habitNotes + (habitId to noteText)
                }
                showNoteDialog = false
                noteDialogHabitId = null
            }
        }
    )
}
```

**Validation Checklist**:
- [ ] State variables are added for note dialog
- [ ] Dialog is shown when `showNoteDialog` is true
- [ ] `onSave` callback saves note using HabitManager
- [ ] Method handles both new notes and editing existing notes
- [ ] Notes are refreshed after saving
- [ ] Dialog state is cleared after save/dismiss
- [ ] File compiles without errors

---

#### Step 4.3: Open Note Dialog from Notification

**Objective**: Open note dialog when "Add Note" action is tapped from notification.

**File**: `app/src/main/kotlin/MainActivity.kt`

**Changes Required**:
- Pass intent data to HabitHomeScreen or use a shared state
- Open note dialog with habit information

**Implementation**:
```kotlin
// In handleNotificationIntent():
// Store intent data in a way that HabitHomeScreen can access it
// Options:
// 1. Use a ViewModel or shared state
// 2. Pass as intent extra and check in HabitHomeScreen
// 3. Use a callback/event system

// For simplicity, use a companion object or shared preference
companion object {
    var pendingNoteDialogHabitId: Long? = null
    var pendingNoteDialogHabitName: String? = null
}

private fun handleNotificationIntent(intent: Intent) {
    if (intent.action == NotificationConstants.ACTION_ADD_NOTE) {
        val habitId = intent.getLongExtra(NotificationConstants.EXTRA_HABIT_ID, -1)
        val habitName = intent.getStringExtra(NotificationConstants.EXTRA_HABIT_NAME) ?: ""
        
        if (habitId != -1L) {
            pendingNoteDialogHabitId = habitId
            pendingNoteDialogHabitName = habitName
            // HabitHomeScreen will check this and open dialog
        }
    }
}
```

**Validation Checklist**:
- [ ] Intent data is stored for HabitHomeScreen to access
- [ ] HabitHomeScreen checks for pending note dialog on composition
- [ ] Note dialog opens with correct habit information
- [ ] Pending state is cleared after dialog opens
- [ ] File compiles without errors

---

### Phase 5: Integration & Testing

#### Step 5.1: End-to-End Testing

**Objective**: Test complete notification flow from scheduling to actions.

**Test Scenarios**:
1. Create a habit and verify notification is scheduled
2. Wait for notification to appear (or use test time)
3. Tap "Complete" action and verify habit is marked complete
4. Tap "Add Note" action and verify dialog opens
5. Save note and verify it's stored
6. Update habit and verify notifications are rescheduled
7. Delete habit and verify notifications are cancelled

**Validation Checklist**:
- [ ] All test scenarios pass
- [ ] Notifications appear at correct times
- [ ] Notification actions work correctly
- [ ] Notes are saved correctly from notification
- [ ] Habit updates reschedule notifications
- [ ] Habit deletion cancels notifications
- [ ] No crashes or errors occur

---

#### Step 5.2: Edge Case Testing

**Objective**: Test edge cases and error conditions.

**Test Scenarios**:
1. Test with notification permission denied (app should still work)
2. Test device reboot (notifications should reschedule)
3. Test timezone changes
4. Test with habits that have no preferredTime
5. Test with completed habits (should not show notification)
6. Test with habits before startDate (should not schedule)
7. Test month-end dates (31st → 30th/28th)
8. Test leap years
9. Test all recurrence types

**Validation Checklist**:
- [ ] App works without notification permission
- [ ] Device reboot reschedules notifications
- [ ] Timezone changes are handled (if implemented)
- [ ] Null preferredTime is handled correctly
- [ ] Completed habits don't show notifications
- [ ] Past dates and dates before startDate are not scheduled
- [ ] Month-end edge cases are handled
- [ ] All recurrence types schedule correctly

---

#### Step 5.3: UI/UX Polish

**Objective**: Polish notification and dialog UI to match app theme.

**Tasks**:
- Verify notification colors match label colors
- Verify notification text is clear and concise
- Verify note dialog matches app theme
- Verify action buttons are clearly labeled
- Test on different screen sizes
- Test in light/dark mode (if app supports it)

**Validation Checklist**:
- [ ] Notification styling matches app theme
- [ ] Notification text is clear and helpful
- [ ] Note dialog matches app design system
- [ ] Action buttons are intuitive
- [ ] UI works on different screen sizes
- [ ] No visual regressions

---

## Status

- [ ] Planning phase
- [ ] Design finalized
- [ ] Phase 1: Core Infrastructure
  - [ ] Step 1.1: Add Completion Check Methods (DAO)
  - [ ] Step 1.2: Add Completion Check Methods (Repository)
  - [ ] Step 1.3: Add Completion Check Methods (Manager)
  - [ ] Step 1.4: Create Notification Constants
  - [ ] Step 1.5: Set Up Notification Channel
  - [ ] Step 1.6: Create Permission Handler
  - [ ] Step 1.7: Create Notification Manager
  - [ ] Step 1.8: Create Boot Receiver
  - [ ] Step 1.9: Update AndroidManifest
  - [ ] Step 1.10: Add WorkManager Dependency
- [ ] Phase 2: Notification Scheduling
  - [ ] Step 2.1: Create Date Calculation Utility
  - [ ] Step 2.2: Create Notification Scheduler
  - [ ] Step 2.3: Create WorkManager Worker
  - [ ] Step 2.4: Integrate with HabitManager
  - [ ] Step 2.5: Initialize in MainActivity
  - [ ] Step 2.6: Update BootReceiver
- [ ] Phase 3: Notification Actions
  - [ ] Step 3.1: Create Pending Intents
  - [ ] Step 3.2: Create Notification Receiver
  - [ ] Step 3.3: Register Receiver in Manifest
  - [ ] Step 3.4: Handle Add Note Intent
- [ ] Phase 4: Note Dialog
  - [ ] Step 4.1: Create HabitNoteDialog
  - [ ] Step 4.2: Integrate with HabitManager
  - [ ] Step 4.3: Open from Notification
- [ ] Phase 5: Integration & Testing
  - [ ] Step 5.1: End-to-End Testing
  - [ ] Step 5.2: Edge Case Testing
  - [ ] Step 5.3: UI/UX Polish
- [ ] Testing completed
- [ ] Documentation updated

## Status

- [ ] Planning phase
- [ ] Design finalized
- [ ] Phase 1: Core Infrastructure
- [ ] Phase 2: Notification Scheduling
- [ ] Phase 3: Notification Actions
- [ ] Phase 4: Note Dialog
- [ ] Phase 5: Integration & Testing
- [ ] Testing completed
- [ ] Documentation updated

---


*This document will be expanded with detailed implementation steps, validation checklists, code examples, and design specifications as the feature is developed.*

