package com.milk.global.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.milk.global.databinding.ActivityBackStackBinding
import com.milk.global.ui.vm.BackStackViewModel
import com.milk.simple.ktx.immersiveStatusBar

class BackStackActivity : AbstractActivity() {
    private val binding by lazy { ActivityBackStackBinding.inflate(layoutInflater) }
    private val backStackViewModel by viewModels<BackStackViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        initializeObserver()
    }

    private fun initializeView() {
        immersiveStatusBar(false)
        binding.lineLottieView.setAnimation("back_stack_progress.json")
        binding.lineLottieView.playAnimation()
    }

    private fun initializeObserver() {
        val isLaunched = intent.getBooleanExtra(IS_APP_LAUNCH_AD, false)
        if (isLaunched) {
            backStackViewModel.loadLaunchAd(
                activity = this,
                viewGroup = binding.root,
                finishRequest = {
                    MainActivity.create(this)
                    finish()
                }
            )
        } else backStackViewModel.loadBackStackAd(this) { finish() }
    }

    override fun onInterceptKeyDownEvent(): Boolean = true

    companion object {
        private const val IS_APP_LAUNCH_AD = "IS_APP_LAUNCH_AD"
        fun create(context: Context, isAppLaunchAd: Boolean = false) {
            val intent = Intent(context, BackStackActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(IS_APP_LAUNCH_AD, isAppLaunchAd)
            context.startActivity(intent)
        }
    }
}