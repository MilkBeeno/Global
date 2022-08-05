package com.milk.smartvpn.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.milk.smartvpn.R
import com.milk.smartvpn.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}