package com.milk.smartvpn.friebase

object FirebaseKey {
    /** 首次启动时同意隐私政策的用户 */
    internal const val CLICK_START = "Click_start"

    /** 进入启动页的次数 */
    internal const val ENTER_THE_STARTUP_PAGE = "Enter_the_startup_page"

    /** 首页的展示次数 */
    internal const val ENTER_MAIN_PAGE = "enter_main_page"

    /** 在首页开始连接的次数 */
    internal const val CLICK_TO_CONNECT_NODE = "Click_to_connect_node"

    /** 3S内连接成功 */
    internal const val CONNECTION_SUCCESSFUL_WITHIN_3S = "connection_successful_within_3S"

    /** 3-8s内连接成功 */
    internal const val CONNECTION_SUCCESSFUL_WITHIN_3_8S = "connection_successful_within_3_8s"

    /** 8-15s内连接成功 */
    internal const val CONNECTION_SUCCESSFUL_WITHIN_8_15S = "connection_successful_within_8_15s"

    /** 15s以上连接成功 */
    internal const val CONNECTION_SUCCESSFUL_FOR_MORE_THAN_15S =
        "connection_successful_for_more_than_15s"

    /** 连接成功的次数 */
    internal const val CONNECT_SUCCESSFULLY = "Connect_successfully"

    /** 连接失败的次数 */
    internal const val CONNECT_FAILED = "Connect_Failed"

    /** VPN使用时长在1min以内 */
    internal const val VPN_USAGE_TIME_IS_LESS_THAN_1MIN = "VPN_usage_time_is_less_than_1min"

    /** VPN使用时长在1-30min */
    internal const val VPN_USAGE_TIME_IS_1_30MIN = "VPN_usage_time_is_1_30min"

    /** VPN使用时长在30-60min */
    internal const val VPN_USAGE_TIME_IS_30_60MIN = "VPN_usage_time_is_30_60min"

    /** 点击首页查看更多次数 */
    internal const val CLICK_ON_MORE = "Click_on_more"

    /** 点击分享的次数 */
    internal const val CLICK_THE_SHARE = "Click_the_share"

    /** 点击节点列表入口次数 */
    internal const val CLICK_ON_THE_NODE_LIST_ENTRY = "Click_on_the_node_list_entry"

    /** 点击切换节点 */
    internal const val CLICK_TO_SWITCH_NODE = "Click_to_switch_node"

    /** 点击普通节点 */
    internal const val CLICK_ON_NORMAL_NODE = "Click_on_normal_node"

    /** 点击自动节点 */
    internal const val CLICK_ON_THE_AUTOMATIC_NODE = "Click_on_the_automatic_node"

    /** 发起广告请求的次数 */
    internal const val MAKE_AN_AD_REQUEST = "Make_an_ad_request"

    /** 广告请求成功的次数 */
    internal const val AD_REQUEST_SUCCEEDED = "Ad_request_succeeded"

    /** 广告请求失败的次数 */
    internal const val AD_REQUEST_FAILED = "Ad_request_failed"

    /** 广告展示成功的次数 */
    internal const val THE_AD_SHOW_SUCCESS = "The_ad_show_success"

    /** 广告展示失败的次数 */
    internal const val AD_SHOW_FAILED = "Ad_show_failed"

    /** 点击广告次数 */
    internal const val CLICK_AD = "click_ad"
}