package com.avsdeveloper.pomodoro.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.avsdeveloper.pomodoro.presentation.timer.TimerScreen
import com.avsdeveloper.pomodoro.presentation.timer.TimerViewModel
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val timerViewModel: TimerViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TimerScreen(viewModel = timerViewModel)
            }
        }
    }
}