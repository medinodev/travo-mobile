package com.example.applicationtravo.ui.listaServicos

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationtravo.R
import com.example.applicationtravo.retrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaServicos : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var cardFiltros: View
    private lateinit var btnToggleFiltros: ImageButton
    private lateinit var adapter: ServicoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_servicos)

        recycler = findViewById(R.id.recycler_servicos)
        cardFiltros = findViewById(R.id.cardFiltros)
        btnToggleFiltros = findViewById(R.id.btnToggleFiltros)

        // cria o adapter com o clique
        adapter = ServicoAdapter { servico ->
            val intent = android.content.Intent(
                this@ListaServicos,
                com.example.applicationtravo.ui.detalhesLocal.DetalhesLocal::class.java
            )
            // ajuste o campo conforme seu model de listagem (assumindo Int)
            intent.putExtra("SERVICO_ID", servico.id)
            startActivity(intent)
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        btnToggleFiltros.setOnClickListener {
            val mostrar = cardFiltros.visibility != View.VISIBLE
            cardFiltros.visibility = if (mostrar) View.VISIBLE else View.GONE
            btnToggleFiltros.setImageResource(
                if (mostrar) R.drawable.ic_chevron_left_24 else R.drawable.ic_chevron_left_24
            )
        }

        val token = getSharedPreferences("TravoApp", Context.MODE_PRIVATE).getString("token", null)
        val api = if (!token.isNullOrEmpty())
            RetrofitService.getTravoServiceAPIWithToken(token)
        else
            RetrofitService.getTravoServiceAPI()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = api.listarServicos() // suspend ok aqui
                withContext(Dispatchers.Main) {
                    if (resp.isSuccessful) {
                        val lista = resp.body() ?: emptyList()
                        adapter.submit(lista)
                        Toast.makeText(
                            this@ListaServicos,
                            "Carregados ${lista.size} serviços",
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
    }
}
