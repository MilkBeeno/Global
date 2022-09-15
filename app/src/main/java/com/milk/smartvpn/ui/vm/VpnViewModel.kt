package com.milk.smartvpn.ui.vm

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anythink.interstitial.api.ATInterstitial
import com.freetech.vpn.data.VpnProfile
import com.freetech.vpn.data.VpnType
import com.google.android.gms.ads.nativead.NativeAd
import com.milk.simple.ktx.ioScope
import com.milk.simple.ktx.mainScope
import com.milk.smartvpn.ad.AdConfig
import com.milk.smartvpn.ad.AdLoadStatus
import com.milk.smartvpn.ad.AdManager
import com.milk.smartvpn.ad.TopOnManager
import com.milk.smartvpn.constant.AdCodeKey
import com.milk.smartvpn.data.VpnModel
import com.milk.smartvpn.friebase.FireBaseManager
import com.milk.smartvpn.friebase.FirebaseKey
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
    internal var vpnConnectDuration = 0L

    /** 是否连接 VPN 的状态和连接 VPN 的状态 */
    internal val vpnStartConnect = MutableLiveData<Boolean>()
    internal val connectionState = MutableStateFlow(VpnStatus.NotConnect)

    /** 当前连接的节点 ID 和是否是连接成功 */
    internal var currentNodeId: Long = 0
    internal var currentConnected: Boolean = false
    internal var currentImageUrl: String = ""
    internal var currentName: String = ""
    internal var currentPing: Long = 0
    private var adLoadStatus: AdLoadStatus = AdLoadStatus.Loading

    /** 是否显示结果页面 */
    internal var showResultPage: Boolean = false

    internal var mainNativeAd = MutableStateFlow<Pair<String, NativeAd?>>(Pair("", null))

    internal fun loadNativeAdByTimer(activity: FragmentActivity) {
        MilkTimer.Builder()
            .setMillisInFuture(30000)
            .setCountDownInterval(1000)
            .setOnFinishedListener {
                loadMainNativeAd(activity)
                loadNativeAdByTimer(activity)
            }.build().start()
    }

    internal fun loadMainNativeAd(activity: FragmentActivity) {
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.MAIN_BOTTOM)
        AdManager.loadNativeAds(
            context = activity,
            adUnitId = unitId,
            failedRequest = {
                FireBaseManager
                    .logEvent(FirebaseKey.AD_REQUEST_FAILED, unitId, it)
            },
            successRequest = {
                FireBaseManager
                    .logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED, unitId, unitId)
                ioScope { mainNativeAd.emit(Pair(unitId, it)) }
            },
            clickAdRequest = {
                FireBaseManager
                    .logEvent(FirebaseKey.CLICK_AD, unitId, unitId)
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
        MilkTimer.Builder()
            .setOnFinishedListener {
                if (connectionState.value == VpnStatus.Connecting)
                    ioScope { connectionState.emit(VpnStatus.Failure) }
            }
            .setMillisInFuture(15000)
            .build()
            .start()
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

    internal fun startTiming(successRequest: (String) -> Unit, finishRequest: () -> Unit) {
        vpnConnectDuration = 0L
        val timerTask = object : TimerTask() {
            override fun run() {
                vpnConnectDuration += 1
                when (vpnConnectDuration) {
                    in 0 until 60 -> {
                        FireBaseManager.logEvent(FirebaseKey.VPN_USAGE_TIME_IS_LESS_THAN_1MIN)
                    }
                    in 60 until 30 * 60 -> {
                        FireBaseManager.logEvent(FirebaseKey.VPN_USAGE_TIME_IS_1_30MIN)
                    }
                    in 30 * 60 until 60 * 60 -> {
                        FireBaseManager.logEvent(FirebaseKey.VPN_USAGE_TIME_IS_30_60MIN)
                    }
                }
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
                mainScope { successRequest(hourString.plus(minuteString).plus(secondString)) }
                if (vpnConnectDuration > 60 * 60) finishRequest()
            }
        }
        if (timer == null) timer = Timer()
        timer?.schedule(timerTask, 0, 1000)
    }

    internal fun endTiming() {
        timer?.cancel()
        timer = null
    }

    internal fun showConnectedAd(activity: FragmentActivity, finishRequest: () -> Unit) {
        loadConnectedNativeAd(activity)
        var aTInterstitial: ATInterstitial? = null
        val unitId = AdConfig.getAdvertiseUnitId(AdCodeKey.CONNECT_SUCCESS)
        val timer = MilkTimer.Builder()
            .setMillisInFuture(10000)
            .setOnFinishedListener {
                if (adLoadStatus == AdLoadStatus.Success)
                    aTInterstitial?.show(activity)
                else
                    finishRequest()
            }
            .build()
        timer.start()
        if (unitId.isNotBlank()) {
            adLoadStatus = AdLoadStatus.Loading
            aTInterstitial = TopOnManager.loadInterstitial(
                activity = activity,
                adUnitId = unitId,
                loadFailureRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_SHOW_FAILED, unitId, it)
                    adLoadStatus = AdLoadStatus.Failure
                },
                loadSuccessRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS, unitId, unitId)
                    adLoadStatus = AdLoadStatus.Success
                    timer.finish()
                },
                showFailureRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS, unitId, it)
                },
                showSuccessRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.THE_AD_SHOW_SUCCESS, unitId, unitId)
                },
                finishedRequest = {
                    finishRequest()
                },
                clickRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.CLICK_AD, unitId, unitId)
                })
        } else adLoadStatus = AdLoadStatus.Failure
    }

    private fun loadConnectedNativeAd(activity: FragmentActivity) {
        val currentNativeAd =
            DataRepository.connectSuccessAd.value.second
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.CONNECT_SUCCESS_RESULT)
        if (unitId.isNotBlank() && currentNativeAd == null) {
            AdManager.loadNativeAds(activity, unitId,
                failedRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_FAILED, unitId, it)
                },
                successRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED, unitId, unitId)
                    ioScope {
                        DataRepository.connectSuccessAd.emit(Pair(unitId, it))
                    }
                },
                clickAdRequest = {
                    FireBaseManager
                        .logEvent(FirebaseKey.CLICK_AD, unitId, unitId)
                })
        }
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
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_FAILED, unitId, it)
                    finishRequest(unitId)
                },
                successRequest = {
                    // 加载成功原因和理由
                    FireBaseManager
                        .logEvent(FirebaseKey.AD_REQUEST_SUCCEEDED, unitId, unitId)
                    finishRequest(unitId)
                    ioScope {
                        DataRepository.disconnectAd.emit(Pair(unitId, it))
                    }
                },
                clickAdRequest = {
                    // 点击广告页面
                    FireBaseManager
                        .logEvent(FirebaseKey.CLICK_AD, unitId, unitId)
                })
        } else ioScope {
            delay(1500)
            finishRequest(unitId)
        }
    }
}