package com.milk.smartvpn.ui.vm

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.milk.simple.ktx.ioScope
import com.milk.smartvpn.ad.AdConfig
import com.milk.smartvpn.ad.TopOnManager
import com.milk.smartvpn.constant.AdCodeKey
import com.milk.smartvpn.friebase.FireBaseManager
import com.milk.smartvpn.friebase.FirebaseKey
import com.milk.smartvpn.repository.DataRepository
import com.milk.smartvpn.util.MilkTimer

class ResultViewModel : ViewModel() {
    private var successMilkTimer: MilkTimer? = null
    private var failureMilkTimer: MilkTimer? = null

    internal fun loadNativeSuccess(activity: FragmentActivity) {
        successMilkTimer = MilkTimer.Builder()
            .setMillisInFuture(30000)
            .setCountDownInterval(1000)
            .setOnFinishedListener {
                loadConnectedNativeAd(activity)
                loadNativeSuccess(activity)
            }.build()
        successMilkTimer?.start()
    }

    private fun loadConnectedNativeAd(activity: FragmentActivity) {
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.CONNECT_SUCCESS_RESULT)
        if (unitId.isNotBlank()) {
            TopOnManager.loadNativeAd(
                activity = activity,
                adUnitId = unitId,
                loadFailureRequest = {
                    // 加载失败、原因和理由
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_FAILED, unitId, it)
                },
                loadSuccessRequest = {
                    // 加载成功原因和理由
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED, unitId, unitId)
                    ioScope {
                        DataRepository.connectSuccessAd.emit(Pair(unitId, it?.nativeAd))
                    }
                })
        }
    }

    internal fun loadNativeFailure(activity: FragmentActivity) {
        failureMilkTimer = MilkTimer.Builder()
            .setMillisInFuture(30000)
            .setCountDownInterval(1000)
            .setOnFinishedListener {
                loadDisconnectNativeAd(activity)
                loadNativeFailure(activity)
            }.build()
        failureMilkTimer?.start()
    }

    private fun loadDisconnectNativeAd(activity: FragmentActivity) {
        val currentNativeAd =
            DataRepository.disconnectAd.value.second
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.DISCONNECT_SUCCESS_RESULT)
        if (unitId.isNotBlank() && currentNativeAd == null) {
            TopOnManager.loadNativeAd(
                activity = activity,
                adUnitId = unitId,
                loadFailureRequest = {
                    // 加载失败、原因和理由
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_FAILED, unitId, it)
                },
                loadSuccessRequest = {
                    // 加载成功原因和理由
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED, unitId, unitId)
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