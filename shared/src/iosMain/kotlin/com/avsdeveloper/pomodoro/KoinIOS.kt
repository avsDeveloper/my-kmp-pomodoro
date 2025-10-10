package com.avsdeveloper.pomodoro

import com.avsdeveloper.pomodoro.di.appModule
import com.avsdeveloper.pomodoro.ios.service.IOSPomodoroService
import com.avsdeveloper.pomodoro.platform.PomodoroService
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin() {
    val iosModule = module {
        single<PomodoroService> { IOSPomodoroService() }
    }

    startKoin {
        modules(appModule, iosModule)
    }
}
