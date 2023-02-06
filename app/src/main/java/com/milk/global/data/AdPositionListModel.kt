package com.milk.global.data

import com.google.gson.annotations.SerializedName

data class AdPositionListModel(
    val cfg: Any,
    @SerializedName("position")
    val positionListModel: List<AdPositionModel>
)