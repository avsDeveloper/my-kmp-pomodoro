package com.avsdeveloper.pomodoro.di

import com.avsdeveloper.pomodoro.data.repository.TimerRepositoryImpl
import com.avsdeveloper.pomodoro.domain.repository.TimerRepository
import com.avsdeveloper.pomodoro.domain.usecase.*
import com.avsdeveloper.pomodoro.presentation.timer.TimerViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    // Repository
    single<TimerRepository> { TimerRepositoryImpl() }
    
    // Use Cases
    singleOf(::StartTimerUseCase)
    singleOf(::PauseTimerUseCase)
    singleOf(::ResetTimerUseCase)
    singleOf(::CompleteSessionUseCase)
    
    // ViewModel
    single {
        TimerViewModel(
            startTimerUseCase = get(),
            pauseTimerUseCase = get(),
            resetTimerUseCase = get(),
            completeSessionUseCase = get(),
            timerRepository = get()
        )
    }
}