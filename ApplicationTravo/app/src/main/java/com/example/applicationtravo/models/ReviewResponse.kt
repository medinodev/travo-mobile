package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    val id: Int,

    @SerializedName("autor")
    val nomeAutor: String,

    @SerializedName("estrelas")
    val quantidadeEstrelas: Int,

    @SerializedName("comentario")
    val comentario: String?,

    @SerializedName("data")
    val dataPublicacao: String?
)
