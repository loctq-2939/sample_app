package com.example.detect_voice_app.utils

import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.detect_voice_app.R
import com.example.detect_voice_app.ui.main.MainActivity
import com.example.detect_voice_app.utils.NotificationConstants.NOTIFICATION_ID


class NotificationUtils(private val service: Service) {

    private var notificationManager: NotificationManagerCompat? = null
    private var notificationBuilder: NotificationCompat.Builder? = null

    /**
     * Creates the notification if it does not exist already and recreates it if forceRecreate is
     * true. Updates the notification with the data in the player.
     * @param forceRecreate whether to force the recreation of the notification even if it already
     * exists
     */
    @Synchronized
    fun createNotificationIfNeededAndUpdate(forceRecreate: Boolean) {
        if (forceRecreate || notificationBuilder == null) {
            notificationBuilder = createNotification()
        }
        //updateNotification()
        notificationBuilder?.build()?.let { notificationManager?.notify(NOTIFICATION_ID, it) }
    }

    @Synchronized
    private fun createNotification(): NotificationCompat.Builder {
        notificationManager = NotificationManagerCompat.from(service)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            service,
            service.getString(R.string.notification_channel_id)
        )
        builder.setStyle(
            NotificationCompat.BigTextStyle().bigText(service.getString(R.string.service_is_running))
        )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setShowWhen(false)
            .setSound(null)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setColor(
                ContextCompat.getColor(
                    service,
                    R.color.notifyIconBackground
                )
            )
            .setDeleteIntent(
                PendingIntentCompat.getBroadcast(
                    service,
                    NOTIFICATION_ID, Intent(NotificationConstants.ACTION_CLOSE), FLAG_UPDATE_CURRENT
                )
            ).setContentIntent(
                PendingIntentCompat.getActivity(
                    service,
                    NotificationConstants.NOTIFICATION_REQUEST_ID,
                    Intent(service, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        action = Intent.ACTION_MAIN
                        addCategory(Intent.CATEGORY_LAUNCHER)
                    },
                    FLAG_UPDATE_CURRENT
                )
            )
        return builder
    }

    fun createNotificationAndStartForeground() {
        if (notificationBuilder == null) {
            notificationBuilder = createNotification()
        }
        updateNotification()
        service.startForeground(NOTIFICATION_ID, notificationBuilder?.build())
    }

    private fun updateNotification() {
        notificationBuilder?.apply {
            setContentTitle(service.getString(R.string.location_tracker))
            setContentText(service.getString(R.string.content_text))
            setTicker(service.getString(R.string.sticker))
        }
    }
}