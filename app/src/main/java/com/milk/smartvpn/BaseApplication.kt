package com.milk.smartvpn

import android.app.Application
import com.milk.simple.ktx.ioScope
import com.milk.simple.log.Logger
import com.milk.simple.mdr.KvManger
import com.milk.smartvpn.ad.TopOnManager
import com.milk.smartvpn.friebase.FireBaseManager
import com.milk.smartvpn.media.LoaderConfig
import com.milk.smartvpn.ui.act.BackStackActivity
import com.milk.smartvpn.ui.act.LaunchActivity
import com.milk.smartvpn.util.BackStack

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