package com.example.applicationtravo.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationtravo.R
import com.example.applicationtravo.ui.perfil.PerfilEditarActivity

class PerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)

        val btnEditarPerfil: Button = findViewById(R.id.btnEditarPerfil)

        btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, PerfilEditarActivity::class.java)
            startActivity(intent)
        }
    }
}

