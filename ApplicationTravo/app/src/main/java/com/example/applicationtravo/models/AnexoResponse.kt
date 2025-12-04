package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName

data class AnexoResponse(
    val id: Int,
    @SerializedName("entidade_id")
    val entidadeId: Int,
    @SerializedName("nome_arquivo")
    val nomeArquivo: String,
    val path: String,
    @SerializedName("url_publica")
    val urlPublica: String,
    val mimetype: String,
    val tamanho: String,
    @SerializedName("entidade_tipo")
    val entidadeTipo: String,
)
