package com.milk.smartvpn.ui.vm

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.anythink.interstitial.api.ATInterstitial
import com.milk.smartvpn.ad.AdConfig
import com.milk.smartvpn.ad.AdLoadStatus
import com.milk.smartvpn.ad.TopOnManager
import com.milk.smartvpn.constant.AdCodeKey
import com.milk.smartvpn.friebase.FireBaseManager
import com.milk.smartvpn.friebase.FirebaseKey
import com.milk.smartvpn.util.MilkTimer

class BackStackViewModel : ViewModel() {
    private var adLoadStatus: AdLoadStatus = AdLoadStatus.Loading

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
            FireBaseManager.logEvent(FirebaseKey.Make_an_ad_request_3)
            aTInterstitial = TopOnManager.loadInterstitial(
                activity = activity,
                adUnitId = unitId,
                loadFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_failed_3, it)
                    adLoadStatus = AdLoadStatus.Failure
                },
                loadSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_succeeded_3)
                    adLoadStatus = AdLoadStatus.Success
                    timer.finish()
                },
                showFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_show_failed_3, it)
                },
                showSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.The_ad_show_success_3)
                },
                finishedRequest = {
                    adLoadStatus = AdLoadStatus.Loading
                    finishRequest()
                },
                clickRequest = {
                    FireBaseManager.logEvent(FirebaseKey.click_ad_3)
                })
        } else adLoadStatus = AdLoadStatus.Failure
    }

    internal fun loadBackStackAd(activity: FragmentActivity, finishRequest: () -> Unit) {
        var aTInterstitial: ATInterstitial? = null
        val unitId = AdConfig.getAdvertiseUnitId(AdCodeKey.BACK_STACK)
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
            FireBaseManager.logEvent(FirebaseKey.Make_an_ad_request_2)
            aTInterstitial = TopOnManager.loadInterstitial(
                activity = activity,
                adUnitId = unitId,
                loadFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_failed_2, it)
                    adLoadStatus = AdLoadStatus.Failure
                    timer.finish()
                },
                loadSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_succeeded_2)
                    adLoadStatus = AdLoadStatus.Success
                    timer.finish()
                },
                showFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_show_failed_2, it)
                },
                showSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.The_ad_show_success_2)
                },
                finishedRequest = {
                    adLoadStatus = AdLoadStatus.Loading
                    finishRequest()
                },
                clickRequest = {
                    FireBaseManager.logEvent(FirebaseKey.click_ad_2)
                })
        } else adLoadStatus = AdLoadStatus.Failure
    }
}