package com.milk.smartvpn.data

import com.google.gson.annotations.SerializedName

data class AdPositionListModel(
    val cfg: Any,
    @SerializedName("position")
    val positionListModel: List<AdPositionModel>
)