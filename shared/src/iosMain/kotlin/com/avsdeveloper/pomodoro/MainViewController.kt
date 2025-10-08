package com.avsdeveloper.pomodoro

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.ComposeUIViewController
import com.avsdeveloper.pomodoro.presentation.timer.TimerScreen
import com.avsdeveloper.pomodoro.presentation.timer.TimerViewModel
import org.koin.compose.koinInject

fun MainViewController() = ComposeUIViewController {
    MaterialTheme {
        val viewModel: TimerViewModel = koinInject()
        TimerScreen(viewModel = viewModel)
    }
}