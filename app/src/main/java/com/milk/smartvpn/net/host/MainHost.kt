package com.milk.smartvpn.net.host

class MainHost : ApiHost {
    override fun releaseUrl(): String {
        return "http://ap.milksmartvpn.click"
    }

    override fun debugUrl(): String {
        return "https://ap.getsimplesmart.com"
    }
}