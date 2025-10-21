package com.example.applicationtravo.ui.configuracoes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationtravo.R
import com.example.applicationtravo.ui.listaCupons.ListaCupons
import com.example.applicationtravo.ui.listaServicos.ListaServicos
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class Configuracoes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        // ===== Toolbar =====
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // mostra seta de voltar
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // ===== Bottom Navigation =====
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.toolbar

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, ListaServicos::class.java))
                    true
                }
                R.id.nav_descontos -> {
                    startActivity(Intent(this, ListaCupons::class.java))
                    true
                }
                R.id.nav_config -> true // já está aqui
                else -> false
            }
        }

        // ===== Botões da tela =====
        val btnNotificacao = findViewById<Button>(R.id.btnNotificacao)
        val btnAcessibilidade = findViewById<Button>(R.id.btnAcessibilidade)
        val btnSuporte = findViewById<Button>(R.id.btnSuporte)
        val btnInformacoes = findViewById<Button>(R.id.btnInformacoes)

        btnNotificacao.setOnClickListener {
            // TODO: adicionar ação
        }
        btnAcessibilidade.setOnClickListener {
            // TODO: adicionar ação
        }
        btnSuporte.setOnClickListener {
            // TODO: adicionar ação
        }
        btnInformacoes.setOnClickListener {
            // TODO: adicionar ação
        }
    }
}
