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
import com.example.applicationtravo.ui.recuperarSenha.RecuperarSenhaActivity
import com.example.applicationtravo.models.LoginRequest
import com.example.applicationtravo.retrofit.RetrofitService
import com.example.applicationtravo.ui.TesteHomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editSenha: EditText
    private lateinit var btnEntrar: Button
    private lateinit var btnCadastrar: Button
    private lateinit var tvEsqueciSenha: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("DEBUG: LoginActivity onCreate INICIADO")
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
                        println("DEBUG: Tentando fazer login com email: $email")
                        val response = travoServiceAPI.login(loginRequest)
                        println("DEBUG: Response code: ${response.code()}")
                        println("DEBUG: Response body: ${response.body()}")
                        println("DEBUG: Response error: ${response.errorBody()?.string()}")
                        
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

                                println("DEBUG: Redirecionando para TesteHomeActivity")
                                val intent = Intent(this@LoginActivity, TesteHomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val errorMessage = try {
                                val jsonObject = org.json.JSONObject(errorBody)
                                jsonObject.optString("mensagem", "Erro ao fazer login. Verifique suas credenciais.")
                            } catch (e: Exception) {
                                "Erro ao fazer login. Verifique suas credenciais."
                            }
                            
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            println("DEBUG: Erro no login: $errorBody")
                        }
                    } catch (e: Exception) {
                        println("DEBUG: Erro no login: ${e.message}")
                        e.printStackTrace()
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
