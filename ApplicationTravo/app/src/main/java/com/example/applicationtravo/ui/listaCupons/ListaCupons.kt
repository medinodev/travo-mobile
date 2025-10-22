package com.example.applicationtravo.ui.listaCupons

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationtravo.R
import com.example.applicationtravo.retrofit.RetrofitService
import com.example.applicationtravo.models.CupomResponse
import com.example.applicationtravo.ui.configuracoes.Configuracoes
import com.example.applicationtravo.ui.listaServicos.ListaServicos
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class ListaCupons : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CupomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_cupons)

        recyclerView = findViewById(R.id.recycler_cupons)
        recyclerView.layoutManager = LinearLayoutManager(this)

        carregarCupons() // 🔥 chama a API ao abrir a tela

        // ===== Bottom Navigation =====
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_descontos

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, ListaServicos::class.java))
                    true
                }
                R.id.nav_descontos -> true
                R.id.nav_config -> {
                    startActivity(Intent(this, Configuracoes::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun carregarCupons() {
        lifecycleScope.launch {
            try {
                val prefs = getSharedPreferences("TravoApp", MODE_PRIVATE)
                val token = prefs.getString("token", null)

                if (token == null) {
                    Log.e("API", "Token não encontrado — usuário não logado")
                    return@launch
                }

                // 🔥 Usa Retrofit autenticado
                val response = RetrofitService.getTravoServiceAPIWithToken(token)
                    .listarCuponsDoServico()

                if (response.isSuccessful && response.body() != null) {
                    val lista = response.body()!!
                    adapter = CupomAdapter(lista)
                    recyclerView.adapter = adapter
                    Log.d("API", "Total de cupons recebidos: ${lista.size}")
                } else {
                    Log.e("API", "Erro na resposta: ${response.code()} - ${response.message()}")
                }

            } catch (e: Exception) {
                Log.e("API", "Erro ao carregar cupons: ${e.message}")
            }
        }
    }

}
