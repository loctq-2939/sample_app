package com.example.detect_voice_app.ui

import androidx.fragment.app.viewModels
import com.example.detect_voice_app.R
import com.example.detect_voice_app.base.BaseFragment
import com.example.detect_voice_app.databinding.FragmentDetectAudioBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetectAudioFragment : BaseFragment<FragmentDetectAudioBinding, DetectAudioViewModel>() {
    override val viewModel: DetectAudioViewModel by viewModels()
    override val layoutId: Int = R.layout.fragment_detect_audio

}