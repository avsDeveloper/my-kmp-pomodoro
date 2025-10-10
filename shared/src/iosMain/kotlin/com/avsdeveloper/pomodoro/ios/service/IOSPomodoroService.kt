package com.avsdeveloper.pomodoro.ios.service

import com.avsdeveloper.pomodoro.domain.model.PomodoroTimer
import com.avsdeveloper.pomodoro.domain.model.SessionType
import com.avsdeveloper.pomodoro.domain.model.TimerState
import com.avsdeveloper.pomodoro.platform.PomodoroService
import platform.UserNotifications.*

class IOSPomodoroService : PomodoroService {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    private val notificationId = "pomodoro_timer_notification"

    init {
        requestNotificationPermission()
    }

    override fun startService(timer: PomodoroTimer) {
        updateNotification(timer)
    }

    override fun updateService(timer: PomodoroTimer) {
        updateNotification(timer)
    }

    override fun stopService() {
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(listOf(notificationId))
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(listOf(notificationId))
    }

    private fun requestNotificationPermission() {
        notificationCenter.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { granted, error ->
            if (error != null) {
                println("Error requesting notification permission: ${error.localizedDescription}")
            }
        }
    }

    private fun updateNotification(timer: PomodoroTimer) {
        val formattedTime = formatTime(timer.timeLeftInSeconds)
        val sessionTypeText = when (timer.sessionType) {
            SessionType.WORK -> "🍅 Work Session"
            SessionType.SHORT_BREAK -> "☕ Short Break"
            SessionType.LONG_BREAK -> "🌴 Long Break"
        }

        val stateText = when (timer.timerState) {
            TimerState.RUNNING -> "Running"
            TimerState.PAUSED -> "Paused"
            TimerState.COMPLETED -> "Completed"
            TimerState.IDLE -> "Idle"
        }

        val content = UNMutableNotificationContent().apply {
            setTitle("$sessionTypeText - Session ${timer.sessionCount}")
            setBody("$formattedTime - $stateText")
            setSound(null)
            if (timer.timerState == TimerState.COMPLETED) {
                setSound(UNNotificationSound.defaultSound())
            }
        }

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = notificationId,
            content = content,
            trigger = null // Show immediately
        )

        notificationCenter.addNotificationRequest(request) { error ->
            error?.let {
                println("Error showing notification: ${it.localizedDescription}")
            }
        }
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return "${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    }
}
