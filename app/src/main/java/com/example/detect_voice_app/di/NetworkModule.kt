package com.example.detect_voice_app.di

import android.content.Context
import com.example.detect_voice_app.BuildConfig
import com.example.detect_voice_app.data.intercept.HeaderInterceptor
import com.example.detect_voice_app.data.remote.api.Service
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

    @Singleton
    @Provides
    fun provideAppRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("")
            .client(okHttpClient)
            .build()

    @Singleton
    @Provides
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        header: Interceptor,
        cache: Interceptor
    ) =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(cache)
            .addInterceptor(header)
            .retryOnConnectionFailure(true)
            .build()

    @Singleton
    @Provides
    fun provideHeaderInterceptor(
        context: Context,
        logging: HttpLoggingInterceptor
    ): Interceptor =
        HeaderInterceptor(context)

    @Singleton
    @Provides
    fun provideService(
        retrofit: Retrofit
    ): Service = retrofit.create(Service::class.java)
}