package com.milk.smartvpn.ui.dialog

import androidx.fragment.app.FragmentActivity
import com.milk.smartvpn.databinding.DialogWaitBinding

class WaitDialog(activity: FragmentActivity) : SimpleDialog<DialogWaitBinding>(activity) {

    init {
        setWidthMatchParent(true)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        initializeView()
    }

    private fun initializeView() {
        binding.lottieView.setAnimation("wait_dialog.json")
        binding.lottieView.playAnimation()
    }

    fun setContent(content: String) {
        binding.tvContent.text = content
    }

    override fun getViewBinding(): DialogWaitBinding {
        return DialogWaitBinding.inflate(activity.layoutInflater)
    }
}