# Notifications Implementation Plan

**Status:** Phase 1 MVP **in progress** — design decisions **locked**  
**Last revised:** 2026-09-24  
**Package:** `com.example.rewire`  
**SDK:** minSdk 24 · targetSdk 36  

User-facing mirror (Project store): `docs/notifications-implementation-plan.md` (kept in sync with this file).

---

## 1. Goals

Remind the user of **habits due today** at each habit’s **`preferredTime`**, with two notification actions:

| Action | Behavior |
|--------|----------|
| **Complete** | Mark the habit complete for the notification’s date in the background via `HabitManager.completeHabit` — dismiss the notification. Do **not** require opening the app. |
| **Add note** | **Inline RemoteInput** on the notification (no app UI required). Persist via `HabitManager.upsertNoteForDate(habitId, content, date)` from a `BroadcastReceiver` / short-lived service. |

**Non-goals (Phase 1):** per-habit notification toggles, quiet hours, addiction-habit reminders, notification history, grouped summaries, custom copy. Exact-alarm **settings deep-link UX** when `SCHEDULE_EXACT_ALARM` is denied is deferred (document only — see [§8 Risks](#8-risks)).

**Product constraints already in the app:**

- Habit home sorts due habits by `preferredTime`; due set comes from `HabitManager.getHabitsDueOn(today)`.
- Regular `HabitEntity.preferredTime` is **non-null** `String` (`LocalTime.toString()`, e.g. `"09:00"`).
- Inline Habit home notes use tap-elsewhere dismiss + auto-save; notification Add note uses **RemoteInput** instead (locked).

---

## 2. Current state (code gaps)

| Area | State |
|------|--------|
| Notification channel / permissions / receivers | **None** until Phase 1 lands |
| Exact AlarmManager scheduling | **None** until Phase 1 lands |
| Scheduling hooks on habit CRUD | **None** until Phase 1 lands |
| `isHabitCompletedForDate` | **Missing** until Phase 1 lands |
| Note from notification | RemoteInput → `upsertNoteForDate` (locked) |
| Weekly recurrence in `getHabitsDueOn` | **Bug:** treated as every day after `startDate` — **fix with Phase 1** (locked D5 / former D8) |

---

## 3. Architecture (locked)

### 3.1 Scheduler: Exact alarms (AlarmManager)

| Option | Decision |
|--------|----------|
| **AlarmManager `setExactAndAllowWhileIdle` / `setAlarmClock`** | **Locked for Phase 1** — minute-level reminder timing |
| WorkManager-only delays | Rejected for fire time (may still help with boot reseed work if useful) |

- Schedule **one alarm per habit occurrence** (habitId + date), with deterministic request codes for cancel/reschedule.
- Horizon: **5 days** ahead; **reseed** on app start, habit CRUD, and boot.
- Alarm fire path: load habit → if still due and **not** completed for that date → show notification.
- Manifest: `SCHEDULE_EXACT_ALARM` (and/or `USE_EXACT_ALARM` if required by API level). When `AlarmManager.canScheduleExactAlarms()` is false, fall back to inexact (`setAndAllowWhileIdle`) and note that a **settings deep-link** for exact-alarm permission is still needed (not built in Phase 1 UI).

### 3.2 Channels & permissions

| Item | Locked detail |
|------|----------------|
| Channel ID | `habit_reminders` |
| Name / importance | “Habit Reminders” · **`IMPORTANCE_DEFAULT`** |
| `POST_NOTIFICATIONS` | Request on **first habit created** (not first launch) — **non-blocking**; app works if denied |
| `RECEIVE_BOOT_COMPLETED` | Reseed schedule after reboot |
| `SCHEDULE_EXACT_ALARM` | Declared for exact fire times; grant/UX deep-link deferred |
| Notifications | **Separate notification per habit** (no grouping in Phase 1) |

Create the channel on app start (`MainActivity` or `Application`).

### 3.3 Components (logical)

```
Habit CRUD / App start / Boot
        │
        ▼
HabitNotificationScheduler  ──setExact──►  AlarmManager
                                                   │
                                                   ▼
                                         HabitAlarmReceiver (show)
                                                   │
                          ┌────────────────────────┴────────────────────────┐
                          ▼                                                 ▼
                HabitNotificationManager                         (skip if completed /
                (channel + NotificationCompat                      habit gone / not due)
                 + Complete / RemoteInput Add Note)
                          │
          ┌───────────────┴────────────────┐
          ▼                                ▼
 HabitNotificationReceiver          HabitNotificationReceiver
 (ACTION_COMPLETE_HABIT →           (RemoteInput → upsertNoteForDate)
  HabitManager.completeHabit)       — no UI required
```

**HabitManager** accepts an optional `HabitNotificationScheduler` (or `Context?`) and calls schedule/cancel after CRUD. Unit tests pass `null` / no-op.

### 3.4 Data flow

1. **Who?** `habitManager.getHabitsDueOn(D)` — same as home (after Weekly fix).
2. **When?** `LocalDateTime.of(D, LocalTime.parse(habit.preferredTime))` in device default zone. Unparseable → skip + log.
3. **Skip if done:** `isHabitCompletedForDate` at schedule time and again before `notify()`.
4. **Complete:** Receiver → `completeHabit` → cancel notification.
5. **Add note:** RemoteInput result → `upsertNoteForDate` → update/dismiss notification.
6. **IDs:** `"habit_${habitId}_${date}".hashCode()`; alarm request codes derived the same way.
7. **Addiction habits:** Out of scope for Phase 1.

---

## 4. Phased delivery

### Phase 1 — MVP (+ Weekly fix)

1. Dependencies + manifest permissions + channel (`IMPORTANCE_DEFAULT`)
2. `isHabitCompletedForDate` DAO / repo / manager
3. **Fix Weekly** in `getHabitsDueOn` (+ align core `Habit.isDueOn` if needed) + tests
4. `NotificationConstants` + `NotificationPermissionHelper` (request on first habit create)
5. `HabitNotificationManager` — Complete + RemoteInput Add note; separate per habit
6. `HabitNotificationScheduler` — AlarmManager exact path; **5-day** horizon
7. CRUD / app-start / boot reseed hooks
8. `HabitNotificationReceiver` for Complete + RemoteInput note
9. Unit tests for Weekly + completion check + occurrence selection
10. Manual device checklist

### Phase 2 — Reliability

- Timezone / `TIME_SET` / `TIMEZONE_CHANGED` reseed
- Cancel today’s alarm when completed in-app
- Exact-alarm **settings deep-link** when permission denied
- Horizon tuning; scheduled-alarm debug table (optional)

### Phase 3 — Settings & polish

- Global / per-habit enable, quiet hours, grouping / summary, addiction reminders, copy/sound prefs

---

## 5. Phase 1 file / touch list

### Create

| File | Role |
|------|------|
| `util/NotificationConstants.kt` | Channel IDs, actions, extras, id helper |
| `util/NotificationPermissionHelper.kt` | `POST_NOTIFICATIONS` + first-habit request flag |
| `manager/HabitNotificationManager.kt` | Build/show; Complete + RemoteInput |
| `manager/HabitNotificationScheduler.kt` | Exact AlarmManager enqueue/cancel/reschedule |
| `receiver/HabitAlarmReceiver.kt` | Alarm fire → show (or skip) |
| `receiver/HabitNotificationReceiver.kt` | Complete + Add-note RemoteInput |
| `receiver/BootReceiver.kt` | Boot → reseed |
| `db/RewireDatabase` singleton helper | Safe DB access from receivers |
| Tests | Weekly due, completion helper, horizon selection |

### Update

| File | Change |
|------|--------|
| `AndroidManifest.xml` | Permissions + receivers |
| `HabitCompletionDao` / repo / `HabitManager` | Completion helper; scheduler hooks; Weekly fix |
| `core/.../Habit.kt` | Align `Weekly` `isDueOn` with startDate weekday |
| `MainActivity.kt` | Channel; cold-start reseed; DB singleton |
| `AddEditHabitScreen.kt` | Request `POST_NOTIFICATIONS` after first successful create |
| `docs/00_SUMMARY.md` | Status |

---

## 6. Test plan

### Automated

- [ ] Weekly: due only on startDate’s weekday (not every day)
- [ ] `isHabitCompletedForDate` true/false
- [ ] Scheduler: only future due dates within **5-day** horizon; completed skipped
- [ ] HabitManager CRUD with null scheduler: existing tests pass

### Manual / device

| # | Scenario | Expect |
|---|----------|--------|
| 1 | Create first habit; grant notifications; preferredTime ~2–3 min ahead | Permission prompt on create; notification near time |
| 2 | Deny permission | App usable; no crash |
| 3 | Tap **Complete** | Completed; notification dismissed |
| 4 | **Add note** via inline reply | Note persisted; visible on Habit home without opening from action |
| 5 | Complete in app before fire | No notification |
| 6 | Update preferredTime / delete habit | Alarms rescheduled / cancelled |
| 7 | Reboot | Reseed after unlock |
| 8 | Weekly habit | Reminds only on correct weekday |
| 9 | Exact alarm denied (API 31+) | Inexact fallback; document settings deep-link still needed |

---

## 7. Design decisions (locked)

| ID | Question | **Locked decision** |
|----|----------|---------------------|
| **D1** | Timing accuracy | **Exact alarms** (AlarmManager), not WorkManager-only drift |
| **D2** | Channel importance | **`DEFAULT`** |
| **D3** | When to request `POST_NOTIFICATIONS` | **First habit created** (not first launch) |
| **D4** | Add-note UI | **RemoteInput / inline reply** — persist without opening the app (`upsertNoteForDate`) |
| **D5** | Fix Weekly before/with reminders? | **Yes** — real weekly semantics with Phase 1 |
| **D6** | Schedule horizon | **5 days** ahead |
| **D7** | Same-time habits | **Separate notification per habit** |
| **D8** | Null/unparseable `preferredTime`? | Skip schedule; log once |
| **D9** | HabitManager hooks? | Optional scheduler dependency on HabitManager |
| **D10** | Complete opens app for feedback? | **No** — silent complete + dismiss |
| **D11** | Addiction habits? | **No** until Phase 3 |

Former open IDs D1–D7 in the user-facing summary map to the table above (Weekly was previously D8 in the long list; now **D5** in the locked product set).

---

## 8. Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| **`SCHEDULE_EXACT_ALARM` denied** | Drift / missed punctuality | Inexact fallback; **Phase 2:** settings deep-link (`ACTION_REQUEST_SCHEDULE_EXACT_ALARM`) |
| **OEM battery killers** | Alarms delayed / killed | BootReceiver + app-start reseed; FAQ for unrestricted battery |
| **Timezone / DST** | Wrong wall clock | Reseed on timezone change (Phase 2) |
| **Weekly bug** | Over-notify | Fixed in Phase 1 |
| **Duplicate alarms** | Multi-fire | Cancel-by-request-code before enqueue |
| **Permission denied** | Silent feature | Soft prompt on first habit only |
| **RemoteInput / process death** | Missed note | `goAsync()` + IO; dismiss/update notification after save |
| **Room from receiver** | Wrong thread | `Dispatchers.IO` + DB singleton |

---

## 9. Compatibility with other work

- **Habit home notes (merged):** use `upsertNoteForDate`; do not regress card note UX.
- **Utilities menu (planned):** distinct state names if any dialog remains.
- **Labels:** optional accent color — nice-to-have.

---

## 10. Implementation checklist (Phase 1)

- [x] D1–D7 (product) + D8–D11 reviewed / **locked**
- [x] Manifest permissions + receivers + exact-alarm declaration
- [x] Completion EXISTS helper end-to-end
- [x] Weekly due-logic fix + tests
- [x] Channel (`DEFAULT`) + permission on first habit create
- [x] Exact AlarmManager scheduler + 5-day horizon + per-habit notifications
- [x] CRUD / start / boot reseed
- [x] Complete receiver + RemoteInput Add note
- [x] Automated tests (Weekly, completion, horizon selection)
- [x] Update `docs/00_SUMMARY.md`

**Still open (Phase 2 UX):** in-app deep-link to exact-alarm settings when `canScheduleExactAlarms()` is false (`NotificationPermissionHelper.exactAlarmSettingsIntent` exists; no settings UI yet).

---

## 11. Out of scope for the plan-only PR

Plan document updates only. Feature code ships in the Phase 1 implementation PR(s).

---

*Source of truth for notifications design. Prefer checklist updates in PRs over re-expanding this file into multi-thousand-line pseudo-code.*
