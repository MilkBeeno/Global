package com.milk.smartvpn.ui.vm

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freetech.vpn.data.VpnProfile
import com.freetech.vpn.data.VpnType
import com.milk.simple.ktx.ioScope
import com.milk.simple.ktx.mainScope
import com.milk.smartvpn.data.VpnModel
import com.milk.smartvpn.repository.VpnRepository
import com.milk.smartvpn.ui.type.VpnStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class VpnViewModel : ViewModel() {
    private val vpnRepository by lazy { VpnRepository() }
    private var vpnModel = VpnModel()

    /** 连接 VPN 后的计时器、以及连接 VPN 的时长 */
    private var timer: Timer? = null
    private var vpnConnectDuration = 0L

    /** 是否连接 VPN 的状态和连接 VPN 的状态 */
    internal val vpnStartConnect = MutableLiveData<Boolean>()
    internal val connectionState = MutableStateFlow(VpnStatus.NotConnect)

    /** 当前连接的节点 ID 和是否是连接成功 */
    internal var currentNodeId: Long = 0
    internal var currentConnected: Boolean = false
    internal var currentImageUrl: String = ""
    internal var currentName: String = ""
    internal var currentPing: Long = 0

    /** 是否显示结果页面 */
    internal var showResultPage: Boolean = false

    internal fun getVpnInfo(nodeId: Long = 0, switchNode: Boolean = false) {
        ioScope {
            if (connectionState.value == VpnStatus.Connecting) return@ioScope
            connectionState.emit(VpnStatus.Connecting)
            if (nodeId <= 0) {
                if (currentNodeId <= 0 || switchNode) currentNodeId = 0
            } else currentNodeId = nodeId
            // get new data from service every time.
            val response = vpnRepository.getVpnInfo(currentNodeId)
            val result = response.data
            if (response.code == 2000 && result != null) {
                vpnModel = result
                vpnStartConnect.postValue(currentConnected)
            } else connectionState.emit(VpnStatus.Failure)
        }
    }

    internal fun getVpnProfile(): VpnProfile {
        val vpnProfile = VpnProfile()
        vpnProfile.id = vpnModel.nodeId
        vpnProfile.name = vpnModel.nodeName
        vpnProfile.gateway = vpnModel.dns
        vpnProfile.username = vpnModel.userName
        vpnProfile.password = vpnModel.password
        vpnProfile.mtu = 1400
        vpnProfile.vpnType = VpnType.fromIdentifier("ikev2-eap")
        return vpnProfile
    }

    internal fun startTiming(request: (String) -> Unit) {
        vpnConnectDuration = 0L
        val timerTask = object : TimerTask() {
            override fun run() {
                vpnConnectDuration += 1
                // Hour
                val hour = vpnConnectDuration / (60 * 60)
                val hourString = if (hour >= 10)
                    hour.toString().plus(":")
                else
                    "0".plus(hour).plus(":")
                val timeLeftMinute = vpnConnectDuration - hour * (60 * 60)
                // Minute
                val minute = timeLeftMinute / 60
                val minuteString = if (minute >= 10)
                    minute.toString().plus(":")
                else
                    "0".plus(minute).plus(":")
                val timeLeftSecond = vpnConnectDuration - hour * (60 * 60) - minute * 60
                // Second
                val secondString = if (timeLeftSecond >= 10)
                    timeLeftSecond.toString()
                else
                    "0".plus(timeLeftSecond)
                mainScope { request(hourString.plus(minuteString).plus(secondString)) }
            }
        }
        if (timer == null) timer = Timer()
        timer?.schedule(timerTask, 0, 1000)
    }

    internal fun endTiming() {
        timer?.cancel()
        timer = null
    }

    fun loadMainAd(
        activity: FragmentActivity,
        finishRequest: () -> Unit
    ) {
        ioScope {
            delay(2000)
            finishRequest()
        }
    }
}