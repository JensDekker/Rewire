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
- **Icon**: Search icon (`Icons.Default.Search` or similar)
- **Behavior**: Opens a search modal/dialog
- **Functionality**: Search habits by name
- **Implementation**: New search modal component
- **Integration**: Search results displayed in HabitHomeScreen or in modal

#### 2. Filter Functionality
- **Icon**: Filter icon (`Icons.Default.FilterList` or similar)
- **Behavior**: Toggles visibility of existing label filter UI
- **Functionality**: Filter habits by selected labels (existing functionality)
- **Implementation**: Move existing filter UI to be toggleable via menu
- **Integration**: Current filter UI becomes hidden by default, shown when filter icon is clicked

#### 3. Settings/Manage Labels
- **Icon**: Settings gear icon (`Icons.Default.Settings`)
- **Behavior**: Navigate to Label Management Screen
- **Functionality**: Same as current "Manage Labels" menu item
- **Implementation**: Navigation to `label_management` route

## Implementation Notes

- Menu should use Material Design DropdownMenu or similar component
- Menu styling should match app theme (AppColors, AppShapes, AppTypography)
- Menu items should have clear icons and text labels
- Menu should be dismissible by clicking outside or pressing back
- Consider accessibility (content descriptions, keyboard navigation)

## Future Enhancements

- Additional menu items (e.g., Statistics, Settings screen)
- Keyboard shortcuts for menu items
- Custom icons or styling
- Menu animations/transitions

## Files to Update

- `app/src/main/kotlin/ui/screens/HabitHomeScreen.kt` - Replace settings icon with MoreVert icon, update menu items
- New: Search modal component (to be created)
- Potentially: Filter UI component refactoring (if making it toggleable)

## Status

- [ ] Planning phase
- [ ] Design finalized
- [ ] Implementation started
- [ ] Testing completed

---

*This document will be expanded with detailed implementation steps, validation checklists, and design specifications as the feature is developed.*

