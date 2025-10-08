package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName

data class RegistroRequest(

    @SerializedName("nome_fantasia")
    val nomeFantasia:String,
    val telefone: String,
    val email: String,
    val senha:String
)
