# Manual Test Script for Recurrence Logic

This script provides step-by-step instructions for manually testing all recurrence logic functionality in the Rewire habit tracking app.

## Prerequisites
- Android device or emulator running the app
- App built and installed successfully
- All recurrence types should be accessible through the Add/Edit Habit screen

## Test Environment Setup
1. Launch the Rewire app
2. Navigate to the main habit list screen
3. Tap the "+" button to add a new habit
4. You should see the "Add/Edit Habit" screen with all recurrence options

---

## Test 1: Daily Recurrence
**Objective**: Verify daily recurrence works correctly

### Steps:
1. **Select Daily Recurrence**
   - Tap the recurrence dropdown
   - Select "Daily"
   - Verify: No additional configuration appears
   - Verify: Validation message shows no errors

2. **Configure Habit**
   - Enter habit name: "Drink Water"
   - Set preferred time: 9:00 AM
   - Set estimated time: 5 minutes

3. **Save Habit**
   - Tap the save button (checkmark)
   - Verify: Habit saves successfully
   - Verify: Returns to habit list
   - Verify: New habit appears with "Daily" recurrence

### Expected Results:
- ✅ Daily recurrence requires no additional configuration
- ✅ Habit saves successfully
- ✅ Habit appears in list with correct recurrence type

---

## Test 2: Weekly Recurrence
**Objective**: Verify weekly recurrence with day selection works correctly

### Steps:
1. **Select Weekly Recurrence**
   - Tap the recurrence dropdown
   - Select "Weekly"
   - Verify: Day selection grid appears (M, T, W, T, F, S, S)
   - Verify: All days are unselected initially

2. **Test Day Selection**
   - Tap Monday (M) - should highlight in primary color
   - Tap Wednesday (W) - should highlight in primary color
   - Tap Friday (F) - should highlight in primary color
   - Verify: Selected days are visually distinct
   - Verify: Validation message shows no errors

3. **Test Deselection**
   - Tap Monday again - should become unselected
   - Verify: Only Wednesday and Friday remain selected

4. **Test No Days Selected**
   - Deselect all remaining days
   - Verify: Validation message appears: "No days selected - will default to Monday"

5. **Save Habit**
   - Enter habit name: "Exercise"
   - Set preferred time: 7:00 PM
   - Set estimated time: 30 minutes
   - Tap save button
   - Verify: Habit saves successfully

### Expected Results:
- ✅ Day selection grid appears and functions correctly
- ✅ Visual feedback for selected/unselected days
- ✅ Validation message appears when no days selected
- ✅ Custom weekly habit saves successfully

---

## Test 3: Monthly by Date Recurrence
**Objective**: Verify monthly recurrence by specific date works correctly

### Steps:
1. **Select Monthly Recurrence**
   - Tap the recurrence dropdown
   - Select "Monthly"
   - Verify: Radio buttons appear for "Day of month" and "Weekday of month"

2. **Select Day of Month**
   - Ensure "Day of month" radio button is selected
   - Verify: Calendar-style day grid appears (1-31)
   - Verify: All days are unselected initially

3. **Test Day Selection**
   - Tap day 15 - should highlight in primary color
   - Verify: Only day 15 is selected
   - Tap day 20 - should select day 20, deselect day 15
   - Verify: Single day selection works correctly

4. **Test Edge Cases**
   - Tap day 31 - should select successfully
   - Tap day 1 - should select successfully
   - Verify: All days 1-31 are selectable

5. **Save Habit**
   - Enter habit name: "Pay Bills"
   - Set preferred time: 10:00 AM
   - Set estimated time: 15 minutes
   - Tap save button
   - Verify: Habit saves successfully

### Expected Results:
- ✅ Radio button selection works correctly
- ✅ Day grid appears with numbers 1-31
- ✅ Single day selection works
- ✅ Monthly by date habit saves successfully

---

## Test 4: Monthly by Weekday Recurrence
**Objective**: Verify monthly recurrence by weekday works correctly

### Steps:
1. **Select Monthly Recurrence**
   - Tap the recurrence dropdown
   - Select "Monthly"

2. **Select Weekday of Month**
   - Tap "Weekday of month" radio button
   - Verify: Week selection appears (1st, 2nd, 3rd, 4th)
   - Verify: Day selection appears (Mon, Tue, Wed, Thu, Fri, Sat, Sun)

