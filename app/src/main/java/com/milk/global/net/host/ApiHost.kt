package com.milk.global.net.host

import com.milk.global.BuildConfig

interface ApiHost {
    val realUrl: String
        get() = if (BuildConfig.DEBUG) debugUrl() else releaseUrl()

    fun releaseUrl(): String
    fun debugUrl(): String
}