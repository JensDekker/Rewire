# Utilities Menu Implementation Plan

## Overview

Replace the current settings gear icon in the HabitHomeScreen header with a three vertical dots menu icon. The menu will appear in a styled container with a semi-circular top and bottom. The utilities menu will provide quick access to search, filter, and settings functionality.

## Current State

- HabitHomeScreen currently uses a settings gear icon (`Icons.Default.Settings`) in the top right corner
- Clicking the settings icon opens a dropdown menu with "Manage Labels" option
- Filter functionality for labels already exists in HabitHomeScreen but is always visible

## Proposed Changes

### Visual Design

- **Menu Icon**: Change from settings gear to three vertical dots (`Icons.Default.MoreVert`)
- **Menu Container**: Rectangle with semi-circular top and bottom edges (rounded corners)
- **Menu Items**: Three items with icons:
  1. **Search Icon** - Search for habits by name
  2. **Filter Icon** - Filter habits by labels
  3. **Settings Gear Icon** - Manage labels (navigate to Label Management Screen)

### Functional Changes

#### 1. Search Functionality
- **Icon**: `Icons.Default.Search`
- **Behavior**: Opens a search modal/dialog when clicked
- **Functionality**: Search habits by name (case-insensitive, partial match)
- **Implementation Details**:
  - **New Component**: `HabitSearchModal.kt` in `ui/components/`
  - **Search Input**: TextField with search icon, clear button, and real-time search
  - **Search Logic**: Use `HabitRepository.searchHabits(query)` (already exists)
  - **HabitManager Integration**: Add `suspend fun searchHabits(query: String): List<HabitEntity>` method to expose repository search
  - **Search Results Display**: List of habit cards matching search query
  - **Empty State**: Show "No habits found" message when search returns empty
  - **Modal Behavior**: Full-screen modal with dark overlay, dismissible by back button or outside click
  - **Search Trigger**: Real-time search as user types (debounced by 300ms for performance)
- **Integration**: 
  - Search results displayed in modal
  - Clicking a habit in search results opens `HabitDetailModal` or navigates to edit screen
  - Search modal state managed in `HabitHomeScreen`

#### 2. Filter Functionality
- **Icon**: `Icons.Default.FilterList`
- **Behavior**: Toggles visibility of existing label filter UI section
- **Functionality**: Filter habits by selected labels (existing functionality - OR logic)
- **Implementation Details**:
  - **State Management**: Add `var showFilterUI by remember { mutableStateOf(false) }`
  - **Toggle Logic**: When filter icon clicked, toggle `showFilterUI` state
  - **Filter UI Location**: Existing filter UI (lines 221-298 in HabitHomeScreen.kt) remains in LazyColumn
  - **Conditional Rendering**: Wrap existing filter UI section with `if (showFilterUI && allAvailableLabels.isNotEmpty())`
  - **Filter State Persistence**: `selectedFilterLabelIds` state persists regardless of filter UI visibility
  - **Visual Indicator**: When filter is active (`selectedFilterLabelIds.isNotEmpty()`), show filter icon with badge or different color
  - **Menu Behavior**: Menu closes after toggling filter (standard DropdownMenu behavior)
- **Integration**: 
  - Current filter UI becomes hidden by default (`showFilterUI = false` initially)
  - Filter functionality remains unchanged (OR logic, clear button, etc.)
  - Filter state persists when UI is hidden (habits remain filtered)

#### 3. Settings/Manage Labels
- **Icon**: Settings gear icon (`Icons.Default.Settings`)
- **Behavior**: Navigate to Label Management Screen
- **Functionality**: Same as current "Manage Labels" menu item
- **Implementation**: Navigation to `label_management` route

## Detailed Design Specifications

### Menu Container Styling
- **Shape**: Use `AppShapes.largeCardShape` (24dp corner radius) for semi-circular top and bottom
- **Background**: `AppColors.surface` with elevation/shadow
- **Width**: Auto-width based on content, minimum 200dp
- **Padding**: `AppSpacing.modalPadding` (16dp) for menu content
- **Positioning**: Anchored to the MoreVert icon button, positioned below and aligned to the right edge
- **Elevation**: 8dp shadow for depth

