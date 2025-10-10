package com.avsdeveloper.pomodoro.desktop.service

import com.avsdeveloper.pomodoro.domain.model.PomodoroTimer
import com.avsdeveloper.pomodoro.domain.model.SessionType
import com.avsdeveloper.pomodoro.domain.model.TimerState
import com.avsdeveloper.pomodoro.platform.PomodoroService
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.Color
import java.awt.Image
import java.awt.RenderingHints
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage

class DesktopPomodoroService(private val onTrayIconClick: () -> Unit) : PomodoroService {
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

                // Add click listener to bring window to front
                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        if (e.button == MouseEvent.BUTTON1) { // Left click
                            onTrayIconClick()
                        }
                    }
                })
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
        val size = 64  // Size for better text rendering
        val image = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        // Set color for tomato based on session type and state
        val tomatoColor = when {
            timer.timerState == TimerState.RUNNING && timer.sessionType == SessionType.WORK -> Color.RED
            timer.timerState == TimerState.RUNNING && timer.sessionType == SessionType.SHORT_BREAK -> Color.ORANGE
            timer.timerState == TimerState.RUNNING && timer.sessionType == SessionType.LONG_BREAK -> Color.GREEN
            timer.timerState == TimerState.PAUSED -> Color(255, 200, 0) // More saturated yellow
            else -> Color.GRAY
        }

        // Draw tomato shape - position it higher to create space for text
        val tomatoSize = 38  // Slightly smaller to make room
        val tomatoX = (size - tomatoSize) / 2
        val tomatoY = 0  // Move to the very top

        // Draw tomato body
        g2d.color = tomatoColor
        g2d.fillOval(tomatoX, tomatoY + 6, tomatoSize, tomatoSize - 6)

        // Draw tomato stem (green top)
        g2d.color = Color(34, 139, 34) // Forest green
        val stemPoints = intArrayOf(
            tomatoX + tomatoSize / 2 - 4, tomatoY + 6,
            tomatoX + tomatoSize / 2 - 2, tomatoY,
            tomatoX + tomatoSize / 2 + 2, tomatoY,
            tomatoX + tomatoSize / 2 + 4, tomatoY + 6
        )
        g2d.fillPolygon(
            intArrayOf(stemPoints[0], stemPoints[2], stemPoints[4], stemPoints[6]),
            intArrayOf(stemPoints[1], stemPoints[3], stemPoints[5], stemPoints[7]),
            4
        )

        // Add highlight to make it look more 3D
        g2d.color = Color(255, 255, 255, 100)
        g2d.fillOval(tomatoX + 7, tomatoY + 9, 11, 11)

        // Draw timer text at the bottom with more space from tomato
        val formattedTime = formatTime(timer.timeLeftInSeconds)
        g2d.color = Color.BLACK
        g2d.font = java.awt.Font("Monospaced", java.awt.Font.BOLD, 22)

        val fontMetrics = g2d.fontMetrics
        val textWidth = fontMetrics.stringWidth(formattedTime)

        // Center the text horizontally and place it at the bottom with more spacing
        val x = (size - textWidth) / 2
        val y = size - 4  // Closer to the bottom edge

        // Draw white outline for better visibility
        g2d.color = Color.WHITE
        for (dx in -1..1) {
            for (dy in -1..1) {
                if (dx != 0 || dy != 0) {
                    g2d.drawString(formattedTime, x + dx, y + dy)
                }
            }
        }

        // Draw black text on top
        g2d.color = Color.BLACK
        g2d.drawString(formattedTime, x, y)

        g2d.dispose()

        return image
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return "${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    }
}
