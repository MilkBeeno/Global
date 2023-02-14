package com.milk.global.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.global.data.VpnGroup
import com.milk.global.data.VpnNode
import com.milk.global.repository.VpnRepository
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.BufferedReader
import java.io.InputStreamReader

class SwitchNodeViewModel : ViewModel() {
    var vpnGroups = MutableStateFlow(arrayListOf<VpnGroup>())

    var currentNodeId: Long = 0L
    var currentConnected: Boolean = false

    fun getVpnListInfo() {
        ioScope {
            val response = VpnRepository.getVpnListInfo()
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