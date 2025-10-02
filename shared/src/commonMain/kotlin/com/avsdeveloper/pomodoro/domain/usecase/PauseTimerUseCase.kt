package com.avsdeveloper.pomodoro.domain.usecase

import com.avsdeveloper.pomodoro.domain.repository.TimerRepository

class PauseTimerUseCase(private val repository: TimerRepository) {
    suspend operator fun invoke() {
        repository.pauseTimer()
    }
}