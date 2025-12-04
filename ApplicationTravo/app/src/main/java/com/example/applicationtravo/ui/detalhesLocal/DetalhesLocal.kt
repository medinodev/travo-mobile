package com.example.applicationtravo.ui.detalhesLocal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.applicationtravo.R
import com.example.applicationtravo.retrofit.RetrofitService
import com.example.applicationtravo.ui.home.HomeActivity
import com.example.applicationtravo.ui.configuracoes.Configuracoes
import com.example.applicationtravo.ui.listaCupons.ListaCupons
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalhesLocal : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar

    // Header
    private lateinit var tvTitulo: TextView
    private lateinit var tvEndereco: TextView
    private lateinit var tvHoje: TextView
    private lateinit var imgCapa: ImageView

    // Detalhes/Sobre
    private lateinit var tvResumo: TextView
    private lateinit var tvTipo: TextView
    private lateinit var tvCep: TextView
    private lateinit var tvHorarios: TextView

    // Cupons
    private lateinit var rvCupons: RecyclerView
    private val cuponsAdapter = CupomAdapter() // seu adapter existente (horizontal)

    // Avaliações
    private lateinit var rvReviews: RecyclerView
    private val reviewsAdapter = ReviewsAdapter() // adapter simples de reviews

    // Favoritos
    private var servicoId: Int = -1
    private var isFavorito: Boolean = false
    private var menuItemFavorite: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalhes_local)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViews()
        setupToolbar()
        setupCuponsList()
        setupReviewsList()

        servicoId = intent.getIntExtra("SERVICO_ID", -1)
        if (servicoId <= 0) {
            Log.e("DetalhesLocal", "SERVICO_ID inválido")
            finish()
            return
        }

        carregarDetalhes(servicoId)
        carregarCupons(servicoId)
        carregarAvaliacoes(servicoId)
        verificarSeFavorito(servicoId)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_descontos -> {
                    startActivity(Intent(this, ListaCupons::class.java))
                    true
                }

                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
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

    private fun bindViews() {
        toolbar = findViewById(R.id.toolbar)

        tvTitulo = findViewById(R.id.tvTitulo)
        tvEndereco = findViewById(R.id.tvEndereco)
        tvHoje = findViewById(R.id.tvHoje)
        imgCapa = findViewById(R.id.imgCapa)

        tvResumo = findViewById(R.id.tvResumo)
        tvTipo = findViewById(R.id.tvTipo)
        tvCep = findViewById(R.id.tvCep)

        rvCupons = findViewById(R.id.rvCupons)
        rvReviews = findViewById(R.id.rvReviews)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_24)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_favorite -> {
                    toggleFavorito()
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_detalhes_local, menu)
        menuItemFavorite = menu?.findItem(R.id.action_favorite)
        atualizarIconeFavorito()
        return true
    }

    private fun setupCuponsList() {
        rvCupons.apply {
            layoutManager = LinearLayoutManager(this@DetalhesLocal, LinearLayoutManager.HORIZONTAL, false)
            adapter = cuponsAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupReviewsList() {
        rvReviews.apply {
            layoutManager = LinearLayoutManager(this@DetalhesLocal)
            adapter = reviewsAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun carregarDetalhes(servicoId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = getSharedPreferences("TravoApp", MODE_PRIVATE)
                val token = prefs.getString("token", null)

                val api = if (!token.isNullOrEmpty())
                    RetrofitService.getTravoServiceAPIWithToken(token)
                else
                    RetrofitService.getTravoServiceAPI()

                val resp = api.obterServicoPorId(servicoId)
                withContext(Dispatchers.Main) {
                    if (resp.isSuccessful && resp.body() != null) {
                        val d = resp.body()!!

                        tvTitulo.text = d.nome
                        tvEndereco.text = d.endereco ?: ""
                        tvHoje.text = "Funcionamento hoje: ${d.funcionamentoHoje ?: "—"}"

                        tvResumo.text = d.sobre ?: d.resumo ?: "—"
                        tvTipo.text = "Tipo: ${d.tipo ?: "—"}"
                        tvCep.text = "CEP: ${d.cep ?: "—"}"
                        tvHorarios.text = d.horarios ?: "—"

                        imgCapa.load(d.imagemCapaUrl) {
                            crossfade(true)
                            placeholder(android.R.drawable.ic_menu_report_image)
                            error(android.R.drawable.ic_menu_report_image)
                        }

                    } else {
                        Log.e("API", "Erro ${resp.code()} - ${resp.message()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Falha ao obter detalhes: ${e.message}")
            }
        }
    }

    private fun carregarCupons(servicoId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = getSharedPreferences("TravoApp", MODE_PRIVATE)
                val token = prefs.getString("token", null)

                val api = if (!token.isNullOrEmpty())
                    RetrofitService.getTravoServiceAPIWithToken(token)
                else
                    RetrofitService.getTravoServiceAPI()

                val resp = api.listarCuponsDoServico(servicoId)
                withContext(Dispatchers.Main) {
                    if (resp.isSuccessful && resp.body() != null) {
                        cuponsAdapter.submit(resp.body()!!)
                    } else {
                        cuponsAdapter.submit(emptyList())
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Falha ao listar cupons: ${e.message}")
            }
        }
    }

    private fun carregarAvaliacoes(servicoId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = getSharedPreferences("TravoApp", MODE_PRIVATE)
                val token = prefs.getString("token", null)

                val api = if (!token.isNullOrEmpty())
                    RetrofitService.getTravoServiceAPIWithToken(token)
                else
                    RetrofitService.getTravoServiceAPI()

                val resp = api.listarAvaliacoesDoServico(servicoId)
                withContext(Dispatchers.Main) {
                    if (resp.isSuccessful && resp.body() != null) {
                        reviewsAdapter.submit(resp.body()!!)
                    } else {
                        reviewsAdapter.submit(emptyList())
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Falha ao listar avaliações: ${e.message}")
            }
        }
    }

    private fun verificarSeFavorito(servicoId: Int) {
        val sharedPref = getSharedPreferences("TravoApp", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)

        if (token.isNullOrEmpty()) {
            // Usuário não logado, não mostra opção de favoritar
            return
        }

        val api = RetrofitService.getTravoServiceAPIWithToken(token)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.listarFavoritos()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val favoritos = response.body() ?: emptyList()
                        isFavorito = favoritos.any { it.id == servicoId }
                        atualizarIconeFavorito()
                    }
                }
            } catch (e: Exception) {
                Log.e("DetalhesLocal", "Erro ao verificar favorito: ${e.message}")
            }
        }
    }

    private fun toggleFavorito() {
        val sharedPref = getSharedPreferences("TravoApp", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Faça login para favoritar", Toast.LENGTH_SHORT).show()
            return
        }

        val api = RetrofitService.getTravoServiceAPIWithToken(token)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = if (isFavorito) {
                    api.removerFavorito(servicoId)
                } else {
                    api.adicionarFavorito(servicoId)
                }

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        isFavorito = !isFavorito
                        atualizarIconeFavorito()
                        val mensagem = if (isFavorito) {
                            "Adicionado aos favoritos"
                        } else {
                            "Removido dos favoritos"
                        }
                        Toast.makeText(this@DetalhesLocal, mensagem, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this@DetalhesLocal,
                            "Erro ao ${if (isFavorito) "remover" else "adicionar"} favorito",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DetalhesLocal,
                        "Erro: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun atualizarIconeFavorito() {
        menuItemFavorite?.icon = if (isFavorito) {
            ContextCompat.getDrawable(this, R.drawable.ic_heart_filled)
        } else {
            ContextCompat.getDrawable(this, R.drawable.ic_heart_border)
        }
    }
}
