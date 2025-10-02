package com.avsdeveloper.pomodoro.android

import android.app.Application
import com.avsdeveloper.pomodoro.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PomodoroApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@PomodoroApplication)
            modules(appModule)
        }
    }
}