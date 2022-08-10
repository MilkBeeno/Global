package com.milk.smartvpn.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.simple.ktx.ioScope
import com.milk.smartvpn.data.VpnGroup
import com.milk.smartvpn.data.VpnNode
import com.milk.smartvpn.repository.VpnRepository
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.BufferedReader
import java.io.InputStreamReader

class SwitchNodeViewModel : ViewModel() {
    private val vpnRepository by lazy { VpnRepository() }
    var vpnGroups = MutableStateFlow(arrayListOf<VpnGroup>())
    var currentNodeId: Long = 0L

    fun getVpnListInfo() {
        ioScope {
            val response = vpnRepository.getVpnListInfo()
            val result = response.data
            if (response.code == 2000 && result != null) {
                val groups = arrayListOf<VpnGroup>()
                val map = result.groupBy { it.areaCode }
                map.forEach {
                    val vpnListModels = it.value
                    if (vpnListModels.isNotEmpty()) {
                        val group = VpnGroup()
                        group.areaImage = vpnListModels[0].areaImage
                        group.areaName = vpnListModels[0].areaName
                        val nodes = arrayListOf<VpnNode>()
                        vpnListModels.forEach { vpnListModel ->
                            val node = VpnNode()
                            node.areaImage = vpnListModel.areaImage
                            node.areaName = vpnListModel.areaName
                            ioScope { node.ping = ping(vpnListModel.nodeDns) }
                            node.isSelect = vpnListModel.nodeId == currentNodeId
                            if (vpnListModel.nodeId == currentNodeId)
                                group.isSelect = true
                            nodes.add(node)
                        }
                        group.itemSublist = nodes
                        groups.add(group)
                    }
                }
                vpnGroups.emit(groups)
            }
        }
    }

    fun switchVpn(index: Int) {
        if (vpnGroups.value.size <= index) return
        val vpnListModel = vpnGroups.value[index]
        //if (KvManger.getLong(KvKey.SAVE_VPN_ID) != vpnListModel.nodeId) {
        // getVpnInfo(vpnListModel.nodeId)
        // }
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