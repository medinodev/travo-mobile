package com.example.applicationtravo.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.applicationtravo.R
import com.example.applicationtravo.ui.cadastro.CadastroActivity
import com.example.applicationtravo.ui.restaurarSenha.RecuperarSenha

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val tvVoltarRestaurarSenha = findViewById<TextView>(R.id.txtEsqueciSenha)

        btnCadastrar.setOnClickListener{

            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        tvVoltarRestaurarSenha.setOnClickListener {
            val intent = Intent(this, RecuperarSenha::class.java)
            startActivity(intent)
            finish()
        }
    }
}