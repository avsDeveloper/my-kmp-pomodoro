package com.avsdeveloper.pomodoro.android

import android.app.Application
import com.avsdeveloper.pomodoro.android.service.AndroidPomodoroService
import com.avsdeveloper.pomodoro.di.appModule
import com.avsdeveloper.pomodoro.platform.PomodoroService
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class PomodoroApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val androidModule = module {
            single<PomodoroService> { AndroidPomodoroService(androidContext()) }
        }

        startKoin {
            androidContext(this@PomodoroApplication)
            modules(appModule, androidModule)
        }
    }
}