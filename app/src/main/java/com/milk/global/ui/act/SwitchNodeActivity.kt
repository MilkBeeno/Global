package com.milk.global.ui.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.global.R
import com.milk.global.constant.EventKey
import com.milk.global.data.VpnGroup
import com.milk.global.data.VpnNode
import com.milk.global.databinding.ActivitySwitchNodeBinding
import com.milk.global.friebase.FireBaseManager
import com.milk.global.friebase.FirebaseKey
import com.milk.global.ui.vm.SwitchNodeViewModel
import com.milk.simple.ktx.collectLatest
import java.util.*

class SwitchNodeActivity : AbstractActivity() {
    private val binding by lazy { ActivitySwitchNodeBinding.inflate(layoutInflater) }
    private val switchNodeViewModel by viewModels<SwitchNodeViewModel>()
    private val currentNodeId by lazy { intent.getLongExtra(CURRENT_NODE_ID, 0) }
    private val currentConnected by lazy { intent.getBooleanExtra(CURRENT_CONNECTED, false) }
    private val random by lazy { Random() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        loadNativeAd()
        initializeData()
    }

    private fun initializeView() {
        switchNodeViewModel.currentNodeId = currentNodeId
        switchNodeViewModel.currentConnected = currentConnected
        binding.ivBack.setOnClickListener(this)
        binding.ivRefresh.setOnClickListener(this)
        switchNodeViewModel.vpnGroups.collectLatest(this) {
            binding.rvNode.linear().setup {
                addType<VpnGroup>(R.layout.item_switch_group)
                addType<VpnNode>(R.layout.item_switch_node)
                R.id.ivGroupExpand.onClick {
                    expandOrCollapse()
                }
                R.id.ivGroupSelect.onClick {
                    val vpnGroup = this._data as VpnGroup
                    if (vpnGroup.isSelect) return@onClick
                    val nodes = vpnGroup.itemSublist
                    when {
                        nodes != null && nodes.isNotEmpty() -> {
                            FireBaseManager.logEvent(FirebaseKey.CLICK_ON_NORMAL_NODE)
                            if (currentConnected)
                                FireBaseManager.logEvent(FirebaseKey.CLICK_TO_SWITCH_NODE)
                            val index = random.nextInt(nodes.size)
                            val node = nodes[index] as VpnNode
                            LiveEventBus.get<ArrayList<String>>(EventKey.SWITCH_VPN_NODE)
                                .post(
                                    arrayListOf(
                                        node.nodeId.toString(),
                                        node.areaImage,
                                        node.areaName,
                                        node.ping.toString()
                                    )
                                )
                        }
                        else -> {
                            FireBaseManager.logEvent(FirebaseKey.CLICK_ON_THE_AUTOMATIC_NODE)
                            LiveEventBus.get<ArrayList<String>>(EventKey.SWITCH_VPN_NODE)
                                .post(arrayListOf("0", "", "", "0"))
                        }
                    }
                    finish()
                }
                R.id.ivNodeSelect.onClick {
                    val node = this._data as VpnNode
                    if (node.isSelect) return@onClick
                    FireBaseManager.logEvent(FirebaseKey.CLICK_ON_NORMAL_NODE)
                    if (currentConnected)
                        FireBaseManager.logEvent(FirebaseKey.CLICK_TO_SWITCH_NODE)
                    LiveEventBus.get<ArrayList<String>>(EventKey.SWITCH_VPN_NODE)
                        .post(
                            arrayListOf(
                                node.nodeId.toString(),
                                node.areaImage,
                                node.areaName,
                                node.ping.toString()
                            )
                        )
                    finish()
                }
            }.models = it
        }
    }

    private fun loadNativeAd() {
        binding.nativeView.setLoadFailureRequest {

        }
        binding.nativeView.setLoadSuccessRequest {

        }
        binding.nativeView.setClickRequest {

        }
        binding.nativeView.loadNativeAd()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initializeData() {
        switchNodeViewModel.getVpnListInfo()
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivBack -> finish()
            binding.ivRefresh -> switchNodeViewModel.getVpnListInfo()
        }
    }

    companion object {
        private const val CURRENT_CONNECTED = "CURRENT_CONNECTED"
        private const val CURRENT_NODE_ID = "CURRENT_NODE_ID"
        fun create(context: Context, currentNodeId: Long, currentConnected: Boolean) {
            val intent = Intent(context, SwitchNodeActivity::class.java)
            intent.putExtra(CURRENT_NODE_ID, currentNodeId)
            intent.putExtra(CURRENT_CONNECTED, currentConnected)
            context.startActivity(intent)
        }
    }
}