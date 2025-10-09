package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName

data class CupomResponse(
    val id: Int,

    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("descricao")
    val descricao: String?,

    @SerializedName("codigo")
    val codigo: String?,

    @SerializedName("validade")
    val validade: String?
)
