package com.example.applicationtravo.ui.favoritos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationtravo.R
import com.example.applicationtravo.models.FavoriteResponse
import com.example.applicationtravo.retrofit.RetrofitService
import com.example.applicationtravo.ui.home.HomeActivity
import com.example.applicationtravo.ui.configuracoes.Configuracoes
import com.example.applicationtravo.ui.listaCupons.ListaCupons
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Modelo simples para a UI da lista
data class FavoriteUi(
    val favoritoId: Int,
    val estabelecimentoId: Int,
    val title: String,
    val subtitle: String?
)

class Favoritos : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: FavoriteAdapter
    private lateinit var etSearch: EditText

    private var fullList: List<FavoriteUi> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        recycler = findViewById(R.id.rvFavorites)
        etSearch = findViewById(R.id.etSearch)

        adapter = FavoriteAdapter { item ->
            // Navega para a tela de detalhes usando o ID do estabelecimento/serviço
            val intent = Intent(
                this@Favoritos,
                com.example.applicationtravo.ui.detalhesLocal.DetalhesLocal::class.java
            )
            intent.putExtra("SERVICO_ID", item.estabelecimentoId)
            startActivity(intent)
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // Instância do API com/sem token (mesmo padrão da sua ListaServicos)
        val token = getSharedPreferences("TravoApp", Context.MODE_PRIVATE).getString("token", null)
        val api = if (!token.isNullOrEmpty())
            RetrofitService.getTravoServiceAPIWithToken(token)
        else
            RetrofitService.getTravoServiceAPI()

        // Carrega lista: favoritos -> detalhes por estabelecimento_id
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val favResp = api.getAllFavoritos()
                if (!favResp.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@Favoritos,
                            "Erro ${favResp.code()} ao buscar favoritos",
                            Toast.LENGTH_LONG
                        ).show()
                        recycler.visibility = View.GONE
                    }
                    return@launch
                }

                val favoritos: List<FavoriteResponse> = favResp.body() ?: emptyList()

                // Para cada favorito, busca o detalhe do serviço/estabelecimento
                val expanded: List<FavoriteUi> = favoritos.map { fav ->
                    async {
                        val det = api.obterServicoPorId(fav.estabelecimento_id)
                        if (det.isSuccessful) {
                            val body = det.body()
                            // Ajuste os campos abaixo conforme o seu LocalDetalheResponse
                            val title = body?.nome ?: "Estabelecimento ${fav.estabelecimento_id}"
                            val subtitle = body?.resumo ?: body?.endereco
                            FavoriteUi(
                                favoritoId = fav.id,
                                estabelecimentoId = fav.estabelecimento_id,
                                title = title,
                                subtitle = subtitle
                            )
                        } else {
                            null
                        }
                    }
                }.mapNotNull { it.await() }

                withContext(Dispatchers.Main) {
                    fullList = expanded
                    adapter.submit(expanded)
                    recycler.visibility = if (expanded.isEmpty()) View.GONE else View.VISIBLE
                    Toast.makeText(
                        this@Favoritos,
                        "Carregados ${expanded.size} favoritos",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Favoritos, "Falha: ${e.message}", Toast.LENGTH_LONG).show()
                    recycler.visibility = View.GONE
                }
            }
        }

        // Busca local no campo de pesquisa
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s?.toString()?.trim()?.lowercase().orEmpty()
                val filtered = if (q.isEmpty()) fullList else fullList.filter {
                    it.title.lowercase().contains(q) || (it.subtitle?.lowercase()?.contains(q) == true)
                }
                adapter.submit(filtered)
            }
        })

        // BottomNav igual ao padrão do app
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_descontos -> {
                    startActivity(Intent(this, ListaCupons::class.java)); true
                }
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java)); true
                }
                R.id.nav_config -> {
                    startActivity(Intent(this, Configuracoes::class.java)); true
                }
                else -> false
            }
        }
    }
}
