package com.example.detect_voice_app.data.repository.impl

import com.example.detect_voice_app.data.remote.api.Service
import com.example.detect_voice_app.data.repository.Repository
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val service: Service
) : Repository {
    override suspend fun getVoice(): Any {
        return service.getVoice()
    }

}