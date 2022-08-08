package com.milk.smartvpn.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.smartvpn.databinding.ActivityBackStackBinding
import com.milk.smartvpn.ui.vm.BackStackViewModel

class BackStackActivity : AbstractActivity() {
    private val binding by lazy { ActivityBackStackBinding.inflate(layoutInflater) }
    private val backStackViewModel by viewModels<BackStackViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        immersiveStatusBar(false)
        binding.lottieView.setAnimation("back_stack_icon.json")
        binding.lottieView.playAnimation()
        binding.lineLottieView.setAnimation("back_stack_progress.json")
        binding.lineLottieView.playAnimation()
        backStackViewModel.loadBackStackAd(
            activity = this,
            onSuccessRequest = {
                MainActivity.create(this)
                finish()
            },
            onFailureRequest = {

            })
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