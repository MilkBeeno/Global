package com.milk.smartvpn.constant

object AdCodeKey {
    /** 广告测试账号 */
    internal val TEST_DEVICE_NUMBER = listOf("c1aadd83-3bcd-474d-aac2-cd1e2e83ef2a")

    /** 启动页插页广告 */
    internal const val APP_LAUNCH = "q98765654321"

    /** 首页底部原生广告 */
    internal const val MAIN_BOTTOM = "q98765654322"

    /** 每次（app外部）返回app主页插页广告 */
    internal const val BACK_STACK = "q98765654323"

    /** 节点列表内的原生广告 */
    internal const val VPN_LIST = "q98765654324"

    /** 连接成功插页广告 */
    internal const val CONNECT_SUCCESS = "q98765654325"

    /** 连接成功结果页原生广告 */
    internal const val CONNECT_SUCCESS_RESULT = "q98765654326"

    /** 断开连接完成结果页原生广告 */
    internal const val DISCONNECT_SUCCESS_RESULT = "q98765654327"
}