package com.milk.smartvpn.ui.vm

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.anythink.interstitial.api.ATInterstitial
import com.milk.smartvpn.ad.AdConfig
import com.milk.smartvpn.ad.AdLoadStatus
import com.milk.smartvpn.ad.AdManager
import com.milk.smartvpn.ad.TopOnManager
import com.milk.smartvpn.constant.AdCodeKey
import com.milk.smartvpn.friebase.FireBaseManager
import com.milk.smartvpn.friebase.FirebaseKey
import com.milk.smartvpn.util.MilkTimer

class BackStackViewModel : ViewModel() {
    private var adLoadStatus: AdLoadStatus = AdLoadStatus.Loading

    /** 第一次打开 app 进入程序时候启动打开插页广告 */
    internal fun loadLaunchAd(activity: FragmentActivity, finishRequest: () -> Unit) {
        var aTInterstitial: ATInterstitial? = null
        val unitId = AdConfig.getAdvertiseUnitId(AdCodeKey.APP_LAUNCH)
        val timer = MilkTimer.Builder()
            .setMillisInFuture(10000)
            .setOnFinishedListener {
                if (adLoadStatus == AdLoadStatus.Success)
                    aTInterstitial?.show(activity)
                else
                    finishRequest()
            }
            .build()
        timer.start()
        if (unitId.isNotBlank()) {
            adLoadStatus = AdLoadStatus.Loading
            aTInterstitial = TopOnManager.loadInterstitial(
                activity = activity,
                adUnitId = unitId,
                loadFailureRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_FAILED, unitId, it)
                    adLoadStatus = AdLoadStatus.Failure
                },
                loadSuccessRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED, unitId, unitId)
                    adLoadStatus = AdLoadStatus.Success
                    timer.finish()
                },
                showFailureRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_SHOW_FAILED, unitId, it)
                },
                showSuccessRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS, unitId, unitId)
                },
                finishedRequest = {
                    adLoadStatus = AdLoadStatus.Loading
                    finishRequest()
                },
                clickRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.CLICK_AD, unitId, unitId)
                })
        }
    }

    internal fun loadBackStackAd(activity: FragmentActivity, finishRequest: (String) -> Unit) {
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.BACK_STACK)
        val timer = MilkTimer.Builder()
            .setMillisInFuture(10000)
            .setOnFinishedListener { finishRequest(unitId) }
            .build()
        timer.start()
        if (unitId.isNotBlank())
            TopOnManager.loadInterstitial(
                activity = activity,
                adUnitId = unitId,
                loadFailureRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_FAILED, unitId, it)
                    timer.finish()
                },
                loadSuccessRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED, unitId, unitId)
                    timer.finish()
                },
                showFailureRequest = {

                },
                showSuccessRequest = {

                },
                clickRequest = {

                })
    }

    internal fun showBackStackAd(
        activity: FragmentActivity,
        unitId: String,
        dismissRequest: () -> Unit
    ) {
        AdManager.showInterstitial(
            activity = activity,
            failureRequest = {
                FireBaseManager
                    .logEvent(FirebaseKey.AD_SHOW_FAILED, unitId, it)
                dismissRequest()
            },
            successRequest = {
                FireBaseManager
                    .logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS, unitId, unitId)
                dismissRequest()
            },
            clickRequest = {
                // unitId 埋点事件ID
                FireBaseManager
                    .logEvent(FirebaseKey.CLICK_AD, unitId, unitId)
            })
    }
}