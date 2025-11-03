package com.example.applicationtravo.retrofit

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.IOException

class LoggingInterceptor : Interceptor {
    
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        val requestBody = request.body
        var bodyString: String? = null
        if (requestBody != null) {
            try {
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                bodyString = buffer.readUtf8()
            } catch (e: IOException) {
                bodyString = "Erro ao ler body"
            }
        }
        
        Log.e("HTTP_REQUEST", "═══════════════════════════════════════════")
        Log.e("HTTP_REQUEST", "🔵 URL COMPLETA: ${request.url}")
        Log.e("HTTP_REQUEST", "🔵 PATH: ${request.url.encodedPath}")
        Log.e("HTTP_REQUEST", "🔵 Method: ${request.method}")
        Log.e("HTTP_REQUEST", "🔵 Headers: ${request.headers}")
        if (bodyString != null) {
            Log.e("HTTP_REQUEST", "🔵 Body: $bodyString")
        }
        Log.e("HTTP_REQUEST", "═══════════════════════════════════════════")
        
        val response = chain.proceed(request)
        
        val responseBody = response.peekBody(1024 * 1024) // Limita a 1MB
        val responseBodyString = try {
            responseBody.string()
        } catch (e: Exception) {
            "Erro ao ler response body: ${e.message}"
        }
        
        Log.e("HTTP_RESPONSE", "═══════════════════════════════════════════")
        Log.e("HTTP_RESPONSE", "URL: ${request.url}")
        Log.e("HTTP_RESPONSE", "Status: ${response.code} ${response.message}")
        Log.e("HTTP_RESPONSE", "Response Body: $responseBodyString")
        Log.e("HTTP_RESPONSE", "═══════════════════════════════════════════")
        
        return response
    }
}

