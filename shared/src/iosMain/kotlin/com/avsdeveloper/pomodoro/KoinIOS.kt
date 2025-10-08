package com.avsdeveloper.pomodoro

import com.avsdeveloper.pomodoro.di.appModule
import org.koin.core.context.startKoin

fun initKoin() {
    try {
        startKoin {
            modules(appModule)
        }
    } catch (e: Exception) {
        // Koin already started, ignore
    }
}

