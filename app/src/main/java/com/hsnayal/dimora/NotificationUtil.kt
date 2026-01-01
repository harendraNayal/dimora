package com.hsnayal.dimora

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

object NotificationUtil {

    fun create(context: Context): Notification {
        val channelId = "dimora_channel"

        val channel = NotificationChannel(
            channelId,
            "Dimora Screen Dimmer",
            NotificationManager.IMPORTANCE_LOW
        )
        context
            .getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)

        return NotificationCompat.Builder(context, channelId)
            .setContentTitle("Dimora active")
            .setContentText("Screen dimmer is running")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
    }
}