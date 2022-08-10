package com.milk.smartvpn.data

import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.drake.brv.item.ItemExpand
import com.drake.brv.item.ItemHover
import com.drake.brv.item.ItemPosition
import com.milk.smartvpn.databinding.ItemSwitchGroupBinding
import com.milk.smartvpn.media.ImageLoader

class VpnGroup : ItemExpand, ItemHover, ItemPosition, ItemBind {
    var areaImage: String = ""
    var areaName: String = ""
    var isSelect: Boolean = false
    override var itemExpand: Boolean = false
    override var itemGroupPosition: Int = 0
    override var itemSublist: List<Any?>? = null
    override var itemHover: Boolean = true
    override var itemPosition: Int = 0

    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = ItemSwitchGroupBinding.bind(holder.itemView)
        val item = itemSublist?.get(holder.layoutPosition)
        if (item != null) {
            try {
                val vpnGroup = item as VpnGroup
                ImageLoader.Builder()
                    .request(vpnGroup.areaImage)
                    .target(binding.ivGroupImage)
                    .build()
                binding.tvGroupName.text = vpnGroup.areaName
                binding.ivGroupExpand
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}