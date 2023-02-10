package com.milk.global.repository

import com.freetech.vpn.utils.VpnWhiteList
import com.milk.global.BuildConfig
import com.milk.global.ad.AdConfig
import com.milk.global.constant.KvKey
import com.milk.global.data.VpnGroup
import com.milk.simple.ktx.ioScope
import com.milk.simple.mdr.KvManger
import kotlinx.coroutines.flow.MutableStateFlow

object DataRepository {
    private val adRepository by lazy { AdRepository() }

    /** 保存在内存中的 VPN 列表数据、保证快速获取 VPN 列表 */
    val vpnListData = arrayListOf<VpnGroup>()
    val shareAppUrl = MutableStateFlow("")

    fun appConfig() {
        ioScope {
            val apiResponse = adRepository.getVpnConfig()
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                AdConfig.adRefreshTime = apiResult.yuanshua_hao
                val random = Math.random() * 100
                if (apiResult.isjie_vp == 0) {
                    VpnWhiteList.addCloseList("com.google.android.gms")
                    VpnWhiteList.addCloseList("com.android.vending")
                } else {
                    if (random <= apiResult.isjie_vp) {
                        VpnWhiteList.addCloseList("com.google.android.gms")
                        VpnWhiteList.addCloseList("com.android.vending")
                    }
                }
            }
        }
        ioScope {
            val appUrl = KvManger.getString(KvKey.APP_SHARE_URL)
            if (appUrl.isBlank()) {
                val apiResponse = adRepository.getAppConfig(BuildConfig.AD_APP_ID)
                val apiResult = apiResponse.data
                if (apiResponse.success && apiResult != null) {
                    shareAppUrl.emit(apiResult.imgPath)
                }
            } else shareAppUrl.emit(appUrl)
        }
    }
}