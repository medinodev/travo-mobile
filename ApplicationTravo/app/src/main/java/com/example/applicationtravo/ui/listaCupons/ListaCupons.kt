package com.example.applicationtravo.ui.listaCupons

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationtravo.R
import com.example.applicationtravo.ui.configuracoes.Configuracoes
import com.example.applicationtravo.ui.listaServicos.ListaServicos
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListaCupons : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_cupons)

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
}
