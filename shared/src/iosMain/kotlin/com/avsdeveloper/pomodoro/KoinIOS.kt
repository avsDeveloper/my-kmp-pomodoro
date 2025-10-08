package com.avsdeveloper.pomodoro

import com.avsdeveloper.pomodoro.di.appModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun doInitKoin() = startKoin {
    modules(
        appModule,
        module {
            // No-op for iOS, but ensures the module is present
        }
    )
}
