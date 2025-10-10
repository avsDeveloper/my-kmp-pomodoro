package com.avsdeveloper.pomodoro

import com.avsdeveloper.pomodoro.presentation.timer.TimerViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object IosKoinHelper : KoinComponent {
    fun getTimerViewModel(): TimerViewModel {
        // Use get() instead of lazy inject to ensure the ViewModel is properly initialized
        return get<TimerViewModel>()
    }
}
