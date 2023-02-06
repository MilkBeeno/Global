package com.milk.global.ui.dialog

import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.milk.global.databinding.DialogConnectingBinding

class ConnectingDialog(activity: FragmentActivity) :
    SimpleDialog<DialogConnectingBinding>(activity) {

    init {
        binding.connectingLottieView.setAnimation("connecting.json")
        binding.connectingLottieView.playAnimation()
    }

    override fun getViewBinding(): DialogConnectingBinding {
        return DialogConnectingBinding.inflate(LayoutInflater.from(activity))
    }
}