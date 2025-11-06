package com.example.applicationtravo.models

data class FavoriteResponse (
    val id: Int,
    val usuario_id: Int,
    val estabelecimento_id: Int,
    val created_at: String
)