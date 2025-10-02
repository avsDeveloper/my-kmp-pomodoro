package com.avsdeveloper.pomodoro.presentation.timer

import com.avsdeveloper.pomodoro.domain.model.PomodoroTimer

data class TimerViewState(
    val timer: PomodoroTimer,
    val formattedTime: String,
    val isLoading: Boolean = false
)