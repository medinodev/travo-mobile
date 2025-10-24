package com.example.applicationtravo.ui.detalhesLocal

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.applicationtravo.R
import com.example.applicationtravo.retrofit.RetrofitService
import com.google.android.material.appbar.MaterialToolbar
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalhes_local)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViews()
        setupToolbar()
        setupCuponsList()
        setupReviewsList()

        val servicoId = intent.getIntExtra("SERVICO_ID", -1)
        if (servicoId <= 0) {
            Log.e("DetalhesLocal", "SERVICO_ID inválido")
            finish()
            return
        }

        carregarDetalhes(servicoId)
        carregarCupons(servicoId)
        carregarAvaliacoes(servicoId)
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
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_24)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
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
}
