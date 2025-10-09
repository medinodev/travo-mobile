package com.example.applicationtravo.ui.cadastro

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationtravo.R
import com.example.applicationtravo.models.RegistroRequest
import com.example.applicationtravo.retrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CadastroActivity : AppCompatActivity() {

    private lateinit var editNomeFantasia: EditText
    private lateinit var editEmail: EditText
    private lateinit var editTelefone: EditText
    private lateinit var editSenha: EditText
    private lateinit var btnCadastrar: Button
    private lateinit var tvVoltarLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        // Vincula os campos do layout
        editNomeFantasia = findViewById(R.id.editNomeFantasia)
        editEmail = findViewById(R.id.editEmail)
        editTelefone = findViewById(R.id.editTelefone)
        editSenha = findViewById(R.id.editSenha)
        btnCadastrar = findViewById(R.id.btnCadastrar)
        tvVoltarLogin = findViewById(R.id.tvVoltarLogin)

        // Ação do botão de cadastro
        btnCadastrar.setOnClickListener {
            if (isValidForm()) {
                val nomeFantasia = editNomeFantasia.text.toString()
                val email = editEmail.text.toString()
                val telefone = editTelefone.text.toString()
                val senha = editSenha.text.toString()

                val registroRequest = RegistroRequest(
                    nomeFantasia = nomeFantasia,
                    telefone = telefone,
                    email = email,
                    senha = senha
                )

                val travoServiceAPI = RetrofitService.getTravoServiceAPI()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = travoServiceAPI.registrar(registroRequest)

                        if (response.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@CadastroActivity,
                                    "Cadastro realizado com sucesso!",
                                    Toast.LENGTH_LONG
                                ).show()
                                finish() // Volta para a tela de login
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@CadastroActivity,
                                    "Erro ao cadastrar. Tente novamente.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@CadastroActivity,
                                "Erro de conexão: ${e.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        Log.e("CadastroActivity", "Erro: ${e.message}")
                    }
                }
            }
        }

        // Ação do link "Voltar ao login"
        tvVoltarLogin.setOnClickListener {
            finish()
        }
    }

    // Validação dos campos
    private fun isValidForm(): Boolean {
        var isValid = true

        if (editNomeFantasia.text.isEmpty()) {
            editNomeFantasia.error = "Preencha o nome fantasia"
            isValid = false
        }
        if (editEmail.text.isEmpty()) {
            editEmail.error = "Preencha o e-mail"
            isValid = false
        }
        if (editTelefone.text.isEmpty()) {
            editTelefone.error = "Preencha o telefone"
            isValid = false
        }
        if (editSenha.text.isEmpty()) {
            editSenha.error = "Preencha a senha"
            isValid = false
        }

        return isValid
    }
}
