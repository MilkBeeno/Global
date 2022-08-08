package com.milk.smartvpn.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.statusBarPadding
import com.milk.smartvpn.databinding.ActivityMainBinding

class MainActivity : AbstractActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
    }

    private fun initializeView() {
        immersiveStatusBar(false)
        binding.llHeaderToolbar.statusBarPadding()
        binding.llHeaderToolbar.setOnClickListener(this)
        binding.ivShare.setOnClickListener(this)
        binding.llNetwork.setOnClickListener(this)
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.llHeaderToolbar -> AboutActivity.create(this)
            binding.ivShare -> toShareAppStoreAddress()
            binding.llNetwork -> {}
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