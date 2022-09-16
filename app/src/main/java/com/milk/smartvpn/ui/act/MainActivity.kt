package com.milk.smartvpn.ui.act

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.NotificationManagerCompat
import com.milk.simple.ktx.*
import com.milk.smartvpn.R
import com.milk.smartvpn.databinding.ActivityMainBinding
import com.milk.smartvpn.friebase.FireBaseManager
import com.milk.smartvpn.friebase.FirebaseKey
import com.milk.smartvpn.media.ImageLoader
import com.milk.smartvpn.proxy.VpnProxy
import com.milk.smartvpn.repository.DataRepository
import com.milk.smartvpn.ui.dialog.FailureDialog
import com.milk.smartvpn.ui.dialog.OpenNotificationDialog
import com.milk.smartvpn.ui.dialog.WaitDialog
import com.milk.smartvpn.ui.type.VpnStatus
import com.milk.smartvpn.ui.vm.VpnViewModel
import com.milk.smartvpn.util.Notification

class MainActivity : AbstractActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val vpnViewModel by viewModels<VpnViewModel>()
    private lateinit var vpnProxy: VpnProxy
    private val loadAdDialog by lazy { WaitDialog(this) }
    private val failureDialog by lazy { FailureDialog(this) }
    private val openNotificationDialog by lazy { OpenNotificationDialog(this) }
    private var currentTime: Long = 0

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
        binding.lottieViewConnecting.setAnimation("main_vpn_connecting.json")
        binding.lottieViewConnected.setAnimation("main_vpn_connected.json")
        binding.lottieViewConnected
            .addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) = Unit
                override fun onAnimationCancel(animation: Animator?) = Unit
                override fun onAnimationRepeat(animation: Animator?) = Unit
                override fun onAnimationEnd(animation: Animator?) {
                    binding.ivBackground.visible()
                    binding.ivConnect.visible()
                    binding.ivConnect
                        .setBackgroundResource(R.drawable.main_connected)
                    binding.lottieViewConnected.gone()
                    binding.tvConnectTime.visible()
                    binding.tvConnect.text = string(R.string.main_connected)
                    vpnViewModel.startTiming(
                        successRequest = {
                            binding.tvConnectTime.text = it
                            if (NotificationManagerCompat.from(this@MainActivity)
                                    .areNotificationsEnabled()
                            ) {
                                when (vpnViewModel.vpnConnectDuration) {
                                    40 * 60L -> {
                                        Notification.showConnectedNotification(
                                            this@MainActivity,
                                            "The latest high-speed nodes have been updated",
                                            "Click to view now."
                                        )
                                    }
                                    20 * 60L -> {
                                        Notification.showConnectedNotification(
                                            this@MainActivity,
                                            "The current node has high latency",
                                            "Click to switch nodes now."
                                        )
                                    }
                                }
                            }
                        },
                        finishRequest = {
                            vpnProxy.closeVpn()
                        })
                    binding.tvConnect
                        .setBackgroundResource(R.drawable.shape_main_connected)
                    binding.tvConnect.setTextColor(color(R.color.FF121250))
                    vpnConnectResult(true)
                    if (NotificationManagerCompat.from(this@MainActivity)
                            .areNotificationsEnabled()
                    ) {
                        Notification.showConnectedNotification(
                            this@MainActivity,
                            vpnViewModel.currentName.ifBlank { "United States" })
                    }
                }
            })
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            openNotificationDialog.show()
            openNotificationDialog.setConfirm {
                Notification.obtainNotification(this)
            }
        }
    }

    private fun initializeObserver() {
        vpnViewModel.loadMainNativeAd(this)
        vpnViewModel.loadNativeAdByTimer(this)
        vpnViewModel.mainNativeAd.collectLatest(this) {
            val nativeAd = it.second
            if (nativeAd != null) {
                binding.nativeView.visible()
                binding.nativeView.showNativeAd(nativeAd.nativeAd)
            }
        }
        vpnViewModel.connectionState.collectLatest(this) {
            when (it) {
                VpnStatus.NotConnect -> vpnNotConnect()
                VpnStatus.Connecting -> vpnConnecting()
                VpnStatus.Connected -> vpnConnected()
                VpnStatus.Failure -> {
                    FireBaseManager.logEvent(FirebaseKey.CONNECT_FAILED)
                    failureDialog.show()
                    vpnNotConnect()
                }
            }
        }
    }

    private fun vpnNotConnect() {
        updateConnectInfo()
        binding.ivBackground.visible()
        binding.ivConnect.visible()
        binding.ivConnect
            .setBackgroundResource(R.drawable.main_not_connect)
        binding.lottieViewConnecting.gone()
        binding.lottieViewConnecting.pauseAnimation()
        binding.lottieViewConnected.gone()
        binding.lottieViewConnected.pauseAnimation()
        binding.tvConnectTime.gone()
        binding.tvConnect.text = string(R.string.main_not_connect)
        binding.tvConnect
            .setBackgroundResource(R.drawable.shape_main_not_connect)
        binding.tvConnect.setTextColor(color(R.color.white))
        if (vpnViewModel.showResultPage) {
            vpnConnectResult(false)
            vpnViewModel.showResultPage = false
        }
    }

    private fun vpnConnecting() {
        binding.ivBackground.gone()
        binding.ivConnect.gone()
        binding.lottieViewConnecting.visible()
        binding.lottieViewConnecting.playAnimation()
        binding.lottieViewConnected.gone()
        binding.lottieViewConnected.pauseAnimation()
        binding.tvConnectTime.gone()
        binding.tvConnect.text = string(R.string.main_connecting)
        binding.tvConnect
            .setBackgroundResource(R.drawable.shape_main_connecting)
        binding.tvConnect.setTextColor(color(R.color.FFC5C8E4))
    }

    private fun vpnConnected() {
        updateConnectInfo()
        binding.lottieViewConnecting.gone()
        binding.lottieViewConnecting.pauseAnimation()
        binding.lottieViewConnected.visible()
        binding.lottieViewConnected.playAnimation()
    }

    /** 连接结果就是 1.加载广告 2.显示结果页面 */
    private fun vpnConnectResult(isConnected: Boolean) {
        loadAdDialog.setContent(
            string(if (isConnected) R.string.main_connect_now else R.string.main_disconnect_now)
        )
        loadAdDialog.show()
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
                loadAdDialog.dismiss()
                ResultActivity.create(
                    this,
                    isConnected,
                    vpnViewModel.currentImageUrl,
                    vpnViewModel.currentName,
                    vpnViewModel.currentPing
                )
            }
        } else {
            vpnViewModel.loadDisconnectNativeAd(this) {
                loadAdDialog.dismiss()
                ResultActivity.create(
                    this,
                    isConnected,
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
                    VpnStatus.Connected -> vpnProxy.closeVpn()
                    else -> {
                        FireBaseManager.logEvent(FirebaseKey.CLICK_TO_CONNECT_NODE)
                        vpnProxy.openVpn()
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