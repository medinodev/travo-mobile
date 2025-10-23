package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName

data class UsuarioUpdateRequest(
    val email: String? = null,
    @SerializedName("nome_usuario") val nomeUsuario: String? = null,
    @SerializedName("nome_completo") val nomeCompleto: String? = null,
    val sobre: String? = null,
    @SerializedName("foto_perfil") val fotoPerfil: String? = null,
    val telefone: String? = null,
)


