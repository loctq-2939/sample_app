package com.example.detect_voice_app.extensions

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat


fun Context.arePermissionsGranted(requireAll: Boolean = true, vararg permissions: String): Boolean {
    val permissionChecker = { permission: String ->
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    return if (requireAll) permissions.all(permissionChecker) else permissions.any(permissionChecker)
}

fun Context.areLocationPermissionsGranted(requireBackground: Boolean): Boolean {
    val foregroundPermissionsGranted = arePermissionsGranted(requireAll = false,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val backgroundPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arePermissionsGranted(requireAll = true, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    } else {
        true
    }

    return if (!requireBackground) foregroundPermissionsGranted else foregroundPermissionsGranted && backgroundPermissionGranted
}

fun Context.createChannel(channelId: String, channelName: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.setSound(null, null)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}