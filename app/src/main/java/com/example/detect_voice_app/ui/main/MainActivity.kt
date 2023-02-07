package com.example.detect_voice_app.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import com.example.detect_voice_app.R
import com.example.detect_voice_app.base.BaseActivity
import com.example.detect_voice_app.databinding.ActivityMainBinding
import com.example.detect_voice_app.ui.detectAudio.DetectAudioFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override val viewModel: MainViewModel by viewModels()
    override val layoutId: Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        replaceFragment(DetectAudioFragment(), R.id.container, addToBackStack = false)
    }
}