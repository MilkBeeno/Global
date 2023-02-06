package com.milk.global.ad

import android.app.Application
import androidx.fragment.app.FragmentActivity
import com.anythink.core.api.*
import com.anythink.interstitial.api.ATInterstitial
import com.anythink.interstitial.api.ATInterstitialListener
import com.anythink.nativead.api.ATNative
import com.anythink.nativead.api.ATNativeNetworkListener
import com.anythink.network.facebook.FacebookATInitConfig
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdExtraInfo
import com.anythink.splashad.api.ATSplashAdListener
import com.milk.simple.log.Logger
import com.milk.global.BuildConfig

/**
 * TopOn 广告聚合平台管理、目前只添加 Facebook 广告接入
 */
object TopOnManager {
    private val atInitConfigs = arrayListOf<ATInitConfig>()

    internal fun initialize(application: Application) {
        // 初始化 SDK
        val facebookATInitConfig = FacebookATInitConfig()
        atInitConfigs.add(facebookATInitConfig)
        val builder = ATNetworkConfig.Builder()
        builder.withInitConfigList(atInitConfigs)
        val atNetworkConfig = builder.build()
        ATSDK.init(application, BuildConfig.TOP_ON_ID, BuildConfig.TOP_ON_KEY, atNetworkConfig)
        ATSDK.setNetworkLogDebug(BuildConfig.DEBUG)
        if (BuildConfig.DEBUG) {
            val deviceId = "9b7ae0dd286ed369"
            ATSDK.setDebuggerConfig(application, deviceId, null)
            // 检查广告平台的集成状态，提交审核时需注释此 API
            ATSDK.integrationChecking(application)
            // (v5.7.77新增) 打印当前设备的设备信息(IMEI、OAID、GAID、AndroidID等)
            ATSDK.testModeDeviceInfo(application) { deviceInfo ->
                Logger.d("deviceInfo: $deviceInfo", "TopOnManager")
            }
        }
    }

    /** 加载插页广告 */
    internal fun loadOpenAd(
        activity: FragmentActivity,
        adUnitId: String,
        loadFailureRequest: (String) -> Unit = {},
        loadSuccessRequest: () -> Unit = {},
        showFailureRequest: (String) -> Unit = {},
        showSuccessRequest: () -> Unit = {},
        finishedRequest: () -> Unit = {},
        clickRequest: () -> Unit = {}
    ): ATSplashAd {
        val splashAd = ATSplashAd(activity, adUnitId, object : ATSplashAdListener {
            override fun onAdLoaded(p0: Boolean) {
                loadSuccessRequest()
            }

            override fun onAdLoadTimeout() {
                loadFailureRequest("加载广告超时了")
            }

            override fun onNoAdError(p0: AdError?) {
                showFailureRequest(p0?.desc.toString())

            }

            override fun onAdShow(p0: ATAdInfo?) {
                // ATAdInfo 可区分广告平台以及获取广告平台的广告位ID等
                showSuccessRequest()
            }

            override fun onAdClick(p0: ATAdInfo?) {
                clickRequest()
            }

            override fun onAdDismiss(p0: ATAdInfo?, p1: ATSplashAdExtraInfo?) {
                finishedRequest()
            }
        })
        splashAd.loadAd()
        return splashAd
    }

    /** 加载插页广告 */
    internal fun loadInterstitial(
        activity: FragmentActivity,
        adUnitId: String,
        loadFailureRequest: (String) -> Unit = {},
        loadSuccessRequest: () -> Unit = {},
        showFailureRequest: (String) -> Unit = {},
        showSuccessRequest: () -> Unit = {},
        finishedRequest: () -> Unit = {},
        clickRequest: () -> Unit = {}
    ): ATInterstitial {
        val interstitialAd = ATInterstitial(activity, adUnitId)
        interstitialAd.setAdListener(object : ATInterstitialListener {
            override fun onInterstitialAdLoaded() {
                loadSuccessRequest()
            }

            override fun onInterstitialAdLoadFail(p0: AdError?) {
                // 注意：禁止在此回调中执行广告的加载方法进行重试，否则会引起很多无用请求且可能会导致应用卡顿
                loadFailureRequest(p0?.desc.toString())
            }

            override fun onInterstitialAdClicked(p0: ATAdInfo?) {
                clickRequest()
            }

            override fun onInterstitialAdShow(p0: ATAdInfo?) {
                // ATAdInfo 可区分广告平台以及获取广告平台的广告位ID等
                showSuccessRequest()
            }

            override fun onInterstitialAdClose(p0: ATAdInfo?) {
                // 建议在此回调中调用load进行广告的加载，方便下一次广告的展示（不需要调用isAdReady())
                finishedRequest()
            }

            override fun onInterstitialAdVideoStart(p0: ATAdInfo?) {

            }

            override fun onInterstitialAdVideoEnd(p0: ATAdInfo?) {

            }

            override fun onInterstitialAdVideoError(p0: AdError?) {
                showFailureRequest(p0?.desc.toString())
            }
        })
        interstitialAd.load()
        return interstitialAd
    }

    /** 加载原生广告 */
    internal fun loadNativeAd(
        activity: FragmentActivity,
        adUnitId: String,
        loadFailureRequest: (String) -> Unit = {},
        loadSuccessRequest: (ATNative?) -> Unit = {}
    ) {
        var atNative: ATNative? = null
        val listener = object : ATNativeNetworkListener {
            override fun onNativeAdLoaded() {
                loadSuccessRequest(atNative)
            }

            override fun onNativeAdLoadFail(adError: AdError) {
                // 注意：禁止在此回调中执行广告的加载方法进行重试，否则会引起很多无用请求且可能会导致应用卡顿
                loadFailureRequest(adError.desc.toString())
            }
        }
        atNative = ATNative(activity, adUnitId, listener)
        atNative.makeAdRequest()
    }
}