package com.milk.global.ui.vm

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.milk.simple.ktx.ioScope
import com.milk.global.ad.AdConfig
import com.milk.global.ad.TopOnManager
import com.milk.global.constant.AdCodeKey
import com.milk.global.friebase.FireBaseManager
import com.milk.global.friebase.FirebaseKey
import com.milk.global.repository.DataRepository
import com.milk.global.util.MilkTimer

class ResultViewModel : ViewModel() {
    private var successMilkTimer: MilkTimer? = null
    private var failureMilkTimer: MilkTimer? = null

    internal fun loadNativeSuccess(activity: FragmentActivity) {
        successMilkTimer = MilkTimer.Builder()
            .setMillisInFuture(AdConfig.adRefreshTime.toLong())
            .setCountDownInterval(1000)
            .setOnFinishedListener {
                loadConnectedNativeAd(activity)
                loadNativeSuccess(activity)
            }.build()
        successMilkTimer?.start()
    }

    private fun loadConnectedNativeAd(activity: FragmentActivity) {
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.OTHER_NATIVE_AD_KEY)
        FireBaseManager.logEvent(FirebaseKey.Make_an_ad_request_5)
        if (unitId.isNotBlank()) {
            TopOnManager.loadNativeAd(
                activity = activity,
                adUnitId = unitId,
                loadFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_failed_5)
                },
                loadSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_succeeded_5)
                    ioScope {
                        DataRepository.connectSuccessAd.emit(Pair(unitId, it?.nativeAd))
                    }
                })
        }
    }

    internal fun loadNativeFailure(activity: FragmentActivity) {
        failureMilkTimer = MilkTimer.Builder()
            .setMillisInFuture(30 * 60 * 1000)
            .setCountDownInterval(1000)
            .setOnFinishedListener {
                loadDisconnectNativeAd(activity)
                loadNativeFailure(activity)
            }.build()
        failureMilkTimer?.start()
    }

    private fun loadDisconnectNativeAd(activity: FragmentActivity) {
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.OTHER_NATIVE_AD_KEY)
        if (unitId.isNotBlank()) {
            FireBaseManager.logEvent(FirebaseKey.Make_an_ad_request_6)
            TopOnManager.loadNativeAd(
                activity = activity,
                adUnitId = unitId,
                loadFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_failed_6, it)
                },
                loadSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_succeeded_6)
                    ioScope {
                        DataRepository.disconnectAd.emit(Pair(unitId, it?.nativeAd))
                    }
                })
        }
    }

    internal fun destroy() {
        successMilkTimer?.destroy()
        failureMilkTimer?.destroy()
    }
}