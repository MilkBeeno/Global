package com.milk.global.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.global.R
import com.milk.global.constant.EventKey
import com.milk.global.databinding.ActivityMainBinding
import com.milk.global.friebase.FireBaseManager
import com.milk.global.friebase.FirebaseKey
import com.milk.global.media.ImageLoader
import com.milk.global.proxy.VpnProxy
import com.milk.global.repository.DataRepository
import com.milk.global.ui.dialog.ConnectingDialog
import com.milk.global.ui.dialog.DisConnectDialog
import com.milk.global.ui.dialog.FailureDialog
import com.milk.global.ui.type.VpnState
import com.milk.global.ui.vm.VpnViewModel
import com.milk.simple.ktx.*

class MainActivity : AbstractActivity() {
    private val vpnViewModel by viewModels<VpnViewModel>()
    private val vpnProxy by lazy { VpnProxy(this) }
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private var currentTime: Long = 0

    // 连接状态弹窗
    private val connectingDialog by lazy { ConnectingDialog(this) }
    private val disconnectDialog by lazy { DisConnectDialog(this) }
    private val connectFailureDialog by lazy { FailureDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        FireBaseManager.logEvent(FirebaseKey.ENTER_MAIN_PAGE)
        initializeView()
        initializeObserver()
    }

    private fun initializeView() {
        immersiveStatusBar(false)
        binding.llHeaderToolbar.statusBarPadding()
        binding.ivMenu.setOnClickListener(this)
        binding.ivShare.setOnClickListener(this)
        binding.llNetwork.setOnClickListener(this)
        binding.tvConnect.setOnClickListener(this)
        binding.llNetwork.setOnClickListener(this)
        binding.ivConnect.setOnClickListener(this)

        openVpnListener()
        vpnStateChangedListener()
        vpnDisconnect(false)
    }

    private fun openVpnListener() {
        vpnProxy.setVpnOpenedListener {
            connectingDialog.show()
            vpnViewModel.getVpnProfileInfo {
                vpnProxy.connectVpn(it, vpnViewModel.vpnIsConnected)
            }
        }
    }

    private fun vpnStateChangedListener() {
        vpnProxy.setVpnStateChangedListener { vpnState, success ->
            if (success) {
                when (vpnState) {
                    VpnState.DISCONNECT -> {
                        vpnDisconnect()
                    }
                    VpnState.CONNECTED -> {
                        vpnConnected()
                    }
                    else -> Unit
                }
            } else {
                connectingDialog.dismiss()
                disconnectDialog.dismiss()
                connectFailureDialog.show()
                vpnDisconnect(false)
                FireBaseManager.logEvent(FirebaseKey.CONNECT_FAILED)
            }
            vpnViewModel.vpnIsConnected = success && vpnState == VpnState.CONNECTED
        }
    }

    private fun initializeObserver() {
        vpnViewModel.loadMainNativeAd(this)
        vpnViewModel.loadNativeAdByTimer(this)
        // 原生广告
        vpnViewModel.mainNativeAd.collectLatest(this) {
            binding.nativeView.visible()
//            if (it.nativeAd != null) {
//                binding.nativeView.showNativeAd(AdType.Main, it.nativeAd)
//            }
        }
        // 切换 VPN 节点
        LiveEventBus.get<ArrayList<String>>(EventKey.SWITCH_VPN_NODE)
            .observe(this) {
                vpnViewModel.vpnNodeId = it[0].toLong()
                vpnViewModel.vpnImageUrl = it[1]
                vpnViewModel.vpnName = it[2]
                vpnViewModel.vpnPing = it[3].toLong()
                updateConnectInfo()
                vpnProxy.tryOpenVpn()
            }
    }

