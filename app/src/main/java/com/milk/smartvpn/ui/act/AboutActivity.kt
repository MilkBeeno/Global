package com.milk.smartvpn.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.statusBarPadding
import com.milk.smartvpn.BuildConfig
import com.milk.smartvpn.databinding.ActivityAboutBinding

class AboutActivity : AbstractActivity() {
    private val binding by lazy { ActivityAboutBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        immersiveStatusBar(false)
        binding.flHeaderToolbar.statusBarPadding()
        binding.tvVersion.text = "V ".plus(BuildConfig.VERSION_NAME)
        binding.ivBack.setOnClickListener(this)
        binding.llPrivacy.setOnClickListener(this)
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivBack -> finish()
            binding.llPrivacy ->
                WebActivity.create(this, "https://justfuncall.com/terms.html")
        }
    }

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, AboutActivity::class.java))
    }
}