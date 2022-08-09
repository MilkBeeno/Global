package com.milk.smartvpn.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VpnModel(
    @SerializedName("b01")
    val nodeId: Long = 0,
    @SerializedName("b02")
    val nodeName: String = "",
    @SerializedName("b03")
    val dns: String = "",
    @SerializedName("b04")
    var userName: String = "",
    @SerializedName("b05")
    val password: String = "",
    @SerializedName("b06")
    var certificate: Int = 0
) : Serializable