package com.milk.smartvpn.ad

import com.milk.smartvpn.BuildConfig
import com.milk.smartvpn.constant.AdCodeKey

object AdConfig {
    private val releasePositionMap = mutableMapOf<String, String>()
    private val debugPositionMap = mutableMapOf<String, String>()

    internal var adRefreshTime: Int = 10

    internal fun initialize() {
        // 正式服
        releasePositionMap[AdCodeKey.INTERSTITIAL_AD_KEY] =
            "ca-app-pub-7382266762101400/9900518741"
        releasePositionMap[AdCodeKey.VPN_LIST_NATIVE_AD_KEY] =
            "a-app-pub-7382266762101400/8555647487"
        releasePositionMap[AdCodeKey.LAUNCH_OPEN_AD_KEY] =
            "ca-app-pub-7382266762101400/9062419106"
        releasePositionMap[AdCodeKey.OTHER_NATIVE_AD_KEY] =
            "ca-app-pub-7382266762101400/5905433417"
        releasePositionMap[AdCodeKey.MAIN_NATIVE_AD_KEY] =
            "ca-app-pub-7382266762101400/5917381010"
        // 测试服
        debugPositionMap[AdCodeKey.INTERSTITIAL_AD_KEY] =
            "b62b03b9a8bff9"
        debugPositionMap[AdCodeKey.VPN_LIST_NATIVE_AD_KEY] =
            "b62b03e2b6c205"
        debugPositionMap[AdCodeKey.LAUNCH_OPEN_AD_KEY] =
            "b62b028dcba917"
        debugPositionMap[AdCodeKey.OTHER_NATIVE_AD_KEY] =
            "b62b03e2b6c205"
        debugPositionMap[AdCodeKey.MAIN_NATIVE_AD_KEY] =
            "b62b03e2b6c205"
    }

    fun getAdvertiseUnitId(key: String): String {
        val advertiseUnitId = if (BuildConfig.DEBUG) {
            debugPositionMap[key]
        } else {
            releasePositionMap[key]
        }
        return advertiseUnitId.toString()
    }
}