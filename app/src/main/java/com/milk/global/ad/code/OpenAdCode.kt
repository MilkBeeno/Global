package com.milk.global.ad.code

import com.milk.global.BuildConfig

class OpenAdCode : AdCode {
    override fun debug(): String {
        return "ca-app-pub-3940256099942544/3419835294"
    }

    override fun release(): String {
        return ""
    }

    companion object {
        val value = if (BuildConfig.DEBUG) OpenAdCode().debug() else OpenAdCode().release()
    }
}