### Menu Items Styling
- **Item Height**: 48dp (Material Design standard)
- **Item Padding**: 16dp horizontal, 12dp vertical
- **Icon Size**: 24dp
- **Icon Color**: `AppColors.textPrimary`
- **Text Style**: `AppTypography.materialTypography.subtitle1`
- **Text Color**: `AppColors.textPrimary`
- **Spacing Between Items**: 4dp divider or spacing
- **Hover/Pressed State**: `AppColors.surfaceVariant` background

### Icons Specification
- **Menu Icon**: `Icons.Default.MoreVert` (24dp)
- **Search Icon**: `Icons.Default.Search` (24dp)
- **Filter Icon**: `Icons.Default.FilterList` (24dp)
- **Settings Icon**: `Icons.Default.Settings` (24dp)

### State Management
- **Menu Visibility**: `var showMenu by remember { mutableStateOf(false) }`
- **Filter Visibility**: `var showFilterUI by remember { mutableStateOf(false) }` (new state)
- **Search Modal State**: `var showSearchModal by remember { mutableStateOf(false) }` (new state)
- **Search Query**: `var searchQuery by remember { mutableStateOf("") }` (new state)
- **Search Results**: `var searchResults by remember { mutableStateOf<List<HabitEntity>>(emptyList()) }` (new state)

## Implementation Notes

- Menu should use Material Design `DropdownMenu` component
- Menu styling should match app theme (AppColors, AppShapes, AppTypography, AppSpacing)
- Menu items should have clear icons and text labels
- Menu should be dismissible by clicking outside or pressing back
- Consider accessibility (content descriptions, keyboard navigation)
- Filter UI state should persist when menu is closed (filter remains active if shown)
- Search modal should be a full-screen or large modal dialog

## Future Enhancements

- Additional menu items (e.g., Statistics, Settings screen)
- Keyboard shortcuts for menu items
- Custom icons or styling
- Menu animations/transitions

## Files to Update

### Core Implementation Files

1. **`app/src/main/kotlin/ui/screens/HabitHomeScreen.kt`**
   - Replace `Icons.Default.Settings` with `Icons.Default.MoreVert` (line 190)
   - Update `DropdownMenu` to include three menu items with icons
   - Add state management for filter visibility (`showFilterUI`)
   - Add state management for search modal (`showSearchModal`, `searchQuery`, `searchResults`)
   - Wrap existing filter UI section with conditional rendering based on `showFilterUI`
   - Add `HabitSearchModal` composable call when `showSearchModal` is true
   - Update menu items to trigger appropriate actions

2. **`app/src/main/kotlin/manager/HabitManager.kt`** (NEW)
   - Add method: `suspend fun searchHabits(query: String): List<HabitEntity>`
   - Implementation: `return habitRepository.searchHabits(query)`
   - This exposes the existing `HabitRepository.searchHabits()` method

3. **`app/src/main/kotlin/ui/components/HabitSearchModal.kt`** (NEW)
   - Create new composable for search modal
   - Include search TextField with real-time search
   - Display search results as list of habit cards
   - Handle empty state and loading state
   - Integrate with HabitManager for search functionality
   - Use theme system (AppColors, AppShapes, AppTypography, AppSpacing)

### Optional Refactoring

4. **`app/src/main/kotlin/ui/components/FilterSection.kt`** (OPTIONAL)
   - Consider extracting filter UI into separate composable for better code organization
   - Would make HabitHomeScreen cleaner and filter UI reusable
   - Not required for initial implementation

## Implementation Steps

### Phase 1: Menu Icon and Structure

#### Step 1: Replace settings icon with `Icons.Default.MoreVert` in HabitHomeScreen
- Change icon from `Icons.Default.Settings` to `Icons.Default.MoreVert` (line 190)
- Update content description to "Utilities menu" or "More options"

**Testing Checklist:**
- [ ] Icon displays correctly (three vertical dots)
- [ ] Icon is properly sized (24dp)
- [ ] Content description is set for accessibility
- [ ] Icon button is clickable and responsive

