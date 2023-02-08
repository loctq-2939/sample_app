package com.example.detect_voice_app.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.detect_voice_app.R
import com.example.detect_voice_app.base.BaseActivity
import com.example.detect_voice_app.databinding.ActivityMainBinding
import com.example.detect_voice_app.ui.detectAudio.DetectAudioFragment
import com.example.detect_voice_app.ui.service.LocationTrackerService
import com.example.detect_voice_app.utils.NotificationConstants.ACTION_NEAR_LOCATION
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override val viewModel: MainViewModel by viewModels()
    override val layoutId: Int = R.layout.activity_main

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            this@MainActivity.stopService(
                Intent(
                    this@MainActivity,
                    LocationTrackerService::class.java
                )
            )
        }
    }

    private val intentFilter: IntentFilter by lazy {
        IntentFilter().apply {
            addAction(ACTION_NEAR_LOCATION)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        replaceFragment(DetectAudioFragment(), R.id.container, addToBackStack = false)
        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
}