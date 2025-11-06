package com.example.applicationtravo.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    private var travoServiceAPI: TravoServiceAPI? = null
    private var travoServiceAPIWithToken: TravoServiceAPI? = null

    // Constante para a URL base
    //private const val BASE_URL = "http://192.168.0.11:3000/rest/v1/" // Dispositivo físico
    private const val BASE_URL = "http://10.0.2.2:3000/rest/v1/" // Emulador

    fun getTravoServiceAPI(): TravoServiceAPI {
        // Força recriação para evitar cache (útil durante desenvolvimento)
        // TODO: Remover recriação forçada em produção para melhor performance
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(LoggingInterceptor()) // Adiciona logging de todas as requisições
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(TravoServiceAPI::class.java)
        
        // Versão com cache (descomente em produção):
        /*
        if(travoServiceAPI == null){
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            this.travoServiceAPI = retrofit.create(TravoServiceAPI::class.java)
        }
        return travoServiceAPI!!
        */
    }

    fun getTravoServiceAPIWithToken(token: String): TravoServiceAPI {
        if(travoServiceAPIWithToken == null){

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(token))
                .build()

            val retrofit = Retrofit.Builder()
                //.baseUrl("http://192.168.0.245:3000/rest/v1/")
                //.baseUrl("http://192.168.0.5:3000/rest/v1/")
                .baseUrl("http://10.0.2.2:3000/rest/v1/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            this.travoServiceAPIWithToken = retrofit.create(TravoServiceAPI::class.java)
        }
        return travoServiceAPIWithToken!!
    }
}