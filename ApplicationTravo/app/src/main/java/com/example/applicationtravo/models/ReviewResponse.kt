package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName


data class ReviewResponse(
    val id: Long,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("servico_id") val servicoId: Long,
    @SerializedName("usuario_id") val usuarioId: Long?,
    @SerializedName("comentario") val comentario: String?,
    @SerializedName("numero_estrelas") val numeroEstrelas: Int,
    @SerializedName("data_comentario") val dataComentario: String?,
    @SerializedName("organizacao_id") val organizacaoId: Long?
)
