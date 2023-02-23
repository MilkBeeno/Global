package com.milk.global.repository

import com.freetech.vpn.utils.VpnWhiteList
import com.milk.global.data.body.AppConfigBody
import com.milk.global.net.api.ApiService
import com.milk.global.net.retrofit
import com.milk.simple.ktx.ioScope
import com.milk.simple.mdr.KvManger

object AppRepository {
    var shareAppUrl = ""
    var showOpenAd: Boolean = true
        set(value) {
            KvManger.put("showOpenAd", value)
            field = value
        }
        get() {
            field = KvManger.getBoolean("showOpenAd")
            return field
        }
    var showMainNativeAd: Boolean = true
        set(value) {
            KvManger.put("showMainNativeAd", value)
            field = value
        }
        get() {
            field = KvManger.getBoolean("showMainNativeAd")
            return field
        }
    var showConnectedInsertAd: Boolean = true
        set(value) {
            KvManger.put("showConnectedInsertAd", value)
            field = value
        }
        get() {
            field = KvManger.getBoolean("showConnectedInsertAd")
            return field
        }
    var showConnectedNativeAd: Boolean = true
        set(value) {
            KvManger.put("showConnectedNativeAd", value)
            field = value
        }
        get() {
            field = KvManger.getBoolean("showConnectedNativeAd")
            return field
        }
    var showDisconnectInsertAd: Boolean = true
        set(value) {
            KvManger.put("showDisconnectInsertAd", value)
            field = value
        }
        get() {
            field = KvManger.getBoolean("showDisconnectInsertAd")
            return field
        }
    var showDisconnectNativeAd: Boolean = true
        set(value) {
            KvManger.put("showDisconnectNativeAd", value)
            field = value
        }
        get() {
            field = KvManger.getBoolean("showDisconnectNativeAd")
            return field
        }
    var showSwitchNativeAd: Boolean = true
        set(value) {
            KvManger.put("showSwitchNativeAd", value)
            field = value
        }
        get() {
            field = KvManger.getBoolean("showSwitchNativeAd")
            return field
        }

    fun getConfig() {
        ioScope {
            val body = AppConfigBody()
            val apiResponse = retrofit { ApiService.main.getAppConfig(body) }
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                shareAppUrl = apiResult.share_copy
                if (apiResult.Shield_app_bundle != "0") {
                    VpnWhiteList.vpnList.clear()
                    val parts = apiResult.Shield_app_bundle.split("&")
                    parts.forEach { VpnWhiteList.vpnList.add(it) }
                }
                try {
                    showOpenAd = apiResult.openAd.toInt() == 0
                    showMainNativeAd = apiResult.mainNativeAd.toInt() == 0
                    showConnectedInsertAd = apiResult.connectedInsertAd.toInt() == 0
                    showConnectedNativeAd = apiResult.connectedNativeAd.toInt() == 0
                    showDisconnectInsertAd = apiResult.disconnectInsertAd.toInt() == 0
                    showDisconnectNativeAd = apiResult.disconnectNativeAd.toInt() == 0
                    showSwitchNativeAd = apiResult.switchNativeAd.toInt() == 0
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}