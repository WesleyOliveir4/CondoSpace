package com.example.condospace

import android.app.Application
import com.example.condospace.di.dataModule
import com.example.condospace.di.login.loginModule
import com.example.condospace.di.register.registerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CondoSpaceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CondoSpaceApp)
            modules(
                dataModule,
                loginModule,
                registerModule
            )
        }
    }
}
