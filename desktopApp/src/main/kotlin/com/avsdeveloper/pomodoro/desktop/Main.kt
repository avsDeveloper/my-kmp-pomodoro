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

fun main() = application {
    startKoin {
        val desktopModule = module {
            single<PomodoroService> { DesktopPomodoroService() }
        }
        modules(appModule, desktopModule)
    }

    val timerViewModel: TimerViewModel by inject(TimerViewModel::class.java)

    Window(
        onCloseRequest = ::exitApplication,
        title = "Pomodoro Timer",
        state = rememberWindowState()
    ) {
        TimerScreen(
            viewModel = timerViewModel,
            onClose = ::exitApplication
        )
    }
}