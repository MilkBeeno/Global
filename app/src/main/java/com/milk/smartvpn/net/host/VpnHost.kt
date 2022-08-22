package com.milk.smartvpn.net.host

class VpnHost : ApiHost {
    override fun releaseUrl(): String {
        return "https://apv.getsimplesmart.com"
    }

    override fun debugUrl(): String {
        return "http://apv.milksmartvpn.click"
    }
}