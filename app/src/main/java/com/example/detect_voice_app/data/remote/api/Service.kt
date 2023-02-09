package com.example.detect_voice_app.data.remote.api

import com.example.detect_voice_app.data.repository.impl.Mp3
import retrofit2.http.GET

interface Service {
    @GET("track/1109737")
    suspend fun getVoice(): Mp3
}