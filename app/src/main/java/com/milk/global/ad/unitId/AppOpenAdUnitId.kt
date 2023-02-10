package com.milk.global.ad.unitId

import com.milk.global.BuildConfig

class AppOpenAdUnitId : AdUnitId {
    override fun debug(): String {
        return "ca-app-pub-3940256099942544/3419835294"
    }

    override fun release(): String {
        return ""
    }

    companion object {
        val value = if (BuildConfig.DEBUG) {
            AppOpenAdUnitId().debug()
        } else {
            AppOpenAdUnitId().release()
        }
    }
}