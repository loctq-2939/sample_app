package com.example.detect_voice_app.data.intercept

import android.content.Context
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class HeaderInterceptor @Inject constructor(
    private val context: Context,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
            .headers(interceptorHeaders())
            .build()
        return chain.proceed(request)
    }

    private fun interceptorHeaders(): Headers {
        val headers = Headers.Builder()
            .add("X-RapidAPI-Key", "b95894c9dcmsh15505fb6f2a0a21p1f8565jsn1ba3172e5774")
            .add("X-RapidAPI-Host", "deezerdevs-deezer.p.rapidapi.com")
        return headers.build()
    }
}