package com.avsdeveloper.pomodoro

import com.avsdeveloper.pomodoro.di.appModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}