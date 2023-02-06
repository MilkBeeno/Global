package com.milk.global.ad.ui

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.anythink.nativead.api.ATNativeImageView
import com.anythink.nativead.api.ATNativePrepareInfo
import com.anythink.nativead.api.NativeAd
import com.milk.global.R

class MainNativeView : FrameLayout, NativeView {
    private var mClickView: MutableList<View> = ArrayList()
    private var prepareInfo: ATNativePrepareInfo? = null

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defAttr: Int) : super(ctx, attrs, defAttr)

    override fun createView(nativeAd: NativeAd) {
        removeAllViews()
        mClickView.clear()
        prepareInfo = ATNativePrepareInfo()
        LayoutInflater
            .from(context).inflate(R.layout.layout_main_ad, this)
        val titleView = findViewById<TextView>(R.id.native_ad_title)
        val contentArea = findViewById<FrameLayout>(R.id.native_ad_content_image_area)
        val descView = findViewById<TextView>(R.id.native_ad_desc)
        val iconArea = findViewById<FrameLayout>(R.id.native_ad_image)
        val ctaView = findViewById<TextView>(R.id.native_ad_install_btn)
        val adFromView = findViewById<TextView>(R.id.native_ad_from)
        val logoView = findViewById<ATNativeImageView>(R.id.native_ad_logo)

        prepareInfo?.titleView = titleView
        prepareInfo?.descView = descView
        prepareInfo?.iconView = iconArea
        prepareInfo?.adFromView = adFromView
        prepareInfo?.ctaView = ctaView

        contentArea.removeAllViews()
        iconArea.removeAllViews()
        logoView.setImageDrawable(null)

        val adMaterial = nativeAd.adMaterial
        val mediaView: View? =
            adMaterial.getAdMediaView(contentArea, contentArea.width)
        /** 模板渲染（个性化模板、自动渲染） */
        if (nativeAd.isNativeExpress) {
            titleView.visibility = GONE
            descView.visibility = GONE
            iconArea.visibility = GONE
            ctaView.visibility = GONE
            logoView.visibility = GONE
            if (mediaView?.parent != null) {
                (mediaView.parent as ViewGroup).removeView(mediaView)
            }
            if (mediaView != null) {
                val params = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )
                contentArea.addView(mediaView, params)
            }
            return
        }
        /** 自渲染（自定义渲染）*/
        titleView.visibility = VISIBLE
        descView.visibility = VISIBLE
        iconArea.visibility = VISIBLE
        ctaView.visibility = VISIBLE
        logoView.visibility = VISIBLE
        contentArea.visibility = VISIBLE

        titleView.text = adMaterial.title
        descView.text = adMaterial.descriptionText
        val adIconView = adMaterial.adIconView
        val iconView = ATNativeImageView(context)
        if (adIconView == null) {
            iconArea.addView(iconView)
            iconView.setImage(adMaterial.iconImageUrl)
        } else {
            iconArea.addView(adIconView)
        }
        ctaView.text = adMaterial.callToActionText

        if (!TextUtils.isEmpty(adMaterial.adChoiceIconUrl)) {
            logoView.setImage(adMaterial.adChoiceIconUrl)
        }

        val imageView = ATNativeImageView(context)
        if (mediaView != null) {
            if (mediaView.parent != null) {
                (mediaView.parent as ViewGroup).removeView(mediaView)
            }
            contentArea.addView(
                mediaView,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            )
            prepareInfo?.mainImageView = mediaView
        } else {
            imageView.setImage(adMaterial.mainImageUrl)
            val params: ViewGroup.LayoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            imageView.layoutParams = params
            contentArea.addView(imageView, params)
            prepareInfo?.mainImageView = imageView
        }

        if (!TextUtils.isEmpty(adMaterial.adFrom)) {
            adFromView.text = if (adMaterial.adFrom != null) adMaterial.adFrom else ""
            adFromView.visibility = GONE
        } else {
            adFromView.visibility = GONE
        }
        /* mClickView.add(titleView)
         mClickView.add(descView)
         mClickView.add(iconView)
         mClickView.add(imageView)
         mClickView.add(iconArea)*/

        mClickView.add(ctaView)
        prepareInfo?.clickViewList = mClickView
    }

    override fun getPrepareInfo(): ATNativePrepareInfo? {
        return prepareInfo
    }
}