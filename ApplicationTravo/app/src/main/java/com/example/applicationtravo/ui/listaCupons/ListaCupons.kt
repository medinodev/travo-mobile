package com.example.applicationtravo.ui.listaCupons

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationtravo.R
import com.example.applicationtravo.models.CupomResponse
import com.example.applicationtravo.retrofit.RetrofitService
import com.example.applicationtravo.ui.TesteHomeActivity
import com.example.applicationtravo.ui.configuracoes.Configuracoes
import com.example.applicationtravo.ui.listaServicos.ListaServicos
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class ListaCupons : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CupomAdapter
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_cupons)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        recyclerView = findViewById(R.id.recycler_cupons)
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchEditText = findViewById(R.id.search_cupons)
        clearButton = findViewById(R.id.btn_clear)

        carregarCupons()
        configurarFiltros()
        configurarBottomNavigation()
        configurarBusca()

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_descontos -> {
                    startActivity(Intent(this, ListaCupons::class.java))
                    true
                }

                R.id.nav_home -> {
                    startActivity(Intent(this, TesteHomeActivity::class.java))
                    true
                }

                R.id.nav_config -> {
                    startActivity(Intent(this, Configuracoes::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun usarCupom(cupom: CupomResponse) {
        lifecycleScope.launch {
            try {
                val prefs = getSharedPreferences("TravoApp", MODE_PRIVATE)
                val token = prefs.getString("token", null)

                if (token == null) {
                    Log.e("API", "Token não encontrado — usuário não logado")
                    return@launch
                }

                val api = RetrofitService.getTravoServiceAPIWithToken(token)

                val response = api.claimCupom(cupom.id)

                if (response.isSuccessful && response.body() != null) {
                    val cupomCliente = response.body()!!
                    val codigo = cupomCliente.codigo

                    androidx.appcompat.app.AlertDialog.Builder(this@ListaCupons)
                        .setTitle("Cupom gerado")
                        .setMessage("Mostre este código no estabelecimento:\n\n$codigo")
                        .setPositiveButton("OK", null)
                        .show()

                    Log.d("API", "Cupom usado: código = $codigo")
                } else {
                    Log.e("API", "Erro ao usar cupom: ${response.code()} - ${response.message()}")
                    androidx.appcompat.app.AlertDialog.Builder(this@ListaCupons)
                        .setTitle("Erro")
                        .setMessage("Não foi possível usar o cupom. Tente novamente.")
                        .setPositiveButton("OK", null)
                        .show()
                }

            } catch (e: Exception) {
                Log.e("API", "Erro ao usar cupom: ${e.message}")
                androidx.appcompat.app.AlertDialog.Builder(this@ListaCupons)
                    .setTitle("Erro")
                    .setMessage("Ocorreu um erro ao usar o cupom.")
                    .setPositiveButton("OK", null)
                    .show()
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

                val response = RetrofitService.getTravoServiceAPIWithToken(token)
                    .listarTodosCupons()

                if (response.isSuccessful && response.body() != null) {
                    val lista = response.body()!!

                    adapter = CupomAdapter(lista) { cupom ->
                        usarCupom(cupom)
                    }

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

    private fun configurarBusca() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (::adapter.isInitialized) {
                    adapter.filter(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        clearButton.setOnClickListener {
            searchEditText.text.clear()
        }
    }

    private fun configurarBottomNavigation() {
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
    private fun configurarFiltros() {
        val btnFavoritos = findViewById<Button>(R.id.btn_favoritos)
        btnFavoritos.setOnClickListener {
            if (::adapter.isInitialized) {
                adapter.mostrarFavoritos()
            }
        }
    }

}
