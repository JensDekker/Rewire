# Notifications Implementation Plan

**Status:** Design ready for coding (Phase 1 MVP) — **not implemented**  
**Last revised:** 2026-09-24  
**Package:** `com.example.rewire`  
**SDK:** minSdk 24 · targetSdk 36  

User-facing mirror (Project store): see linked copy under Project Context `docs/notifications-implementation-plan.md` (kept in sync with this file).

---

## 1. Goals

Remind the user of **habits due today** at each habit’s **`preferredTime`**, with two notification actions:

| Action | Behavior |
|--------|----------|
| **Complete** | Mark the habit complete for the notification’s date (today when fired). Prefer background completion via `HabitManager.completeHabit` — dismiss the notification. Do **not** require opening the app. |
| **Add note** | Bring the app to the foreground and open a note entry surface for that habit/date. Persist via `HabitManager.upsertNoteForDate(habitId, content, date)` (already used on Habit home). |

**Non-goals (Phase 1):** per-habit notification toggles, quiet hours, addiction-habit reminders, notification history, grouped summaries, custom copy, exact-alarm UX beyond a documented fallback.

**Product constraints already in the app:**

- Habit home sorts due habits by `preferredTime`; due set comes from `HabitManager.getHabitsDueOn(today)`.
- Regular `HabitEntity.preferredTime` is **non-null** `String` (`LocalTime.toString()`, e.g. `"09:00"`).
- Inline Habit home notes: tap-elsewhere dismiss + auto-save (no Cancel/Done). Notification “Add note” UX may differ — see [Open design decisions](#7-open-design-decisions-need-user-input).

---

## 2. Current state (code gaps)

| Area | State |
|------|--------|
| Notification channel / permissions / receivers | **None** — `AndroidManifest.xml` only declares `MainActivity` |
| WorkManager / AlarmManager usage | **None** — no `work-runtime-ktx` dependency |
| Scheduling hooks on habit CRUD | **None** — `HabitManager` has no `Context`; create/update/delete do not schedule |
| `isHabitCompletedForDate` | **Missing** — today callers load `getCompletionsForHabit` and scan |
| Note from notification | Prefer existing `upsertNoteForDate` (not raw insert/edit alone) |
| Weekly recurrence in `getHabitsDueOn` | **Bug / mismatch:** always returns due after `startDate` (comment claims weekday-of-start; core `Habit.isDueOn` uses Monday). Notifications must follow **the same** due logic as the home screen, or fix Weekly first — see open decisions |

---

## 3. Architecture recommendation

### 3.1 Scheduler: WorkManager (Phase 1 default)

| Option | Pros | Cons |
|--------|------|------|
| **WorkManager `OneTimeWorkRequest` + delay** (recommended MVP) | Survives process death; integrates with Doze; no exact-alarm permission for typical use; boot persistence via WM; simpler API | Timing is **inexact** (minutes of drift under Doze / OEM battery); not ideal for “exactly 07:00” |
| **AlarmManager `setExactAndAllowWhileIdle` / `setAlarmClock`** | Tighter delivery | Needs `SCHEDULE_EXACT_ALARM` / user settings on API 31+; more OEM breakage; more code; Play policy scrutiny for exact alarms |
| Hybrid (WM for reschedule + AlarmManager for fire) | Best of both | Highest complexity — defer past MVP |

**Phase 1 decision (proposed):** WorkManager only.

- Schedule **one** `OneTimeWorkRequest` per habit occurrence (habitId + date), tagged for cancel/reschedule.
- Horizon: **next 7–14 days** of due occurrences (not 30–90) to limit queued work; **reseed** on app start, habit CRUD, and boot.
- Worker `doWork`: load habit → if still due and **not** completed for that date → show notification → optionally enqueue the **next** occurrence.

**Exact alarms:** Do **not** request `SCHEDULE_EXACT_ALARM` in Phase 1 unless product insists on minute-level accuracy (open decision). Document expected drift in release notes / settings copy later.

### 3.2 Channels & permissions

| Item | Detail |
|------|--------|
| Channel ID | `habit_reminders` |
| Name / importance | “Habit Reminders” · `IMPORTANCE_DEFAULT` or `HIGH` (open decision: HIGH can feel aggressive) |
| `POST_NOTIFICATIONS` | Required API 33+. Request once from MainActivity (or first habit create) — **non-blocking**; app works if denied |
| `RECEIVE_BOOT_COMPLETED` | Reseed schedule after reboot (WM helps, explicit receiver still recommended for immediate reseed) |
| `USE_EXACT_ALARM` / `SCHEDULE_EXACT_ALARM` | **Out of Phase 1** unless decided otherwise |

Create the channel on app start (`MainActivity` or `Application` if added later).

### 3.3 Components (logical)

```
Habit CRUD / App start / Boot
        │
        ▼
HabitNotificationScheduler  ──enqueue──►  WorkManager (OneTimeWorkRequest)
                                                   │
                                                   ▼
                                         HabitNotificationWorker
                                                   │
                          ┌────────────────────────┴────────────────────────┐
                          ▼                                                 ▼
                HabitNotificationManager                         (skip if completed /
                (channel + NotificationCompat                      habit gone / not due)
                 + Complete / Add Note actions)
                          │
          ┌───────────────┴────────────────┐
          ▼                                ▼
 HabitNotificationReceiver          MainActivity (ACTION_ADD_NOTE)
 (ACTION_COMPLETE_HABIT →           → open note UI → upsertNoteForDate
  HabitManager.completeHabit)
```

**Prefer a dedicated scheduler + worker** over stuffing `Context` into `HabitManager` long-term. Phase 1 pragmatic options (pick one — open decision):

1. **Thin hooks:** `HabitManager` accepts optional `HabitNotificationScheduler` (or `Context?`) and calls schedule/cancel after CRUD.  
2. **Call-site hooks:** Screens / MainActivity call the scheduler after successful CRUD (keeps manager pure; easier to miss a call site).

Recommendation: **(1)** with optional dependency defaulting to no-op in unit tests.

### 3.4 Data flow with HabitManager / Room / preferred times

1. **Who gets a reminder for date D?**  
   Same set as UI: `habitManager.getHabitsDueOn(D.toString())`.  
   Do **not** reimplement recurrence in the notification layer. Optionally extract shared “due on date” helpers later; for MVP call HabitManager / reuse its logic.

2. **When?**  
   `LocalDateTime.of(D, LocalTime.parse(habit.preferredTime))` in the **device default zone**.  
   If parse fails → skip (or fallback 09:00 — open decision; home UI already falls back to 09:00 when sorting).

3. **Skip if already done:**  
   Add `isHabitCompletedForDate(habitId, date)` on DAO → Repository → HabitManager (`EXISTS` query). Check at **schedule** time and again in the **worker** before `notify()`.

4. **Complete action:**  
   Receiver → Room/repos (or HabitManager) → `completeHabit(habitId, date)` → cancel notification ID.

5. **Add note action:**  
   Activity PendingIntent → MainActivity → Habit home (or dialog) with habitId/date → `upsertNoteForDate`.

6. **IDs:**  
   Deterministic notification id, e.g. `"habit_${habitId}_${date}".hashCode()`. Work tags: `habit_notification` + `habit_id_$id`.

7. **Addiction habits:** Out of scope for Phase 1 (`preferredTime` nullable there).

---

## 4. Phased delivery

### Phase 1 — MVP (ship reminders + actions)

Enough to remind and act; acceptable timing drift.

1. Dependencies + manifest permissions + channel creation  
2. `isHabitCompletedForDate` DAO / repo / manager  
3. `NotificationConstants` + `NotificationPermissionHelper`  
4. `HabitNotificationManager` (build + show; Complete / Add Note actions)  
5. `HabitNotificationWorker` + `HabitNotificationScheduler` (WorkManager)  
6. CRUD / app-start / boot reseed hooks  
7. `HabitNotificationReceiver` for Complete  
8. MainActivity intent handling for Add Note + permission request  
9. Minimal note UI from notification (dialog **or** deep-link into existing HabitCard note — see open decisions)  
10. Unit tests for completion check + scheduler date selection; manual device test checklist  

### Phase 2 — Reliability & recurrence correctness

- Fix or clarify `Weekly` due logic so home + notifications agree with product intent  
- Timezone / `TIME_SET` / `TIMEZONE_CHANGED` reseed  
- Reschedule after completion from **in-app** UI (cancel today’s notification)  
- Horizon tuning; cancel stale work on update  
- Optional: database table of scheduled work for debugging (else rely on WorkManager tags)

### Phase 3 — Settings & polish

- Global and/or per-habit notification enable  
- Quiet hours  
- Exact alarms / `setAlarmClock` if product requires punctuality  
- Grouping / daily summary  
- Addiction reminders  
- Notification copy / sound preferences  

---

## 5. Phase 1 file / touch list

### Create

| File | Role |
|------|------|
| `app/src/main/kotlin/util/NotificationConstants.kt` | Channel IDs, actions, extras, id helper, work tags |
| `app/src/main/kotlin/util/NotificationPermissionHelper.kt` | `POST_NOTIFICATIONS` check / “should request” |
| `app/src/main/kotlin/manager/HabitNotificationManager.kt` | Build/show `NotificationCompat`; PendingIntents |
| `app/src/main/kotlin/manager/HabitNotificationScheduler.kt` | Enqueue/cancel/reschedule WorkManager work |
| `app/src/main/kotlin/service/HabitNotificationWorker.kt` | `CoroutineWorker` — gate + show |
| `app/src/main/kotlin/receiver/HabitNotificationReceiver.kt` | Complete action |
| `app/src/main/kotlin/receiver/BootReceiver.kt` | Boot → reseed (enqueue short-delay reschedule work) |
| `app/src/main/kotlin/ui/components/HabitNoteDialog.kt` | **Only if** product chooses dialog for notification note (else reuse Habit home note UX) |
| `app/src/test/kotlin/...` | Tests for `isHabitCompletedForDate`, scheduler occurrence picking (pure logic) |

Optional extract (nice-to-have, not blocking MVP):

| File | Role |
|------|------|
| `app/src/main/kotlin/util/NotificationDateCalculator.kt` | Thin wrapper that calls into due-date logic for “next N due dates” — **must** stay consistent with `getHabitsDueOn` |

### Update

| File | Change |
|------|--------|
| `app/build.gradle.kts` | `implementation("androidx.work:work-runtime-ktx:2.9.x")` (or catalog entry) |
| `app/src/main/AndroidManifest.xml` | `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED`; register receivers |
| `app/src/main/kotlin/db/dao/HabitCompletionDao.kt` | `isHabitCompletedForDate` |
| `app/src/main/kotlin/repository/HabitCompletionRepository.kt` | expose method |
| `app/src/main/kotlin/manager/HabitManager.kt` | expose method; optional scheduler hooks on create/update/delete (+ `*WithLabels`) |
| `app/src/main/kotlin/MainActivity.kt` | channel; permission request; HabitManager/scheduler wiring; `onNewIntent` for Add Note; cold-start reseed |
| `app/src/main/kotlin/ui/screens/HabitHomeScreen.kt` | consume pending Add Note intent / show note UI |
| `app/src/test/kotlin/manager/HabitManagerTest.kt` | completion helper + null/no-op scheduler |

### Do not change in Phase 1 (unless fixing Weekly — see decisions)

- Recurrence sealed types / converters  
- Addiction habit stack  
- Labels schema  

---

## 6. Test plan

### Automated (Phase 1)

- [ ] `isHabitCompletedForDate` true/false for matching / missing rows  
- [ ] Scheduler: given fixed “now”, only future due dates within horizon are enqueued; completed dates skipped  
- [ ] HabitManager CRUD with null scheduler: existing unit tests still pass  
- [ ] (Optional) WorkManager test APIs for worker skip-when-completed  

### Manual / device

| # | Scenario | Expect |
|---|----------|--------|
| 1 | Grant notification permission; create daily habit with preferredTime ~2–3 min ahead | Notification appears near that time |
| 2 | Deny permission | App usable; no crash; no notification |
| 3 | Tap **Complete** | Habit completed for that date; notification dismissed; home reflects complete |
| 4 | Tap **Add note**, save text | Note stored for habit/date; visible on Habit home |
| 5 | Complete habit **in app** before fire time | Notification does not appear (worker skip) |
| 6 | Update preferredTime | Old work cancelled; new schedule used |
| 7 | Delete habit | Pending work cancelled |
| 8 | Reboot device | After unlock, notifications reseed (BootReceiver / WM) |
| 9 | App killed / background | Complete still works; Add note launches app |
| 10 | API 33+ and API 26–32 emulator/device | Permission path correct on both |

### Phase 2+

- Timezone change, DST, travel  
- OEM battery restriction (Samsung/Xiaomi) — document user steps if delivery fails  
- All recurrence types against home due list  

---

## 7. Open design decisions (need user input)

Call these out before or during Phase 1 coding; defaults below are **proposals only**.

| ID | Question | Proposed default |
|----|----------|------------------|
| **D1** | WorkManager (inexact) vs AlarmManager / exact alarms for fire time? | WorkManager MVP; exact later if needed |
| **D2** | Channel importance `HIGH` vs `DEFAULT`? | `DEFAULT` (less interruptive); vibrate optional |
| **D3** | When to request `POST_NOTIFICATIONS`? | First app launch after install **or** first habit create — prefer soft rationale, never block |
| **D4** | Add-note UI: Material dialog vs deep-link into Habit home inline note? | Dialog from notification is clearer for cold start; align dismiss/save with home (`upsertNoteForDate`). Avoid introducing Cancel/Done on home. |
| **D5** | Null/unparseable `preferredTime`? | Skip schedule (habits should always have a time); log once |
| **D6** | Schedule horizon (days ahead)? | **14 days**, reseed on start/CRUD/boot |
| **D7** | Multiple habits same time: separate notifications vs group? | Separate in Phase 1 |
| **D8** | Fix `Weekly` due bug before notifications? | **Yes recommended** — otherwise Weekly habits notify every day like Daily |
| **D9** | HabitManager gets optional scheduler/`Context` vs call-site-only hooks? | Optional scheduler dependency on HabitManager |
| **D10** | Should completing from notification also open the app briefly for feedback? | No — silent complete + dismiss |
| **D11** | Include addiction habits in reminders? | No until Phase 3 |

---

## 8. Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| **Doze / App Standby** | WorkManager delays past `preferredTime` | Accept for MVP; document; escalate to exact/`setAlarmClock` if users complain |
| **OEM battery killers** | Work never runs / boot delayed | BootReceiver + app-start reseed; FAQ for unrestricted battery |
| **Timezone / DST** | Fire at wrong wall clock | Store local date+time; reseed on `TIMEZONE_CHANGED` / `TIME_SET` (Phase 2) |
| **Weekly due bug** | Over-notifying | Fix Weekly or treat as known issue before shipping reminders |
| **Duplicate work** | Multiple notifications same habit/day | Cancel-by-tag before enqueue; deterministic ids |
| **Permission denied** | Feature silent | Soft prompt; settings deep link later |
| **Process death in Complete receiver** | Missed complete | Use `goAsync()` / coroutine with timeout; or short `CoroutineWorker` for complete |
| **Room access from receiver/worker** | Wrong thread / no DB | Always `Dispatchers.IO`; open DB via `RewireDatabase.getInstance` pattern (extract singleton if needed) |

---

## 9. Compatibility with other work

- **Habit home notes (merged):** use `upsertNoteForDate`; do not regress tap-elsewhere auto-save on cards.  
- **Utilities menu (planned):** use distinct state names (`showNoteDialog` vs search/filter state).  
- **Labels:** optional notification accent color from first label — nice-to-have, not MVP-blocking.

---

## 10. Implementation checklist (Phase 1)

- [ ] D1–D11 reviewed / defaults accepted  
- [ ] WorkManager dependency + sync  
- [ ] Manifest permissions + receivers  
- [ ] Completion EXISTS helper end-to-end  
- [ ] Channel + permission helper + request UX  
- [ ] Scheduler + Worker + show notification  
- [ ] CRUD / start / boot reseed  
- [ ] Complete receiver  
- [ ] Add-note path + persistence  
- [ ] Automated + manual tests above  
- [ ] Update `docs/00_SUMMARY.md` status when Phase 1 coding starts/finishes  

---

## 11. Out of scope for this PR

This document revision only. **No feature implementation** in the plan PR (tiny stubs optional later; none required to start coding from this plan).

---

*Replace prior long draft samples with this plan as the source of truth. When coding starts, keep checklists in PRs; avoid re-expanding this file into multi-thousand-line pseudo-code.*
