package com.milk.smartvpn.ui.vm

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.milk.smartvpn.util.MilkTimer

class BackStackViewModel : ViewModel() {

    fun loadBackStackAd(
        activity: Activity,
        onSuccessRequest: () -> Unit,
        onFailureRequest: () -> Unit
    ) {
        MilkTimer.Builder()
            .setMillisInFuture(10000)
            .setOnTickListener { _, time ->
                if (time <= 7) onSuccessRequest()
            }
            .setOnFinishedListener {
                onSuccessRequest()
            }
            .build()
            .start()
    }
}