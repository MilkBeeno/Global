package com.milk.smartvpn.net.api

import com.milk.smartvpn.data.ApiResponse
import com.milk.smartvpn.data.AppConfigModel
import com.milk.smartvpn.data.VpnConfigModel
import com.milk.smartvpn.data.body.AppConfigBody
import com.milk.smartvpn.data.body.VpnConfigBody
import retrofit2.http.Body
import retrofit2.http.POST

interface MainApiService {
    @POST("/v1/app/link/list")
    suspend fun getAppConfig(@Body appConfigBody: AppConfigBody): ApiResponse<AppConfigModel>

    @POST("/v1/app/mobile/conf")
    suspend fun getVpnConfig(@Body vpnConfigBody: VpnConfigBody): ApiResponse<VpnConfigModel>
}