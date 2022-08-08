package com.milk.smartvpn.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.milk.smartvpn.databinding.ActivityWebBinding

class WebActivity : AbstractActivity() {
    private val binding by lazy { ActivityWebBinding.inflate(layoutInflater) }
    private val url by lazy { intent.getStringExtra(WEB_VIEW_URL).toString() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.webView.loadUrl(url)
        binding.webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url.toString())
                return true
            }
        }
    }

    companion object {
        private const val WEB_VIEW_URL = "WEB_VIEW_URL"
        fun create(context: Context, url: String) {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra(WEB_VIEW_URL, url)
            context.startActivity(intent)
        }
    }
}