#### Step 2: Update DropdownMenu to include three menu items
- Add three `DropdownMenuItem` entries:
  - Search (with `Icons.Default.Search` and "Search Habits" text)
  - Filter (with `Icons.Default.FilterList` and "Filter by Labels" text)
  - Settings (with `Icons.Default.Settings` and "Manage Labels" text)
- Each menu item should have an icon and text label

**Testing Checklist:**
- [ ] All three menu items are visible when menu opens
- [ ] Each menu item displays correct icon
- [ ] Each menu item displays correct text label
- [ ] Icons and text are properly aligned
- [ ] Menu items are properly spaced

#### Step 3: Apply theme styling to menu
- Use `AppColors` for colors (surface, textPrimary, etc.)
- Use `AppShapes.largeCardShape` for menu container shape
- Use `AppTypography.materialTypography.subtitle1` for menu item text
- Apply proper padding using `AppSpacing.modalPadding`

**Testing Checklist:**
- [ ] Menu background color matches app theme
- [ ] Menu has rounded corners (semi-circular top and bottom)
- [ ] Menu text uses correct typography
- [ ] Menu has proper padding and spacing
- [ ] Menu styling is consistent with rest of app

#### Step 4: Test menu opens/closes correctly
- Verify menu opens when icon is clicked
- Verify menu closes when item is selected
- Verify menu closes when clicking outside
- Verify menu closes when back button is pressed

**Testing Checklist:**
- [ ] Menu opens when MoreVert icon is clicked
- [ ] Menu closes when any menu item is selected
- [ ] Menu closes when clicking outside the menu
- [ ] Menu closes when back button is pressed
- [ ] Menu positioning is correct (anchored to icon, right-aligned)

### Phase 2: Filter Toggle Functionality

#### Step 1: Add `showFilterUI` state variable
- Add `var showFilterUI by remember { mutableStateOf(false) }` to HabitHomeScreen
- Initialize to `false` so filter UI is hidden by default

**Testing Checklist:**
- [ ] State variable is properly declared
- [ ] State initializes to `false` (filter UI hidden by default)
- [ ] State can be toggled programmatically

#### Step 2: Wrap existing filter UI section with conditional rendering
- Wrap the filter UI section (lines 221-298) with `if (showFilterUI && allAvailableLabels.isNotEmpty())`
- Ensure filter UI only shows when `showFilterUI` is `true`

**Testing Checklist:**
- [ ] Filter UI section is wrapped in conditional
- [ ] Filter UI is hidden when `showFilterUI` is `false`
- [ ] Filter UI shows when `showFilterUI` is `true`
- [ ] Filter UI only shows when labels are available
- [ ] No layout issues when filter UI is hidden/shown

#### Step 3: Update filter menu item to toggle `showFilterUI` state
- In the filter menu item's `onClick`, toggle `showFilterUI` state
- Menu should close after toggling (default DropdownMenu behavior)

**Testing Checklist:**
- [ ] Clicking filter menu item toggles filter UI visibility
- [ ] Menu closes after filter menu item is clicked
- [ ] Filter UI appears/disappears correctly when toggled
- [ ] Toggle works multiple times (show/hide/show)

#### Step 4: Verify filter state persists when UI is hidden
- Test that `selectedFilterLabelIds` state persists when filter UI is hidden
- Verify that habits remain filtered even when filter UI is not visible

**Testing Checklist:**
- [ ] Filter state (`selectedFilterLabelIds`) persists when UI is hidden
- [ ] Habits remain filtered when filter UI is hidden
- [ ] Filter state is maintained when toggling filter UI visibility
- [ ] Clear filter button still works when filter UI is visible

#### Step 5: Test filter functionality works correctly
- Verify OR logic (habit matches if it has ANY selected label)
- Test with multiple labels selected
- Test clear filter functionality

**Testing Checklist:**
- [ ] Filter uses OR logic correctly (habit matches if it has ANY selected label)
- [ ] Filter works with single label selected
- [ ] Filter works with multiple labels selected
- [ ] Clear filter button clears all selected labels
- [ ] Filtered habits display correctly

