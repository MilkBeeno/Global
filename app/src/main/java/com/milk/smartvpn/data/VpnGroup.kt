package com.milk.smartvpn.data

import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.drake.brv.item.ItemExpand
import com.drake.brv.item.ItemHover
import com.drake.brv.item.ItemPosition
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.visible
import com.milk.smartvpn.R
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
        binding.root.setBackgroundResource(
            if (itemExpand)
                R.drawable.shape_switch_group_expand
            else
                R.drawable.shape_switch_group_not_expand
        )
        ImageLoader.Builder()
            .request(areaImage)
            .target(binding.ivGroupImage)
            .build()
        binding.tvGroupName.text = areaName
            .plus("(")
            .plus(itemSublist?.size.toString())
            .plus(")")
        binding.ivGroupSelect.isSelected = isSelect
        binding.ivGroupExpand.setImageResource(
            if (itemExpand)
                R.drawable.switch_node_packup
            else
                R.drawable.switch_node_expand
        )
        if (itemExpand) binding.vLine.visible() else binding.vLine.gone()
    }
}