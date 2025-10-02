package com.avsdeveloper.pomodoro.presentation.timer

import com.avsdeveloper.pomodoro.domain.model.TimerState
import com.avsdeveloper.pomodoro.domain.repository.TimerRepository
import com.avsdeveloper.pomodoro.domain.usecase.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TimerViewModel(
    private val startTimerUseCase: StartTimerUseCase,
    private val pauseTimerUseCase: PauseTimerUseCase,
    private val resetTimerUseCase: ResetTimerUseCase,
    private val completeSessionUseCase: CompleteSessionUseCase,
    private val timerRepository: TimerRepository
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    private var tickJob: Job? = null

    private val _state = MutableStateFlow(
        TimerViewState(
            timer = com.avsdeveloper.pomodoro.domain.model.PomodoroTimer(
                timeLeftInSeconds = 1500,
                sessionType = com.avsdeveloper.pomodoro.domain.model.SessionType.WORK,
                sessionCount = 0,
                timerState = TimerState.IDLE
            ),
            formattedTime = "25:00"
        )
    )
    val state: StateFlow<TimerViewState> = _state.asStateFlow()

    init {
        observeTimer()
    }

    private fun observeTimer() {
        viewModelScope.launch {
            timerRepository.observeTimer().collectLatest { timer ->
                _state.value = TimerViewState(
                    timer = timer,
                    formattedTime = formatTime(timer.timeLeftInSeconds)
                )

                if (timer.timerState == TimerState.RUNNING) {
                    startTicking()
                } else {
                    stopTicking()
                }
            }
        }
    }

    fun handleIntent(intent: TimerIntent) {
        viewModelScope.launch {
            when (intent) {
                is TimerIntent.StartTimer -> startTimerUseCase()
                is TimerIntent.PauseTimer -> pauseTimerUseCase()
                is TimerIntent.ResetTimer -> resetTimerUseCase()
                is TimerIntent.StartNextSession -> {
                    resetTimerUseCase()
                    startTimerUseCase()
                }
            }
        }
    }

    private fun startTicking() {
        if (tickJob?.isActive == true) return
        
        tickJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                timerRepository.tick()
            }
        }
    }

    private fun stopTicking() {
        tickJob?.cancel()
        tickJob = null
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return "%02d:%02d".format(minutes, secs)
    }

    fun onCleared() {
        stopTicking()
    }
}