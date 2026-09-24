# UI Shape & Corner Radius Guidelines

Forward-looking design direction for corner radii and shapes across Rewire. Prefer updating shared tokens over one-off `RoundedCornerShape` values in screens.

## Source of truth

Shape tokens live in `app/src/main/kotlin/ui/theme/Shape.kt` (`AppShapes`):

| Token | Current radius | Typical use |
|-------|----------------|-------------|
| `smallCardShape` | 8dp | Compact elements |
| `inputShape` | 16dp | Outlined text fields / form inputs |
| `buttonShape` | 12dp | Buttons and interactive controls |
| `cardShape` | 16dp | Habit cards, main surfaces |
| `largeCardShape` | 24dp | Modals, prominent containers |

`RewireTheme` maps Material `Shapes.small` to `AppShapes.inputShape` so `OutlinedTextField` / `TextField` inherit the token. Prefer `AppShapes` for other surfaces so radii stay consistent and easy to bump globally.

## Design direction: more rounded corners

**Product feedback (first-time UI review):** Prefer **more rounded corners** going forward — a general visual direction, not a one-off tweak on a single screen.

### Guidelines

1. **Raise radii via tokens** — When polishing UI, increase `AppShapes` values (and keep Material theme shapes aligned if/when wired up) rather than hardcoding larger radii in individual composables.
2. **Bias softer** — Prefer the next-softer token when choosing between two sizes (e.g. cards toward `largeCardShape`-level softness; inputs/buttons less boxy than today).
3. **Home and new surfaces first** — Habit home redesign and new components should lean into rounder cards, sheets, and controls (see also `HABIT_HOME_SCREEN_BACKGROUND_DESIGN.md`).
4. **No one-off exceptions** — Avoid local `RoundedCornerShape(...)` that diverge from `AppShapes` unless temporarily prototyping; fold winners back into tokens.

### Suggested follow-up (implementation, separate PR)

- Review and bump remaining `AppShapes` radii (cards/buttons) after a quick visual pass.
- Sweep remaining hardcoded corner radii to `AppShapes`.
- Spot-check habit cards, dialogs, label chips, and home chrome on device/emulator.

## Related

- Habit home concepts: `HABIT_HOME_SCREEN_BACKGROUND_DESIGN.md`
- Theme package: `app/src/main/kotlin/ui/theme/` (`Shape.kt`, `Theme.kt`, `Colour.kt`, `Typography.kt`)
- Docs hub: `docs/00_SUMMARY.md`

## Status

- [x] Design direction captured from product feedback
- [x] Input token radius increased (`inputShape` → 16dp)
- [x] Material theme `shapes.small` aligned with `AppShapes.inputShape`
- [ ] Remaining token radii (cards/buttons) reviewed / increased
- [ ] Hardcoded radii swept to tokens

---

*Input-field rounding is implemented via `AppShapes.inputShape` + theme wiring; broader surface radius bumps remain follow-up.*
