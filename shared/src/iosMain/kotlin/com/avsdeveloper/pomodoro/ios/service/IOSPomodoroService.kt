package com.avsdeveloper.pomodoro.ios.service

import com.avsdeveloper.pomodoro.domain.model.PomodoroTimer
import com.avsdeveloper.pomodoro.domain.model.SessionType
import com.avsdeveloper.pomodoro.domain.model.TimerState
import com.avsdeveloper.pomodoro.platform.PomodoroService
import platform.Foundation.NSNumber
import platform.UserNotifications.*
import platform.darwin.NSObject
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationState
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_sync
import kotlinx.cinterop.ExperimentalForeignApi

// Delegate to control when notifications are shown
private class NotificationDelegate : NSObject(), UNUserNotificationCenterDelegateProtocol {

    var shouldShowInForeground: Boolean = false

    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (UNNotificationPresentationOptions) -> Unit
    ) {
        println("🔔 iOS: Notification will present - shouldShowInForeground: $shouldShowInForeground")

        if (shouldShowInForeground) {
            // Show notification in foreground with banner and sound
            withCompletionHandler(UNNotificationPresentationOptionBanner or UNNotificationPresentationOptionSound)
        } else {
            // Don't show notification in foreground
            withCompletionHandler(0u)
        }
    }
}

class IOSPomodoroService : PomodoroService {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    private val notificationId = "pomodoro_timer_notification"
    private val delegate = NotificationDelegate()

    // Track previous state to only update notification when needed
    private var previousTimerState: TimerState? = null
    private var previousSessionType: SessionType? = null
    private var previousSessionCount: Int? = null

    init {
        println("🔔 iOS: IOSPomodoroService initialized")
        // Set delegate to control foreground notifications
        notificationCenter.delegate = delegate
        requestNotificationPermission()
    }

    override fun startService(timer: PomodoroTimer) {
        println("🔔 iOS: startService called - State: ${timer.timerState}, Session: ${timer.sessionCount}")

        // Check if this is a new session starting
        // New session is when: session type changed OR first time starting (previousSessionCount is null)
        val isNewSession = previousSessionCount == null ||
                          (previousSessionCount != timer.sessionCount) ||
                          (previousSessionType != timer.sessionType)

        if (isNewSession) {
            // Always show notification when a new session starts
            println("🔔 iOS: New session started - showing notification regardless of app state")
            showNotification(timer, forceShow = true)
        } else if (shouldUpdateNotification(timer)) {
            // For running/paused states, only show if in background
            showNotification(timer, forceShow = false)
        } else {
            println("🔔 iOS: Skipping update - no state change detected")
        }
    }

    override fun updateService(timer: PomodoroTimer) {
        println("🔔 iOS: updateService called - State: ${timer.timerState}, Session: ${timer.sessionCount}")

        // Check if session just completed
        val sessionCompleted = timer.timerState == TimerState.COMPLETED && previousTimerState != TimerState.COMPLETED

        if (sessionCompleted) {
            // Always show notification when session completes
            println("🔔 iOS: Session completed - showing notification regardless of app state")
            showNotification(timer, forceShow = true)
        } else if (shouldUpdateNotification(timer)) {
            // For running/paused states, only show if in background
            showNotification(timer, forceShow = false)
        } else {
            println("🔔 iOS: Skipping update - no state change detected")
        }
    }

    override fun stopService() {
        println("🔔 iOS: stopService called")
        removeAllNotifications()
        resetTrackedState()
    }

    private fun shouldUpdateNotification(timer: PomodoroTimer): Boolean {
        // Always update if this is the first call (previous state is null)
        if (previousTimerState == null) {
            println("🔔 iOS: First notification - will update")
            return true
        }

        val stateChanged = previousTimerState != timer.timerState
        val sessionChanged = previousSessionType != timer.sessionType ||
                            previousSessionCount != timer.sessionCount

        println("🔔 iOS: State check - Previous: $previousTimerState, Current: ${timer.timerState}, Changed: $stateChanged")
        println("🔔 iOS: Session check - Previous: $previousSessionType/$previousSessionCount, Current: ${timer.sessionType}/${timer.sessionCount}, Changed: $sessionChanged")

        // Update notification when:
        // 1. State changed (started/paused/resumed/completed)
        // 2. Session changed (new session started)
        return stateChanged || sessionChanged
    }

    private fun requestNotificationPermission() {
        println("🔔 iOS: Requesting notification permission...")
        notificationCenter.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { granted, error ->
            if (error != null) {
                println("🔔 iOS: ❌ Notification Error: ${error.localizedDescription}")
            } else {
                println("🔔 iOS: ✅ Notification Permission granted: $granted")
            }
        }
    }

    private fun removeAllNotifications() {
        println("🔔 iOS: Removing all notifications")
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(listOf(notificationId))
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(listOf(notificationId))
    }

