package com.milk.global.net.host

class MainHost : ApiHost {
    override fun releaseUrl(): String {
        return "https://ap.getsimplesmart.com"
    }

    override fun debugUrl(): String {
        return "http://ap.milksmartvpn.click"
    }
}