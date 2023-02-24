package com.milk.global.net.reteceptor

import com.milk.global.BuildConfig
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ApiHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        val headerBuilder = request.headers.newBuilder()
        // vpn 网络请求接口特殊处理,通过 Header 是否包含 PARAMS_IN_PATH 的 value 来判断
        if (request.header(PARAMS_IN_PATH) == "true") {
            val params = getRequestParams(request)
            val host =
                (if (BuildConfig.DEBUG) "http://" else "https://").plus(request.url.host)
            val pathSegments = request.url.pathSegments
            var finalUrl = host
            pathSegments.forEach { finalUrl = finalUrl.plus("/").plus(it) }
            val sortParams = sortParam(params)
            finalUrl = finalUrl.plus(sortParams)
            requestBuilder.url(finalUrl)
            val packageName =
                if (BuildConfig.DEBUG) "com.milk.global" else "com.milk.simpleglobal"
            headerBuilder
                .add("H007", "1")
                .add("H006", packageName)
                .add("Content-Type", "application/json")
        }
        requestBuilder.headers(headerBuilder.build())
        return chain.proceed(requestBuilder.build())
    }

    private fun getRequestParams(request: Request): MutableMap<String, String> {
        val params = hashMapOf<String, String>()
        if (request.method.equals("GET", true)) {
            request.url.queryParameterNames.forEach {
                params[it] = request.url.queryParameter(it) ?: ""
            }
        } else if (request.method.equals("POST", true)) {
            if (request.body is FormBody) {
                val formBody = request.body as FormBody
                for (i in 0 until formBody.size) {
                    params[formBody.name(i)] = formBody.value(i)
                }
            }
        }
        return params
    }

    private fun sortParam(params: Map<String, String>): String {
        if (params.isEmpty()) return ""
        val listParams: List<Map.Entry<String, String>> = ArrayList(params.entries)
        val sb = StringBuilder()
        for (i in listParams.indices) {
            val item = listParams[i]
            val value = item.value
            sb.append("/".plus(value))
        }
        return sb.toString()
    }

    companion object {
        const val PARAMS_IN_PATH = "PARAMS_IN_PATH"
    }
}