package com.milk.smartvpn.net.host

import com.milk.smartvpn.BuildConfig

interface ApiHost {
    val realUrl: String
        get() = if (BuildConfig.DEBUG) debugUrl() else releaseUrl()

    fun releaseUrl(): String
    fun debugUrl(): String
}