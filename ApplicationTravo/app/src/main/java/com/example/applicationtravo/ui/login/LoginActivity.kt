package com.example.applicationtravo.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationtravo.R
import com.example.applicationtravo.ui.cadastro.CadastroActivity
import com.example.applicationtravo.ui.listaCupons.ListaCupons
import com.example.applicationtravo.ui.recuperarSenha.RecuperarSenhaActivity
import com.example.applicationtravo.models.LoginRequest
import com.example.applicationtravo.retrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editSenha: EditText
    private lateinit var btnEntrar: Button
    private lateinit var btnCadastrar: Button
    private lateinit var tvEsqueciSenha: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Referências dos elementos da interface
        editEmail = findViewById(R.id.editEmail)
        editSenha = findViewById(R.id.editSenha)
        btnEntrar = findViewById(R.id.btnEntrar)
        btnCadastrar = findViewById(R.id.btnCadastrar)
        tvEsqueciSenha = findViewById(R.id.txtEsqueciSenha)

        // Botão de cadastro → vai pra tela de registro
        btnCadastrar.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        // Link "Esqueci minha senha"
        tvEsqueciSenha.setOnClickListener {
            val intent = Intent(this, RecuperarSenhaActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Botão de login → executa a lógica Retrofit
        btnEntrar.setOnClickListener {
            if (isValidForm()) {
                val email = editEmail.text.toString()
                val password = editSenha.text.toString()

                val travoServiceAPI = RetrofitService.getTravoServiceAPI()
                val loginRequest = LoginRequest(email, password)

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = travoServiceAPI.login(loginRequest)
                        if (response.isSuccessful) {
                            val loginResponse = response.body()

                            // Armazena o token de login
                            val sharedPref = getSharedPreferences("TravoApp", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("token", loginResponse?.token)
                                apply()
                            }

                            // Mostra feedback e vai pra tela principal
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    loginResponse?.mensagem ?: "Login realizado com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(this@LoginActivity, ListaCupons::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Erro ao fazer login. Verifique suas credenciais.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Erro de conexão: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun isValidForm(): Boolean {
        var isValid = true

        if (editEmail.text.isEmpty()) {
            editEmail.error = "Este campo não pode ser vazio."
            isValid = false
        }

        if (editSenha.text.isEmpty()) {
            editSenha.error = "Este campo não pode ser vazio."
            isValid = false
        }

        return isValid
    }
}
