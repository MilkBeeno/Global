package com.milk.global

import android.app.Application
import com.milk.simple.ktx.ioScope
import com.milk.simple.log.Logger
import com.milk.simple.mdr.KvManger
import com.milk.global.ad.TopOnManager
import com.milk.global.friebase.FireBaseManager
import com.milk.global.media.LoaderConfig
import com.milk.global.ui.act.BackStackActivity
import com.milk.global.ui.act.LaunchActivity
import com.milk.global.util.BackStack

class BaseApplication : Application() {
    companion object {
        lateinit var current: Application
    }

    override fun onCreate() {
        super.onCreate()
        current = this
        initialize()
    }

    private fun initialize() {
        ioScope {
            BackStack.backToForegroundMonitor(current) {
                if (it !is LaunchActivity && it !is BackStackActivity)
                    BackStackActivity.create(current)
            }
            Logger.initialize(BuildConfig.DEBUG)
            KvManger.initialize(current)
            LoaderConfig.initialize(current)
            TopOnManager.initialize(current)
            FireBaseManager.initialize(current)
        }
    }
}