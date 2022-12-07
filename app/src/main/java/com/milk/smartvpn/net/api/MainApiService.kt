package com.milk.smartvpn.net.api

import com.milk.smartvpn.data.ApiResponse
import com.milk.smartvpn.data.AppConfigModel
import com.milk.smartvpn.data.body.AppConfigBody
import retrofit2.http.Body
import retrofit2.http.POST

interface MainApiService {
    @POST("/v1/app/link/list")
    suspend fun getAppConfig(@Body appConfigBody: AppConfigBody): ApiResponse<AppConfigModel>
}