package com.example.applicationtravo.ui.listaServicos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationtravo.R
import com.example.applicationtravo.ui.configuracoes.Configuracoes
import com.example.applicationtravo.ui.listaCupons.ListaCupons
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListaServicos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_servicos)

        // ===== Bottom Navigation =====
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_descontos -> {
                    startActivity(Intent(this, ListaCupons::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_config -> {
                    startActivity(Intent(this, Configuracoes::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
}
