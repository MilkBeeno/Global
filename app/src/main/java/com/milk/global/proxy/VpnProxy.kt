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
import androidx.lifecycle.ViewModelProvider
import com.freetech.vpn.logic.VpnStateService
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.simple.ktx.ioScope
import com.milk.global.constant.KvKey
import com.milk.global.ui.act.BackStackActivity
import com.milk.global.ui.act.MainActivity
import com.milk.global.ui.type.VpnStatus
import com.milk.global.ui.vm.VpnViewModel

class VpnProxy(private val activity: MainActivity) {
    private var vpnService: VpnStateService? = null
    private val vpnViewModel: VpnViewModel by lazy {
        ViewModelProvider(activity)[VpnViewModel::class.java]
    }

    /** vpn permission is available and start connect */
    private val activityResult = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) openVpn()
    }

    /** vpn state changed */
    private val vpnStateListener = VpnStateService.VpnStateListener {
        ioScope {
            when (vpnService?.errorState) {
                VpnStateService.ErrorState.NO_ERROR ->
                    when (vpnService?.state) {
                        VpnStateService.State.CONNECTING ->
                            vpnViewModel.connectionState.emit(VpnStatus.Connecting)
                        VpnStateService.State.CONNECTED ->
                            vpnViewModel.connectionState.emit(VpnStatus.Connected)
                        VpnStateService.State.DISCONNECTING ->
                            vpnViewModel.connectionState.emit(VpnStatus.DisConnect)
                        else -> Unit
                    }
                else -> vpnViewModel.connectionState.emit(VpnStatus.Failure)
            }
            // 连接结果回调
            vpnViewModel.currentConnected =
                vpnService?.errorState == VpnStateService.ErrorState.NO_ERROR &&
                    vpnService?.state == VpnStateService.State.CONNECTED
        }
    }

    /** vpn service connection */
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
        vpnViewModel.vpnStartConnect.observe(activity) {
            vpnService?.disconnect()
            connecting()
        }
        LiveEventBus.get<ArrayList<String>>(KvKey.SWITCH_VPN_NODE).observe(activity) {
            BackStackActivity.create(activity)
            vpnViewModel.endTiming()
            vpnViewModel.currentImageUrl = it[1]
            vpnViewModel.currentName = it[2]
            vpnViewModel.currentPing = it[3].toLong()
            vpnViewModel.getVpnInfo(it[0].toLong(), true)
        }
    }

    fun openVpn() {
        val prepare = VpnService.prepare(activity)
        if (prepare == null)
            vpnViewModel.getVpnInfo()
        else
            activityResult.launch(prepare)
    }

    fun closeVpn() {
        vpnViewModel.showResultPage = true
        vpnViewModel.endTiming()
        vpnService?.disconnect()
    }

    private fun connecting() {
        val vpnProfile = vpnViewModel.getVpnProfile()
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