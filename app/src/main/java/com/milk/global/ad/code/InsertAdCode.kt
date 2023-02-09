package com.milk.global.ad.code

import com.milk.global.BuildConfig

class InsertAdCode : AdCode {
    override fun debug(): String {
        return ""
    }

    override fun release(): String {
        return ""
    }

    companion object {
        val value = if (BuildConfig.DEBUG) InsertAdCode().debug() else InsertAdCode().release()
    }
}