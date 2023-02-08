package com.milk.global.net.host

class VpnHost : ApiHost {
    override fun releaseUrl(): String {
        return "https://apv.duoglobalmaster.com"
    }

    override fun debugUrl(): String {
        return "http://apv.duoglobalmastert.click"
    }
}