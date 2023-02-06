package com.milk.global.data

import com.google.gson.annotations.SerializedName

data class AdPositionModel(
    @SerializedName("ad_type")
    val adType: Int,
    val adv: String,
    @SerializedName("pos_id")
    var posId: String,
    val ratio: Int,
    @SerializedName("type_ratio")
    val typeRatio: Int,
    var placementId: String,
    @SerializedName("limit_show_count")
    val limitShowCount: Int,
    @SerializedName("limit_click_count")
    val limitClickCount: Int
)