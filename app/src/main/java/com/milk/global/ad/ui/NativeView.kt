package com.milk.global.ad.ui

import com.anythink.nativead.api.ATNativePrepareInfo
import com.anythink.nativead.api.NativeAd

interface NativeView {
    fun createView(nativeAd: NativeAd)
    fun getPrepareInfo(): ATNativePrepareInfo?
}