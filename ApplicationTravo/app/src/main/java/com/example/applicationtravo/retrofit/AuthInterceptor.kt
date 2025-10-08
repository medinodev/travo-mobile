package com.example.applicationtravo.retrofit

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor (val token: String): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        requestBuilder.addHeader("Authorization", "Bearer $token") // Or "JWT $it" depending on your backend

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}