package com.milk.global.proxy

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.freetech.vpn.data.VpnProfile
import com.freetech.vpn.logic.VpnStateService
import com.milk.global.ui.act.MainActivity
import com.milk.global.ui.type.VpnState
import com.milk.global.util.MilkTimer

class VpnProxy(private val activity: MainActivity) {
    private var vpnService: VpnStateService? = null
    private var isConnecting: Boolean = false

    private var vpnOpenedRequest: (() -> Unit)? = null
    private var vpnStateChangedRequest: ((VpnState, Boolean) -> Unit)? = null

    private val result = ActivityResultContracts.StartActivityForResult()
    private val activityResult = activity.registerForActivityResult(result) {
        if (it.resultCode == Activity.RESULT_OK) {
            vpnOpenedRequest?.invoke()
        }
    }

    private val vpnStateListener = VpnStateService.VpnStateListener {
        when (vpnService?.errorState) {
            VpnStateService.ErrorState.NO_ERROR ->
                when (vpnService?.state) {
                    VpnStateService.State.DISABLED -> {
                        if (isConnecting) {
                            isConnecting = false
                        } else {
                            vpnStateChangedRequest?.invoke(VpnState.DISCONNECT, true)
                        }
                    }
                    VpnStateService.State.CONNECTING -> {
                        vpnStateChangedRequest?.invoke(VpnState.CONNECTING, true)
                    }
                    VpnStateService.State.CONNECTED -> {
                        isConnecting = false
                        vpnStateChangedRequest?.invoke(VpnState.CONNECTED, true)
                    }
                    VpnStateService.State.DISCONNECTING -> {
                        vpnStateChangedRequest?.invoke(VpnState.DISCOUNTING, true)
                    }
                    else -> Unit
                }
            else -> {
                isConnecting = false
                vpnStateChangedRequest?.invoke(VpnState.DISCONNECT, false)
            }
        }
    }

    private val vpnServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            vpnService = (service as VpnStateService.LocalBinder).service
            vpnService?.registerListener(vpnStateListener)
            vpnService?.setUserTimeout(10)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            vpnService = null
        }
    }

    init {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                activity.bindService(
                    Intent(activity, VpnStateService::class.java),
                    vpnServiceConnection,
                    VpnStateService.BIND_AUTO_CREATE
                )
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                vpnService?.unregisterListener(vpnStateListener)
                activity.unbindService(vpnServiceConnection)
            }
        })
    }

    fun setVpnOpenedListener(vpnOpenedRequest: () -> Unit) {
        this.vpnOpenedRequest = vpnOpenedRequest
    }

    fun setVpnStateChangedListener(vpnStateChangedRequest: (VpnState, Boolean) -> Unit) {
        this.vpnStateChangedRequest = vpnStateChangedRequest
    }

    fun tryOpenVpn() {
        val prepare = VpnService.prepare(activity)
        if (prepare == null) {
            vpnOpenedRequest?.invoke()
        } else {
            activityResult.launch(prepare)
        }
    }

    fun connectVpn(vpnProfile: VpnProfile, isConnected: Boolean) {
        if (isConnecting) {
            return
        }
        isConnecting = true
        // 设置默认一分钟未连上为超时操作
        MilkTimer.Builder()
            .setCountDownInterval(1000)
            .setMillisInFuture(1 * 60 * 1000)
            .setOnFinishedListener {
                if (isConnecting) {
                    vpnStateChangedRequest?.invoke(VpnState.DISCONNECT, false)
                    vpnService?.disconnect()
                }
            }
            .build()
            .start()
        if (isConnected) {
            vpnService?.disconnect()
        }
        val profileInfo = Bundle()
        profileInfo.putSerializable(PROFILE, vpnProfile)
        profileInfo.putInt(G_ID, vpnProfile.id.toInt())
        vpnService?.connect(profileInfo, true)
    }

    fun closeVpn() {
        vpnService?.disconnect()
    }

    companion object {
        private const val G_ID = "b01"
        private const val PROFILE = "profile"
    }
}