package com.avsdeveloper.pomodoro.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.avsdeveloper.pomodoro.di.appModule
import com.avsdeveloper.pomodoro.presentation.timer.TimerScreen
import org.koin.core.context.startKoin

fun main() = application {
    startKoin {
        modules(appModule)
    }
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "Pomodoro Timer",
        state = rememberWindowState()
    ) {
        TimerScreen()
    }
}