#### Step 6: Add visual indicator when filter is active (optional enhancement)
- When `selectedFilterLabelIds.isNotEmpty()`, show visual indicator on filter icon
- Could be a badge, different color, or checkmark

**Testing Checklist:**
- [ ] Visual indicator appears when filter is active
- [ ] Visual indicator disappears when filter is cleared
- [ ] Indicator is clearly visible but not intrusive

### Phase 3: Search Functionality

#### Step 1: Add `searchHabits()` method to HabitManager
- Add `suspend fun searchHabits(query: String): List<HabitEntity>`
- Implementation: `return habitRepository.searchHabits(query)`
- This exposes the existing `HabitRepository.searchHabits()` method

**Testing Checklist:**
- [ ] Method is added to HabitManager
- [ ] Method signature is correct (suspend function)
- [ ] Method calls `habitRepository.searchHabits(query)`
- [ ] Method returns `List<HabitEntity>`

#### Step 2: Create `HabitSearchModal` component
- Create new file `app/src/main/kotlin/ui/components/HabitSearchModal.kt`
- Include:
  - Search TextField with search icon, clear button, and real-time search
  - Results list displaying habit cards
  - Empty state handling ("No habits found")
  - Modal overlay and dismiss behavior
- Use theme system (AppColors, AppShapes, AppTypography, AppSpacing)
- Implement debounced search (300ms delay)

**Testing Checklist:**
- [ ] Component file is created in correct location
- [ ] Search TextField accepts input
- [ ] Search icon is visible in TextField
- [ ] Clear button appears when text is entered
- [ ] Clear button clears search query
- [ ] Empty state displays when no results found
- [ ] Modal has dark overlay background
- [ ] Modal is dismissible (back button, outside click)
- [ ] Component uses app theme system
- [ ] Search is debounced (300ms delay)

#### Step 3: Add search state management to HabitHomeScreen
- Add state variables:
  - `var showSearchModal by remember { mutableStateOf(false) }`
  - `var searchQuery by remember { mutableStateOf("") }`
  - `var searchResults by remember { mutableStateOf<List<HabitEntity>>(emptyList()) }`
- Add LaunchedEffect to perform search when query changes

**Testing Checklist:**
- [ ] All three state variables are declared
- [ ] State initializes correctly (modal closed, empty query, empty results)
- [ ] Search executes when query changes
- [ ] Search is debounced properly
- [ ] Search results update correctly

#### Step 4: Integrate search modal with menu item click
- In search menu item's `onClick`, set `showSearchModal = true`
- Add `HabitSearchModal` composable call when `showSearchModal` is `true`
- Pass necessary parameters (habitManager, searchQuery, searchResults, etc.)

**Testing Checklist:**
- [ ] Search modal opens when search menu item is clicked
- [ ] Menu closes after search menu item is clicked
- [ ] Search modal displays correctly
- [ ] Search modal receives correct parameters

#### Step 5: Test search functionality with various queries
- Test with exact matches
- Test with partial matches
- Test with case variations
- Test with no matches
- Test with empty query

**Testing Checklist:**
- [ ] Search finds exact matches
- [ ] Search finds partial matches
- [ ] Search is case-insensitive
- [ ] Empty state shows when no results found
- [ ] Empty query shows all habits or empty list (decide on behavior)
- [ ] Search results update in real-time as user types

#### Step 6: Test search modal interactions
- Test clicking habit in results opens detail modal or edit screen
- Test modal dismiss behavior
- Test search query persistence

**Testing Checklist:**
- [ ] Clicking habit in search results opens HabitDetailModal
- [ ] Clicking habit in search results allows editing
- [ ] Modal dismisses when back button is pressed
- [ ] Modal dismisses when clicking outside
- [ ] Search query clears when modal is dismissed (or persists - decide on behavior)

### Phase 4: Integration and Polish

#### Step 1: Ensure all menu items work correctly
- Test all three menu items independently
- Verify no conflicts between menu items

