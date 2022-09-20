package com.milk.smartvpn.ad.ui

import com.anythink.nativead.api.ATNativePrepareInfo
import com.anythink.nativead.api.NativeAd

interface INativeView {
    fun createView(nativeAd: NativeAd)
    fun getPrepareInfo(): ATNativePrepareInfo?
}