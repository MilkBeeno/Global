package com.milk.smartvpn.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.milk.simple.ktx.collectLatest
import com.milk.smartvpn.R
import com.milk.smartvpn.data.VpnListModel
import com.milk.smartvpn.databinding.ActivitySwitchNodeBinding
import com.milk.smartvpn.ui.vm.SwitchNodeViewModel

class SwitchNodeActivity : AbstractActivity() {
    private val binding by lazy { ActivitySwitchNodeBinding.inflate(layoutInflater) }
    private val switchNodeViewModel by viewModels<SwitchNodeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeData()
    }

    private fun initializeView() {
        binding.ivBack.setOnClickListener(this)
        binding.ivRefresh.setOnClickListener(this)
        switchNodeViewModel.vpnGroups.collectLatest(this) {
            binding.rvNode.linear().setup {
                addType<VpnListModel>(R.layout.item_switch_group)
                addType<VpnListModel>(R.layout.item_switch_node)
                onBind {
                    when(itemViewType){

                    }
                   // val binding = ItemSwitchGroupBinding.bind(itemView)
                }
            }.models = it
        }
    }

    private fun initializeData() {
        switchNodeViewModel.getVpnListInfo()
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivBack -> finish()
            binding.ivRefresh ->
                switchNodeViewModel.getVpnListInfo()
        }
    }

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, SwitchNodeActivity::class.java))
    }
}