package com.milk.global.ui.vm

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.anythink.interstitial.api.ATInterstitial
import com.anythink.nativead.api.ATNative
import com.freetech.vpn.data.VpnProfile
import com.freetech.vpn.data.VpnType
import com.milk.global.ad.AdConfig
import com.milk.global.ad.AdLoadStatus
import com.milk.global.ad.TopOnManager
import com.milk.global.constant.AdCodeKey
import com.milk.global.data.VpnModel
import com.milk.global.friebase.FireBaseManager
import com.milk.global.friebase.FirebaseKey
import com.milk.global.repository.DataRepository
import com.milk.global.repository.VpnRepository
import com.milk.global.util.MilkTimer
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableSharedFlow

class VpnViewModel : ViewModel() {
    private val vpnRepository by lazy { VpnRepository() }

    internal var mainNativeAd = MutableSharedFlow<ATNative>()
    private val adUnitId by lazy { AdConfig.getAdvertiseUnitId(AdCodeKey.MAIN_NATIVE_AD_KEY) }

    // 当前连接的节点 ID 和是否是连接成功
    internal var currentNodeId: Long = 0
    internal var currentConnected: Boolean = false
    internal var currentImageUrl: String = ""
    internal var currentName: String = ""
    internal var currentPing: Long = 0

    private var adLoadStatus: AdLoadStatus = AdLoadStatus.Loading

    internal fun loadNativeAdByTimer(activity: FragmentActivity) {
        MilkTimer.Builder()
            .setMillisInFuture(AdConfig.adRefreshTime.toLong())
            .setCountDownInterval(1000)
            .setOnFinishedListener {
                loadMainNativeAd(activity)
                loadNativeAdByTimer(activity)
            }.build().start()
    }

    internal fun loadMainNativeAd(activity: FragmentActivity) {
        FireBaseManager.logEvent(FirebaseKey.Make_an_ad_request)
        if (adUnitId.isNotBlank()) {
            TopOnManager.loadNativeAd(
                activity = activity,
                adUnitId = adUnitId,
                loadFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_failed, it)
                },
                loadSuccessRequest = { native ->
                    ioScope { native?.let { mainNativeAd.emit(it) } }
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_succeeded)
                })
        }
    }

    internal fun getVpnProfileInfo(
        nodeId: Long = 0,
        switchNode: Boolean = false,
        vpnProfileRequest: (VpnProfile) -> Unit
    ) {
        ioScope {
            if (nodeId <= 0) {
                if (currentNodeId <= 0 || switchNode) {
                    currentNodeId = 0
                }
            } else {
                currentNodeId = nodeId
            }
            // get new data from service every time.
            val response = vpnRepository.getVpnInfo(currentNodeId)
            val result = response.data
            if (response.code == 2000 && result != null) {
                vpnProfileRequest(getVpnProfile(result))
            }
        }
    }

    private fun getVpnProfile(vpnModel: VpnModel): VpnProfile {
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

    internal fun showConnectedAd(activity: FragmentActivity, finishRequest: () -> Unit) {
        DataRepository.loadConnectedNativeAd(activity)
        var aTInterstitial: ATInterstitial? = null
        val unitId = AdConfig.getAdvertiseUnitId(AdCodeKey.INTERSTITIAL_AD_KEY)
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
            FireBaseManager.logEvent(FirebaseKey.Make_an_ad_request_4)
            aTInterstitial = TopOnManager.loadInterstitial(
                activity = activity,
                adUnitId = unitId,
                loadFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_failed_4, it)
                    adLoadStatus = AdLoadStatus.Failure
                },
                loadSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_succeeded_4)
                    adLoadStatus = AdLoadStatus.Success
                    timer.finish()
                },
                showFailureRequest = {
                    FireBaseManager.logEvent(FirebaseKey.Ad_show_failed_4, it)
                },
                showSuccessRequest = {
                    FireBaseManager.logEvent(FirebaseKey.The_ad_show_success_4)
                },
                finishedRequest = {
                    finishRequest()
                },
                clickRequest = {
                    FireBaseManager.logEvent(FirebaseKey.click_ad_4)
                })
        } else adLoadStatus = AdLoadStatus.Failure
    }
}