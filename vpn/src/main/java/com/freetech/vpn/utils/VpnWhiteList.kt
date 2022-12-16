package com.freetech.vpn.utils

object VpnWhiteList {

    @JvmStatic
    var vpnList = mutableSetOf<String>()

    fun addCloseList(pkn: String) {
        vpnList.add(pkn)
    }
}