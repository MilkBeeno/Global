package com.milk.smartvpn.ui.dialog

import androidx.fragment.app.FragmentActivity
import com.milk.smartvpn.databinding.DialogOpenNotificationBinding

class OpenNotificationDialog(activity: FragmentActivity) :
    SimpleDialog<DialogOpenNotificationBinding>(activity) {
    private var confirmRequest: (() -> Unit)? = null
    private var cancelRequest: (() -> Unit)? = null

    init {
        setWidthMatchParent(true)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        binding.ivCancel.setOnClickListener {
            cancelRequest?.invoke()
            dismiss()
        }
        binding.tvConfirm.setOnClickListener {
            confirmRequest?.invoke()
            dismiss()
        }
    }

    internal fun setConfirm(request: () -> Unit) {
        confirmRequest = request
    }

    internal fun setCancel(request: () -> Unit) {
        cancelRequest = request
    }

    override fun getViewBinding(): DialogOpenNotificationBinding {
        return DialogOpenNotificationBinding.inflate(activity.layoutInflater)
    }
}