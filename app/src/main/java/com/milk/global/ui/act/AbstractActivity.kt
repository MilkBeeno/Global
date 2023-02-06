package com.milk.global.ui.act

import android.content.res.Resources
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.milk.simple.listener.MultipleClickListener
import me.jessyan.autosize.AutoSizeCompat

abstract class AbstractActivity : FragmentActivity(), View.OnClickListener {

    private val multipleClickListener by lazy {
        object : MultipleClickListener() {
            override fun onMultipleClick(view: View) =
                this@AbstractActivity.onMultipleClick(view)
        }
    }

    override fun onClick(p0: View?) = multipleClickListener.onClick(p0)

    protected open fun onMultipleClick(view: View) = Unit

    override fun getResources(): Resources {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            AutoSizeCompat.autoConvertDensityOfGlobal(super.getResources())
        }
        return super.getResources()
    }

    protected open fun onInterceptKeyDownEvent() = false

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && onInterceptKeyDownEvent()) {
            moveTaskToBack(true)
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}