    private fun vpnDisconnect(showResult: Boolean = true) {
        updateConnectInfo()
        binding.root.setBackgroundResource(R.drawable.main_disconnect_background)
        binding.ivConnect.setBackgroundResource(R.drawable.main_not_connect)
        binding.tvConnect.text = string(R.string.main_disconnect)
        binding.tvConnect.setBackgroundResource(R.drawable.shape_main_disconnect)
        binding.tvConnect.setTextColor(color(R.color.white))
        if (showResult && vpnViewModel.vpnIsConnected) {
            vpnConnectResult(false)
        } else {
            connectingDialog.dismiss()
            disconnectDialog.dismiss()
        }
    }

    private fun vpnConnected() {
        updateConnectInfo()
        binding.root.setBackgroundResource(R.drawable.main_connected_background)
        binding.ivConnect.setBackgroundResource(R.drawable.main_connected)
        binding.tvConnect.text = string(R.string.main_connected)
        binding.tvConnect.setBackgroundResource(R.drawable.shape_main_connected)
        binding.tvConnect.setTextColor(color(R.color.FF0C9AFF))
        vpnConnectResult(true)
    }

    /** 连接结果就是 1.加载广告 2.显示结果页面 */
    private fun vpnConnectResult(isConnected: Boolean) {
        if (isConnected) {
            FireBaseManager.logEvent(FirebaseKey.CONNECT_SUCCESSFULLY)
            when ((System.currentTimeMillis() - currentTime)) {
                in 0L until 3000L -> {
                    FireBaseManager.logEvent(FirebaseKey.CONNECTION_SUCCESSFUL_WITHIN_3S)
                }
                in 3L until 8000L -> {
                    FireBaseManager.logEvent(FirebaseKey.CONNECTION_SUCCESSFUL_WITHIN_3_8S)
                }
                in 8L until 15000L -> {
                    FireBaseManager.logEvent(FirebaseKey.CONNECTION_SUCCESSFUL_WITHIN_8_15S)
                }
                else -> {
                    FireBaseManager.logEvent(FirebaseKey.CONNECTION_SUCCESSFUL_FOR_MORE_THAN_15S)
                }
            }
        }
        vpnViewModel.loadInterstitialAd(this) {
            connectingDialog.dismiss()
            disconnectDialog.dismiss()
            ResultActivity.create(
                this,
                isConnected,
                vpnViewModel.vpnImageUrl,
                vpnViewModel.vpnName,
                vpnViewModel.vpnPing
            )
        }
    }

    private fun updateConnectInfo() {
        if (vpnViewModel.vpnNodeId > 0) {
            ImageLoader.Builder()
                .request(vpnViewModel.vpnImageUrl)
                .target(binding.ivNetwork)
                .build()
            binding.tvNetwork.text = vpnViewModel.vpnName
        } else {
            binding.ivNetwork.setImageResource(R.drawable.main_network)
            binding.tvNetwork.text = string(R.string.common_auto_select)
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivMenu -> {
                AboutActivity.create(this)
                FireBaseManager.logEvent(FirebaseKey.CLICK_ON_MORE)
            }
            binding.ivShare -> {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, DataRepository.shareAppUrl.value)
                intent.type = "text/plain"
                startActivity(intent)
                FireBaseManager.logEvent(FirebaseKey.CLICK_THE_SHARE)
            }
            binding.llNetwork -> {
                currentTime = System.currentTimeMillis()
                SwitchNodeActivity.create(
                    this,
                    vpnViewModel.vpnNodeId,
                    vpnViewModel.vpnIsConnected
                )
                FireBaseManager.logEvent(FirebaseKey.CLICK_ON_THE_NODE_LIST_ENTRY)
            }
            binding.ivConnect,
            binding.tvConnect -> {
                if (vpnViewModel.vpnIsConnected) {
                    disconnectDialog.show()
                    binding.tvConnect.postDelayed({
                        vpnProxy.closeVpn()
                    }, 4000)
                } else {
                    vpnProxy.tryOpenVpn()
                    FireBaseManager.logEvent(FirebaseKey.CLICK_TO_CONNECT_NODE)
                }
            }
        }
    }

    override fun onInterceptKeyDownEvent() = true

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
}