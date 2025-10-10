package com.avsdeveloper.pomodoro.android.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.avsdeveloper.pomodoro.domain.model.SessionType
import com.avsdeveloper.pomodoro.domain.model.TimerState

class PomodoroForegroundService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1
        const val EXTRA_TIME_LEFT = "time_left"
        const val EXTRA_SESSION_TYPE = "session_type"
        const val EXTRA_SESSION_COUNT = "session_count"
        const val EXTRA_TIMER_STATE = "timer_state"

        fun createIntent(context: Context): Intent {
            return Intent(context, PomodoroForegroundService::class.java)
        }
    }

    override fun onCreate() {
        super.onCreate()
        PomodoroNotificationManager.createNotificationChannel(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            intent?.let {
                val timeLeft = it.getLongExtra(EXTRA_TIME_LEFT, 1500)
                val sessionTypeOrdinal = it.getIntExtra(EXTRA_SESSION_TYPE, 0)
                val sessionCount = it.getIntExtra(EXTRA_SESSION_COUNT, 0)
                val timerStateOrdinal = it.getIntExtra(EXTRA_TIMER_STATE, 0)

                val sessionType = SessionType.entries[sessionTypeOrdinal]
                val timerState = TimerState.entries[timerStateOrdinal]

                val timer = com.avsdeveloper.pomodoro.domain.model.PomodoroTimer(
                    timeLeftInSeconds = timeLeft,
                    sessionType = sessionType,
                    sessionCount = sessionCount,
                    timerState = timerState
                )

                val notification = PomodoroNotificationManager.buildNotification(this, timer)

                try {
                    startForeground(NOTIFICATION_ID, notification)
                } catch (e: Exception) {
                    Log.e("PomodoroForegroundService", "Failed to start foreground", e)
                    // If we can't start as foreground, stop the service
                    stopSelf()
                }
            }
        } catch (e: Exception) {
            Log.e("PomodoroForegroundService", "Error in onStartCommand", e)
            stopSelf()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }
}
