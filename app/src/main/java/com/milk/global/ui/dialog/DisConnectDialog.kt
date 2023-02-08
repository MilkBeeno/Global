package com.milk.global.ui.dialog

import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.milk.global.databinding.DialogDisconnectBinding

class DisConnectDialog(activity: FragmentActivity) :
    SimpleDialog<DialogDisconnectBinding>(activity) {

    init {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding.disconnectLottieView.setAnimation("disconnect.json")
        binding.disconnectLottieView.playAnimation()
    }

    override fun getViewBinding(): DialogDisconnectBinding {
        return DialogDisconnectBinding.inflate(LayoutInflater.from(activity))
    }
}