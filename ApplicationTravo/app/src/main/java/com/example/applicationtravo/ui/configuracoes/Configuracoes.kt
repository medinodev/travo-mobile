package com.example.applicationtravo.ui.configuracoes

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.applicationtravo.R

class Configuracoes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)
        val btnNotificacao = findViewById<Button>(R.id.btnNotificacao)
        val btnAcessibilidade = findViewById<Button>(R.id.btnAcessibilidade)
        val btnSuporte = findViewById<Button>(R.id.btnSuporte)
        val btnInformacoes = findViewById<Button>(R.id.btnInformacoes)
        val btnMenu = findViewById<TextView>(R.id.btnMenu)
        val btnHome = findViewById<Button>(R.id.btnHome)
        val btnConfiguracoes = findViewById<Button>(R.id.btnConfiguracoes)
        val btnDescontos = findViewById<Button>(R.id.btnDescontos)
    }
}