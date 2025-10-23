package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName

data class LocalDetalheResponse(
    val id: Int,

    @SerializedName("nome") val nome: String,
    @SerializedName("endereco") val endereco: String?,
    @SerializedName("resumo") val resumo: String?,
    @SerializedName("imagem_capa_url") val imagemCapaUrl: String?,
    @SerializedName("funcionamento_hoje") val funcionamentoHoje: String?,

    // NOVOS CAMPOS
    @SerializedName("sobre") val sobre: String?,
    @SerializedName("cep") val cep: String?,
    @SerializedName("horarios") val horarios: String?,   // se vier JSON estruturado, depois tipamos melhor
    @SerializedName("tipo") val tipo: String?,

    // Embutidos (se a API enviar junto)
    @SerializedName("cupons") val listaCupons: List<CupomResponse> = emptyList(),
    @SerializedName("avaliacoes") val avaliacoes: List<ReviewResponse>? = null
)
