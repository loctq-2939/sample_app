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
            .build()
        return chain.proceed(request)
    }

    private fun interceptorHeaders(): Headers {
        val headers = Headers.Builder()
        return headers.build()
    }
}