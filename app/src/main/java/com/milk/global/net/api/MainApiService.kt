package com.milk.global.net.api

import com.milk.global.data.ApiResponse
import com.milk.global.data.AppConfigModel
import com.milk.global.data.body.AppConfigBody
import retrofit2.http.Body
import retrofit2.http.POST

interface MainApiService {

    @POST("/v1/app/mobile/conf")
    suspend fun getAppConfig(@Body appConfigBody: AppConfigBody): ApiResponse<AppConfigModel>
}