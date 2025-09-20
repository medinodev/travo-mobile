package com.example.applicationtravo.ui.cadastro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationtravo.R
import com.example.applicationtravo.ui.login.LoginActivity

class CadastroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val tvVoltarLogin = findViewById<TextView>(R.id.tvVoltarLogin)

        btnCadastrar.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

            // Adicionar lógica do cadastro para interagir com o banco
        }

        tvVoltarLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

