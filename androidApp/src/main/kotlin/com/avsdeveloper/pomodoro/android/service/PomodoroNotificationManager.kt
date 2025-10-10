package com.avsdeveloper.pomodoro.android.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.avsdeveloper.pomodoro.android.MainActivity
import com.avsdeveloper.pomodoro.domain.model.PomodoroTimer
import com.avsdeveloper.pomodoro.domain.model.SessionType
import com.avsdeveloper.pomodoro.domain.model.TimerState

object PomodoroNotificationManager {
    private const val CHANNEL_ID = "pomodoro_timer_channel"
    private const val NOTIFICATION_ID = 1
    private const val TAG = "PomodoroNotification"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pomodoro Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows the current Pomodoro timer status"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }

    fun showNotification(context: Context, timer: PomodoroTimer) {
        Log.d(TAG, "showNotification called - State: ${timer.timerState}, Time: ${timer.timeLeftInSeconds}")

        createNotificationChannel(context)

        val notification = buildNotification(context, timer)
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)

        Log.d(TAG, "Notification posted with ID: $NOTIFICATION_ID")
    }

    fun buildNotification(context: Context, timer: PomodoroTimer): android.app.Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

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

        Log.d(TAG, "Building notification: $sessionTypeText - $formattedTime - $stateText")

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("$sessionTypeText - Session ${timer.sessionCount}")
            .setContentText("$formattedTime - $stateText")
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentIntent(pendingIntent)
            .setOngoing(timer.timerState == TimerState.RUNNING)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setAutoCancel(false)
            .build()
    }

    fun cancelNotification(context: Context) {
        Log.d(TAG, "cancelNotification called")
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return "${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    }
}
