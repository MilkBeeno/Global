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

class AppOpenAd {
    private val tag = "AppOpenAd"
    private var isLoadingAd: Boolean = false
    private var isShowingAd: Boolean = false
    private var isClosedAd: Boolean = false
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
                success()
                Logger.d("AppOpenAd：Ad was loaded.", tag)
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                isLoadingAd = false
                failure(loadAdError.message)
                Logger.d("AppOpenAd：${loadAdError.message}", tag)
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
            Logger.d("AppOpenAd：The app open ad is already showing.", tag)
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                close()
                appOpenAd = null
                isShowingAd = false
                isClosedAd = true
                Logger.d("AppOpenAd：Ad dismissed fullscreen content.", tag)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                failure(adError.message)
                Logger.d("AppOpenAd：${adError.message}", tag)
            }

            override fun onAdShowedFullScreenContent() {
                success()
                Logger.d("AppOpenAd：Ad showed fullscreen content.", tag)
            }

            override fun onAdClicked() {
                super.onAdClicked()
                click()
                Logger.d("AppOpenAd：Click ad content.", tag)
            }
        }
        isShowingAd = true
        isClosedAd = false
        appOpenAd?.show(activity)
    }

    fun isClosedAd() = isClosedAd
}