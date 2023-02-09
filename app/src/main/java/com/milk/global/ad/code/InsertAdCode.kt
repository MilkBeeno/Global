package com.milk.global.ad.code

import com.milk.global.BuildConfig

class InsertAdCode : AdCode {
    override fun debug(): String {
        return "ca-app-pub-3940256099942544/1033173712"
    }

    override fun release(): String {
        return ""
    }

    companion object {
        val value = if (BuildConfig.DEBUG) InsertAdCode().debug() else InsertAdCode().release()
    }
}