package com.avsdeveloper.pomodoro

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.ComposeUIViewController
import com.avsdeveloper.pomodoro.presentation.timer.TimerScreen
import com.avsdeveloper.pomodoro.presentation.timer.TimerViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object IosKoinHelper : KoinComponent {
    fun getTimerViewModel() = get<TimerViewModel>()
}

fun MainViewController(viewModel: TimerViewModel) = ComposeUIViewController {
    MaterialTheme {
        TimerScreen(viewModel = viewModel)
    }
}