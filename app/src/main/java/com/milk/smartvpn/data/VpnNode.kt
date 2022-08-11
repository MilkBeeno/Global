package com.milk.smartvpn.data

import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.visible
import com.milk.smartvpn.R
import com.milk.smartvpn.databinding.ItemSwitchNodeBinding
import com.milk.smartvpn.media.ImageLoader

class VpnNode : ItemBind {
    var nodeId: Int = 0
    var areaImage: String = ""
    var areaName: String = ""
    var ping: Int = 0
    var isSelect: Boolean = false
    var itemSize: Int = 0
    var position: Int = 0
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = ItemSwitchNodeBinding.bind(holder.itemView)
        binding.root.setBackgroundResource(
            if (position == itemSize - 1)
                R.drawable.shape_switch_node_footer
            else
                R.drawable.shape_switch_node
        )
        ImageLoader.Builder()
            .request(areaImage)
            .target(binding.ivNodeImage)
            .build()
        binding.ivNodeSelect.isSelected = isSelect
        binding.tvNodeName.text = areaName
        binding.tvPing.text = ping.toString().plus("ms")
        if (position == itemSize - 1) binding.vLine.gone() else binding.vLine.visible()
    }
}