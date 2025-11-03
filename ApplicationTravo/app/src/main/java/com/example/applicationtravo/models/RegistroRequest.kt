package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName

data class RegistroRequest(
    @SerializedName("nome_completo")
    val nomeCompleto: String,
    val email: String,
    val senha: String,
    val telefone: String? = null,
    @SerializedName("data_nascimento")
    val dataNascimento: String? = null,
    @SerializedName("nome_usuario")
    val nomeUsuario: String? = null,
    val sobre: String? = null
)
