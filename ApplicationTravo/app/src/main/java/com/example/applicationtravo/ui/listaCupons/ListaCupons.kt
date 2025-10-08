package com.example.applicationtravo.ui.listaCupons

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.applicationtravo.R

class ListaCupons : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_cupons)
        val btnFiltros = findViewById<Button>(R.id.btnFiltros)
        val btnHome = findViewById<Button>(R.id.btnHome)
        val btnConfiguracoes = findViewById<Button>(R.id.btnConfiguracoes)
        val btnDescontos = findViewById<Button>(R.id.btnDescontos)
        val btnMenu = findViewById<TextView>(R.id.btnMenu)
    }
}