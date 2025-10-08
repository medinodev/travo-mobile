package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class UsuariosResponse(
    val id: Int,
    val admin: Boolean,
    val email: String,
    val senha: String,
    @SerializedName("nome_usuario")
    val nomeUsuario: String,
    @SerializedName("nome_completo")
    val nomeCompleto: String,
    val sobre: String?,
    val fotoPerfil: String?,
    val tipoPlano: Int,
    val createdAt: LocalDateTime,
)
