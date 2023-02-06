package com.milk.global.net.api

import com.milk.global.data.ApiResponse
import com.milk.global.data.AppConfigModel
import com.milk.global.data.VpnConfigModel
import com.milk.global.data.body.AppConfigBody
import com.milk.global.data.body.VpnConfigBody
import retrofit2.http.Body
import retrofit2.http.POST

interface MainApiService {
    @POST("/v1/app/link/list")
    suspend fun getAppConfig(@Body appConfigBody: AppConfigBody): ApiResponse<AppConfigModel>

    @POST("/v1/app/mobile/conf")
    suspend fun getVpnConfig(@Body vpnConfigBody: VpnConfigBody): ApiResponse<VpnConfigModel>
}