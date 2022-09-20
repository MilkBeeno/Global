package com.milk.smartvpn.ad.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import com.anythink.core.api.ATAdInfo
import com.anythink.nativead.api.ATNativeAdView
import com.anythink.nativead.api.ATNativeDislikeListener
import com.anythink.nativead.api.ATNativeEventListener
import com.anythink.nativead.api.NativeAd
import com.milk.smartvpn.friebase.FireBaseManager
import com.milk.smartvpn.friebase.FirebaseKey

/**
 * 原生广告  分为 google原生  Facebook 原生， is原生
 */
class AdView : FrameLayout {

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    private var anyThinkNativeAdView: ATNativeAdView? = null
    private var mNativeAdTop: NativeAd? = null

    fun showNativeAd(adType: AdType, nativeAd: NativeAd) {
        removeAllViews()
        visibility = VISIBLE
        anyThinkNativeAdView?.removeAllViews()
        anyThinkNativeAdView = ATNativeAdView(context)
        anyThinkNativeAdView?.let {
            if (it.parent == null) {
                val params = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
                )
                addView(it, params)
            }
        }
        mNativeAdTop?.destory()
        mNativeAdTop = nativeAd
        mNativeAdTop?.let {
            it.setNativeEventListener(object : ATNativeEventListener {
                override fun onAdImpressed(view: ATNativeAdView, atAdInfo: ATAdInfo) {

                }

                override fun onAdClicked(view: ATNativeAdView, atAdInfo: ATAdInfo) {
                    when (adType) {
                        AdType.Main ->
                            FireBaseManager.logEvent(FirebaseKey.click_ad)
                        AdType.VpnList ->
                            FireBaseManager.logEvent(FirebaseKey.click_ad_1)
                        AdType.Connected ->
                            FireBaseManager.logEvent(FirebaseKey.click_ad_5)
                        AdType.DisConnect ->
                            FireBaseManager.logEvent(FirebaseKey.click_ad_6)
                    }
                }

                override fun onAdVideoStart(view: ATNativeAdView) {
                    when (adType) {
                        AdType.Main ->
                            FireBaseManager.logEvent(FirebaseKey.The_ad_show_success)
                        AdType.VpnList ->
                            FireBaseManager.logEvent(FirebaseKey.The_ad_show_success_1)
                        AdType.Connected ->
                            FireBaseManager.logEvent(FirebaseKey.The_ad_show_success_5)
                        AdType.DisConnect ->
                            FireBaseManager.logEvent(FirebaseKey.The_ad_show_success_6)
                    }
                }

                override fun onAdVideoEnd(view: ATNativeAdView) {

                }

                override fun onAdVideoProgress(view: ATNativeAdView, progress: Int) {

                }
            })
            it.setDislikeCallbackListener(object : ATNativeDislikeListener() {
                override fun onAdCloseButtonClick(view: ATNativeAdView, atAdInfo: ATAdInfo) {
                    if (view.parent != null) {
                        (view.parent as ViewGroup).removeView(view)
                    }
                }
            })
            val selfRender = when (adType) {
                AdType.Main,
                AdType.Connected,
                AdType.DisConnect -> MainNativeView(context)
                else -> NativeView(context)
            }
            selfRender.createView(it)
            try {
                it.renderAdContainer(anyThinkNativeAdView, selfRender)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            anyThinkNativeAdView?.visibility = VISIBLE
            it.prepare(anyThinkNativeAdView, selfRender.getPrepareInfo())
        }
    }
}