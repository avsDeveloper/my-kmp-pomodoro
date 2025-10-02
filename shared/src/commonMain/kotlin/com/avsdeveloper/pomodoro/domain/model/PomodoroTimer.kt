package com.avsdeveloper.pomodoro.domain.model

data class PomodoroTimer(
    val timeLeftInSeconds: Long,
    val sessionType: SessionType,
    val sessionCount: Int,
    val timerState: TimerState
)

enum class SessionType(val durationInSeconds: Long) {
    WORK(25 * 60),
    SHORT_BREAK(5 * 60),
    LONG_BREAK(15 * 60)
}

enum class TimerState {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED
}