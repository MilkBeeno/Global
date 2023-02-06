package com.milk.global.data

data class AdModel(
    val adv: Any,
    val enabled: Boolean,
    val interval: Int,
    val pos: Map<String, AdPositionListModel>
)