**Testing Checklist:**
- [ ] Search menu item opens search modal
- [ ] Filter menu item toggles filter UI
- [ ] Settings menu item navigates to label management
- [ ] All menu items work independently
- [ ] No conflicts between menu item actions

#### Step 2: Test state management (menu, filter, search)
- Verify state doesn't conflict between features
- Test state persistence across recompositions

**Testing Checklist:**
- [ ] Menu state doesn't interfere with filter state
- [ ] Menu state doesn't interfere with search state
- [ ] Filter state doesn't interfere with search state
- [ ] All states persist correctly across recompositions
- [ ] State resets appropriately when needed

#### Step 3: Verify theme consistency across all components
- Check all components use AppColors, AppShapes, AppTypography, AppSpacing
- Ensure visual consistency

**Testing Checklist:**
- [ ] Menu uses app theme system
- [ ] Filter UI uses app theme system
- [ ] Search modal uses app theme system
- [ ] All components have consistent styling
- [ ] Colors match app theme
- [ ] Typography matches app theme
- [ ] Spacing matches app theme

#### Step 4: Test accessibility (content descriptions, keyboard navigation)
- Add content descriptions to all interactive elements
- Test keyboard navigation if applicable

**Testing Checklist:**
- [ ] All icons have content descriptions
- [ ] Menu items have content descriptions
- [ ] Search TextField has proper label
- [ ] Filter UI has proper labels
- [ ] Screen reader can navigate all elements
- [ ] Keyboard navigation works (if applicable)

#### Step 5: Test edge cases
- Test with no habits
- Test with empty search
- Test with very long search queries
- Test with special characters in search

**Testing Checklist:**
- [ ] App handles no habits gracefully
- [ ] Empty search shows appropriate message
- [ ] Long search queries work correctly
- [ ] Special characters in search don't cause errors
- [ ] Filter works with no labels available
- [ ] All edge cases handled without crashes

## Technical Details

### Search Implementation
- **Repository Method**: `HabitRepository.searchHabits(query: String)` already exists (line 62-64)
- **Search Algorithm**: Case-insensitive partial match using `contains(query, ignoreCase = true)`
- **Performance**: Consider debouncing search input (300ms delay) to avoid excessive queries
- **Scope**: Search all habits, not just habits due today

### Filter Implementation
- **Current Filter Logic**: OR logic - habit matches if it has ANY selected label (lines 137-147)
- **State Location**: `selectedFilterLabelIds` already exists in HabitHomeScreen (line 66)
- **UI Location**: Filter UI is in LazyColumn items section (lines 221-298)
- **Toggle Behavior**: Simply show/hide the existing filter UI section

### Menu Implementation
- **Component**: Use `DropdownMenu` from Material Design (already in use, line 196)
- **Positioning**: Anchored to IconButton, positioned below
- **Styling**: Custom styling using AppShapes.largeCardShape for rounded corners
- **Dismissal**: Automatic dismissal on item click (default DropdownMenu behavior)

## Testing Checklist Summary

This section provides a quick reference summary of all testing items. Detailed testing checklists are included after each implementation step above.

### Quick Reference
- **Menu Functionality**: See Phase 1, Steps 1-4
- **Filter Toggle**: See Phase 2, Steps 1-6
- **Search Functionality**: See Phase 3, Steps 1-6
- **Integration & Polish**: See Phase 4, Steps 1-5

## Known Considerations

1. **Search Scope**: Currently searches all habits. Consider if search should be limited to habits due today or all habits.
2. **Filter Persistence**: Filter state persists when UI is hidden. Consider if this is desired behavior or if filter should clear when hidden.
3. **Search Performance**: For large habit lists, consider implementing database-level search query instead of in-memory filtering.
4. **Menu Positioning**: On smaller screens, ensure menu doesn't overflow screen bounds.
5. **Accessibility**: Ensure all interactive elements have proper content descriptions for screen readers.

## Status

- [x] Planning phase - **In Progress** (this document)
- [ ] Design finalized
- [ ] Implementation started
- [ ] Testing completed

---

*This document provides comprehensive implementation details, technical specifications, and testing procedures for the Utilities Menu feature.*

