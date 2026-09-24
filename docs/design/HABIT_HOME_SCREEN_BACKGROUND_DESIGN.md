# Habit Home Screen Background Design Ideas

## Overview

Exploration of new design concepts for the HabitHomeScreen background and layout. This document captures initial ideas and concepts that will be developed into a formal implementation plan.

## Current State

- HabitHomeScreen uses a unified background with no distinct header section
- Title "Today's Habits" is centered in the header row
- Content scrolls in a LazyColumn below the header
- No background gradients or distinct visual sections

## Design Concepts

### Title Block / Header Section

**Concept**: Create a distinct "block" or section in the upper portion of the screen that contains the title and potentially other information.

**Ideas**:
- **Visual Block**: Styled container/box in the top section with distinct background, border, or shadow
- **Information Display**: Could show summary information, statistics, or contextual data
- **Visual Separation**: Clear distinction between the header block and the scrolling content area

### Dynamic Title Block

**Concept**: Title block that changes its appearance or content based on scroll position or user interaction.

**Ideas**:
- **Scroll-Based Changes**: Title block expands/collapses or shows/hides additional information as user scrolls
- **Information Toggle**: Show more information at top when scrolled to top, hide/collapse when scrolling down
- **Unclear Information Content**: Need to determine what information would be valuable to show/hide
  - Potential ideas:
    - Habit statistics (total habits, completed today, completion rate)
    - Date/time information
    - Quick action buttons
    - Progress indicators
    - Upcoming habits summary
    - Recent activity

### Background Styling

**Concept**: Enhance the visual background of the screen with gradients, colors, or patterns.

**Ideas**:
- **Gradient Backgrounds**: Subtle gradients that complement the app theme
- **Color Transitions**: Smooth color transitions based on label colors of visible habits
- **Pattern Overlays**: Subtle patterns or textures
- **Dynamic Colors**: Background that changes based on time of day, habit completion status, or other factors

### Layout Considerations

**Ideas**:
- **Sticky Header**: Title block could remain sticky at top while content scrolls
- **Collapsible Header**: Header collapses on scroll, expands when at top
- **Card-Based Layout**: Title block styled as a card with elevation and rounded corners
- **Section Dividers**: Clear visual separation between header block and content area

## Questions to Explore

1. What information should be displayed in the title block?
2. Should the title block be static or dynamic (scroll-responsive)?
3. What visual styling should the title block have?
4. How should the title block interact with scrolling content?
5. Should the background change based on content (e.g., label colors)?
6. What is the priority information that users need at a glance?

## Known UX Gaps (Habit Home)

Product feedback from first-time UI review on device/emulator. Track here until fixed; these are home-screen UX bugs/improvements, not background-only work.

### Note-adding lacks a clear dismiss / cancel path

**Surface**: Habit home (`HabitHomeScreen`) → today's habit cards (`HabitCard`). Tapping the add-note icon expands an inline `OutlinedTextField` ("Today's Notes") via `expandedNoteHabits` / `isNoteFieldVisible`.

**Problem**: There is no clean way to exit or cancel out of the note-adding UI. Collapse today depends on re-tapping the note icon; there is no Cancel / Done / Dismiss control, no clear discard path, and no obvious back/outside-tap exit.

**Desired outcome**: A clear dismiss or cancel affordance so users can leave note entry without hunting for a toggle (e.g. Done/Cancel actions, explicit close control, and/or back/outside-tap that closes the field). Saving vs discarding unsaved edits should be intentional.

**Status**: ✅ Fixed — `HabitCard` shows Cancel / Done under the inline note field. Edits are held in a draft while open; **Done** commits via the existing `insertNote` path (blank notes still skip save) and collapses; **Cancel** (or re-tapping the note icon) discards the draft, clears focus/keyboard, and collapses. No new notes architecture.

## Future Development

This document will be expanded into a formal implementation plan once:
- Design concepts are refined and selected
- Information content for dynamic sections is determined
- Visual mockups or detailed specifications are created
- User experience considerations are evaluated
- Technical implementation approach is defined

## Related Features

- Utilities Menu (see `UTILITIES_MENU_IMPLEMENTATION_PLAN.md`)
- Label filtering and display
- Habit statistics and analytics
- Custom themes and styling
- UI shape / more rounded corners (see `UI_SHAPE_GUIDELINES.md`)

## Status

- [ ] Concept exploration phase
- [ ] Design decisions made
- [ ] Implementation plan created
- [ ] Implementation started
- [ ] Testing completed

---

*This document is a work in progress and will be expanded with detailed design specifications, implementation steps, and validation checklists as concepts are refined.*

