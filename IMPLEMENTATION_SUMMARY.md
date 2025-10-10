# Pomodoro Timer - Background Service Implementation

## Summary of Changes

This document summarizes the implementation of reliable background services and notifications for the Pomodoro Timer app across all three platforms (Android, Desktop, and iOS).

## What Was Implemented

### 1. **Common Module (Shared)**

#### New Files:
- `shared/src/commonMain/kotlin/com/avsdeveloper/pomodoro/platform/PomodoroService.kt`
  - Platform-agnostic interface for managing timer notifications/services
  - Methods: `startService()`, `updateService()`, `stopService()`

#### Modified Files:
- `shared/src/commonMain/kotlin/com/avsdeveloper/pomodoro/presentation/timer/TimerViewModel.kt`
  - Integrated with `PomodoroService` to manage notifications based on timer state
  - Automatically starts/updates/stops service when timer state changes
  - RUNNING state → starts foreground service/notification
  - PAUSED/COMPLETED → updates notification
  - IDLE/Reset → stops service completely

- `shared/src/commonMain/kotlin/com/avsdeveloper/pomodoro/presentation/timer/TimerScreen.kt`
  - Added optional `onClose` callback parameter
  - Added Close button (IconButton with X icon) at the top-right corner
  - Button is only shown when `onClose` callback is provided

- `shared/src/commonMain/kotlin/com/avsdeveloper/pomodoro/di/AppModule.kt`
  - Updated to inject `PomodoroService` into `TimerViewModel`

---

### 2. **Android Platform**

#### New Files:
- `androidApp/src/main/kotlin/com/avsdeveloper/pomodoro/android/service/PomodoroForegroundService.kt`
  - Full foreground service implementation
  - Creates persistent notification that shows timer status, formatted time, session type, and session count
  - Notification is **ongoing** (cannot be dismissed) when timer is RUNNING
  - Notification can be dismissed when timer is PAUSED/COMPLETED
  - Uses notification channel with LOW importance to avoid intrusive alerts
  - Service runs with START_STICKY flag for reliability

- `androidApp/src/main/kotlin/com/avsdeveloper/pomodoro/android/service/AndroidPomodoroService.kt`
  - Android-specific implementation of `PomodoroService`
  - Manages foreground service lifecycle
  - Handles Android O+ (API 26+) foreground service requirements

#### Modified Files:
- `androidApp/src/main/AndroidManifest.xml`
  - Added `FOREGROUND_SERVICE` permission
  - Added `POST_NOTIFICATIONS` permission (for Android 13+)
  - Declared `PomodoroForegroundService` with foreground service type

- `androidApp/src/main/kotlin/com/avsdeveloper/pomodoro/android/PomodoroApplication.kt`
  - Created Android-specific Koin module
  - Provides `AndroidPomodoroService` as implementation of `PomodoroService`

- `androidApp/src/main/kotlin/com/avsdeveloper/pomodoro/android/MainActivity.kt`
  - Added `onClose = { finish() }` callback to TimerScreen
  - Allows users to close the app while service continues running in background

**Key Features:**
- ✅ Foreground service prevents OS from killing the app
- ✅ Persistent notification shows real-time timer status
- ✅ Notification cannot be dismissed when timer is running
- ✅ Notification is hidden when timer is reset/idle
- ✅ Close button allows minimizing app while timer runs

---

### 3. **Desktop Platform**

#### New Files:
- `desktopApp/src/main/kotlin/com/avsdeveloper/pomodoro/desktop/service/DesktopPomodoroService.kt`
  - Desktop-specific implementation using Java AWT System Tray
  - Creates a tray icon in the system status bar
  - Icon color changes based on timer state:
    - 🔴 Red: Work session running
    - 🟠 Orange: Short break running
    - 🟢 Green: Long break running
    - 🟡 Yellow: Paused
    - ⚪ Gray: Idle
  - Tooltip shows session type, time remaining, and current state

#### Modified Files:
- `desktopApp/src/main/kotlin/com/avsdeveloper/pomodoro/desktop/Main.kt`
  - Created Desktop-specific Koin module
  - Provides `DesktopPomodoroService` as implementation of `PomodoroService`
  - Added `onClose = ::exitApplication` to TimerScreen

**Key Features:**
- ✅ System tray icon shows timer status at a glance
- ✅ Tooltip provides detailed timer information
- ✅ Color-coded visual feedback
- ✅ Close button exits the application

---

### 4. **iOS Platform**

#### New Files:
- `shared/src/iosMain/kotlin/com/avsdeveloper/pomodoro/ios/service/IOSPomodoroService.kt`
  - iOS-specific implementation using UserNotifications framework
  - Requests notification permissions on initialization
  - Shows local notifications with timer status
  - Plays sound when session completes
  - Automatically updates notification as timer changes

#### Modified Files:
- `shared/src/iosMain/kotlin/com/avsdeveloper/pomodoro/KoinIOS.kt`
  - Created iOS-specific Koin module
  - Provides `IOSPomodoroService` as implementation of `PomodoroService`

- `shared/src/iosMain/kotlin/com/avsdeveloper/pomodoro/MainViewController.kt`
  - Updated to pass `onClose = null` to TimerScreen
  - iOS apps typically don't have close buttons (following platform conventions)

**Key Features:**
- ✅ Local notifications show timer status
- ✅ Sound alert when session completes
- ✅ Notifications update in real-time
- ✅ Follows iOS platform conventions (no close button)

---

## How It Works

### Service Lifecycle:

1. **Timer Started (RUNNING state)**
   - `TimerViewModel` detects state change
   - Calls `pomodoroService.startService(timer)`
   - Platform-specific service creates notification/tray icon
   - Service keeps app alive in background

2. **Timer Updated (time ticks)**
   - Service automatically updates notification with new time
   - Shows current session, time remaining, and state

3. **Timer Paused/Completed**
   - Service updates notification to show paused/completed state
   - Notification becomes dismissible (Android)

4. **Timer Reset (IDLE state)**
   - Service is completely stopped
   - Notification/tray icon is removed
   - App can be killed by OS if in background

### Close Button Behavior:

- **Android**: Closes activity, service continues running in background
- **Desktop**: Exits application
- **iOS**: Not shown (follows platform convention)

---

## Testing Instructions

### Android:
1. Build and run the app
2. Start a Pomodoro session
3. Check notification tray - you should see persistent notification
4. Tap Close button - app closes but notification remains
5. Tap notification to return to app
6. Reset timer - notification should disappear
7. Try force-stopping app from settings - service should protect it

### Desktop:
1. Run the app
2. Start a Pomodoro session
3. Check system tray - you should see colored icon
4. Hover over icon to see tooltip
5. Timer should update in tooltip
6. Close app - icon should disappear

### iOS:
1. Build and run on device/simulator
2. Grant notification permissions when prompted
3. Start a Pomodoro session
4. Background the app
5. Check notifications - should show timer status
6. Wait for session to complete - should hear sound alert

---

## Notes

- All implementations follow platform-specific best practices
- Android service uses foreground service to prevent process death
- Desktop uses system tray for unobtrusive notifications
- iOS uses local notifications (background limitations apply)
- Close button shown where appropriate for platform UX

