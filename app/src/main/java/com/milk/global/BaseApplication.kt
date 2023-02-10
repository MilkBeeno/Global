package com.milk.global

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.milk.global.ad.TopOnManager
import com.milk.global.friebase.FireBaseManager
import com.milk.global.media.LoaderConfig
import com.milk.global.ui.act.BackStackActivity
import com.milk.global.ui.act.LaunchActivity
import com.milk.global.util.BackStack
import com.milk.simple.ktx.ioScope
import com.milk.simple.log.Logger
import com.milk.simple.mdr.KvManger

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
        initializeAdmob(current)
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

    private fun initializeAdmob(context: Context) {
        MobileAds.initialize(context) {
            if (BuildConfig.DEBUG) {
                val testDeviceNumbers =
                    listOf("c1aadd83-3bcd-474d-aac2-cd1e2e83ef2a")
                MobileAds.setRequestConfiguration(
                    RequestConfiguration
                        .Builder()
                        .setTestDeviceIds(testDeviceNumbers)
                        .build()
                )
            }
        }
    }
}