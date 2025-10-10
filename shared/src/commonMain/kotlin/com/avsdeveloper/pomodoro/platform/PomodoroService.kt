package com.avsdeveloper.pomodoro.platform

import com.avsdeveloper.pomodoro.domain.model.PomodoroTimer

interface PomodoroService {
    fun startService(timer: PomodoroTimer)
    fun updateService(timer: PomodoroTimer)
    fun stopService()
}

