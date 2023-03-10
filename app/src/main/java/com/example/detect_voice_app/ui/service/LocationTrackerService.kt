package com.example.detect_voice_app.ui.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.detect_voice_app.data.repository.Repository
import com.example.detect_voice_app.utils.NotificationConstants
import com.example.detect_voice_app.utils.NotificationConstants.RADIUS
import com.example.detect_voice_app.utils.NotificationUtils
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LocationTrackerService : Service() {

    @Inject
    lateinit var repository: Repository

    private var mLocationRequest: LocationRequest? = null
    private val notificationUtils: NotificationUtils by lazy { NotificationUtils(this) }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val locationCallback = object :
        LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val resultsDis = FloatArray(1)
            result.lastLocation?.apply {
                Location.distanceBetween(
                    NotificationConstants.LATITUDE,
                    NotificationConstants.LONGITUDE,
                    latitude,
                    longitude,
                    resultsDis
                )
            }
            Timber.tag("TAG").d("onLocationResult: %s", resultsDis[0])
            if (resultsDis[0] > RADIUS) {
                Timber.tag("TAG").d("onLocationResult: You are not in area")
            } else {
                Timber.tag("TAG").d("onLocationResult: You are in area")
                LocationServices.getFusedLocationProviderClient(this@LocationTrackerService)
                    .removeLocationUpdates(this)
                scope.launch {
                    //delay(3000)

                    try {
                        val mp3Link = repository.getVoice()
                        if (mp3Link.isNullOrEmpty()) {
                            startLocationUpdates()
                        } else {
                            Timber.tag("TAG").d("Mp3 = $mp3Link")
                            LocalBroadcastManager.getInstance(applicationContext)
                                .sendBroadcast(
                                    Intent(NotificationConstants.ACTION_NEAR_LOCATION).apply {
                                        putExtra("MP3", mp3Link)
                                    }
                                )
                        }
                    } catch (ex: Exception) {

                    }
                }
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

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}