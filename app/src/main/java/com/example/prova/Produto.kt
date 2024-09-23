package com.example.prova

data class Produto(
    val nome: String,
    val categoria: String,
    val preco: Double,
    val quantidade: Int
)

object Inventario {
    val produtos = mutableListOf<Produto>()
}
