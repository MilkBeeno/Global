package com.milk.smartvpn.ui.act

import android.os.Bundle
import com.milk.smartvpn.databinding.ActivityResultBinding

class ResultActivity : AbstractActivity() {
    private val binding by lazy { ActivityResultBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}