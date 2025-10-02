package com.avsdeveloper.pomodoro

import androidx.compose.ui.window.ComposeUIViewController
import com.avsdeveloper.pomodoro.presentation.timer.TimerScreen

fun MainViewController() = ComposeUIViewController {
    TimerScreen()
}