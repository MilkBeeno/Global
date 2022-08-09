package com.milk.smartvpn.proxy

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
import androidx.lifecycle.ViewModelProvider
import com.freetech.vpn.logic.VpnStateService
import com.milk.simple.ktx.ioScope
import com.milk.smartvpn.ui.act.MainActivity
import com.milk.smartvpn.ui.type.VpnStatus
import com.milk.smartvpn.ui.vm.VpnViewModel

class VpnProxy(private val activity: MainActivity) {
    private var vpnService: VpnStateService? = null
    private val vpnVm: VpnViewModel by lazy {
        ViewModelProvider(activity)[VpnViewModel::class.java]
    }

    // vpn permission is available and start connect
    private val activityResult = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) openVpn()
    }

    // vpn state changed
    private val vpnStateListener = VpnStateService.VpnStateListener {
        ioScope {
            when (vpnService?.errorState) {
                VpnStateService.ErrorState.NO_ERROR ->
                    when (vpnService?.state) {
                        VpnStateService.State.CONNECTING ->
                            vpnVm.connectionState.emit(VpnStatus.Connecting)
                        VpnStateService.State.CONNECTED ->
                            vpnVm.connectionState.emit(VpnStatus.Connected)
                        VpnStateService.State.DISCONNECTING ->
                            vpnVm.connectionState.emit(VpnStatus.NotConnect)
                        VpnStateService.State.DISABLED ->
                            vpnVm.connectionState.emit(VpnStatus.NotConnect)
                        else -> vpnVm.connectionState.emit(VpnStatus.NotConnect)
                    }
                else -> vpnVm.connectionState.emit(VpnStatus.Failure)
            }
        }
        // update vpn profile of kv
        vpnVm.saveProfile(
            vpnService?.errorState == VpnStateService.ErrorState.NO_ERROR &&
                    vpnService?.state == VpnStateService.State.CONNECTED
        )
    }

    // vpn service connection
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
        initObserver()
    }

    private fun initObserver() {
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
        vpnVm.vpnStartConnect.observe(activity) {
            if (it == true) vpnService?.disconnect()
            connecting()
        }
    }

    fun openVpn() {
        val prepare = VpnService.prepare(activity)
        if (prepare == null)
            vpnVm.getVpnInfo()
        else
            activityResult.launch(prepare)
    }

    fun closeVpn() {
        vpnService?.disconnect()
    }

    private fun connecting() {
        val vpnProfile = vpnVm.getVpnProfile()
        val profileInfo = Bundle().apply {
            putSerializable(PROFILE, vpnProfile)
            putInt(G_ID, vpnProfile.id.toInt())
        }
        vpnService?.connect(profileInfo, true)
    }

    companion object {
        private const val G_ID = "b01"
        private const val PROFILE = "profile"
    }
}