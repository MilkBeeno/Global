package com.milk.smartvpn.ui.vm

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freetech.vpn.data.VpnProfile
import com.freetech.vpn.data.VpnType
import com.google.android.gms.ads.nativead.NativeAd
import com.milk.simple.ktx.ioScope
import com.milk.simple.ktx.mainScope
import com.milk.smartvpn.ad.AdConfig
import com.milk.smartvpn.ad.AdManager
import com.milk.smartvpn.constant.AdCodeKey
import com.milk.smartvpn.data.VpnModel
import com.milk.smartvpn.repository.DataRepository
import com.milk.smartvpn.repository.VpnRepository
import com.milk.smartvpn.ui.type.VpnStatus
import com.milk.smartvpn.util.MilkTimer
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

    internal var mainNativeAd = MutableStateFlow<Pair<String, NativeAd?>>(Pair("", null))

    internal fun loadMainNativeAd(activity: FragmentActivity) {
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.MAIN_BOTTOM)
        AdManager.loadNativeAds(
            context = activity,
            adUnitId = unitId,
            failedRequest = {
                // 失败埋点
            },
            successRequest = {
                // 成功埋点
                ioScope { mainNativeAd.emit(Pair(unitId, it)) }
            },
            clickAdRequest = {
                // 点击埋点
            })
    }

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

    internal fun loadSuccessAd(activity: FragmentActivity, finishRequest: (String) -> Unit) {
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.CONNECT_SUCCESS)
        val timer = MilkTimer.Builder()
            .setMillisInFuture(10000)
            .setOnFinishedListener { finishRequest(unitId) }
            .build()
        timer.start()
        loadConnectedNativeAd(activity)
        loadConnectedInterstitialAd(activity, { timer.finish() }, { timer.finish() })
    }

    private fun loadConnectedInterstitialAd(
        activity: FragmentActivity,
        successRequest: (String) -> Unit,
        failureRequest: () -> Unit,
    ) {
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.CONNECT_SUCCESS)
        if (unitId.isNotBlank())
            AdManager.loadInterstitial(activity, unitId,
                onFailedRequest = {
                    failureRequest()
                },
                onSuccessRequest = {
                    successRequest(unitId)
                })
        else failureRequest()
    }

    private fun loadConnectedNativeAd(activity: FragmentActivity) {
        val currentNativeAd =
            DataRepository.connectSuccessAd.value.second
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.CONNECT_SUCCESS_RESULT)
        if (unitId.isNotBlank() && currentNativeAd == null) {
            AdManager.loadNativeAds(activity, unitId,
                failedRequest = {
                    // 加载失败、原因和理由
                },
                successRequest = {
                    // 加载成功原因和理由
                    ioScope {
                        DataRepository.connectSuccessAd.emit(Pair(unitId, it))
                    }
                },
                clickAdRequest = {
                    // 点击广告页面
                })
        }
    }

    internal fun showConnectedAd(
        activity: FragmentActivity,
        unitId: String,
        dismissRequest: () -> Unit
    ) {
        AdManager.showInterstitial(
            activity = activity,
            failureRequest = {
                dismissRequest()
            },
            successRequest = {
                dismissRequest()
            },
            clickRequest = {
                // unitId 埋点事件ID
            })
    }

    internal fun loadDisconnectNativeAd(
        activity: FragmentActivity,
        finishRequest: (String) -> Unit
    ) {
        val currentNativeAd =
            DataRepository.disconnectAd.value.second
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.DISCONNECT_SUCCESS_RESULT)
        if (unitId.isNotBlank() && currentNativeAd == null) {
            AdManager.loadNativeAds(activity, unitId,
                failedRequest = {
                    // 加载失败、原因和理由
                    finishRequest(unitId)
                },
                successRequest = {
                    // 加载成功原因和理由
                    finishRequest(unitId)
                    ioScope {
                        DataRepository.disconnectAd.emit(Pair(unitId, it))
                    }
                },
                clickAdRequest = {
                    // 点击广告页面
                })
        } else ioScope {
            delay(1500)
            finishRequest(unitId)
        }
    }
}