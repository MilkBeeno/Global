package com.milk.global.repository

import com.milk.global.data.body.AppConfigBody
import com.milk.global.data.body.VpnConfigBody
import com.milk.global.net.api.ApiService
import com.milk.global.net.retrofit

class AdRepository {
    suspend fun getAppConfig(appId: String) = retrofit {
        val body = AppConfigBody(appId)
        ApiService.main.getAppConfig(body)
    }

    suspend fun getVpnConfig() = retrofit {
        val body = VpnConfigBody()
        ApiService.main.getVpnConfig(body)
    }
}