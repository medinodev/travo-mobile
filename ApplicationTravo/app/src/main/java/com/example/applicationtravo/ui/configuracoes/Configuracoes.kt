package com.example.applicationtravo.ui.configuracoes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationtravo.R
import com.example.applicationtravo.ui.TesteHomeActivity
import com.example.applicationtravo.ui.favoritos.Favoritos
import com.example.applicationtravo.ui.listaCupons.ListaCupons
import com.example.applicationtravo.ui.listaServicos.ListaServicos
import com.example.applicationtravo.ui.login.LoginActivity
import com.example.applicationtravo.ui.perfil.PerfilActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class Configuracoes : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Botões de navegação
        findViewById<Button>(R.id.btnFavoritos).setOnClickListener {
            startActivity(Intent(this, Favoritos::class.java))
        }

        findViewById<Button>(R.id.btnListaCupons).setOnClickListener {
            startActivity(Intent(this, ListaCupons::class.java))
        }

        findViewById<Button>(R.id.btnListaServicos).setOnClickListener {
            startActivity(Intent(this, ListaServicos::class.java))
        }

        findViewById<Button>(R.id.btnPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        // Botão sair → volta para tela de login e limpa a pilha
        findViewById<Button>(R.id.btnSair).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

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
}
