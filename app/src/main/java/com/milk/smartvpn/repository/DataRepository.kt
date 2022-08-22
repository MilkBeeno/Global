package com.milk.smartvpn.repository

import com.google.android.gms.ads.nativead.NativeAd
import com.milk.simple.ktx.ioScope
import com.milk.simple.mdr.KvManger
import com.milk.smartvpn.BuildConfig
import com.milk.smartvpn.constant.KvKey
import kotlinx.coroutines.flow.MutableStateFlow

object DataRepository {
    internal val connectSuccessAd = MutableStateFlow<Pair<String, NativeAd?>>(Pair("", null))
    internal val disconnectAd = MutableStateFlow<Pair<String, NativeAd?>>(Pair("", null))
    internal val vpnListAd = MutableStateFlow<Pair<String, NativeAd?>>(Pair("", null))
    internal val shareAppUrl = MutableStateFlow("")

    fun appConfig() {
        ioScope {
            val appUrl = KvManger.getString(KvKey.APP_SHARE_URL)
            if (appUrl.isBlank()) {
                val apiResponse =
                    AdRepository().getAppConfig(BuildConfig.AD_APP_ID)
                val apiResult = apiResponse.data
                if (apiResponse.success && apiResult != null) {
                    shareAppUrl.emit(apiResult.imgPath)
                }
            } else shareAppUrl.emit(appUrl)
        }
    }
}