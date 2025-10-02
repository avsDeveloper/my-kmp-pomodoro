package com.avsdeveloper.pomodoro.data.repository

import com.avsdeveloper.pomodoro.domain.model.PomodoroTimer
import com.avsdeveloper.pomodoro.domain.model.SessionType
import com.avsdeveloper.pomodoro.domain.model.TimerState
import com.avsdeveloper.pomodoro.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerRepositoryImpl : TimerRepository {
    private val _timerState = MutableStateFlow(
        PomodoroTimer(
            timeLeftInSeconds = SessionType.WORK.durationInSeconds,
            sessionType = SessionType.WORK,
            sessionCount = 0,
            timerState = TimerState.IDLE
        )
    )

    override fun observeTimer(): Flow<PomodoroTimer> = _timerState.asStateFlow()

    override suspend fun startTimer() {
        _timerState.value = _timerState.value.copy(timerState = TimerState.RUNNING)
    }

    override suspend fun pauseTimer() {
        _timerState.value = _timerState.value.copy(timerState = TimerState.PAUSED)
    }

    override suspend fun resetTimer() {
        val currentSession = _timerState.value.sessionType
        _timerState.value = PomodoroTimer(
            timeLeftInSeconds = currentSession.durationInSeconds,
            sessionType = currentSession,
            sessionCount = _timerState.value.sessionCount,
            timerState = TimerState.IDLE
        )
    }

    override suspend fun completeSession() {
        val current = _timerState.value
        val newCount = if (current.sessionType == SessionType.WORK) {
            current.sessionCount + 1
        } else {
            current.sessionCount
        }

        val nextSession = when {
            current.sessionType == SessionType.WORK && newCount % 4 == 0 -> SessionType.LONG_BREAK
            current.sessionType == SessionType.WORK -> SessionType.SHORT_BREAK
            else -> SessionType.WORK
        }

        _timerState.value = PomodoroTimer(
            timeLeftInSeconds = nextSession.durationInSeconds,
            sessionType = nextSession,
            sessionCount = newCount,
            timerState = TimerState.COMPLETED
        )
    }

    override suspend fun tick() {
        val current = _timerState.value
        if (current.timerState == TimerState.RUNNING && current.timeLeftInSeconds > 0) {
            _timerState.value = current.copy(
                timeLeftInSeconds = current.timeLeftInSeconds - 1
            )
            
            if (_timerState.value.timeLeftInSeconds == 0L) {
                completeSession()
            }
        }
    }
}