3. **Test Week Selection**
   - Tap "3rd" - should highlight in primary color
   - Verify: Only "3rd" is selected

4. **Test Day Selection**
   - Tap "Fri" - should highlight in primary color
   - Verify: Only "Fri" is selected
   - Verify: Natural language display shows "Every 3rd friday"

5. **Test Different Combinations**
   - Select "1st" and "Mon"
   - Verify: Display shows "Every 1st monday"
   - Select "4th" and "Sun"
   - Verify: Display shows "Every 4th sunday"

6. **Save Habit**
   - Enter habit name: "Team Meeting"
   - Set preferred time: 2:00 PM
   - Set estimated time: 60 minutes
   - Tap save button
   - Verify: Habit saves successfully

### Expected Results:
- ✅ Week and day selection work independently
- ✅ Natural language display updates correctly
- ✅ Monthly by weekday habit saves successfully

---

## Test 5: Quarterly by Date Recurrence
**Objective**: Verify quarterly recurrence by specific date works correctly

### Steps:
1. **Select Quarterly Recurrence**
   - Tap the recurrence dropdown
   - Select "Quarterly"
   - Verify: Radio buttons appear for "Day of month" and "Weekday of month"

2. **Select Day of Month**
   - Ensure "Day of month" radio button is selected
   - Verify: Quarter selection appears (Q1, Q2, Q3)
   - Verify: Day grid appears (1-31)

3. **Test Quarter Selection**
   - Tap "Q1" - should highlight in primary color
   - Verify: Quarter description shows "Quarter: January, April, July, October"
   - Tap "Q2" - should select Q2
   - Verify: Description updates to "February, May, August, November"

4. **Test Day Selection**
   - Select day 10
   - Verify: Day 10 is highlighted

5. **Test Different Combinations**
   - Select "Q3" and day 25
   - Verify: Quarter description shows "March, June, September, December"
   - Verify: Day 25 is selected

6. **Save Habit**
   - Enter habit name: "Quarterly Review"
   - Set preferred time: 9:00 AM
   - Set estimated time: 120 minutes
   - Tap save button
   - Verify: Habit saves successfully

### Expected Results:
- ✅ Quarter selection works with correct month groupings
- ✅ Quarter descriptions are accurate
- ✅ Day selection works within quarterly context
- ✅ Quarterly by date habit saves successfully

---

## Test 6: Quarterly by Weekday Recurrence
**Objective**: Verify quarterly recurrence by weekday works correctly

### Steps:
1. **Select Quarterly Recurrence**
   - Tap the recurrence dropdown
   - Select "Quarterly"

2. **Select Weekday of Month**
   - Tap "Weekday of month" radio button
   - Verify: Quarter selection, week selection, and day selection all appear

3. **Test Full Configuration**
   - Select "Q2" (February, May, August, November)
   - Select "2nd" week
   - Select "Tue" day
   - Verify: All selections are highlighted
   - Verify: Natural language display shows "Every 2nd tuesday"

4. **Test Different Combinations**
   - Select "Q1", "4th", "Sun"
   - Verify: Display shows "Every 4th sunday"
   - Verify: Quarter description shows "January, April, July, October"

5. **Save Habit**
   - Enter habit name: "Board Meeting"
   - Set preferred time: 3:00 PM
   - Set estimated time: 90 minutes
   - Tap save button
   - Verify: Habit saves successfully

### Expected Results:
- ✅ All three selection components work together
- ✅ Natural language display is accurate
- ✅ Quarter descriptions are correct
- ✅ Quarterly by weekday habit saves successfully

---

## Test 7: Validation System
**Objective**: Verify validation system prevents invalid configurations

### Steps:
1. **Test Invalid Day Selection**
   - Select Monthly → Day of month
   - Try to select day 32 (if possible)
   - Verify: Validation message appears
   - Verify: Save button is disabled or shows error

2. **Test Invalid Week Selection**
   - Select Monthly → Weekday of month
   - Try to select week 5 (if possible)
   - Verify: Validation message appears

3. **Test Invalid Month Offset**
   - Select Quarterly → Day of month
   - Try to select invalid quarter (if possible)
   - Verify: Validation message appears

