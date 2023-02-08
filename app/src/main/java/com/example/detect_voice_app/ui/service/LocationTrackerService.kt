package com.example.detect_voice_app.ui.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.detect_voice_app.utils.NotificationConstants
import com.example.detect_voice_app.utils.NotificationUtils
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import timber.log.Timber

class
LocationTrackerService : Service() {

    private var mLocationRequest: LocationRequest? = null
    private val notificationUtils: NotificationUtils by lazy { NotificationUtils(this) }

    private val resultsDis = FloatArray(1)
    private val radius = 0.03 * 1000 // 30m

    private val locationCallback = object :
        LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.apply {
                Location.distanceBetween(
                    NotificationConstants.LATITUDE,
                    NotificationConstants.LONGITUDE,
                    latitude,
                    longitude,
                    resultsDis
                )
            }
            if (resultsDis[0] > radius) {
                Log.d("TAG", "onLocationResult: You are not in area")
            } else {
                LocalBroadcastManager.getInstance(applicationContext)
                    .sendBroadcastSync(
                        Intent(NotificationConstants.ACTION_NEAR_LOCATION)
                    )
                LocationServices.getFusedLocationProviderClient(this@LocationTrackerService)
                    .removeLocationUpdates(this)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        generateForegroundNotification()
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createLocationRequest()
        startLocationUpdates()
        return START_STICKY
    }

    private fun generateForegroundNotification() {
        notificationUtils.createNotificationAndStartForeground()
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(500)
            .setMaxUpdateDelayMillis(5000)
            .build()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        try {
            mLocationRequest?.let {
                LocationServices.getFusedLocationProviderClient(this)
                    .requestLocationUpdates(it, locationCallback, null)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}