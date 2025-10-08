package com.example.applicationtravo.ui.listaServicos

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.applicationtravo.R

class ListaServicos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_servicos)
        val btnHome = findViewById<Button>(R.id.btnHome)
        val btnConfiguracoes = findViewById<Button>(R.id.btnConfiguracoes)
        val btnFiltros = findViewById<Button>(R.id.btnFiltros)
        val btnAplicarFil = findViewById<Button>(R.id.btnAplicarFil)
        val btnLimparFil = findViewById<Button>(R.id.btnLimparFil)
        val btnServicos = findViewById<Button>(R.id.btnServicos)
        val btnMenu = findViewById<TextView>(R.id.btnMenu)
    }
}