4. **Test Save Prevention**
   - Configure invalid parameters
   - Tap save button
   - Verify: Save is prevented
   - Verify: User sees validation message

### Expected Results:
- ✅ Validation messages appear for invalid configurations
- ✅ Save is prevented for invalid parameters
- ✅ User guidance is clear and helpful

---

## Test 8: Time Picker Integration
**Objective**: Verify Material 3 time picker works correctly

### Steps:
1. **Open Time Picker**
   - Tap the preferred time field
   - Verify: Material 3 time picker dialog appears
   - Verify: Current time is displayed

2. **Change Time**
   - Scroll to select different hour
   - Scroll to select different minute
   - Verify: Time updates in real-time
   - Tap "OK"
   - Verify: Selected time appears in field

3. **Test Different Times**
   - Try morning time (6:30 AM)
   - Try afternoon time (2:15 PM)
   - Try evening time (9:45 PM)
   - Verify: All times save correctly

### Expected Results:
- ✅ Time picker dialog appears correctly
- ✅ Time selection works smoothly
- ✅ Selected time displays correctly
- ✅ Different times save successfully

---

## Test 9: Navigation and State Management
**Objective**: Verify navigation and state management work correctly

### Steps:
1. **Test Back Navigation**
   - Configure a habit with complex recurrence
   - Tap back button
   - Verify: Returns to habit list without saving
   - Verify: No habit is created

2. **Test Edit Mode**
   - Create a habit with daily recurrence
   - Tap edit button on habit card
   - Verify: Edit screen opens with existing data
   - Verify: All fields are populated correctly

3. **Test State Persistence**
   - Start configuring a habit
   - Switch between different recurrence types
   - Verify: Each type maintains its configuration
   - Verify: No data is lost during switching

### Expected Results:
- ✅ Back navigation works correctly
- ✅ Edit mode populates existing data
- ✅ State persists during configuration changes

---

## Test 10: Delete Functionality
**Objective**: Verify delete functionality works correctly

### Steps:
1. **Create Test Habit**
   - Create a habit with any recurrence type
   - Verify: Habit appears in list

2. **Edit and Delete**
   - Tap edit button on the habit
   - Tap delete button (trash icon)
   - Verify: Confirmation dialog appears
   - Tap "Confirm" in dialog
   - Verify: Habit is deleted
   - Verify: Returns to habit list

3. **Test Cancel Delete**
   - Create another test habit
   - Edit → Delete → Cancel
   - Verify: Habit is not deleted
   - Verify: Returns to edit screen

### Expected Results:
- ✅ Delete button appears in edit mode
- ✅ Confirmation dialog prevents accidental deletion
- ✅ Delete and cancel both work correctly

---

## Test Summary Checklist

After completing all tests, verify:

- [ ] Daily recurrence works without additional configuration
- [ ] Weekly recurrence with day selection works correctly
- [ ] Monthly by date recurrence works with day grid
- [ ] Monthly by weekday recurrence works with week/day selection
- [ ] Quarterly by date recurrence works with quarter/day selection
- [ ] Quarterly by weekday recurrence works with all three selections
- [ ] Validation system prevents invalid configurations
- [ ] Time picker integrates correctly
- [ ] Navigation and state management work properly
- [ ] Delete functionality works with confirmation
- [ ] All habits save and display correctly in the list
- [ ] No crashes or unexpected behavior occurs

## Troubleshooting

### Common Issues:
1. **UI not responding**: Check if Material 3 dependencies are properly included
2. **Validation not working**: Verify validation functions are properly imported
3. **Save not working**: Check if HabitManager is properly initialized
4. **Time picker not appearing**: Verify Material 3 time picker dependencies

### Performance Notes:
- Build time should be 20-30 seconds
- UI should be responsive during configuration
- No lag when switching between recurrence types
- Smooth scrolling in day selection grids

## Success Criteria

The recurrence logic implementation is successful if:
- ✅ All 7 recurrence types function correctly
- ✅ Validation system prevents invalid configurations
- ✅ UI is intuitive and responsive
- ✅ All habits save and load correctly
- ✅ No crashes or data corruption occur
- ✅ User experience is smooth and professional

This comprehensive test suite ensures the recurrence logic implementation meets production-quality standards.
