package com.milk.global.ad.ui

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.imageview.ShapeableImageView
import com.milk.global.R

abstract class NativeAdView : FrameLayout {

    private var nativeAd: NativeAd? = null
    private var nativeAdView: NativeAdView? = null
    private var primaryView: TextView? = null
    protected var secondaryView: TextView? = null
    protected var ratingBar: RatingBar? = null
    private var tertiaryView: TextView? = null
    private var iconView: ShapeableImageView? = null
    protected var mediaView: MediaView? = null
    private var callToActionView: TextView? = null
    private var background: ConstraintLayout? = null

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet?, style: Int) : super(ctx, attrs, style)

    protected fun setNativeAd(nativeAd: NativeAd) {
        this.nativeAd = nativeAd
        val store = nativeAd.store
        val advertiser = nativeAd.advertiser
        val headline = nativeAd.headline
        val body = nativeAd.body
        val cta = nativeAd.callToAction
        val starRating = nativeAd.starRating
        val icon = nativeAd.icon
        val secondaryText: String?
        nativeAdView?.callToActionView = callToActionView
        nativeAdView?.headlineView = primaryView
        nativeAdView?.mediaView = mediaView
        secondaryView?.visibility = VISIBLE
        if (adHasOnlyStore(nativeAd)) {
            nativeAdView?.storeView = secondaryView
            secondaryText = store
        } else if (!TextUtils.isEmpty(advertiser)) {
            nativeAdView?.advertiserView = secondaryView
            secondaryText = advertiser
        } else {
            secondaryText = ""
        }
        primaryView?.text = headline
        callToActionView?.text = cta

        if (starRating != null && starRating > 0) {
            secondaryView?.visibility = GONE
            ratingBar?.visibility = VISIBLE
            ratingBar?.rating = starRating.toFloat()
            nativeAdView?.starRatingView = ratingBar
        } else {
            secondaryView?.text = secondaryText
            secondaryView?.visibility = VISIBLE
            ratingBar?.visibility = GONE
        }
        if (icon != null) {
            iconView?.visibility = VISIBLE
            iconView?.setImageDrawable(icon.drawable)
        } else {
            iconView?.visibility = GONE
        }
        if (tertiaryView != null) {
            tertiaryView?.text = body
            nativeAdView?.bodyView = tertiaryView
        }
        nativeAdView?.setNativeAd(nativeAd)
        nativeAdView?.visibility = View.VISIBLE
    }

    private fun adHasOnlyStore(nativeAd: NativeAd): Boolean {
        val store = nativeAd.store
        val advertiser = nativeAd.advertiser
        return !TextUtils.isEmpty(store) && TextUtils.isEmpty(advertiser)
    }

    open fun destroyNativeAd() = nativeAd?.destroy()

    public override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            nativeAdView = findViewById(R.id.native_ad_view)
            primaryView = findViewById(R.id.primary)
            secondaryView = findViewById(R.id.secondary)
            tertiaryView = findViewById(R.id.body)
            ratingBar = findViewById(R.id.rating_bar)
            ratingBar?.isEnabled = false
            callToActionView = findViewById(R.id.cta)
            iconView = findViewById(R.id.icon)
            mediaView = findViewById(R.id.media_view)
            background = findViewById(R.id.background)
        }
    }
}