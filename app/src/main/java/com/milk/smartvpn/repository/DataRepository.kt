package com.milk.smartvpn.repository

import androidx.fragment.app.FragmentActivity
import com.anythink.nativead.api.NativeAd
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

    internal fun loadConnectedNativeAd(activity: FragmentActivity) {
        ioScope {
            val unitId =
                AdConfig.getAdvertiseUnitId(AdCodeKey.CONNECT_SUCCESS_RESULT)
            if (unitId.isNotBlank()) {
                TopOnManager.loadNativeAd(
                    activity = activity,
                    adUnitId = unitId,
                    loadFailureRequest = {
                        FireBaseManager
                            .logEvent(FirebaseKey.AD_REQUEST_FAILED, unitId, it)
                    },
                    loadSuccessRequest = {
                        FireBaseManager
                            .logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED, unitId, unitId)
                        ioScope {
                            connectSuccessAd.emit(Pair(unitId, it?.nativeAd))
                        }
                    })
            }
        }
    }

    internal fun loadDisconnectNativeAd(activity: FragmentActivity, finishRequest: () -> Unit) {
        ioScope {
            val unitId =
                AdConfig.getAdvertiseUnitId(AdCodeKey.DISCONNECT_SUCCESS_RESULT)
            if (unitId.isNotBlank()) {
                TopOnManager.loadNativeAd(
                    activity = activity,
                    adUnitId = unitId,
                    loadFailureRequest = {
                        // 加载失败、原因和理由
                        FireBaseManager
                            .logEvent(FirebaseKey.AD_REQUEST_FAILED, unitId, it)
                        finishRequest()
                    },
                    loadSuccessRequest = {
                        // 加载成功原因和理由
                        FireBaseManager
                            .logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED, unitId, unitId)
                        finishRequest()
                        ioScope {
                            DataRepository.disconnectAd.emit(Pair(unitId, it?.nativeAd))
                        }
                    })
            } else ioScope {
                delay(1500)
                finishRequest()
            }
        }
    }
}