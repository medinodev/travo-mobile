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
            Log.e("CadastroActivity", "🔵 BOTÃO CADASTRAR CLICADO!")
            if (isValidForm()) {
                Log.e("CadastroActivity", "✅ Formulário válido, prosseguindo...")
                val nomeCompleto = editNomeFantasia.text.toString().trim()
                val email = editEmail.text.toString().trim()
                val telefone = editTelefone.text.toString().trim().takeIf { it.isNotEmpty() }
                val senha = editSenha.text.toString()

                val registroRequest = RegistroRequest(
                    nomeCompleto = nomeCompleto,
                    email = email,
                    senha = senha,
                    telefone = telefone
                )

                val travoServiceAPI = RetrofitService.getTravoServiceAPI()
                
                Log.e("CadastroActivity", "═══════════════════════════════════════════")
                Log.e("CadastroActivity", "🔵🔵🔵 INICIANDO CADASTRO 🔵🔵🔵")
                Log.e("CadastroActivity", "🔵 travoServiceAPI obtido: ${travoServiceAPI.javaClass.name}")
                Log.e("CadastroActivity", "🔵 Método chamado será: registrar()")
                Log.e("CadastroActivity", "🔵 Endpoint esperado: POST /rest/v1/usuarios")
                Log.e("CadastroActivity", "🔵 nomeCompleto=${registroRequest.nomeCompleto}")
                Log.e("CadastroActivity", "🔵 email=${registroRequest.email}")
                Log.e("CadastroActivity", "🔵 telefone=${registroRequest.telefone}")
                Log.e("CadastroActivity", "═══════════════════════════════════════════")

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        Log.e("CadastroActivity", "DEBUG: Enviando POST para: usuarios")
                        Log.e("CadastroActivity", "DEBUG: Dados: nomeCompleto=${registroRequest.nomeCompleto}, email=${registroRequest.email}")
                        val response = travoServiceAPI.registrar(registroRequest)
                        Log.e("CadastroActivity", "DEBUG: Response code: ${response.code()}")
                        Log.e("CadastroActivity", "DEBUG: Response successful: ${response.isSuccessful}")
                        Log.e("CadastroActivity", "DEBUG: Response body: ${response.body()}")

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
                            val errorBody = response.errorBody()?.string()
                            val errorMessage = try {
                                val jsonObject = org.json.JSONObject(errorBody)
                                // Tenta pegar mensagem ou errors
                                if (jsonObject.has("mensagem")) {
                                    jsonObject.getString("mensagem")
                                } else if (jsonObject.has("errors")) {
                                    val errorsArray = jsonObject.getJSONArray("errors")
                                    errorsArray.getString(0)
                                } else {
                                    "Erro ao cadastrar. Tente novamente."
                                }
                            } catch (e: Exception) {
                                "Erro ao cadastrar. ${errorBody ?: "Tente novamente."}"
                            }
                            
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@CadastroActivity,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            Log.e("CadastroActivity", "Erro no cadastro: $errorBody")
                        }
                    } catch (e: Exception) {
                        Log.e("CadastroActivity", "═══════════════════════════════════════════")
                        Log.e("CadastroActivity", "ERRO EXCEÇÃO CAPTURADA!")
                        Log.e("CadastroActivity", "Erro tipo: ${e.javaClass.simpleName}")
                        Log.e("CadastroActivity", "Erro mensagem: ${e.message}")
                        Log.e("CadastroActivity", "Erro stacktrace:", e)
                        Log.e("CadastroActivity", "═══════════════════════════════════════════")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@CadastroActivity,
                                "Erro de conexão: ${e.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
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
        Log.e("CadastroActivity", "🔍 Validando formulário...")

        if (editNomeFantasia.text.toString().trim().isEmpty()) {
            Log.e("CadastroActivity", "❌ Nome vazio")
            editNomeFantasia.error = "Preencha o nome completo"
            isValid = false
        }
        if (editEmail.text.toString().trim().isEmpty()) {
            editEmail.error = "Preencha o e-mail"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail.text.toString().trim()).matches()) {
            editEmail.error = "E-mail inválido"
            isValid = false
        }
        // Telefone é opcional para usuarios, então não validamos como obrigatório
        if (editSenha.text.toString().isEmpty()) {
            editSenha.error = "Preencha a senha"
            isValid = false
        } else if (editSenha.text.toString().length < 6) {
            editSenha.error = "A senha deve ter pelo menos 6 caracteres"
            isValid = false
            Log.e("CadastroActivity", "❌ Senha muito curta")
        }

        Log.e("CadastroActivity", "🔍 Resultado da validação: ${if (isValid) "✅ VÁLIDO" else "❌ INVÁLIDO"}")
        return isValid
    }
}
