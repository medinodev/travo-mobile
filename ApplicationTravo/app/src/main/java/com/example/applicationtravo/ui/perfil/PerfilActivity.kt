package com.example.applicationtravo.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationtravo.R
import com.example.applicationtravo.ui.perfil.PerfilEditarActivity
import com.example.applicationtravo.retrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import android.util.Base64
import org.json.JSONObject

class PerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)

        val btnEditarPerfil: Button = findViewById(R.id.btnEditarPerfil)
        val tvNomeUsuario: TextView = findViewById(R.id.tvNomeUsuario)
        val tvBioUsuario: TextView = findViewById(R.id.tvBioUsuario)
        val tvEmailUsuario: TextView = findViewById(R.id.tvEmailUsuario)
        val tvNascimentoUsuario: TextView = findViewById(R.id.tvNascimentoUsuario)
        val tvTelefoneUsuario: TextView = findViewById(R.id.tvTelefoneUsuario)
        val imgFotoUsuario: ImageView = findViewById(R.id.imgFotoUsuario)

        btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, PerfilEditarActivity::class.java)
            startActivity(intent)
        }

        val sharedPref = getSharedPreferences("TravoApp", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userId = extractUserIdFromJwt(token)
        println("DEBUG: Token completo: $token")
        println("DEBUG: ID extraído do token: $userId")
        
        // Logs detalhados do JWT
        println("DEBUG: Token parts count: ${token.split(".").size}")
        if (token.split(".").size >= 2) {
            val payloadJson = String(Base64.decode(token.split(".")[1], Base64.URL_SAFE or Base64.NO_WRAP))
            println("DEBUG: Payload decodificado: $payloadJson")
        }
        if (userId == null) {
            Toast.makeText(this, "Não foi possível identificar o usuário.", Toast.LENGTH_SHORT).show()
            return
        }

        // Buscar dados reais do usuário
        CoroutineScope(Dispatchers.IO).launch {
            try {
                println("DEBUG: Tentando buscar usuário com ID: $userId")
                println("DEBUG: Token sendo usado: ${token.take(20)}...")
                
                val travoServiceAPI = RetrofitService.getTravoServiceAPIWithToken(token)
                val response = travoServiceAPI.getUserById(userId)
                
                println("DEBUG: Response code: ${response.code()}")
                println("DEBUG: Response body: ${response.body()}")
                println("DEBUG: Response error: ${response.errorBody()?.string()}")
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val usuario = response.body()
                        if (usuario != null) {
                            tvNomeUsuario.text = usuario.nomeCompleto.ifEmpty { usuario.nomeUsuario }
                            tvBioUsuario.text = usuario.sobre ?: "Sem bio"
                            tvEmailUsuario.text = usuario.email
                            tvNascimentoUsuario.text = usuario.dataNascimento ?: "Não informado"
                            tvTelefoneUsuario.text = usuario.telefone ?: "Não informado"
                            Toast.makeText(this@PerfilActivity, "Perfil carregado com sucesso!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@PerfilActivity, "Dados do usuário não encontrados", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            org.json.JSONObject(errorBody).getString("error")
                        } catch (e: Exception) {
                            "Erro ao carregar perfil"
                        }
                        Toast.makeText(this@PerfilActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                println("DEBUG: Erro na PerfilActivity: ${e.message}")
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilActivity, "Falha de conexão: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun extractUserIdFromJwt(token: String): Int? {
        return try {
            val parts = token.split(".")
            println("DEBUG: Token parts count: ${parts.size}")
            if (parts.size < 2) return null
            
            val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))
            println("DEBUG: Payload decodificado: $payloadJson")
            
            val obj = JSONObject(payloadJson)
            println("DEBUG: JSON Object: $obj")
            println("DEBUG: Keys no JSON: ${obj.keys().asSequence().toList()}")
            
            val userId = obj.optInt("id")
            println("DEBUG: ID extraído do JSON: $userId")
            
            userId.takeIf { it != 0 }
        } catch (e: Exception) {
            println("DEBUG: Erro ao extrair ID do token: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}

