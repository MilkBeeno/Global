package com.milk.smartvpn.data.body

import com.milk.smartvpn.BuildConfig

data class VpnConfigBody(
    var appId: String = BuildConfig.AD_APP_ID,
    var channel: String = BuildConfig.AD_APP_CHANNEL,
    var pkgVersion: String = BuildConfig.AD_APP_VERSION
)