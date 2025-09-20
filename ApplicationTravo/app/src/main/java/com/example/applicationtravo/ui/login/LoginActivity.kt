package com.example.applicationtravo.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.applicationtravo.R
import com.example.applicationtravo.ui.cadastro.CadastroActivity
import com.example.applicationtravo.ui.home.HomeActivity
import com.example.applicationtravo.ui.recuperacaoSenha.RecuperacaoDeSenhaActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val tvRecuperacaoDeSenha = findViewById<Button>(R.id.btnEntrar)

        btnCadastrar.setOnClickListener{

            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        btnEntrar.setOnClickListener{

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        tvRecuperacaoDeSenha.setOnClickListener {
            val intent = Intent(this, RecuperacaoDeSenhaActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}