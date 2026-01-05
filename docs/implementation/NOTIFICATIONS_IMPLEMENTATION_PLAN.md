# Notifications Implementation Plan

## Overview

Implement a notification system to remind users of their habits at their estimated/preferred time. Notifications will provide quick actions to mark habits as complete or add a note. When the "add note" action is selected, a small dialog will open allowing the user to enter a note for the habit.

## Current State

- Habits have a `preferredTime: LocalTime` field indicating when the user wants to be reminded
- Habits have an `estimatedMinutes: Int` field for duration
- Habits have a `recurrence: RecurrenceType` field for scheduling frequency
- Completion tracking exists via `HabitCompletion` entity
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
- Schedule notifications based on habit `preferredTime` and `recurrence`
- Calculate notification times based on:
  - Daily habits: Every day at `preferredTime`
  - Weekly habits: On specified days at `preferredTime`
  - Monthly habits: On specified dates/days at `preferredTime`
  - Custom weekly habits: On custom days at `preferredTime`
- Recalculate schedules when:
  - Habits are created, updated, or deleted
  - App starts/restarts
  - Device reboots

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
   - Request notification permission on Android 13+ (API 33+)
   - Use `NotificationManagerCompat` for compatibility
   - Handle permission denial gracefully

2. **WorkManager vs AlarmManager**:
   - **WorkManager** (Recommended):
     - Better battery optimization
     - Handles doze mode and app standby
     - More reliable for recurring tasks
     - Better integration with Android system
   - **AlarmManager**:
     - More precise timing
     - More complex implementation
     - Less battery friendly

3. **Notification IDs**:
   - Use unique IDs per habit per day
   - Format: `habitId.hashCode() + date.hashCode()`
   - Or use a mapping system to track notification IDs

4. **State Management**:
   - Track scheduled notifications in a repository or database
   - Store notification IDs for cancellation
   - Handle app updates and notification rescheduling

5. **Testing Considerations**:
   - Test notification scheduling with various recurrence types
   - Test notification actions (complete, add note)
   - Test behavior when app is in foreground/background/killed
   - Test on different Android versions (especially permissions)
   - Test timezone changes
   - Test device reboots and app updates

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

3. **Notification Manager/Helper**:
   - `app/src/main/kotlin/manager/NotificationManager.kt` - Helper class for notification creation and scheduling

4. **Note Dialog Component**:
   - `app/src/main/kotlin/ui/components/HabitNoteDialog.kt` - Dialog component for adding/editing notes

5. **Intent Extras/Constants**:
   - `app/src/main/kotlin/util/NotificationConstants.kt` - Constants for notification actions, intents, and extras

6. **Repository/Database** (if needed):
   - Consider adding a table to track scheduled notifications
   - Or use WorkManager's internal storage

## Files to Update

1. **AndroidManifest.xml**:
   - Add notification permission (`POST_NOTIFICATIONS`)
   - Register `HabitNotificationReceiver`
   - Add intent filters for notification actions
   - Register WorkManager constraints

2. **HabitManager.kt**:
   - Add method to schedule/cancel notifications when habits are created/updated/deleted
   - Integrate notification scheduling with habit lifecycle

3. **HabitHomeScreen.kt** (or relevant screen):
   - Integrate `HabitNoteDialog` for adding notes
   - Handle deep links from notifications
   - Update UI after notification actions

4. **MainActivity.kt**:
   - Handle notification action intents
   - Request notification permissions
   - Initialize notification scheduling on app start

5. **Gradle Dependencies** (app/build.gradle):
   - Add WorkManager dependency: `androidx.work:work-runtime-ktx`
   - Ensure notification-compat library is included

## Implementation Phases

### Phase 1: Core Infrastructure
- [ ] Set up notification channel
- [ ] Create notification permission handling
- [ ] Create basic notification manager/helper class
- [ ] Create notification constants file

### Phase 2: Notification Scheduling
- [ ] Implement WorkManager worker for notifications
- [ ] Create scheduling logic based on habit recurrence
- [ ] Integrate scheduling with HabitManager
- [ ] Test scheduling with different recurrence types

### Phase 3: Notification Actions
- [ ] Create notification receiver for actions
- [ ] Implement "Mark as Complete" action
- [ ] Implement "Add Note" action intent handling
- [ ] Test actions in various app states

### Phase 4: Note Dialog
- [ ] Create HabitNoteDialog component
- [ ] Integrate with HabitManager note methods
- [ ] Handle opening dialog from notification
- [ ] Style dialog to match app theme

### Phase 5: Integration & Testing
- [ ] Integrate all components
- [ ] Test end-to-end notification flow
- [ ] Test edge cases (permissions, timezone changes, device reboot)
- [ ] Polish UI/UX

### Phase 6: Future Enhancements
- [ ] Notification settings screen
- [ ] Smart notifications
- [ ] Rich notifications
- [ ] Notification history/analytics

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

