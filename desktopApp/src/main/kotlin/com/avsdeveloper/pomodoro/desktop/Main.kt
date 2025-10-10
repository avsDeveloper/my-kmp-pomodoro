package com.avsdeveloper.pomodoro.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.avsdeveloper.pomodoro.desktop.service.DesktopPomodoroService
import com.avsdeveloper.pomodoro.di.appModule
import com.avsdeveloper.pomodoro.platform.PomodoroService
import com.avsdeveloper.pomodoro.presentation.timer.TimerScreen
import com.avsdeveloper.pomodoro.presentation.timer.TimerViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import java.awt.Window as AwtWindow

fun main() = application {
    val windowState = rememberWindowState()

    startKoin {
        val desktopModule = module {
            single<PomodoroService> {
                DesktopPomodoroService(
                    onTrayIconClick = {
                        // Bring the window to front when tray icon is clicked
                        AwtWindow.getWindows().forEach { window ->
                            if (window.isVisible) {
                                window.toFront()
                                window.requestFocus()
                            }
                        }
                    }
                )
            }
        }
        modules(appModule, desktopModule)
    }

    val timerViewModel: TimerViewModel by inject(TimerViewModel::class.java)

    Window(
        onCloseRequest = ::exitApplication,
        title = "Pomodoro Timer",
        state = windowState
    ) {
        TimerScreen(
            viewModel = timerViewModel,
            onClose = ::exitApplication
        )
    }
}