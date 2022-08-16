package com.milk.smartvpn.ui.vm

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.milk.smartvpn.ad.AdConfig
import com.milk.smartvpn.ad.AdManager
import com.milk.smartvpn.constant.AdCodeKey
import com.milk.smartvpn.util.MilkTimer

class BackStackViewModel : ViewModel() {

    internal fun loadLaunchAd(activity: Activity, finishRequest: (String) -> Unit) {
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.APP_LAUNCH)
        val timer = MilkTimer.Builder()
            .setMillisInFuture(10000)
            .setOnFinishedListener { finishRequest(unitId) }
            .build()
        timer.start()
        if (unitId.isNotBlank())
            AdManager.loadInterstitial(activity, unitId,
                onFailedRequest = {
                    timer.finish()
                },
                onSuccessRequest = {
                    timer.finish()
                })
    }

    internal fun showLaunchAd(
        activity: FragmentActivity,
        unitId: String,
        dismissRequest: () -> Unit
    ) {
        AdManager.showInterstitial(
            activity = activity,
            failureRequest = {
                dismissRequest()
            },
            successRequest = {
                dismissRequest()
            },
            clickRequest = {
                // unitId 埋点事件ID
            })
    }

    internal fun loadBackStackAd(activity: Activity, finishRequest: (String) -> Unit) {
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.BACK_STACK)
        val timer = MilkTimer.Builder()
            .setMillisInFuture(10000)
            .setOnFinishedListener { finishRequest(unitId) }
            .build()
        timer.start()
        if (unitId.isNotBlank()) AdManager.loadInterstitial(activity, unitId,
            onSuccessRequest = {
                timer.finish()
            },
            onFailedRequest = {
                timer.finish()
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
                dismissRequest()
            },
            successRequest = {
                dismissRequest()
            },
            clickRequest = {
                // unitId 埋点事件ID
            })
    }
}