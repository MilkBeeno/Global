package com.milk.smartvpn.ui.vm

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.milk.smartvpn.ad.AdConfig
import com.milk.smartvpn.ad.AdManager
import com.milk.smartvpn.constant.AdCodeKey
import com.milk.smartvpn.util.MilkTimer

class BackStackViewModel : ViewModel() {

    internal fun loadLaunchAd(activity: Activity, dismissRequest: (String) -> Unit) {
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.APP_LAUNCH)
        val timer = MilkTimer.Builder()
            .setMillisInFuture(10000)
            .setOnFinishedListener { dismissRequest(unitId) }
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

    internal fun showLaunchAd(activity: Activity, unitId: String, dismissRequest: () -> Unit) {
        AdManager.showInterstitial(activity, unitId, onDismissRequest = dismissRequest)
    }

    internal fun loadBackStackAd(activity: Activity, dismissRequest: (String) -> Unit) {
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.BACK_STACK)
        val timer = MilkTimer.Builder()
            .setMillisInFuture(10000)
            .setOnFinishedListener { dismissRequest(unitId) }
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

    internal fun showBackStackAd(activity: Activity, unitId: String, dismissRequest: () -> Unit) {
        AdManager.showInterstitial(activity, unitId, onDismissRequest = dismissRequest)
    }
}