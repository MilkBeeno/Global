package com.milk.smartvpn.repository

import com.milk.smartvpn.net.api.ApiService
import com.milk.smartvpn.net.retrofit

class VpnRepository {

    suspend fun getVpnInfo(id: Long) = retrofit { ApiService.vpn.getVpnInfo(id) }

    suspend fun getVpnListInfo() = retrofit { ApiService.vpn.getVpnListInfo() }
}