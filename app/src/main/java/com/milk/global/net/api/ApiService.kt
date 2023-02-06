package com.milk.global.net.api

import com.milk.global.net.ApiClient

object ApiService {
    val main: MainApiService =
        ApiClient.obtainMain().create(MainApiService::class.java)
    val vpn: VpnApiService =
        ApiClient.obtainVpn().create(VpnApiService::class.java)
}