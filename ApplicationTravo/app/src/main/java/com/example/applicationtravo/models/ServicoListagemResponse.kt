package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName

data class ServicoListagemResponse(
    val id: Int,

    @SerializedName("nome")
    val nome: String,

    @SerializedName("endereco")
    val endereco: String?,

    @SerializedName("imagem_capa_url")
    val imagemCapaUrl: String?
)
