package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName

data class ServicoListagemResponse(
    val id: Int,

    @SerializedName("nome")
    val nome: String,

    @SerializedName("endereco")
    val endereco: String?,

    @SerializedName("tipo")
    val tipo: String?,

    @SerializedName("lat")
    val lat: Double?,

    @SerializedName("lng")
    val lng: Double?,

    @SerializedName("imagem_capa_url")
    val imagemCapaUrl: String?,

    @SerializedName("media_avaliacao")
    val mediaAvaliacao: Double?,

    @SerializedName("total_avaliacoes")
    val totalAvaliacoes: Int?
)
