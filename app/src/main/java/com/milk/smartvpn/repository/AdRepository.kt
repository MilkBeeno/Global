package com.milk.smartvpn.repository

import com.milk.smartvpn.data.body.AdBody
import com.milk.smartvpn.net.api.ApiService
import com.milk.smartvpn.net.retrofit

class AdRepository {
    suspend fun getAdConfig(appId: String, channel: String, pkgVersion: String) = retrofit {
        val body = AdBody(appId, channel, pkgVersion)
        ApiService.main.getAdConfig(body)
    }
}