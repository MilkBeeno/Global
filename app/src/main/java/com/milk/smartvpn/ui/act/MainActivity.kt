package com.milk.smartvpn.ui.act

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.milk.simple.ktx.*
import com.milk.smartvpn.R
import com.milk.smartvpn.databinding.ActivityMainBinding
import com.milk.smartvpn.media.ImageLoader
import com.milk.smartvpn.proxy.VpnProxy
import com.milk.smartvpn.ui.dialog.WaitDialog
import com.milk.smartvpn.ui.type.VpnStatus
import com.milk.smartvpn.ui.vm.VpnViewModel

class MainActivity : AbstractActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val vpnViewModel by viewModels<VpnViewModel>()
    private lateinit var vpnProxy: VpnProxy
    private val dialog by lazy { WaitDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeObserver()
        vpnProxy = VpnProxy(this)
    }

    private fun initializeView() {
        immersiveStatusBar(false)
        binding.llHeaderToolbar.statusBarPadding()
        binding.llHeaderToolbar.setOnClickListener(this)
        binding.ivShare.setOnClickListener(this)
        binding.llNetwork.setOnClickListener(this)
        binding.tvConnect.setOnClickListener(this)
        binding.llNetwork.setOnClickListener(this)
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
                    vpnViewModel.startTiming { binding.tvConnectTime.text = it }
                    binding.tvConnect
                        .setBackgroundResource(R.drawable.shape_main_connected)
                    binding.tvConnect.setTextColor(color(R.color.FF121250))
                }
            })
    }

    private fun initializeObserver() {
        vpnViewModel.connectionState.collectLatest(this) {
            when (it) {
                VpnStatus.NotConnect -> vpnNotConnect()
                VpnStatus.Connecting -> vpnConnecting()
                VpnStatus.Connected -> vpnConnected()
                VpnStatus.Failure -> vpnNotConnect()
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
            binding.llHeaderToolbar -> AboutActivity.create(this)
            binding.ivShare -> toShareAppStoreAddress()
            binding.llNetwork -> SwitchNodeActivity.create(
                this,
                vpnViewModel.currentNodeId,
                vpnViewModel.currentConnected
            )
            binding.tvConnect -> {
                when (vpnViewModel.connectionState.value) {
                    VpnStatus.NotConnect -> vpnProxy.openVpn()
                    VpnStatus.Connected -> vpnProxy.closeVpn()
                    else -> Unit
                }
            }
        }
    }

    private fun toShareAppStoreAddress() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, "文本内容")
        intent.type = "text/plain"
        startActivity(intent)
    }

    override fun onInterceptKeyDownEvent() = true

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, MainActivity::class.java))
    }
}