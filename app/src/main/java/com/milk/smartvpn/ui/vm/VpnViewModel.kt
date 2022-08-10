package com.milk.smartvpn.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freetech.vpn.data.VpnProfile
import com.freetech.vpn.data.VpnType
import com.google.gson.Gson
import com.milk.simple.ktx.ioScope
import com.milk.simple.ktx.mainScope
import com.milk.simple.mdr.KvManger
import com.milk.smartvpn.constant.KvKey
import com.milk.smartvpn.data.VpnListModel
import com.milk.smartvpn.data.VpnModel
import com.milk.smartvpn.repository.VpnRepository
import com.milk.smartvpn.ui.type.VpnStatus
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class VpnViewModel : ViewModel() {
    private val vpnRepository by lazy { VpnRepository() }
    private var vpnModel = VpnModel()
    private var timer: Timer? = null
    private var vpnConnectDuration = 0L
    internal val vpnStartConnect = MutableLiveData<Boolean>()
    internal val connectionState = MutableStateFlow(VpnStatus.NotConnect)

    var vpnListModels = MutableStateFlow(mutableListOf<VpnListModel>())

    internal fun getVpnInfo(nodeId: Long = -1) {
        ioScope {
            if (connectionState.value == VpnStatus.Connecting) return@ioScope
            connectionState.emit(VpnStatus.Connecting)
            var finalVpnId = nodeId
            if (finalVpnId < 0) {
                finalVpnId = KvManger.getLong(KvKey.SAVE_VPN_ID)
                val profile = KvManger.getString(KvKey.SAVE_VPN_PROFILE)
                if (profile.isNotEmpty()) {
                    vpnModel = Gson().fromJson(profile, VpnModel::class.java)
                }
            }
            // get new data from service every time.
            val response = vpnRepository.getVpnInfo(finalVpnId)
            val result = response.data
            if (response.code == 2000 && result != null) {
                vpnModel = result
                vpnStartConnect.postValue(nodeId >= 0)
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

    internal fun saveProfile() {
        KvManger.put(KvKey.SAVE_VPN_ID, vpnModel.nodeId)
        KvManger.put(KvKey.SAVE_VPN_PROFILE, vpnModel)
    }

    internal fun startTiming(request: (String) -> Unit) {
        vpnConnectDuration = 0L
        val timerTask = object : TimerTask() {
            override fun run() {
                vpnConnectDuration += 1
                // Hour
                val hour = vpnConnectDuration / (60 * 60)
                val hourString = if (hour > 10)
                    hour.toString().plus(":")
                else
                    "0".plus(hour).plus(":")
                val timeLeftMinute = vpnConnectDuration - hour * (60 * 60)
                // Minute
                val minute = timeLeftMinute / 60
                val minuteString = if (minute > 10)
                    minute.toString().plus(":")
                else
                    "0".plus(minute).plus(":")
                val timeLeftSecond = vpnConnectDuration - hour * (60 * 60) - minute * 60
                // Second
                val secondString = if (timeLeftSecond > 10)
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

    fun getVpnListInfo() {
        ioScope {
            val response = vpnRepository.getVpnListInfo()
            val result = response.data
            if (response.code == 2000 && result != null) {
                vpnListModels.value.clear()
                vpnListModels.value.add(VpnListModel(nodeId = 0))
                vpnListModels.value.addAll(result)
                try {
                    vpnListModels.value.onEach {
                        if (it.nodeId > 0) ioScope {
                            it.ping = ping(it.nodeDns)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun switchVpn(index: Int) {
        if (vpnListModels.value.size <= index) return
        val vpnListModel = vpnListModels.value[index]
        if (KvManger.getLong(KvKey.SAVE_VPN_ID) != vpnListModel.nodeId) {
            getVpnInfo(vpnListModel.nodeId)
        }
    }

    private fun ping(ip: String = "54.67.15.250"): Int {
        val r = Runtime.getRuntime().exec("ping -c 1 $ip")
        val bufferedReader = BufferedReader(InputStreamReader(r.inputStream))
        while (true) {
            val line: String = bufferedReader.readLine() ?: break
            if (!line.startsWith("rtt")) continue
            val speed = line.split("=")[1].split("/")[1]
            return speed.toFloat().toInt()
        }
        return 0
    }
}