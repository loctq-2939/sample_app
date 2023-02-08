package com.example.detect_voice_app.ui.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Bundle
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

class LocationTrackerService : Service() {

    private var mLocationRequest: LocationRequest? = null
    private val notificationUtils: NotificationUtils by lazy { NotificationUtils(this) }

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
        mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(500)
            .setMaxUpdateDelayMillis(1000)
            .build()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        try {
            val resultsDis = FloatArray(1)
            val radius = 0.03 * 1000 // 0.5km
            mLocationRequest?.let {
                LocationServices.getFusedLocationProviderClient(this)
                    .requestLocationUpdates(it, object :
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
                                        Intent(NotificationConstants.ACTION_NEAR_LOCATION).apply {
                                            val bundle = Bundle()
                                            bundle.putBoolean("status", true)
                                            putExtras(bundle)
                                        }
                                    )
                            }
                        }
                    }, null)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}