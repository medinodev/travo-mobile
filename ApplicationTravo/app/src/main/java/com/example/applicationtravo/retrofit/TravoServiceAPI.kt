package com.example.applicationtravo.retrofit

import com.example.applicationtravo.models.LoginRequest
import com.example.applicationtravo.models.LoginResponse
import com.example.applicationtravo.models.RegistroRequest
import com.example.applicationtravo.models.UsuariosResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TravoServiceAPI {

    @POST("usuariosOrg")
    suspend fun registrar(@Body registroRequest: RegistroRequest): Response<Unit>

    @POST("usuariosOrg/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("usuarios")
    suspend fun getAllUsers():Response<List<UsuariosResponse>>
}