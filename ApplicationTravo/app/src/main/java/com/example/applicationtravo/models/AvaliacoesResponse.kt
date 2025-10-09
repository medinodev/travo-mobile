package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName

data class AvaliacoesResponse(
    @SerializedName("total")
    val totalAvaliacoes: Int,

    @SerializedName("distribuicao")
    val distribuicaoEstrelas: DistribuicaoEstrelasResponse,

    @SerializedName("itens")
    val listaAvaliacoes: List<ReviewResponse> = emptyList()
)
