package com.milk.global.net

import com.milk.global.data.ApiResponse

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