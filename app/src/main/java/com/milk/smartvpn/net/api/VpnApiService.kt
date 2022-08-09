package com.milk.smartvpn.net.api

import com.milk.smartvpn.data.ApiResponse
import com.milk.smartvpn.data.VpnListModel
import com.milk.smartvpn.data.VpnModel
import com.milk.smartvpn.net.reteceptor.ApiHeaderInterceptor.Companion.PARAMS_IN_PATH
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface VpnApiService {

    @FormUrlEncoded
    @Headers("${PARAMS_IN_PATH}:true")
    @POST("/app/api/v1/c03/c0001")
    suspend fun getVpnInfo(@Field("id") id: Long): ApiResponse<VpnModel>

    @Headers("${PARAMS_IN_PATH}:true")
    @POST("/app/api/v1/c03/c0001")
    suspend fun getVpnListInfo(): ApiResponse<MutableList<VpnListModel>>
}