    private fun resetTrackedState() {
        previousTimerState = null
        previousSessionType = null
        previousSessionCount = null
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun isAppInForeground(): Boolean {
        var isInForeground = false
        // Must access UIApplication state from main thread
        dispatch_sync(dispatch_get_main_queue()) {
            val appState = UIApplication.sharedApplication.applicationState
            isInForeground = appState == UIApplicationState.UIApplicationStateActive
        }
        println("🔔 iOS: App state check - ${if (isInForeground) "FOREGROUND" else "BACKGROUND"}")
        return isInForeground
    }

    private fun showNotification(timer: PomodoroTimer, forceShow: Boolean = false) {
        println("🔔 iOS: showNotification called - State: ${timer.timerState}, Session: ${timer.sessionCount}, forceShow: $forceShow")

        // Don't show notification when timer is idle (reset/stopped)
        if (timer.timerState == TimerState.IDLE) {
            println("🔔 iOS: Skipping notification - timer is IDLE")
            removeAllNotifications()
            resetTrackedState()
            return
        }

        // Check if we should show notification based on app state
        val isInForeground = isAppInForeground()
        val isRunningOrPaused = timer.timerState == TimerState.RUNNING || timer.timerState == TimerState.PAUSED

        // Determine if we should show notification
        val shouldShow: Boolean
        val showInForeground: Boolean

        when {
            // Session completed or started - always show with foreground display
            forceShow -> {
                shouldShow = true
                showInForeground = true
                println("🔔 iOS: Force showing notification (session event)")
            }
            // Running/Paused - only show in background
            isRunningOrPaused && isInForeground -> {
                shouldShow = false
                showInForeground = false
                println("🔔 iOS: Skipping notification - app in foreground and timer is running/paused")
            }
            // Running/Paused in background - show without foreground display
            isRunningOrPaused && !isInForeground -> {
                shouldShow = true
                showInForeground = false
                println("🔔 iOS: Showing notification - app in background")
            }
            else -> {
                shouldShow = true
                showInForeground = false
                println("🔔 iOS: Showing notification - default case")
            }
        }

        if (!shouldShow) {
            // Update tracked state but don't show notification
            previousTimerState = timer.timerState
            previousSessionType = timer.sessionType
            previousSessionCount = timer.sessionCount
            return
        }

        // Set delegate behavior for foreground notifications
        delegate.shouldShowInForeground = showInForeground

        val sessionTypeText = when (timer.sessionType) {
            SessionType.WORK -> "Work Session"
            SessionType.SHORT_BREAK -> "☕ Short Break"
            SessionType.LONG_BREAK -> "🌴 Long Break"
        }

        val title: String
        val body: String

        when {
            timer.timerState == TimerState.COMPLETED -> {
                // Session completed notification
                title = "$sessionTypeText Completed!"
                body = "Session ${timer.sessionCount} finished"
            }
            previousSessionCount != timer.sessionCount && timer.sessionCount > 0 -> {
                // New session started notification
                title = "$sessionTypeText Started"
                body = "Session ${timer.sessionCount}"
            }
            else -> {
                // Running/Paused notification (shown only in background)
                val stateText = when (timer.timerState) {
                    TimerState.RUNNING -> "in progress"
                    TimerState.PAUSED -> "paused"
                    else -> "active"
                }
                title = "Pomodoro $stateText"
                body = "$sessionTypeText - Session ${timer.sessionCount}"
            }
        }

        println("🔔 iOS: Creating notification - Title: '$title', Body: '$body'")

        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
            // Play sound when session completes or starts
            if (timer.timerState == TimerState.COMPLETED ||
                (previousSessionCount != timer.sessionCount && timer.sessionCount > 0)) {
                setSound(UNNotificationSound.defaultSound())
                println("🔔 iOS: Sound enabled for session completion/start")
            } else {
                setSound(null)
            }
            // Set badge to session count
            setBadge(NSNumber(int = timer.sessionCount))
        }

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = notificationId,
            content = content,
            trigger = null // Show immediately
        )

        println("🔔 iOS: Adding notification request to notification center")
        notificationCenter.addNotificationRequest(request) { error ->
            if (error != null) {
                println("🔔 iOS: ❌ Error showing notification: ${error.localizedDescription}")
            } else {
                println("🔔 iOS: ✅ Notification request added successfully")
                // Double-check by getting pending notifications
                notificationCenter.getPendingNotificationRequestsWithCompletionHandler { requests ->
                    println("🔔 iOS: Pending notifications count: ${requests?.size ?: 0}")
                }
                // Check delivered notifications
                notificationCenter.getDeliveredNotificationsWithCompletionHandler { notifications ->
                    println("🔔 iOS: Delivered notifications count: ${notifications?.size ?: 0}")
                }
            }
        }

        // Update tracked state after showing notification
        previousTimerState = timer.timerState
        previousSessionType = timer.sessionType
        previousSessionCount = timer.sessionCount

        println("🔔 iOS: State tracked - State: $previousTimerState, Type: $previousSessionType, Count: $previousSessionCount")
    }
}
