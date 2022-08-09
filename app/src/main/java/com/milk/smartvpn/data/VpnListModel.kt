package com.milk.smartvpn.data

import com.google.gson.annotations.SerializedName

data class VpnListModel(
    @SerializedName("b01")
    val nodeId: Long = 0,
    @SerializedName("b02")
    val areaCountry: String = "",
    @SerializedName("b03")
    val areaName: String = "",
    @SerializedName("b04")
    val areaImage: String = "",
    @SerializedName("b05")
    val nothing: Int = 0,
    @SerializedName("countryCode")
    val areaCode: String = "",
    @SerializedName("ipAddr")
    val nodeDns: String = "",
    @SerializedName("maxConn")
    val maxCount: Int = 0,
    @SerializedName("onlineConn")
    val onlineConnect: Int = 0,
    val ratio: Int = 8,
    var region: String = "",
    var serverName: String = "",
    var status: Int = 0,
    @SerializedName("totalConn")
    val totalConnect: Int = 0,
    var type: Int = 0,
    // 自定义本地延迟
    var ping: Int = 0
)