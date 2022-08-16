package com.milk.smartvpn.repository

import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.MutableStateFlow

object DataRepository {
    internal val connectSuccessAd = MutableStateFlow<Pair<String, NativeAd?>>(Pair("", null))
    internal val disconnectAd = MutableStateFlow<Pair<String, NativeAd?>>(Pair("", null))
}