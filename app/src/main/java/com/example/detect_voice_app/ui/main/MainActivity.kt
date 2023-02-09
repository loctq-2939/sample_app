package com.example.detect_voice_app.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RawRes
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.detect_voice_app.R
import com.example.detect_voice_app.base.BaseActivity
import com.example.detect_voice_app.databinding.ActivityMainBinding
import com.example.detect_voice_app.extensions.getRawUri
import com.example.detect_voice_app.ui.detectAudio.DetectAudioFragment
import com.example.detect_voice_app.ui.service.LocationTrackerService
import com.example.detect_voice_app.utils.NotificationConstants.ACTION_NEAR_LOCATION
import com.example.detect_voice_app.utils.NotificationConstants.ACTION_STOP_MUSIC
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override val viewModel: MainViewModel by viewModels()
    override val layoutId: Int = R.layout.activity_main

    private var mediaPlayer: MediaPlayer? = null

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_NEAR_LOCATION -> {
                    initMediaPlayer(url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3") {
                        startService(Intent(this@MainActivity, LocationTrackerService::class.java))
                    }
                }
                ACTION_STOP_MUSIC -> {
                    initMediaPlayer(R.raw.hpbd) {
                        startService(Intent(this@MainActivity, LocationTrackerService::class.java))
                    }
                }
            }
        }
    }

    private val intentFilter: IntentFilter by lazy {
        IntentFilter().apply {
            addAction(ACTION_NEAR_LOCATION)
            addAction(ACTION_STOP_MUSIC)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        muteSound()
        replaceFragment(DetectAudioFragment(), R.id.container, addToBackStack = false)
        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationTrackerService()
        releaseMedia()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    private fun stopLocationTrackerService() {
        stopService(Intent(this, LocationTrackerService::class.java))
    }

    private fun initMediaPlayer(@RawRes soundRawId: Int, completed: () -> Unit = {}) {
        releaseMedia()
        mediaPlayer = MediaPlayer().apply {
            try {
                getRawUri(soundRawId)?.let {
                    setDataSource(this@MainActivity, it)
                }
                prepareAsync()
                setOnPreparedListener {
                    start()
                }
                setOnCompletionListener {
                    releaseMedia()
                    completed.invoke()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun initMediaPlayer(url: String, completed: (() -> Unit)? = null) {
        releaseMedia()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    start()
                }
                setOnErrorListener { _, i, i2 ->
                    Toast.makeText(this@MainActivity, "($i, $i2)", Toast.LENGTH_LONG)
                        .show()
                    false
                }
                setOnCompletionListener {
                    completed?.invoke()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun releaseMedia() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun muteSound() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_NOTIFICATION,
                AudioManager.ADJUST_MUTE,
                0
            )
            audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0)
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0)
            audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0)
        } else {
            audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true)
            audioManager.setStreamMute(AudioManager.STREAM_ALARM, true)
            audioManager.setStreamMute(AudioManager.STREAM_RING, true)
            audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true)
        }
    }
}