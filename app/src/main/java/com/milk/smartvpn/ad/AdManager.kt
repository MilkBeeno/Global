package com.milk.smartvpn.ad

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.milk.smartvpn.BuildConfig
import com.milk.smartvpn.constant.AdCodeKey
import com.milk.smartvpn.friebase.FireBaseManager
import com.milk.smartvpn.friebase.FirebaseKey

object AdManager {
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

    /** 加载原生广告 */
    internal fun loadNativeAds(
        context: Context,
        adUnitId: String,
        failedRequest: (String) -> Unit = {},
        successRequest: (NativeAd) -> Unit = {},
        clickAdRequest: () -> Unit = {}
    ) {
        FireBaseManager.logEvent(FirebaseKey.MAKE_AN_AD_REQUEST, adUnitId, adUnitId)
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