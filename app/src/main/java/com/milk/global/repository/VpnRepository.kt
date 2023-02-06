package com.milk.global.repository

import com.milk.global.net.api.ApiService
import com.milk.global.net.retrofit

class VpnRepository {

    suspend fun getVpnInfo(id: Long) = retrofit { ApiService.vpn.getVpnInfo(id) }

    suspend fun getVpnListInfo() = retrofit { ApiService.vpn.getVpnListInfo() }
}