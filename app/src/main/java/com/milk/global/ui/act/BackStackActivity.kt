package com.milk.global.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.milk.global.ad.AppOpenAd
import com.milk.global.databinding.ActivityBackStackBinding
import com.milk.global.friebase.FireBaseManager
import com.milk.global.friebase.FirebaseKey
import com.milk.global.repository.AppRepository
import com.milk.global.util.MilkTimer
import com.milk.simple.ktx.immersiveStatusBar

class BackStackActivity : AbstractActivity() {
    private val appOpenAd by lazy { AppOpenAd() }
    private val binding by lazy { ActivityBackStackBinding.inflate(layoutInflater) }
    private val fromStartPage by lazy { intent.getBooleanExtra(FROM_START_PAGE, false) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        loadAppOpenAd()
    }

    private fun initializeView() {
        immersiveStatusBar(false)
        binding.lineLottieView.setAnimation("back_stack_progress.json")
        binding.lineLottieView.playAnimation()
    }

    private fun loadAppOpenAd() {
        MilkTimer.Builder()
            .setMillisInFuture(12000)
            .setOnFinishedListener {
                if (!appOpenAd.isClosedAd()) {
                    next()
                }
            }
            .build()
            .start()

        if (AppRepository.showOpenAd) {
            FireBaseManager.logEvent(FirebaseKey.Make_an_ad_request_3)
            appOpenAd.load(
                context = this,
                failure = {
                    next()
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_failed_3, it)
                },
                success = {
                    showAppOpenAd()
                    FireBaseManager.logEvent(FirebaseKey.Ad_request_succeeded_3)
                }
            )
        }
    }

    private fun showAppOpenAd() {
        appOpenAd.show(
            activity = this,
            failure = {
                next()
                FireBaseManager.logEvent(FirebaseKey.Ad_show_failed_3, it)
            },
            success = {
                FireBaseManager.logEvent(FirebaseKey.The_ad_show_success_3)
            },
            click = {
                FireBaseManager.logEvent(FirebaseKey.click_ad_3)
            },
            close = {
                next()
            }
        )
    }

    private fun next() {
        if (fromStartPage) {
            MainActivity.create(this)
        }
        finish()
    }

    override fun onInterceptKeyDownEvent(): Boolean = true

    companion object {
        private const val FROM_START_PAGE = "FROM_START_PAGE"
        fun create(context: Context, fromStartPage: Boolean = false) {
            val intent = Intent(context, BackStackActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(FROM_START_PAGE, fromStartPage)
            context.startActivity(intent)
        }
    }
}