package com.milk.smartvpn

import android.app.Application

class BaseApplication : Application() {
    companion object {
        lateinit var current: Application
    }

    override fun onCreate() {
        super.onCreate()
        current = this
    }
}