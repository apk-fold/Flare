package fr.apkfold.flare.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import fr.apkfold.flare.R

object FlareChannels {
    const val SERVICE = "service"
    const val ALERTS = "alerts"

    fun create(ctx: Context) {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(SERVICE, ctx.getString(R.string.channel_service), NotificationManager.IMPORTANCE_MIN).apply {
                setShowBadge(false)
            },
        )
        nm.createNotificationChannel(
            NotificationChannel(ALERTS, ctx.getString(R.string.channel_alerts), NotificationManager.IMPORTANCE_DEFAULT),
        )
    }
}
