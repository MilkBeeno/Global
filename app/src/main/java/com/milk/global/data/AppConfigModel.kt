package com.milk.global.data

import com.google.gson.annotations.SerializedName

data class AppConfigModel(
    var share_copy: String = "",
    var Shield_app_bundle: String = "",
    @SerializedName("V12345671")
    var mainNativeAd: String = "",
    @SerializedName("V12345672")
    var openAd: String = "",
    @SerializedName("V12345673")
    var connectedInsertAd: String = "",
    @SerializedName("V12345674")
    var connectedNativeAd: String = "",
    @SerializedName("V12345675")
    var disconnectInsertAd: String = "",
    @SerializedName("V12345676")
    var disconnectNativeAd: String = "",
    @SerializedName("V12345677")
    var switchNativeAd: String = "",
)