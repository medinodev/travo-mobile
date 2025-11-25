package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName

data class CupomClienteResponse(
    val id: Int,

    @SerializedName("cupom_id")
    val cupomId: Int,

    @SerializedName("usuario_id")
    val usuarioId: Int,

    @SerializedName("status_ativo")
    val statusAtivo: Boolean,

    @SerializedName("resgatado")
    val resgatado: String?,

    @SerializedName("codigo")
    val codigo: String
)
data class ValidarCupomRequest(
    val codigo: String
)

data class ValidarCupomResponse(
    val valido: Boolean,
    val mensagem: String,
    val cupom: CupomResponse?,
    val uso: Any?
)
