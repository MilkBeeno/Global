package com.milk.global.ad.unitId

import com.milk.global.BuildConfig

class InterstitialAdUnitId : AdUnitId {
    override fun debug(): String {
        return "ca-app-pub-3940256099942544/1033173712"
    }

    override fun release(): String {
        return "ca-app-pub-9835209825468303/3490705171"
    }

    companion object {
        val value = if (BuildConfig.DEBUG) {
            InterstitialAdUnitId().debug()
        } else {
            InterstitialAdUnitId().release()
        }
    }
}