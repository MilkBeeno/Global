package com.milk.smartvpn.ad

import android.app.Application
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.anythink.banner.api.ATBannerListener
import com.anythink.banner.api.ATBannerView
import com.anythink.core.api.*
import com.anythink.interstitial.api.ATInterstitial
import com.anythink.interstitial.api.ATInterstitialListener
import com.anythink.nativead.api.ATNative
import com.anythink.nativead.api.ATNativeNetworkListener
import com.anythink.rewardvideo.api.ATRewardVideoAd
import com.anythink.rewardvideo.api.ATRewardVideoListener
import com.milk.simple.log.Logger
import com.milk.smartvpn.BuildConfig

/**
 * TopOn 广告聚合平台管理、目前只添加 Facebook 广告接入
 */
object TopOnManager {
    private val atInitConfigs = arrayListOf<ATInitConfig>()

    internal fun initialize(application: Application) {
        // 初始化 SDK
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

    /** 查看横幅广告 */
    internal fun loadBannerAd(
        activity: FragmentActivity,
        adUnitId: String,
        loadFailureRequest: (String) -> Unit = {},
        loadSuccessRequest: () -> Unit = {},
        showFailureRequest: (String) -> Unit = {},
        showSuccessRequest: () -> Unit = {},
        clickRequest: () -> Unit = {},
    ): ATBannerView {
        val bannerView = ATBannerView(activity)
        bannerView.setPlacementId(adUnitId)
        val width = activity.resources.displayMetrics.widthPixels
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        bannerView.layoutParams = FrameLayout.LayoutParams(width, height)
        bannerView.setBannerAdListener(object : ATBannerListener {
            override fun onBannerLoaded() {
                loadSuccessRequest()
            }

            override fun onBannerFailed(p0: AdError?) {
                loadFailureRequest(p0?.desc.toString())
            }

            override fun onBannerClicked(p0: ATAdInfo?) {
                clickRequest()
            }

            override fun onBannerShow(p0: ATAdInfo?) {
                showSuccessRequest()
            }

            override fun onBannerClose(p0: ATAdInfo?) {

            }

            override fun onBannerAutoRefreshed(p0: ATAdInfo?) {

            }

            override fun onBannerAutoRefreshFail(p0: AdError?) {
                showFailureRequest(p0?.desc.toString())
            }
        })
        bannerView.loadAd()
        return bannerView
    }

    /** 激励视频广告展示 */
    internal fun loadIncentiveVideoAd(
        activity: FragmentActivity,
        adUnitId: String,
        loadFailureRequest: (String) -> Unit = {},
        loadSuccessRequest: () -> Unit = {},
        showFailureRequest: (String) -> Unit = {},
        showSuccessRequest: () -> Unit = {},
        clickRequest: () -> Unit = {},
    ) {
        val rewardVideoAd = ATRewardVideoAd(activity, adUnitId)
        rewardVideoAd.setAdListener(object : ATRewardVideoListener {
            override fun onRewardedVideoAdLoaded() {
                loadSuccessRequest()
                rewardVideoAd.show(activity)
            }

            override fun onRewardedVideoAdFailed(p0: AdError?) {
                // 注意：禁止在此回调中执行广告的加载方法进行重试，否则会引起很多无用请求且可能会导致应用卡顿
                loadFailureRequest(p0?.desc.toString())
            }

            override fun onRewardedVideoAdPlayStart(p0: ATAdInfo?) {
                // ATAdInfo可区分广告平台以及获取广告平台的广告位ID等
                showSuccessRequest()
            }

            override fun onRewardedVideoAdPlayEnd(p0: ATAdInfo?) {
            }

            override fun onRewardedVideoAdPlayFailed(p0: AdError?, p1: ATAdInfo?) {
                showFailureRequest(p0?.desc.toString())
            }

            override fun onRewardedVideoAdClosed(p0: ATAdInfo?) {
            }

            override fun onRewardedVideoAdPlayClicked(p0: ATAdInfo?) {
                clickRequest()
            }

            override fun onReward(p0: ATAdInfo?) {
                // 建议在此回调中下发奖励，一般在onRewardedVideoAdClosed之前回调
            }
        })
        rewardVideoAd.load()
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