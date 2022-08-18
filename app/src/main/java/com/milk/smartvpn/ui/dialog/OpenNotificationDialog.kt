package com.milk.smartvpn.ui.dialog

import androidx.fragment.app.FragmentActivity
import com.milk.smartvpn.databinding.DialogOpenNotificationBinding

class OpenNotificationDialog(activity: FragmentActivity) :
    SimpleDialog<DialogOpenNotificationBinding>(activity) {
    private var confirmRequest: (() -> Unit)? = null

    init {
        setWidthMatchParent(true)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        binding.ivCancel.setOnClickListener { dismiss() }
        binding.tvConfirm.setOnClickListener {
            confirmRequest?.invoke()
            dismiss()
        }
    }

    internal fun setConfirm(request: () -> Unit) {
        confirmRequest = request
    }

    override fun getViewBinding(): DialogOpenNotificationBinding {
        return DialogOpenNotificationBinding.inflate(activity.layoutInflater)
    }
}