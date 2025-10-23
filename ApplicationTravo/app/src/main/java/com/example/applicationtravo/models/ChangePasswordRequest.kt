package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("senhaAtual")
    val senhaAtual: String,
    @SerializedName("novaSenha")
    val novaSenha: String
)
