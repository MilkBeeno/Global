package com.milk.global.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.milk.global.ad.unitId.InterstitialAdUnitId
import com.milk.simple.log.Logger

class InterstitialAd {
    private val tag = "InterstitialAd"
    private var isLoadingAd: Boolean = false
    private var isShowingAd: Boolean = false
    private var interstitialAd: InterstitialAd? = null

    fun load(context: Context, failure: (String) -> Unit = {}, success: () -> Unit = {}) {
        if (isLoadingAd) {
            return
        }
        isLoadingAd = true
        val request = AdRequest.Builder().build()

        val callback = object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                isLoadingAd = false
                failure(p0.message)
                Logger.d("InterstitialAd：${p0.message}", tag)

            }

            override fun onAdLoaded(p0: InterstitialAd) {
                super.onAdLoaded(p0)
                interstitialAd = p0
                isLoadingAd = false
                success()
                Logger.d("InterstitialAd：Ad was loaded.", tag)
            }
        }
        InterstitialAd.load(context, InterstitialAdUnitId.value, request, callback)
    }

    fun show(
        activity: Activity,
        failure: (String) -> Unit = {},
        success: () -> Unit = {},
        click: () -> Unit = {},
        close: () -> Unit = {}
    ) {
        if (isShowingAd) {
            Logger.d("InterstitialAd：The app open ad is already showing.", tag)
            return
        }
        interstitialAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    close()
                    isShowingAd = false
                    interstitialAd = null
                    Logger.d("InterstitialAd：Ad dismissed fullscreen content.", tag)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    failure(adError.message)
                    isShowingAd = false
                    interstitialAd = null
                    Logger.d("InterstitialAd：${adError.message}", tag)
                }

                override fun onAdShowedFullScreenContent() {
                    success()
                    Logger.d("InterstitialAd：Ad showed fullscreen content.", tag)
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    click()
                    Logger.d("InterstitialAd：Click ad content.", tag)
                }
            }
        isShowingAd = true
        interstitialAd?.show(activity)
    }

    fun isShowingAd() = isShowingAd
}