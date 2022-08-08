package com.milk.smartvpn.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.milk.smartvpn.R
import com.milk.smartvpn.databinding.ActivityMainBinding

class MainActivity : AbstractActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onInterceptKeyDownEvent() = true

    companion object {
        fun create(context: Context) =
            context.startActivity(Intent(context, MainActivity::class.java))
    }
}