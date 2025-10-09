package com.example.applicationtravo.models

import com.google.gson.annotations.SerializedName

data class DistribuicaoEstrelasResponse(
    @SerializedName("estrela_5")
    val quantidadeEstrela5: Int,

    @SerializedName("estrela_4")
    val quantidadeEstrela4: Int,

    @SerializedName("estrela_3")
    val quantidadeEstrela3: Int,

    @SerializedName("estrela_2")
    val quantidadeEstrela2: Int,

    @SerializedName("estrela_1")
    val quantidadeEstrela1: Int
)
