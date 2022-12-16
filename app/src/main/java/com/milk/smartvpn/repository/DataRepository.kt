package com.milk.smartvpn.repository

import androidx.fragment.app.FragmentActivity
import com.anythink.nativead.api.NativeAd
import com.freetech.vpn.utils.VpnWhiteList
import com.milk.simple.ktx.ioScope
import com.milk.simple.mdr.KvManger
import com.milk.smartvpn.BuildConfig
import com.milk.smartvpn.ad.AdConfig
import com.milk.smartvpn.ad.TopOnManager
import com.milk.smartvpn.constant.AdCodeKey
import com.milk.smartvpn.constant.KvKey
import com.milk.smartvpn.friebase.FireBaseManager
import com.milk.smartvpn.friebase.FirebaseKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

object DataRepository {
    internal val connectSuccessAd = MutableStateFlow<Pair<String, NativeAd?>>(Pair("", null))
    internal val disconnectAd = MutableStateFlow<Pair<String, NativeAd?>>(Pair("", null))
    internal val vpnListAd = MutableStateFlow<Pair<String, NativeAd?>>(Pair("", null))
    internal val shareAppUrl = MutableStateFlow("")
    private val adRepository by lazy { AdRepository() }

    fun appConfig() {
        ioScope {
            val apiResponse = adRepository.getVpnConfig()
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                AdConfig.adRefreshTime = apiResult.yuanshua_hao
                val random = Math.random() * 100
                if (apiResult.isjie_vp == 0) {
                    VpnWhiteList.addCloseList("com.google.android.gms")
                    VpnWhiteList.addCloseList("com.android.vending")
                } else {
                    if (random <= apiResult.isjie_vp) {
                        VpnWhiteList.addCloseList("com.google.android.gms")
                        VpnWhiteList.addCloseList("com.android.vending")
                    }
                }
            }
        }
        ioScope {
            val appUrl = KvManger.getString(KvKey.APP_SHARE_URL)
            if (appUrl.isBlank()) {
                val apiResponse = adRepository.getAppConfig(BuildConfig.AD_APP_ID)
                val apiResult = apiResponse.data
                if (apiResponse.success && apiResult != null) {
                    shareAppUrl.emit(apiResult.imgPath)
                }
            } else shareAppUrl.emit(appUrl)
        }
    }

    internal fun loadConnectedNativeAd(activity: FragmentActivity) {
        ioScope {
            val unitId =
                AdConfig.getAdvertiseUnitId(AdCodeKey.OTHER_NATIVE_AD_KEY)
            FireBaseManager.logEvent(FirebaseKey.Make_an_ad_request_5)
            if (unitId.isNotBlank()) {
                TopOnManager.loadNativeAd(
                    activity = activity,
                    adUnitId = unitId,
                    loadFailureRequest = {
                        FireBaseManager.logEvent(FirebaseKey.Ad_request_failed_5, it)
                    },
                    loadSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.Ad_request_succeeded_5)
                        ioScope { connectSuccessAd.emit(Pair(unitId, it?.nativeAd)) }
                    })
            }
        }
    }

    internal fun loadDisconnectNativeAd(activity: FragmentActivity, finishRequest: () -> Unit) {
        ioScope {
            val unitId =
                AdConfig.getAdvertiseUnitId(AdCodeKey.OTHER_NATIVE_AD_KEY)
            if (unitId.isNotBlank()) {
                FireBaseManager.logEvent(FirebaseKey.Make_an_ad_request_6)
                TopOnManager.loadNativeAd(
                    activity = activity,
                    adUnitId = unitId,
                    loadFailureRequest = {
                        FireBaseManager.logEvent(FirebaseKey.Ad_request_failed_6, it)
                        finishRequest()
                    },
                    loadSuccessRequest = {
                        FireBaseManager.logEvent(FirebaseKey.Ad_request_succeeded_6)
                        finishRequest()
                        ioScope { disconnectAd.emit(Pair(unitId, it?.nativeAd)) }
                    })
            } else ioScope {
                delay(1500)
                finishRequest()
            }
        }
    }
}