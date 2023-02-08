package com.milk.global.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.NotificationManagerCompat
import com.milk.global.R
import com.milk.global.ad.ui.AdType
import com.milk.global.databinding.ActivityMainBinding
import com.milk.global.friebase.FireBaseManager
import com.milk.global.friebase.FirebaseKey
import com.milk.global.media.ImageLoader
import com.milk.global.proxy.VpnProxy
import com.milk.global.repository.DataRepository
import com.milk.global.ui.dialog.ConnectingDialog
import com.milk.global.ui.dialog.DisConnectDialog
import com.milk.global.ui.dialog.FailureDialog
import com.milk.global.ui.dialog.OpenNotificationDialog
import com.milk.global.ui.type.VpnStatus
import com.milk.global.ui.vm.VpnViewModel
import com.milk.global.util.Notification
import com.milk.simple.ktx.*

class MainActivity : AbstractActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val vpnViewModel by viewModels<VpnViewModel>()
    private lateinit var vpnProxy: VpnProxy
    private val failureDialog by lazy { FailureDialog(this) }
    private val openNotificationDialog by lazy { OpenNotificationDialog(this) }
    private var currentTime: Long = 0

    // 连接状态弹窗
    private val connectingDialog by lazy { ConnectingDialog(this) }
    private val disconnectDialog by lazy { DisConnectDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        FireBaseManager.logEvent(FirebaseKey.ENTER_MAIN_PAGE)
        initializeView()
        initializeObserver()
        vpnProxy = VpnProxy(this)
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

        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            openNotificationDialog.show()
            openNotificationDialog.setConfirm {
                Notification.obtainNotification(this)
            }
            openNotificationDialog.setCancel {
                BackStackActivity.create(this)
            }
        }
    }

    private fun initializeObserver() {
        vpnViewModel.loadMainNativeAd(this)
        vpnViewModel.loadNativeAdByTimer(this)
        vpnViewModel.mainNativeAd.collectLatest(this) {
            val nativeAd = it.second?.nativeAd
            if (nativeAd != null) {
                binding.nativeView.visible()
                binding.nativeView.showNativeAd(AdType.Main, nativeAd)
            }
        }
        vpnViewModel.connectionState.collectLatest(this) {
            when (it) {
                VpnStatus.DisConnect -> {
                    disconnectDialog.dismiss()
                    vpnDisconnect()
                }
                VpnStatus.Connecting -> {
                    vpnDisconnect(false)
                }
                VpnStatus.Connected -> {
                    connectingDialog.dismiss()
                    vpnConnected()
                }
                VpnStatus.Failure -> {
                    connectingDialog.dismiss()
                    disconnectDialog.dismiss()
                    failureDialog.show()
                    vpnDisconnect(false)
                    FireBaseManager.logEvent(FirebaseKey.CONNECT_FAILED)
                }
                VpnStatus.Default -> {
                    vpnDisconnect(false)
                }
            }
        }
    }

    private fun vpnDisconnect(showResult: Boolean = true) {
        updateConnectInfo()
        binding.root.setBackgroundResource(R.drawable.main_disconnect_background)
        binding.ivConnect.setBackgroundResource(R.drawable.main_not_connect)
        binding.tvConnect.text = string(R.string.main_disconnect)
        binding.tvConnect.setBackgroundResource(R.drawable.shape_main_disconnect)
        binding.tvConnect.setTextColor(color(R.color.white))
        if (showResult) vpnConnectResult(false)
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
            vpnViewModel.showConnectedAd(this) {
                ResultActivity.create(
                    this,
                    true,
                    vpnViewModel.currentImageUrl,
                    vpnViewModel.currentName,
                    vpnViewModel.currentPing
                )
            }
        } else {
            DataRepository.loadDisconnectNativeAd(this) {
                ResultActivity.create(
                    this,
                    false,
                    vpnViewModel.currentImageUrl,
                    vpnViewModel.currentName,
                    vpnViewModel.currentPing
                )
            }
        }
    }

    private fun updateConnectInfo() {
        if (vpnViewModel.currentNodeId > 0) {
            ImageLoader.Builder()
                .request(vpnViewModel.currentImageUrl)
                .target(binding.ivNetwork)
                .build()
            binding.tvNetwork.text = vpnViewModel.currentName
        } else {
            binding.ivNetwork.setImageResource(R.drawable.main_network)
            binding.tvNetwork.text = string(R.string.common_auto_select)
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivMenu -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_ON_MORE)
                AboutActivity.create(this)
            }
            binding.ivShare -> {
                FireBaseManager.logEvent(FirebaseKey.CLICK_THE_SHARE)
                toShareAppStoreAddress()
            }
            binding.llNetwork -> {
                if (vpnViewModel.connectionState.value == VpnStatus.Connecting) return
                currentTime = System.currentTimeMillis()
                FireBaseManager.logEvent(FirebaseKey.CLICK_ON_THE_NODE_LIST_ENTRY)
                SwitchNodeActivity.create(
                    this,
                    vpnViewModel.currentNodeId,
                    vpnViewModel.currentConnected
                )
            }
            binding.ivConnect,
            binding.tvConnect -> {
                when (vpnViewModel.connectionState.value) {
                    VpnStatus.Connected -> {
                        disconnectDialog.show()
                        binding.tvConnect.postDelayed({ vpnProxy.closeVpn() }, 2000)
                    }
                    else -> {
                        connectingDialog.show()
                        vpnProxy.openVpn()
                        FireBaseManager.logEvent(FirebaseKey.CLICK_TO_CONNECT_NODE)
                    }
                }
            }
        }
    }

    private fun toShareAppStoreAddress() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, DataRepository.shareAppUrl.value)
        intent.type = "text/plain"
        startActivity(intent)
    }

    override fun onInterceptKeyDownEvent() = true

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, MainActivity::class.java))
    }
}