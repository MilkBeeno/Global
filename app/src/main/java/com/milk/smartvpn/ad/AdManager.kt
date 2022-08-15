package com.milk.smartvpn.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.milk.smartvpn.BuildConfig
import com.milk.smartvpn.constant.AdCodeKey

object AdManager {
    private var interstitialAd: InterstitialAd? = null

    fun initialize(context: Context) {
        MobileAds.initialize(context) {
            if (BuildConfig.DEBUG) {
                MobileAds.setRequestConfiguration(
                    RequestConfiguration
                        .Builder()
                        .setTestDeviceIds(AdCodeKey.TEST_DEVICE_NUMBER)
                        .build()
                )
            }
        }
    }

    /** 加载插页广告 */
    fun loadInterstitial(
        context: Context,
        adUnitId: String,
        onFailedRequest: () -> Unit = {},
        onSuccessRequest: () -> Unit = {}
    ) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    onFailedRequest()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    AdManager.interstitialAd = interstitialAd
                    onSuccessRequest()
                }
            })
    }

    /** 显示插页广告 */
    fun showInterstitial(
        activity: Activity,
        unitId: String,
        onFailedRequest: (String) -> Unit = {},
        onSuccessRequest: () -> Unit = {},
        onDismissRequest: () -> Unit = {}
    ) {
        if (interstitialAd == null) {
            onFailedRequest("")
            onDismissRequest()
        } else {
            interstitialAd?.show(activity)
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    onSuccessRequest()
                    onDismissRequest()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    onFailedRequest(p0.message)
                    onDismissRequest()
                }

                override fun onAdShowedFullScreenContent() {
                    interstitialAd = null
                }
            }
        }
    }

    /** 加载原生广告 */
    fun loadNativeAds(
        context: Context,
        adUnitId: String,
        failedRequest: () -> Unit = {},
        successRequest: (NativeAd) -> Unit = {}
    ) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd {
                successRequest(it)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    failedRequest()
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
}