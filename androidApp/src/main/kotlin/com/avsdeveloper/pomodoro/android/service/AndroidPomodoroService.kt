package com.avsdeveloper.pomodoro.android.service

import android.content.Context
import android.os.Build
import android.util.Log
import com.avsdeveloper.pomodoro.domain.model.PomodoroTimer
import com.avsdeveloper.pomodoro.domain.model.TimerState
import com.avsdeveloper.pomodoro.platform.PomodoroService

class AndroidPomodoroService(private val context: Context) : PomodoroService {

    override fun startService(timer: PomodoroTimer) {
        Log.d("AndroidPomodoroService", "startService called with state: ${timer.timerState}")

        // Always show the notification
        PomodoroNotificationManager.showNotification(context, timer)

        // Additionally, try to start the foreground service for better reliability
        // But don't fail if it doesn't work
        if (timer.timerState == TimerState.RUNNING) {
            tryStartForegroundService(timer)
        }
    }

    override fun updateService(timer: PomodoroTimer) {
        Log.d("AndroidPomodoroService", "updateService called with state: ${timer.timerState}")

        if (timer.timerState == TimerState.IDLE) {
            stopService()
            return
        }

        // Update the notification
        PomodoroNotificationManager.showNotification(context, timer)

        // Try to keep foreground service running if timer is running
        if (timer.timerState == TimerState.RUNNING) {
            tryStartForegroundService(timer)
        }
    }

    override fun stopService() {
        Log.d("AndroidPomodoroService", "stopService called")

        try {
            val intent = PomodoroForegroundService.createIntent(context)
            context.stopService(intent)
        } catch (e: Exception) {
            Log.e("AndroidPomodoroService", "Failed to stop service", e)
        }

        // Always cancel the notification when stopping
        PomodoroNotificationManager.cancelNotification(context)
    }

    private fun tryStartForegroundService(timer: PomodoroTimer) {
        try {
            val intent = PomodoroForegroundService.createIntent(context).apply {
                putExtra(PomodoroForegroundService.EXTRA_TIME_LEFT, timer.timeLeftInSeconds)
                putExtra(PomodoroForegroundService.EXTRA_SESSION_TYPE, timer.sessionType.ordinal)
                putExtra(PomodoroForegroundService.EXTRA_SESSION_COUNT, timer.sessionCount)
                putExtra(PomodoroForegroundService.EXTRA_TIMER_STATE, timer.timerState.ordinal)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } catch (e: Exception) {
            Log.w("AndroidPomodoroService", "Could not start foreground service (using notification instead)", e)
            // Notification is already shown above, so we're good
        }
    }
}
