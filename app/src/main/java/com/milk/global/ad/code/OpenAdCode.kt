package com.milk.global.ad.code

import com.milk.global.BuildConfig

class OpenAdCode : AdCode {
    override fun debug(): String {
        return ""
    }

    override fun release(): String {
        return ""
    }

    companion object {
        val value = if (BuildConfig.DEBUG) OpenAdCode().debug() else OpenAdCode().release()
    }
}