package com.hsnayal.dimora

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat

object NotificationUtil {

    private const val CHANNEL_ID = "dimora_dimmer_channel"

    fun create(context: Context): Notification {

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Dimora Dimmer",
            NotificationManager.IMPORTANCE_LOW
        )
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)

        // 🔴 Turn off action
        val stopIntent = Intent(context, DimmerService::class.java).apply {
            action = DimmerService.ACTION_STOP_DIMMER
        }

        val stopPendingIntent = PendingIntent.getService(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // white only
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_notification_large
                )
            ) // white icon
            .setContentTitle("Dimora is active")
            .setContentText("Screen dimmer is running")
            .setOngoing(true) // 🔒 Sticky
            .setOnlyAlertOnce(true)
            .addAction(
                R.drawable.ic_notification,
                "Turn Off",
                stopPendingIntent
            )
            .build()
    }
}

//package com.hsnayal.dimora
//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import androidx.core.app.NotificationCompat
//
//object NotificationUtil {
//
//    fun create(context: Context): Notification {
//        val channelId = "dimora_channel"
//
//        val channel = NotificationChannel(
//            channelId, "Dimora Screen Dimmer", NotificationManager.IMPORTANCE_LOW
//        )
//        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
//
//        return NotificationCompat.Builder(context, channelId).setContentTitle("Dimora active")
//            .setContentText("Screen dimmer is running").setSmallIcon(R.mipmap.ic_launcher)
//            .setOngoing(true).build()
//    }
//}