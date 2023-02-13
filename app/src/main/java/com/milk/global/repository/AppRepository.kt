package com.milk.global.repository

import com.freetech.vpn.utils.VpnWhiteList
import com.milk.global.data.body.AppConfigBody
import com.milk.global.net.api.ApiService
import com.milk.global.net.retrofit
import com.milk.simple.ktx.ioScope

object AppRepository {
    var shareAppUrl = ""
    var showOpenAd: Boolean = true
    var showMainNativeAd: Boolean = true
    var showConnectedInsertAd: Boolean = true
    var showConnectedNativeAd: Boolean = true
    var showDisconnectInsertAd: Boolean = true
    var showDisconnectNativeAd: Boolean = true
    var showSwitchNativeAd: Boolean = true

    fun getConfig() {
        ioScope {
            val body = AppConfigBody()
            val apiResponse = retrofit { ApiService.main.getAppConfig(body) }
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                shareAppUrl = apiResult.share_copy

                VpnWhiteList.vpnList.clear()
                val parts = apiResult.Shield_app_bundle.split("&")
                parts.forEach { VpnWhiteList.vpnList.add(it) }

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