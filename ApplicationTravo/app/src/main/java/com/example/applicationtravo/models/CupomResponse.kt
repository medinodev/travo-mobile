package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName

data class CupomResponse(
    val id: Int,

    val estabelecimento_id: Int,

    @SerializedName("nome")
    val nome: String,

    @SerializedName("descricao")
    val descricao: String?,

    @SerializedName("expiration")
    val expiration: String?,

    @SerializedName("created_at")
    val created_at: String?,

    @SerializedName("codigo")
    val codigo: String?,

)
