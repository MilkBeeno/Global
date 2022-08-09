package com.milk.smartvpn.net.api

import com.milk.smartvpn.net.ApiClient

object ApiService {
    val main: MainApiService =
        ApiClient.obtainMain().create(MainApiService::class.java)
    val vpn: VpnApiService =
        ApiClient.obtainVpn().create(VpnApiService::class.java)
}