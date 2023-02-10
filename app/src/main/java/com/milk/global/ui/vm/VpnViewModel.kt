package com.milk.global.ui.vm

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.anythink.nativead.api.ATNative
import com.freetech.vpn.data.VpnProfile
import com.freetech.vpn.data.VpnType
import com.milk.global.ad.AdConfig
import com.milk.global.ad.InterstitialAd
import com.milk.global.ad.TopOnManager
import com.milk.global.constant.AdCodeKey
import com.milk.global.data.VpnModel
import com.milk.global.friebase.FireBaseManager
import com.milk.global.friebase.FirebaseKey
import com.milk.global.repository.VpnRepository
import com.milk.global.util.MilkTimer
import com.milk.simple.ktx.ioScope
import com.milk.simple.ktx.withMain
import kotlinx.coroutines.flow.MutableSharedFlow

class VpnViewModel : ViewModel() {
    private val interstitialAd by lazy { InterstitialAd() }

    private val vpnRepository by lazy { VpnRepository() }

    internal var mainNativeAd = MutableSharedFlow<ATNative>()
    private val adUnitId by lazy { AdConfig.getAdvertiseUnitId(AdCodeKey.MAIN_NATIVE_AD_KEY) }

    // 当前连接的节点 ID 和是否是连接成功
    internal var vpnNodeId: Long = 0
    internal var vpnPing: Long = 0
    internal var vpnName: String = ""
    internal var vpnImageUrl: String = ""
    internal var vpnIsConnected: Boolean = false

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

    internal fun getVpnProfileInfo(vpnProfileRequest: (VpnProfile) -> Unit) {
        ioScope {
            val response = vpnRepository.getVpnInfo(vpnNodeId)
            val result = response.data
            if (response.code == 2000 && result != null) {
                withMain { vpnProfileRequest(getVpnProfile(result)) }
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

    internal fun loadInterstitialAd(activity: Activity, finishRequest: () -> Unit) {
        MilkTimer.Builder()
            .setMillisInFuture(12000)
            .setOnFinishedListener {
                if (!interstitialAd.isShowingAd()) {
                    finishRequest()
                }
            }
            .build()
            .start()

        FireBaseManager.logEvent(FirebaseKey.Make_an_ad_request_4)
        interstitialAd.load(
            context = activity,
            failure = {
                finishRequest()
                FireBaseManager.logEvent(FirebaseKey.Ad_request_failed_4, it)
            },
            success = {
                showAppOpenAd(activity, finishRequest)
                FireBaseManager.logEvent(FirebaseKey.Ad_request_succeeded_4)
            }
        )
    }

    private fun showAppOpenAd(activity: Activity, finishRequest: () -> Unit) {
        interstitialAd.show(
            activity = activity,
            failure = {
                finishRequest()
                FireBaseManager.logEvent(FirebaseKey.Ad_show_failed_4, it)
            },
            success = {
                FireBaseManager.logEvent(FirebaseKey.The_ad_show_success_4)
            },
            click = {
                FireBaseManager.logEvent(FirebaseKey.click_ad_4)
            },
            close = {
                finishRequest()
            }
        )
    }
}