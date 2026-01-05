# Navigation Test Guide

## 🧪 Testing the Current Navigation Implementation

### Prerequisites
- Android device or emulator connected
- Debug APK built successfully
- App installed and ready to run

### Test Scenarios

#### 1. **Empty State Navigation Test**
**Goal**: Test adding a habit when no habits exist

**Steps**:
1. Launch the app (should show "No habits scheduled for today")
2. Tap the **Add Button** (+ icon)
3. **Expected**: `AddEditHabitScreen` opens
4. Fill in habit details:
   - Name: "Test Habit"
   - Recurrence: Daily
   - Time: 9:00 AM
   - Duration: 15 minutes
5. Tap **Save**
6. **Expected**: Returns to home screen with the new habit visible

**Success Criteria**: ✅
- Add button opens add screen
- Form accepts input
- Save returns to home
- New habit appears in list

---

#### 2. **Habit Card Navigation Test**
**Goal**: Test viewing and editing existing habits

**Steps**:
1. Ensure you have at least one habit in the list
2. **View Habit Details**:
   - Tap anywhere on a habit card (not the edit button)
   - **Expected**: `HabitDetailModal` opens showing habit details
3. **Edit Habit**:
   - In the detail modal, tap the **Edit** button (pencil icon)
   - **Expected**: `AddEditHabitScreen` opens with pre-filled data
   - Modify the habit name to "Updated Test Habit"
   - Tap **Save**
   - **Expected**: Returns to home with updated habit name

**Success Criteria**: ✅
- Card tap opens detail modal
- Edit button opens edit screen
- Pre-filled data is correct
- Changes save and display correctly

---

#### 3. **Add Button from List Test**
**Goal**: Test adding a habit when habits already exist

**Steps**:
1. Scroll to bottom of habit list
2. Tap the **Add Button** at the bottom
3. **Expected**: `AddEditHabitScreen` opens
4. Create a new habit with different settings:
   - Name: "Weekly Habit"
   - Recurrence: Weekly
   - Time: 2:00 PM
   - Duration: 30 minutes
5. Tap **Save**
6. **Expected**: Returns to home with new habit added

**Success Criteria**: ✅
- Add button works from list view
- New habit appears in correct position
- Multiple habits display correctly

---

#### 4. **Back Navigation Test**
**Goal**: Test returning to home screen from all screens

**Steps**:
1. From any screen (Add/Edit or Detail), tap the **Back** button
2. **Expected**: Returns to home screen
3. Test from both Add and Edit modes
4. Test from Detail modal

**Success Criteria**: ✅
- Back button works from all screens
- No data loss when navigating back
- Home screen state is preserved

---

#### 5. **Delete Habit Test**
**Goal**: Test deleting habits from edit screen

**Steps**:
1. Open a habit for editing
2. Scroll to bottom of edit screen
3. Tap the **Delete** button (red trash icon)
4. **Expected**: Confirmation dialog appears
5. Tap **Confirm** in dialog
6. **Expected**: Habit is deleted and returns to home
7. **Expected**: Deleted habit no longer appears in list

**Success Criteria**: ✅
- Delete button is visible
- Confirmation dialog appears
- Habit is actually deleted
- UI updates correctly

---

#### 6. **Habit Completion Test**
**Goal**: Test marking habits as complete

**Steps**:
1. Find a habit in the list
2. Tap the **checkbox** on the habit card
3. **Expected**: Checkbox becomes checked
4. Tap again
5. **Expected**: Checkbox becomes unchecked
6. Test in both home view and detail modal

**Success Criteria**: ✅
- Checkbox toggles correctly
- State persists between views
- Visual feedback is clear

---

#### 7. **Note Functionality Test**
**Goal**: Test adding notes to habits

**Steps**:
1. Tap a habit card to open detail modal
2. Find the note section
3. Add text: "This is a test note"
4. **Expected**: Note is saved
5. Close and reopen the habit
6. **Expected**: Note is still there

**Success Criteria**: ✅
- Notes can be added
- Notes persist between sessions
- Notes display correctly

---

### 🐛 Common Issues to Watch For

#### Navigation Issues
- **Stuck on screen**: App doesn't return to home
- **Data loss**: Form data disappears when navigating
- **Wrong screen**: Tapping buttons goes to wrong screen
- **Modal not closing**: Detail modal doesn't close properly

#### UI Issues
- **Buttons not responding**: Tap targets don't work
- **Layout problems**: Screens don't fit properly
- **Missing elements**: Buttons or fields not visible
- **Styling issues**: Colors, fonts, or spacing problems

#### Data Issues
- **Habits not saving**: New habits don't appear
- **Habits not updating**: Changes don't persist
- **Habits not deleting**: Delete doesn't work
- **Wrong data**: Pre-filled forms show incorrect data

### 📱 Device-Specific Testing

#### Test on Different Screen Sizes
- **Phone**: Standard phone screen
- **Tablet**: Larger screen (if available)
- **Different orientations**: Portrait and landscape

#### Test Performance
- **Navigation speed**: Screens should open quickly
- **Memory usage**: No memory leaks during navigation
- **Battery usage**: Navigation shouldn't drain battery

### 🔧 Troubleshooting

#### If Navigation Fails
1. **Check logs**: Look for error messages in Android Studio
2. **Restart app**: Close and reopen the app
3. **Clear data**: Clear app data and try again
4. **Rebuild**: Clean and rebuild the project

#### If UI Issues Occur
1. **Check screen size**: Ensure UI fits on device
2. **Check permissions**: Ensure app has necessary permissions
3. **Check theme**: Ensure Material theme is applied correctly

### 📊 Test Results Template

```
Navigation Test Results - [Date]

Test 1 - Empty State Navigation: [PASS/FAIL]
Test 2 - Habit Card Navigation: [PASS/FAIL]
Test 3 - Add Button from List: [PASS/FAIL]
Test 4 - Back Navigation: [PASS/FAIL]
Test 5 - Delete Habit: [PASS/FAIL]
Test 6 - Habit Completion: [PASS/FAIL]
Test 7 - Note Functionality: [PASS/FAIL]

Overall Navigation Status: [WORKING/NEEDS FIXES]

Issues Found:
- [List any issues discovered]

Recommendations:
- [Suggestions for improvements]
```

### 🚀 Next Steps After Testing

1. **If all tests pass**: Navigation is ready for production
2. **If issues found**: Document and fix the problems
3. **If performance issues**: Optimize navigation code
4. **If UI issues**: Refine the user experience

---

**Happy Testing! 🎉**

Remember to test thoroughly and document any issues you find. The navigation system should feel smooth and intuitive to use.
