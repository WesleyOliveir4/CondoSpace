package com.example.condospace.model

data class Product(
    val tituloProduto: String,
    val descricaoProduto: String,
    val notaProduto: Double,
    val numeroAvaliacoesProduto: Int,
    val imageRes: Int
)