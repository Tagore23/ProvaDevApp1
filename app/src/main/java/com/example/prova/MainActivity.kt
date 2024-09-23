package com.example.prova

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                    LayoutMain()
        }
    }
}

@Composable
fun LayoutMain() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "cadastro") {
        composable("cadastro") { TelaCadastro(navController) }
        composable("lista") { TelaLista(navController) }
        composable("detalhes/{produtoJson}") { backStackEntry ->
            val produtoJson = backStackEntry.arguments?.getString("produtoJson")
            val produto = Gson().fromJson(produtoJson, Produto::class.java)
            TelaDetalhes(produto, navController)
        }
        composable("estatisticas") { TelaEstatisticas(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastro(navController: NavController) {
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = nome,
            onValueChange = { nome = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nome do Produto") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = categoria,
            onValueChange = { categoria = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Categoria") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = preco,
            onValueChange = { preco = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Preço") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = quantidade,
            onValueChange = { quantidade = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Quantidade em Estoque") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val precoValue = preco.toDoubleOrNull()
            val quantidadeValue = quantidade.toIntOrNull()

            if (nome.isEmpty() || categoria.isEmpty() || precoValue == null || quantidadeValue == null) {
                Toast.makeText(context, "Todos os campos são obrigatórios.", Toast.LENGTH_SHORT).show()
            } else if (precoValue < 0) {
                Toast.makeText(context, "O preço não pode ser menor que 0.", Toast.LENGTH_SHORT).show()
            } else if (quantidadeValue < 1) {
                Toast.makeText(context, "A quantidade deve ser pelo menos 1.", Toast.LENGTH_SHORT).show()
            } else {
                val novoProduto = Produto(nome, categoria, precoValue, quantidadeValue)
                Estoque.adicionarProduto(novoProduto)
                navController.navigate("lista")
            }
        }) {
            Text("Cadastrar Produto")
        }
    }
}

@Composable
fun TelaLista(navController: NavController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        LazyColumn {
            items(Estoque.produtos) { produto ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Text(
                        text = "${produto.nome} (${produto.quantidade} unidades)",
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = {
                        val produtoJson = Gson().toJson(produto)
                        navController.navigate("detalhes/$produtoJson")
                    }) {
                        Text("Detalhes")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate("cadastro")
        }) {
            Text("Voltar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate("estatisticas")
        }) {
            Text("Estatísticas")
        }
    }
}

@Composable
fun TelaDetalhes(produto: Produto, navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Detalhes do Produto", fontSize = 22.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Nome: ${produto.nome}", fontSize = 18.sp)
        Text(text = "Categoria: ${produto.categoria}", fontSize = 18.sp)
        Text(text = "Preço: R$ ${produto.preco}", fontSize = 18.sp)
        Text(text = "Quantidade em estoque: ${produto.quantidade} unidades", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            navController.popBackStack()
        }) {
            Text("Voltar")
        }
    }
}

@Composable
fun TelaEstatisticas(navController: NavController) {
    val valorTotal = Estoque.calcularValorTotalEstoque()
    val quantidadeTotal = Estoque.produtos.sumOf { it.quantidade }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Estatísticas do estoque", fontSize = 22.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Valor total do estoque: R$ ${"%.2f".format(valorTotal)}", fontSize = 18.sp)
        Text(text = "Quantidade total de produtos: $quantidadeTotal unidades", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            navController.popBackStack()
        }) {
            Text("Voltar")
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewLayout(){
    LayoutMain()
}
