package com.avsdeveloper.pomodoro

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.ComposeUIViewController
import com.avsdeveloper.pomodoro.presentation.timer.TimerScreen
import com.avsdeveloper.pomodoro.presentation.timer.TimerViewModel
import platform.UIKit.UIViewController

fun MainViewController(viewModel: TimerViewModel): UIViewController {
    return ComposeUIViewController {
        TimerScreen(
            viewModel = viewModel,
            onClose = null // iOS apps don't typically have close buttons
        )
    }
}