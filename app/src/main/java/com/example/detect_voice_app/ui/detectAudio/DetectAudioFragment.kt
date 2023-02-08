package com.example.detect_voice_app.ui.detectAudio

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.example.detect_voice_app.BuildConfig
import com.example.detect_voice_app.R
import com.example.detect_voice_app.base.BaseFragment
import com.example.detect_voice_app.databinding.FragmentDetectAudioBinding
import com.example.detect_voice_app.ui.service.LocationTrackerService
import com.example.detect_voice_app.utils.showDialog
import dagger.hilt.android.AndroidEntryPoint
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber

@AndroidEntryPoint
@RuntimePermissions
class DetectAudioFragment : BaseFragment<FragmentDetectAudioBinding, DetectAudioViewModel>() {
    override val viewModel: DetectAudioViewModel by viewModels()
    override val layoutId: Int = R.layout.fragment_detect_audio

    var formattedSpeech: StringBuffer = StringBuffer()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        with(viewBinding) {
            btnStart.setOnClickListener {
                startListerWithPermissionCheck()
            }
        }
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_CALLING_PACKAGE,
            BuildConfig.APPLICATION_ID
        )
        val recognizer = SpeechRecognizer
            .createSpeechRecognizer(requireContext())
        val listener: RecognitionListener = object : RecognitionListener {
            override fun onResults(results: Bundle) {
                val voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (voiceResults == null) {
                    Timber.d("No voice results")
                } else {
                    for (match in voiceResults) {
                        formattedSpeech.append(String.format("\n- %s", match.toString()))
                        viewBinding.tvSpeechText.text = formattedSpeech.toString()
                    }
                }
                recognizer.cancel()
                recognizer.startListening(intent)
            }

            override fun onReadyForSpeech(params: Bundle) {
                Timber.d("Ready for speech")
            }

            /**
             * ERROR_NETWORK_TIMEOUT = 1;
             * ERROR_NETWORK = 2;
             * ERROR_AUDIO = 3;
             * ERROR_SERVER = 4;
             * ERROR_CLIENT = 5;
             * ERROR_SPEECH_TIMEOUT = 6;
             * ERROR_NO_MATCH = 7;
             * ERROR_RECOGNIZER_BUSY = 8;
             * ERROR_INSUFFICIENT_PERMISSIONS = 9;
             *
             * @param error code is defined in SpeechRecognizer
             */
            override fun onError(error: Int) {
                System.err.println("Error listening for speech: $error")
                recognizer.cancel()
                recognizer.startListening(intent)
            }

            override fun onBeginningOfSpeech() {
                // TODO Auto-generated method stub
            }

            override fun onBufferReceived(buffer: ByteArray) {
                // TODO Auto-generated method stub
            }

            override fun onEndOfSpeech() {
                // TODO Auto-generated method stub
            }

            override fun onEvent(eventType: Int, params: Bundle) {
                // TODO Auto-generated method stub
            }

            override fun onPartialResults(partialResults: Bundle) {
                // TODO Auto-generated method stub
            }

            override fun onRmsChanged(rmsdB: Float) {
                // TODO Auto-generated method stub
            }
        }
        recognizer.setRecognitionListener(listener)
        recognizer.startListening(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    fun startLister() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startBackgroundLocationWithPermissionCheck()
        } else {
            startListening()
            requireActivity().startService(
                Intent(
                    requireContext(),
                    LocationTrackerService::class.java
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @NeedsPermission(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    )
    fun startBackgroundLocation() {
        startListening()
        requireActivity().startService(
            Intent(
                requireContext(),
                LocationTrackerService::class.java
            )
        )
    }

    @OnShowRationale(Manifest.permission.RECORD_AUDIO)
    fun showRationaleForCamera(request: PermissionRequest) {
        showDialog(
            title = getString(R.string.title_record_audio_permission),
            message = getString(R.string.message_record_audio_permission),
            textPositive = getString(R.string.ok),
            textNegative = getString(R.string.cancel),
            positiveListener = {
                request.proceed()
            }
        )
    }

    @OnNeverAskAgain(Manifest.permission.RECORD_AUDIO)
    fun onCameraNeverAskAgain() {
    }

}