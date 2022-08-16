package com.milk.smartvpn.ad

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.milk.smartvpn.BuildConfig
import com.milk.smartvpn.constant.AdCodeKey

object AdManager {
    private var interstitialAd: InterstitialAd? = null

    internal fun initialize(context: Context) {
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
    internal fun loadInterstitial(
        context: Context,
        adUnitId: String,
        onFailedRequest: (String) -> Unit = {},
        onSuccessRequest: () -> Unit = {}
    ) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    onFailedRequest(adError.message)
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    AdManager.interstitialAd = interstitialAd
                    onSuccessRequest()
                }
            })
    }

    /** 显示插页广告 */
    internal fun showInterstitial(
        activity: FragmentActivity,
        failureRequest: (String) -> Unit = {},
        successRequest: () -> Unit = {},
        clickRequest: () -> Unit = {}
    ) {
        if (interstitialAd == null)
            failureRequest("No interstitialAd object !")
        else {
            interstitialAd?.show(activity)
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    successRequest()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                    failureRequest(p0.message)
                }

                override fun onAdShowedFullScreenContent() {
                    interstitialAd = null
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    clickRequest()
                }
            }
        }
    }

    /** 加载原生广告 */
    internal fun loadNativeAds(
        context: Context,
        adUnitId: String,
        failedRequest: (String) -> Unit = {},
        successRequest: (NativeAd) -> Unit = {},
        clickAdRequest: () -> Unit = {}
    ) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { successRequest(it) }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    failedRequest(p0.message)
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    clickAdRequest()
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
}