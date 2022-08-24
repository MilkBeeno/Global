package com.milk.smartvpn.ad

import android.content.Context
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.simple.ktx.ioScope
import com.milk.simple.mdr.KvManger
import com.milk.smartvpn.BaseApplication
import com.milk.smartvpn.BuildConfig
import com.milk.smartvpn.R
import com.milk.smartvpn.constant.AdCodeKey
import com.milk.smartvpn.constant.EventKey
import com.milk.smartvpn.data.AdModel
import com.milk.smartvpn.data.AdPositionModel
import com.milk.smartvpn.data.AdResponseModel
import com.milk.smartvpn.repository.AdRepository
import com.milk.smartvpn.util.JsonConvert

object AdConfig {
    private const val AD_CONFIG = "AD_CONFIG".plus("-SmartVpn")
    private val positionMap = mutableMapOf<String, String>()

    /** 获取网络中最新的广告信息 */
    fun obtain() {
        if (positionMap.isNotEmpty()) return
        ioScope {
            val apiResult = AdRepository().getAdConfig(
                BuildConfig.AD_APP_ID,
                BuildConfig.AD_APP_CHANNEL,
                BuildConfig.AD_APP_VERSION
            )
            val result = apiResult.data
            if (apiResult.code == 2000) {
                result?.let { updateAdUnitId(it) }
                KvManger.put(AD_CONFIG, apiResult)
            } else {
                val config = getLocalConfig(BaseApplication.current)
                JsonConvert.toModel(config, AdResponseModel::class.java)
                    ?.result?.let { updateAdUnitId(it) }
            }
        }
    }

    /** 网络获取广告失败、使用本地保存的广告 */
    private fun getLocalConfig(context: Context): String {
        val storedConfig = KvManger.getString(AD_CONFIG)
        return storedConfig.ifBlank {
            val defaultConfig = if (BuildConfig.DEBUG)
                String(context.resources.openRawResource(R.raw.config_debug).readBytes())
            else
                String(context.resources.openRawResource(R.raw.config_release).readBytes())
            KvManger.put(AD_CONFIG, defaultConfig)
            defaultConfig
        }
    }

    /** 保存广告信息 */
    private fun updateAdUnitId(result: AdModel) {
        positionMap.clear()
        result.pos[AdCodeKey.APP_LAUNCH]?.positionListModel?.let {
            savePositionId(AdCodeKey.APP_LAUNCH, it)
        }
        result.pos[AdCodeKey.MAIN_BOTTOM]?.positionListModel?.let {
            savePositionId(AdCodeKey.MAIN_BOTTOM, it)
        }
        result.pos[AdCodeKey.BACK_STACK]?.positionListModel?.let {
            savePositionId(AdCodeKey.BACK_STACK, it)
        }
        result.pos[AdCodeKey.VPN_LIST]?.positionListModel?.let {
            savePositionId(AdCodeKey.VPN_LIST, it)
        }
        result.pos[AdCodeKey.CONNECT_SUCCESS]?.positionListModel?.let {
            savePositionId(AdCodeKey.CONNECT_SUCCESS, it)
        }
        result.pos[AdCodeKey.CONNECT_SUCCESS_RESULT]?.positionListModel?.let {
            savePositionId(AdCodeKey.CONNECT_SUCCESS_RESULT, it)
        }
        result.pos[AdCodeKey.DISCONNECT_SUCCESS_RESULT]?.positionListModel?.let {
            savePositionId(AdCodeKey.DISCONNECT_SUCCESS_RESULT, it)
        }
        LiveEventBus.get<Any?>(EventKey.UPDATE_START_AD_UNIT_ID).post(null)
    }

    /** 将广告 ID 保存在 Map 中 */
    private fun savePositionId(positionCode: String, positionList: List<AdPositionModel>) {
        if (positionList.isNotEmpty()) {
            positionMap[positionCode] = positionList[0].posId
        }
    }

    /** 获取广告 ID */
    fun getAdvertiseUnitId(key: String): String {
        val position = positionMap[key]
        return if (position?.isNotEmpty() == true) return position else ""
    }
}