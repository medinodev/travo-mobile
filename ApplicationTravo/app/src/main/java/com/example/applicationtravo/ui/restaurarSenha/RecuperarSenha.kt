package com.example.applicationtravo.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationtravo.R
import com.example.applicationtravo.ui.login.LoginActivity

class RecuperarSenhaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_senha)

        val etEmail = findViewById<EditText>(R.id.etEmailRecuperacao)
        val btnRecuperar = findViewById<Button>(R.id.btnRecuperarSenha)
        val tvVoltarLogin = findViewById<TextView>(R.id.tvVoltarLogin)

        btnRecuperar.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Digite um e-mail válido", Toast.LENGTH_SHORT).show()
            } else {
                // Aqui futuramente chamaremos o Supabase para enviar e-mail de recuperação
                Toast.makeText(this, "Link de recuperação enviado para $email", Toast.LENGTH_LONG).show()
            }
        }

        tvVoltarLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
    }
}