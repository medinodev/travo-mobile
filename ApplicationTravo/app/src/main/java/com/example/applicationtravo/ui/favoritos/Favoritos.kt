package com.example.applicationtravo.ui.favoritos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationtravo.R
import com.example.applicationtravo.models.ServicoListagemResponse
import com.example.applicationtravo.retrofit.RetrofitService
import com.example.applicationtravo.ui.detalhesLocal.DetalhesLocal
import com.example.applicationtravo.ui.listaServicos.ServicoAdapter
import com.example.applicationtravo.ui.configuracoes.Configuracoes
import com.example.applicationtravo.ui.home.HomeActivity
import com.example.applicationtravo.ui.listaCupons.ListaCupons
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Favoritos : AppCompatActivity() {

    private lateinit var recyclerFavoritos: RecyclerView
    private lateinit var etSearch: TextInputEditText
    private lateinit var adapter: ServicoAdapter
    private var listaCompleta: List<ServicoListagemResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        // Inicializar componentes
        recyclerFavoritos = findViewById(R.id.rvFavorites)
        etSearch = findViewById(R.id.etSearch)

        // Configurar adapter
        adapter = ServicoAdapter { servico ->
            // Navegar para detalhes do serviço
            val intent = Intent(this, DetalhesLocal::class.java)
            intent.putExtra("SERVICO_ID", servico.id)
            startActivity(intent)
        }

        recyclerFavoritos.layoutManager = LinearLayoutManager(this)
        recyclerFavoritos.adapter = adapter

        // Configurar busca
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filtrarFavoritos(s.toString())
            }
        })

        // Configurar bottom navigation
        configurarBottomNavigation()

        // Carregar favoritos
        carregarFavoritos()
    }

    private fun configurarBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        // Não seleciona nenhum item, já que favoritos não está no menu
        bottomNav.selectedItemId = -1

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_descontos -> {
                    startActivity(Intent(this, ListaCupons::class.java))
                    finish()
                    true
                }
                R.id.nav_config -> {
                    startActivity(Intent(this, Configuracoes::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Carrega os favoritos do usuário autenticado.
     * A API identifica o usuário através do token JWT e retorna os serviços
     * favoritados (relação na tabela favoritos: usuario_id -> estabelecimento_id/servico_id)
     */
    private fun carregarFavoritos() {
        val sharedPref = getSharedPreferences("TravoApp", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val api = RetrofitService.getTravoServiceAPIWithToken(token)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.listarFavoritos()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val lista = response.body() ?: emptyList()
                        listaCompleta = lista
                        adapter.submit(lista)
                        
                        if (lista.isEmpty()) {
                            Toast.makeText(
                                this@Favoritos,
                                "Você ainda não tem favoritos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@Favoritos,
                            "Erro ${response.code()} ao carregar favoritos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@Favoritos,
                        "Falha ao carregar favoritos: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun filtrarFavoritos(query: String) {
        val queryLower = query.lowercase().trim()
        val listaFiltrada = if (queryLower.isEmpty()) {
            listaCompleta
        } else {
            listaCompleta.filter { servico ->
                servico.nome.lowercase().contains(queryLower) ||
                servico.endereco?.lowercase()?.contains(queryLower) == true
            }
        }
        adapter.submit(listaFiltrada)
    }

    override fun onResume() {
        super.onResume()
        // Recarregar favoritos quando voltar para a tela (caso tenha removido algum)
        carregarFavoritos()
    }
}