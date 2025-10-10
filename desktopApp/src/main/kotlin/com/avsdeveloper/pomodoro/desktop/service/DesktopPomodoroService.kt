package com.avsdeveloper.pomodoro.desktop.service

import com.avsdeveloper.pomodoro.domain.model.PomodoroTimer
import com.avsdeveloper.pomodoro.domain.model.SessionType
import com.avsdeveloper.pomodoro.domain.model.TimerState
import com.avsdeveloper.pomodoro.platform.PomodoroService
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage

class DesktopPomodoroService : PomodoroService {
    private var trayIcon: TrayIcon? = null
    private val systemTray = if (SystemTray.isSupported()) SystemTray.getSystemTray() else null

    override fun startService(timer: PomodoroTimer) {
        updateNotification(timer)
    }

    override fun updateService(timer: PomodoroTimer) {
        updateNotification(timer)
    }

    override fun stopService() {
        trayIcon?.let { icon ->
            systemTray?.remove(icon)
            trayIcon = null
        }
    }

    private fun updateNotification(timer: PomodoroTimer) {
        if (systemTray == null) return

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

        val tooltip = "$sessionTypeText - Session ${timer.sessionCount}\n$formattedTime - $stateText"

        if (trayIcon == null) {
            val image = createTrayIcon(timer)
            trayIcon = TrayIcon(image, tooltip).apply {
                isImageAutoSize = true
            }
            try {
                systemTray.add(trayIcon)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            trayIcon?.apply {
                this.image = createTrayIcon(timer)
                this.toolTip = tooltip
            }
        }
    }

    private fun createTrayIcon(timer: PomodoroTimer): Image {
        val size = 16
        val image = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Set color based on session type and state
        val color = when {
            timer.timerState == TimerState.RUNNING && timer.sessionType == SessionType.WORK -> Color.RED
            timer.timerState == TimerState.RUNNING && timer.sessionType == SessionType.SHORT_BREAK -> Color.ORANGE
            timer.timerState == TimerState.RUNNING && timer.sessionType == SessionType.LONG_BREAK -> Color.GREEN
            timer.timerState == TimerState.PAUSED -> Color.YELLOW
            else -> Color.GRAY
        }

        g2d.color = color
        g2d.fillOval(2, 2, size - 4, size - 4)

        g2d.dispose()

        return image
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return "${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    }
}
