package com.milk.global.ad.code

import com.milk.global.BuildConfig

class NativeAdCode : AdCode {
    override fun debug(): String {
        return "ca-app-pub-3940256099942544/2247696110"
    }

    override fun release(): String {
        return ""
    }

    companion object {
        val value = if (BuildConfig.DEBUG) NativeAdCode().debug() else NativeAdCode().release()
    }
}