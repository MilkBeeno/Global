package com.milk.global.util

import android.app.Activity
import android.app.Application
import android.os.Bundle

object BackStack {
    private var createdActivities = mutableListOf<Activity>()
    private var isRunInBackground: Boolean = false
    fun backToForegroundMonitor(
        application: Application,
        backgroundToForeground: (Activity) -> Unit = {}
    ) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
            override fun onActivityStarted(activity: Activity) {
                createdActivities.add(activity)
                if (createdActivities.size == 1 && isRunInBackground) backgroundToForeground(
                    activity
                )
            }

            override fun onActivityResumed(activity: Activity) = Unit
            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) {
                if (createdActivities.contains(activity)) {
                    createdActivities.remove(activity)
                    isRunInBackground = createdActivities.isEmpty()
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
        })
    }
}