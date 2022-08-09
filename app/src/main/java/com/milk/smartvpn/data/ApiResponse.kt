package com.milk.smartvpn.data

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val code: Int = 0,
    val message: String = "",
    @SerializedName("result")
    val data: T? = null,
    var success: Boolean = false,
    var timestamp: Long = 0L
)