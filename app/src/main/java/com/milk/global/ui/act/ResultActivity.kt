package com.milk.global.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.milk.global.R
import com.milk.global.databinding.ActivityResultBinding
import com.milk.global.friebase.FireBaseManager
import com.milk.global.friebase.FirebaseKey
import com.milk.global.media.ImageLoader
import com.milk.simple.ktx.color
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.string
import com.milk.simple.ktx.visible

class ResultActivity : AbstractActivity() {
    private val binding by lazy { ActivityResultBinding.inflate(layoutInflater) }
    private val vpnImage by lazy { intent.getStringExtra(VPN_IMAGE).toString() }
    private val vpnName by lazy { intent.getStringExtra(VPN_NAME).toString() }
    private val vpnPing by lazy { intent.getLongExtra(VPN_PING, 0) }
    private val isConnected by lazy { intent.getBooleanExtra(IS_CONNECTED, false) }

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
        } else {
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
        // 原生广告展示和统计事件
        if (isConnected) {
            FireBaseManager.logEvent(FirebaseKey.Make_an_ad_request_5)
        } else {
            FireBaseManager.logEvent(FirebaseKey.Make_an_ad_request_6)
        }
        binding.nativeView.setLoadFailureRequest {
            if (isConnected) {
                FireBaseManager.logEvent(FirebaseKey.Ad_request_failed_5)
            } else {
                FireBaseManager.logEvent(FirebaseKey.Ad_request_failed_6, it)
            }
        }
        binding.nativeView.setLoadSuccessRequest {
            if (isConnected) {
                FireBaseManager.logEvent(FirebaseKey.Ad_request_succeeded_5)
            } else {
                FireBaseManager.logEvent(FirebaseKey.Ad_request_succeeded_6)
            }
        }
        binding.nativeView.setClickRequest {
            if (isConnected) {
                FireBaseManager.logEvent(FirebaseKey.click_ad_5)
            } else {
                FireBaseManager.logEvent(FirebaseKey.click_ad_6)
            }
        }
        binding.nativeView.loadNativeAd()
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