package com.milk.smartvpn.ui.vm

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.milk.simple.ktx.ioScope
import com.milk.smartvpn.ad.AdConfig
import com.milk.smartvpn.ad.AdManager
import com.milk.smartvpn.constant.AdCodeKey
import com.milk.smartvpn.data.VpnGroup
import com.milk.smartvpn.data.VpnNode
import com.milk.smartvpn.friebase.FireBaseManager
import com.milk.smartvpn.friebase.FirebaseKey
import com.milk.smartvpn.repository.DataRepository
import com.milk.smartvpn.repository.VpnRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.BufferedReader
import java.io.InputStreamReader

class SwitchNodeViewModel : ViewModel() {
    private val vpnRepository by lazy { VpnRepository() }
    var vpnGroups = MutableStateFlow(arrayListOf<VpnGroup>())

    /** 是否连接 VPN 的状态和连接 VPN 的状态 */
    var currentNodeId: Long = 0L
    var currentConnected: Boolean = false

    internal fun loadNodeNativeAd(
        activity: FragmentActivity,
        finishRequest: (String) -> Unit
    ) {
        val currentNativeAd =
            DataRepository.vpnListAd.value.second
        val unitId =
            AdConfig.getAdvertiseUnitId(AdCodeKey.VPN_LIST)
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
                    DataRepository.vpnListAd.value = Pair(unitId, it)
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

    fun getVpnListInfo() {
        ioScope {
            val response = vpnRepository.getVpnListInfo()
            val result = response.data
            if (response.code == 2000 && result != null) {
                val groups = arrayListOf<VpnGroup>()
                groups.add(VpnGroup().apply {
                    if (currentNodeId <= 0)
                        isSelect = currentConnected
                    isAutoSelectItem = true
                })
                val map = result.groupBy { it.areaCode }
                map.forEach {
                    val vpnListModels = it.value
                    if (vpnListModels.isNotEmpty()) {
                        val group = VpnGroup()
                        group.areaImage = vpnListModels[0].areaImage
                        group.areaName = vpnListModels[0].areaName
                        val nodes = arrayListOf<VpnNode>()
                        vpnListModels.forEachIndexed { index, vpnListModel ->
                            val node = VpnNode()
                            node.nodeId = vpnListModel.nodeId
                            node.areaImage = vpnListModel.areaImage
                            node.areaName = vpnListModel.areaName
                            ioScope { node.ping = ping(vpnListModel.nodeDns) }
                            node.isSelect = vpnListModel.nodeId == currentNodeId
                            node.itemSize = vpnListModels.size
                            node.position = index
                            // 有一个匹配上表示已经连接过
                            if (vpnListModel.nodeId == currentNodeId)
                                group.isSelect = true
                            nodes.add(node)
                        }
                        group.itemSublist = nodes
                        groups.add(group)
                    }
                }
                groups.forEachIndexed { index, _ ->
                    if ((index + 1) % 5 == 0) {
                        val vpnGroup = VpnGroup()
                            .apply { nativeAd = DataRepository.vpnListAd.value.second }
                        groups.add(index, vpnGroup)
                    }
                }
                vpnGroups.emit(groups)
            }
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