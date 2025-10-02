package com.avsdeveloper.pomodoro.presentation.timer

sealed interface TimerIntent {
    data object StartTimer : TimerIntent
    data object PauseTimer : TimerIntent
    data object ResetTimer : TimerIntent
    data object StartNextSession : TimerIntent
}