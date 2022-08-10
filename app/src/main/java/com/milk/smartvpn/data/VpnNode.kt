package com.milk.smartvpn.data

import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.milk.smartvpn.databinding.ItemSwitchNodeBinding

class VpnNode : ItemBind {
    var nodeId: Int = 0
    var areaImage: String = ""
    var areaName: String = ""
    var ping: Int = 0
    var isSelect: Boolean = false
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val bind = ItemSwitchNodeBinding.bind(holder.itemView)
    }
}