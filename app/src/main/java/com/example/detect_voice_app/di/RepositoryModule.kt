package com.example.detect_voice_app.di

import com.example.detect_voice_app.data.remote.api.Service
import com.example.detect_voice_app.data.repository.Repository
import com.example.detect_voice_app.data.repository.impl.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideMovieRepository(userService: Service): Repository = RepositoryImpl(
        userService
    )
}