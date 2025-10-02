package com.avsdeveloper.pomodoro.domain.repository

import com.avsdeveloper.pomodoro.domain.model.PomodoroTimer
import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    fun observeTimer(): Flow<PomodoroTimer>
    suspend fun startTimer()
    suspend fun pauseTimer()
    suspend fun resetTimer()
    suspend fun completeSession()
    suspend fun tick()
}