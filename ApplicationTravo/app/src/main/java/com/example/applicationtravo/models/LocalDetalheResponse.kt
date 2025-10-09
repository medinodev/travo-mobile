package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName

data class LocalDetalheResponse(
    val id: Int,

    @SerializedName("nome")
    val nome: String,

    @SerializedName("endereco")
    val endereco: String?,

    @SerializedName("resumo")
    val resumo: String?,

    @SerializedName("imagem_capa_url")
    val imagemCapaUrl: String?,

    @SerializedName("funcionamento_hoje")
    val funcionamentoHoje: String?,

    // Se o backend devolver tudo numa rota só, já mapeamos:
    @SerializedName("cupons")
    val listaCupons: List<CupomResponse> = emptyList(),

    @SerializedName("avaliacoes")
    val blocoAvaliacoes: AvaliacoesResponse? = null
)
