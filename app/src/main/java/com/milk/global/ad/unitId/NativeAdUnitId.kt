package com.milk.global.ad.unitId

import com.milk.global.BuildConfig

class NativeAdUnitId : AdUnitId {
    override fun debug(): String {
        return "ca-app-pub-3940256099942544/2247696110"
    }

    override fun release(): String {
        return ""
    }

    companion object {
        val value = if (BuildConfig.DEBUG) {
            NativeAdUnitId().debug()
        } else {
            NativeAdUnitId().release()
        }
    }
}