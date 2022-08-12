package com.milk.smartvpn.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.milk.smartvpn.databinding.ActivityResultBinding
import com.milk.smartvpn.ui.vm.ResultViewModel

class ResultActivity : AbstractActivity() {
    private val binding by lazy { ActivityResultBinding.inflate(layoutInflater) }
    private val resultViewModel by viewModels<ResultViewModel>()
    private val isSuccess by lazy { intent.getBooleanExtra(IS_SUCCESS, false) }
    private val vpnImage by lazy { intent.getStringExtra(VPN_IMAGE).toString() }
    private val vpnName by lazy { intent.getStringExtra(VPN_NAME).toString() }
    private val vpnPing by lazy { intent.getLongExtra(VPN_PING, 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
    }

    private fun initializeView() {

    }

    companion object {
        private const val IS_SUCCESS = "IS_SUCCESS"
        private const val VPN_IMAGE = "VPN_IMAGE"
        private const val VPN_NAME = "VPN_NAME"
        private const val VPN_PING = "VPN_PING"
        fun create(
            context: Context,
            isSuccess: Boolean,
            vpnImage: String,
            vpnName: String,
            vpnPing: Long
        ) {
            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(IS_SUCCESS, isSuccess)
            intent.putExtra(VPN_IMAGE, vpnImage)
            intent.putExtra(VPN_NAME, vpnName)
            intent.putExtra(VPN_PING, vpnPing)
            context.startActivity(intent)
        }
    }
}