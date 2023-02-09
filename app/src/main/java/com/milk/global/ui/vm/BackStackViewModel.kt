package com.milk.global.ui.vm

import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.milk.global.ad.AdConfig
import com.milk.global.ad.TopOnManager
import com.milk.global.constant.AdCodeKey
import com.milk.global.friebase.FireBaseManager
import com.milk.global.friebase.FirebaseKey
import com.milk.global.util.MilkTimer

class BackStackViewModel : ViewModel() {
    private var adIsLoadSuccess: Boolean = false

    internal fun loadLaunchAd(
        activity: FragmentActivity,
        viewGroup: ViewGroup,
        finishRequest: () -> Unit
    ) {
        val unitId = AdConfig.getAdvertiseUnitId(AdCodeKey.LAUNCH_OPEN_AD_KEY)
        val timer = MilkTimer.Builder()
            .setMillisInFuture(10000)
            .setOnFinishedListener {
                if (!adIsLoadSuccess) {
                    finishRequest()
                }
                adIsLoadSuccess = false
            }
            .build().apply { start() }

        if (unitId.isNotBlank()) {
            FireBaseManager.logEvent(FirebaseKey.Make_an_ad_request_3)
            TopOnManager.loadOpenAd(
                activity = activity,
                viewGroup = viewGroup,
                adUnitId = unitId,
                loadFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_failed_3, it)
                },
                loadSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_succeeded_3)
                },
                showFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_show_failed_3, it)
                },
                showSuccessRequest = {
                    adIsLoadSuccess = true
                    FireBaseManager.logEvent(FirebaseKey.The_ad_show_success_3)
                },
                finishedRequest = {
                    finishRequest()
                },
                clickRequest = {
                    FireBaseManager.logEvent(FirebaseKey.click_ad_3)
                })
        } else timer.finish()
    }
}