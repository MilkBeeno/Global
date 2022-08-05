package com.milk.smartvpn.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.milk.smartvpn.databinding.ActivityLaunchBinding

class LaunchActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLaunchBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}