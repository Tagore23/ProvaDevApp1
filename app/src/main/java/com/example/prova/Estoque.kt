package com.example.prova

class Estoque {
    companion object {
        val produtos = mutableListOf<Produto>()

        fun adicionarProduto(produto: Produto) {
            produtos.add(produto)
        }

        fun calcularValorTotalEstoque(): Double {
            return produtos.sumOf { it.preco * it.quantidade }
        }
    }
}