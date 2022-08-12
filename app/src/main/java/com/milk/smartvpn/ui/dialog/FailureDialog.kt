package com.milk.smartvpn.ui.dialog

import androidx.fragment.app.FragmentActivity
import com.milk.smartvpn.databinding.DialogFailureBinding

class FailureDialog(activity: FragmentActivity) : SimpleDialog<DialogFailureBinding>(activity) {

    init {
        setWidthMatchParent(true)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }

    override fun getViewBinding(): DialogFailureBinding {
        return DialogFailureBinding.inflate(activity.layoutInflater)
    }
}