package com.milk.smartvpn.ui.dialog

import android.app.Dialog
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.StyleRes
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

abstract class SimpleDialog<T : ViewBinding>(val activity: FragmentActivity) {
    private var dialog: Dialog? = null
    private var gravity: Int = 0
    private var dimAmount: Float = 0f
    private var windowAnimations: Int = 0
    private var cancelable: Boolean = true
    private var widthMatchParent: Boolean = false
    private var canceledOnTouchOutside: Boolean = true
    private var dismissRequest: (() -> Unit)? = null
    protected val binding: T by lazy { getViewBinding() }

    init {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                dialog?.dismiss()
                dialog = null
            }
        })
    }

    abstract fun getViewBinding(): T

    private fun createDialog() {
        if (dialog == null) {
            dialog = Dialog(activity).apply {
                val parent = binding.root.parent
                if (parent != null)
                    (parent as ViewGroup).removeView(binding.root)
                setContentView(binding.root)
                window?.apply {
                    addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    setBackgroundDrawableResource(android.R.color.transparent)
                    if (widthMatchParent) {
                        val params = attributes
                        params.width = WindowManager.LayoutParams.MATCH_PARENT
                        attributes = params
                    }
                    if (gravity != 0) setGravity(gravity)
                    if (windowAnimations != 0) setWindowAnimations(windowAnimations)
                }
                setCancelable(cancelable)
                setCanceledOnTouchOutside(canceledOnTouchOutside)
                setOnCancelListener { dismissRequest?.invoke() }
                setOnDismissListener { dismissRequest?.invoke() }
                // 解决头设置透明度问题、需要放置这里
                dialog?.window?.setDimAmount(dimAmount)
            }
        }
    }

    open fun show() {
        createDialog()
        dialog?.show()
    }

    open fun dismiss() {
        dialog?.dismiss()
    }

    open fun setDimAmount(amount: Float) {
        dimAmount = amount
    }

    open fun setCanceledOnTouchOutside(cancel: Boolean) {
        canceledOnTouchOutside = cancel
    }

    open fun setWindowAnimations(@StyleRes animation: Int) {
        windowAnimations = animation
    }

    open fun setGravity(gravity: Int) {
        this.gravity = gravity
    }

    open fun setWidthMatchParent(matchParent: Boolean) {
        widthMatchParent = matchParent
    }

    open fun setCancelable(flag: Boolean) {
        cancelable = flag
    }

    open fun setOnDismissListener(listener: () -> Unit) {
        dismissRequest = listener
    }
}