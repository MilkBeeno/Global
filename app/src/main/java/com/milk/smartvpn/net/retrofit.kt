package com.milk.smartvpn.net

import com.milk.smartvpn.data.ApiResponse

suspend fun <T> retrofit(action: suspend () -> ApiResponse<T>): ApiResponse<T> {
    return try {
        val response = action()
        when (response.code) {
            200 -> {
                response.success = true
            }
            else -> response.success = false
        }
        response
    } catch (e: Exception) {
        ApiResponse(-1, e.message.toString())
    }
}