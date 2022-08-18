package com.milk.smartvpn.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.milk.smartvpn.R
import com.milk.smartvpn.ui.act.MainActivity

object Notification {
    private const val CONNECT_CHANNEL_ID = "StartVpn1000"
    private const val CONNECT_CHANNEL_NAME = "Vpn Connected"

    /** 获取系统通知开启功能 */
    internal fun obtainNotification(context: Context) {
        val localIntent = Intent()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                localIntent.data =
                    Uri.fromParts("package", context.packageName, null)
            }
            else -> {
                localIntent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                localIntent.putExtra("app_package", context.packageName)
                localIntent.putExtra("app_uid", context.applicationInfo.uid)
            }
        }
        context.startActivity(localIntent)
    }

    @SuppressLint("UnspecifiedImmutableFlag", "InlinedApi")
    internal fun showConnectedNotification(context: Context, vpnName: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent =
            PendingIntent.getActivity(
                context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        val finalChannelId = createNotificationChannel(
            context,
            CONNECT_CHANNEL_ID,
            CONNECT_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        val notification = NotificationCompat.Builder(context, finalChannelId)
            .setContentTitle("VPN activated")
            .setContentText("Connected to \"$vpnName\",please click to view!")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(102, notification)
    }

    private fun createNotificationChannel(
        context: Context,
        channelId: String,
        channelName: String,
        level: Int
    ): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(channelId, channelName, level)
            manager.createNotificationChannel(channel)
            channelId
        } else ""
    }
}