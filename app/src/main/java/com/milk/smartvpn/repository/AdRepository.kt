package com.milk.smartvpn.repository

import com.milk.smartvpn.data.body.AppConfigBody
import com.milk.smartvpn.net.api.ApiService
import com.milk.smartvpn.net.retrofit

class AdRepository {
    suspend fun getAppConfig(appId: String) = retrofit {
        val body = AppConfigBody(appId)
        ApiService.main.getAppConfig(body)
    }
}