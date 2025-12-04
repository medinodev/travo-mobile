package com.example.applicationtravo.ui.listaServicos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationtravo.R
import com.example.applicationtravo.retrofit.RetrofitService
import com.example.applicationtravo.ui.home.HomeActivity
import com.example.applicationtravo.ui.configuracoes.Configuracoes
import com.example.applicationtravo.ui.listaCupons.ListaCupons
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.ceil

class ListaServicos : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var cardFiltros: View
    private lateinit var btnToggleFiltros: ImageButton
    private lateinit var adapter: ServicoAdapter
    private lateinit var edtSearch: EditText

    // pagination UI
    private lateinit var btnPrevPage: MaterialButton
    private lateinit var btnNextPage: MaterialButton
    private lateinit var spinnerPageSize: Spinner
    private lateinit var tvPageInfo: TextView

    // Dados completos e paginação local
    private val fullList = mutableListOf<com.example.applicationtravo.models.ServicoListagemResponse>() // ajuste o package se necessário
    private val filteredList = mutableListOf<com.example.applicationtravo.models.ServicoListagemResponse>()
    private var pageSize = 20
    private var currentPage = 0 // 0-based
    private var isLoadingPage = false
    private var isLastPage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_servicos)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        recycler = findViewById(R.id.recycler_servicos)
        cardFiltros = findViewById(R.id.cardFiltros)
        btnToggleFiltros = findViewById(R.id.btnToggleFiltros)
        edtSearch = findViewById(R.id.etBusca) // corrigido para o id do layout

        // pagination views
        btnPrevPage = findViewById(R.id.btnPrevPage)
        btnNextPage = findViewById(R.id.btnNextPage)
        spinnerPageSize = findViewById(R.id.spinnerPageSize)
        tvPageInfo = findViewById(R.id.tvPageInfo)

        // cria o adapter com o clique
        adapter = ServicoAdapter { servico ->
            val intent = Intent(
                this@ListaServicos,
                com.example.applicationtravo.ui.detalhesLocal.DetalhesLocal::class.java
            )
            intent.putExtra("SERVICO_ID", servico.id)
            startActivity(intent)
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // (Opcional) Infinite scroll — se quiser manter comportamento 'carregar ao rolar'
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val lm = rv.layoutManager as LinearLayoutManager
                val visibleItemCount = lm.childCount
                val totalItemCount = lm.itemCount
                val firstVisible = lm.findFirstVisibleItemPosition()

                // quando chegar perto do final, carregar próxima página
                if (!isLoadingPage && !isLastPage) {
                    if ((visibleItemCount + firstVisible) >= totalItemCount - 3 && firstVisible >= 0) {
                        loadNextPage()
                    }
                }
            }
        })

        btnToggleFiltros.setOnClickListener {
            val mostrar = cardFiltros.visibility != View.VISIBLE
            cardFiltros.visibility = if (mostrar) View.VISIBLE else View.GONE
            // ajusta ícone (troque para ícones corretos se desejar)
            btnToggleFiltros.setImageResource(if (mostrar) R.drawable.ic_chevron_left_24 else R.drawable.ic_chevron_left_24)
        }

        // Search: filtra localmente e reinicia paginação
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""
                applySearch(query)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Spinner: opções itens por página
        val pageSizes = listOf(5, 10, 20, 50)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pageSizes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPageSize.adapter = spinnerAdapter
        // seta valor inicial correspondente ao pageSize atual
        val initialPos = pageSizes.indexOf(pageSize).let { if (it >= 0) it else pageSizes.indexOf(20) }
        spinnerPageSize.setSelection(initialPos)

        spinnerPageSize.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                val newSize = pageSizes[position]
                if (newSize != pageSize) {
                    pageSize = newSize
                    // reset paginação para a primeira página
                    currentPage = 0
                    updatePaginationState()
                    adapter.submit(filteredPage()) // carrega página 0
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        btnPrevPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage -= 1
                updatePaginationState()
                adapter.submit(filteredPage())
                recycler.scrollToPosition(0)
            }
        }

        btnNextPage.setOnClickListener {
            val pageCount = pageCount()
            if (currentPage < pageCount - 1) {
                currentPage += 1
                updatePaginationState()
                adapter.submit(filteredPage())
                recycler.scrollToPosition(0)
            }
        }

        val token = getSharedPreferences("TravoApp", Context.MODE_PRIVATE).getString("token", null)
        val api = if (!token.isNullOrEmpty())
            RetrofitService.getTravoServiceAPIWithToken(token)
        else
            RetrofitService.getTravoServiceAPI()

        // Carrega todos os serviços do backend (assumindo que listarServicos retorna toda lista)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = api.listarServicos() // suspend
                withContext(Dispatchers.Main) {
                    if (resp.isSuccessful) {
                        val lista = resp.body() ?: emptyList()
                        fullList.clear()
                        fullList.addAll(lista)
                        // inicialmente filteredList = fullList
                        filteredList.clear()
                        filteredList.addAll(fullList)
                        // reset paginação e carrega primeira página
                        currentPage = 0
                        isLastPage = false
                        updatePaginationState()
                        adapter.submit(filteredPage()) // método submit assume seu adapter existente
                        Toast.makeText(
                            this@ListaServicos,
                            "Carregados ${fullList.size} serviços",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ListaServicos,
                            "Erro ${resp.code()} ao listar serviços",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ListaServicos,
                        "Falha: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

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

    /**
     * Retorna a sublista (página) atual a partir de filteredList.
     * Mostramos apenas os itens da página atual: subList(from, to).
     */
    private fun filteredPage(): List<com.example.applicationtravo.models.ServicoListagemResponse> {
        val from = currentPage * pageSize
        if (from >= filteredList.size) return emptyList()
        val to = minOf(from + pageSize, filteredList.size)
        // se alcançamos o final, marcar isLastPage
        isLastPage = to >= filteredList.size
        return filteredList.subList(from, to)
    }

    /**
     * Carrega a próxima página (cliente). Incrementa currentPage e atualiza adapter.
     */
    private fun loadNextPage() {
        if (isLoadingPage || isLastPage) return
        isLoadingPage = true
        recycler.post {
            val pageCount = pageCount()
            if (currentPage < pageCount - 1) {
                currentPage += 1
                adapter.submit(filteredPage())
                updatePaginationState()
            }
            isLoadingPage = false
        }
    }

    /**
     * Aplica o filtro de busca sobre fullList e redefine paginação.
     */
    private fun applySearch(query: String) {
        currentPage = 0
        isLastPage = false
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(fullList)
        } else {
            val q = query.lowercase()
            // Ajuste os campos usados na busca conforme seu modelo (nome, descricao, categoria...)
            val results = fullList.filter { s ->
                val nome = s.nome?.lowercase() ?: ""
                val sobre = s.endereco?.lowercase() ?: ""
                val tipo = s.tipo?.lowercase() ?: ""
                nome.contains(q) || sobre.contains(q) || tipo.contains(q)
            }
            filteredList.addAll(results)
        }
        updatePaginationState()
        // carrega a página atual (provavelmente 0)
        adapter.submit(filteredPage())
        Toast.makeText(this, "Encontrados ${filteredList.size} serviços", Toast.LENGTH_SHORT).show()
    }

    /**
     * Atualiza estado UI da paginação: texto e habilita/desabilita botões.
     */
    private fun updatePaginationState() {
        val totalItems = filteredList.size
        val pageCount = pageCount()
        if (currentPage >= pageCount) currentPage = maxOf(0, pageCount - 1)
        val from = if (totalItems == 0) 0 else currentPage * pageSize + 1
        val to = minOf((currentPage + 1) * pageSize, totalItems)

        btnPrevPage.isEnabled = currentPage > 0
        btnNextPage.isEnabled = currentPage < pageCount - 1
        isLastPage = (currentPage >= pageCount - 1)
    }

    private fun pageCount(): Int {
        if (pageSize <= 0) return 1
        return ceil(filteredList.size.toDouble() / pageSize.toDouble()).toInt().coerceAtLeast(1)
    }
}
