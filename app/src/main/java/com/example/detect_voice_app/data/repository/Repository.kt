package com.example.detect_voice_app.data.repository

interface Repository {
    suspend fun getVoice(): String?
}