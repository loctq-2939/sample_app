package com.example.detect_voice_app.data.remote.api

import retrofit2.http.GET

interface Service {
    @GET("api/v1/")
    suspend fun getVoice(): Any
}