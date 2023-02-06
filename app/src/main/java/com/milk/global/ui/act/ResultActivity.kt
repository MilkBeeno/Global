package com.milk.global.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.milk.simple.ktx.*
import com.milk.global.R
import com.milk.global.ad.ui.AdType
import com.milk.global.databinding.ActivityResultBinding
import com.milk.global.media.ImageLoader
import com.milk.global.repository.DataRepository
import com.milk.global.ui.vm.ResultViewModel

class ResultActivity : AbstractActivity() {
    private val binding by lazy { ActivityResultBinding.inflate(layoutInflater) }
    private val resultViewModel by viewModels<ResultViewModel>()
    private val isConnected by lazy { intent.getBooleanExtra(IS_CONNECTED, false) }
    private val vpnImage by lazy { intent.getStringExtra(VPN_IMAGE).toString() }
    private val vpnName by lazy { intent.getStringExtra(VPN_NAME).toString() }
    private val vpnPing by lazy { intent.getLongExtra(VPN_PING, 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
    }

    private fun initializeView() {
        binding.ivBack.setOnClickListener { finish() }
        if (isConnected) {
            binding.ivResult.setBackgroundResource(R.drawable.result_connected)
            binding.tvResult.text = string(R.string.result_connected)
            binding.tvResult.setTextColor(color(R.color.FF0DC2FF))
            resultViewModel.loadNativeSuccess(this)
            DataRepository.connectSuccessAd.collectLatest(this) {
                val native = it.second
                if (native != null) {
                    binding.nativeView.visible()
                    binding.nativeView.showNativeAd(AdType.Connected, native)
                }
            }
        } else {
            resultViewModel.loadNativeFailure(this)
            DataRepository.disconnectAd.collectLatest(this) {
                val native = it.second
                if (native != null) {
                    binding.nativeView.visible()
                    binding.nativeView.showNativeAd(AdType.DisConnect, native)
                }
            }
            binding.ivResult.setBackgroundResource(R.drawable.result_disconnect)
            binding.tvResult.text = string(R.string.result_failure)
            binding.tvResult.setTextColor(color(R.color.FFFEB72A))
        }
        if (vpnImage.isNotBlank())
            ImageLoader.Builder()
                .request(vpnImage)
                .target(binding.ivNetwork)
                .build()
        else binding.ivNetwork.setImageResource(R.drawable.main_network)
        binding.tvNetwork.text =
            vpnName.ifBlank { string(R.string.common_auto_select) }
        if (vpnPing > 0) {
            binding.tvPing.visible()
            binding.tvPingTag.visible()
            binding.tvPing.text = vpnPing.toString().plus("ms")
        } else {
            binding.tvPing.gone()
            binding.tvPingTag.gone()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        resultViewModel.destroy()
    }

    companion object {
        private const val IS_CONNECTED = "IS_CONNECTED"
        private const val VPN_IMAGE = "VPN_IMAGE"
        private const val VPN_NAME = "VPN_NAME"
        private const val VPN_PING = "VPN_PING"
        fun create(
            context: Context,
            isConnected: Boolean,
            vpnImage: String,
            vpnName: String,
            vpnPing: Long
        ) {
            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(IS_CONNECTED, isConnected)
            intent.putExtra(VPN_IMAGE, vpnImage)
            intent.putExtra(VPN_NAME, vpnName)
            intent.putExtra(VPN_PING, vpnPing)
            context.startActivity(intent)
        }
    }
}