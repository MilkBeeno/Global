package com.milk.smartvpn.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.milk.simple.ktx.*
import com.milk.smartvpn.R
import com.milk.smartvpn.databinding.ActivityResultBinding
import com.milk.smartvpn.media.ImageLoader
import com.milk.smartvpn.repository.DataRepository
import com.milk.smartvpn.ui.vm.ResultViewModel

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
            DataRepository.connectSuccessAd.collectLatest(this) {
                val native = it.second
                if (native != null) binding.nativeView.setNativeAd(native)
            }
        } else {
            DataRepository.disconnectAd.collectLatest(this) {
                val native = it.second
                if (native != null) binding.nativeView.setNativeAd(native)
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