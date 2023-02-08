package com.milk.global.data

/**
 * 主页连接的 VPN 节点模型、保存在 ViewModel 中
 */
data class VpnNodeModel(
    var nodeId: Long = 0,
    var isConnected: Boolean = false,
    var imageUrl: String = "",
    var name: String = "",
    var ping: Long = 0
) : java.io.Serializable