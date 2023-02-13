package com.milk.global.repository

import com.milk.global.data.VpnGroup
import com.milk.global.net.api.ApiService
import com.milk.global.net.retrofit

object VpnRepository {
    val vpnListData = arrayListOf<VpnGroup>()

    suspend fun getVpnInfo(id: Long) = retrofit { ApiService.vpn.getVpnInfo(id) }

    suspend fun getVpnListInfo() = retrofit { ApiService.vpn.getVpnListInfo() }
}