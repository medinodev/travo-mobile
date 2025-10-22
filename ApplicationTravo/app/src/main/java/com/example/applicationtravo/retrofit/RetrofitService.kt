package com.example.applicationtravo.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    private var travoServiceAPI: TravoServiceAPI? = null
    private var travoServiceAPIWithToken: TravoServiceAPI? = null


    fun getTravoServiceAPI(): TravoServiceAPI {
        if(travoServiceAPI == null){
            val retrofit = Retrofit.Builder()
                // Quando for o emulador usar esse:
                 .baseUrl("http://10.0.2.2:3000/rest/v1/")
                // Quando for o dispositivo fisico usar esse:
                //.baseUrl("http://192.168.0.11:3000/rest/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            this.travoServiceAPI = retrofit.create(TravoServiceAPI::class.java)
        }
        return travoServiceAPI!!
    }

    fun getTravoServiceAPIWithToken(token: String): TravoServiceAPI {
        if(travoServiceAPIWithToken == null){

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(token))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.0.245:3000/rest/v1/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            this.travoServiceAPIWithToken = retrofit.create(TravoServiceAPI::class.java)
        }
        return travoServiceAPIWithToken!!
    }
}