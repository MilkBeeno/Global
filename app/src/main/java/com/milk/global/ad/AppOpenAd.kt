package com.milk.global.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.milk.global.ad.unitId.AppOpenAdUnitId
import com.milk.simple.log.Logger
import java.util.*

class AppOpenAd {
    private var loadTime: Long = 0
    private var isLoadingAd: Boolean = false
    private var isShowingAd: Boolean = false
    private var appOpenAd: AppOpenAd? = null

    fun load(context: Context, failure: (String) -> Unit = {}, success: () -> Unit = {}) {
        if (isLoadingAd) {
            return
        }
        isLoadingAd = true
        val request = AdRequest.Builder().build()
        val callback = object : AppOpenAd.AppOpenAdLoadCallback() {

            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                isLoadingAd = false
                loadTime = Date().time
                success()
                Logger.d("Ad was loaded.", this::class.java.name)
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                isLoadingAd = false
                failure(loadAdError.message)
                Logger.d(loadAdError.message, this::class.java.name)
            }
        }
        AppOpenAd.load(context, AppOpenAdUnitId.value, request, callback)
    }

    fun show(
        activity: Activity,
        failure: (String) -> Unit = {},
        success: () -> Unit = {},
        click: () -> Unit = {},
        close: () -> Unit = {}
    ) {
        if (isShowingAd) {
            Logger.d("The app open ad is already showing.", this::class.java.name)
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                close()
                appOpenAd = null
                isShowingAd = false
                Logger.d("Ad dismissed fullscreen content.", this::class.java.name)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                failure(adError.message)
                Logger.d(adError.message, this::class.java.name)
            }

            override fun onAdShowedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                success()
                Logger.d("Ad showed fullscreen content.", this::class.java.name)
            }

            override fun onAdClicked() {
                super.onAdClicked()
                click()
                Logger.d("Click ad content.", this::class.java.name)
            }
        }
        isShowingAd = true
        appOpenAd?.show(activity)
    }
}