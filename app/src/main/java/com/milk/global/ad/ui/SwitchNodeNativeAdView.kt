package com.milk.global.ad.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.milk.global.R
import com.milk.global.ad.unitId.NativeAdUnitId
import com.milk.simple.ktx.gone

class SwitchNodeNativeAdView : NativeAdView {
    private var loadFailureRequest: ((String) -> Unit)? = null
    private var loadSuccessRequest: (() -> Unit)? = null
    private var clickRequest: (() -> Unit)? = null

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet?, style: Int) : super(ctx, attrs, style)

    init {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.native_ad_view_switch_node, this)
    }

    fun setLoadFailureRequest(request: (String) -> Unit) {
        loadFailureRequest = request
    }

    fun setLoadSuccessRequest(request: () -> Unit) {
        loadSuccessRequest = request
    }

    fun setClickRequest(request: () -> Unit) {
        clickRequest = request
    }

    fun loadNativeAd() {
        val adLoader = AdLoader.Builder(context, NativeAdUnitId.value)
            .forNativeAd { nativeAd ->
                setNativeAd(nativeAd)
                mediaView?.gone()
                secondaryView?.gone()
                ratingBar?.gone()
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    loadFailureRequest?.invoke(p0.message)
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    loadSuccessRequest?.invoke()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    clickRequest?.invoke()
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
}