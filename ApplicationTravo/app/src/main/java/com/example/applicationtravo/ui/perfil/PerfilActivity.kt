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
        if (userId == null) {
            Toast.makeText(this, "Não foi possível identificar o usuário.", Toast.LENGTH_SHORT).show()
            return
        }

        val api = RetrofitService.getTravoServiceAPIWithToken(token)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getUserById(userId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            tvNomeUsuario.text = user.nomeUsuario.ifEmpty { user.nomeCompleto }
                            tvBioUsuario.text = user.sobre ?: ""
                            tvEmailUsuario.text = user.email
                            tvNascimentoUsuario.text = user.dataNascimento ?: ""
                            // pesquisar sobre glide/coil pra resolver foto de perfil
                        } else {
                            Toast.makeText(this@PerfilActivity, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@PerfilActivity, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PerfilActivity, "Falha de conexão: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun extractUserIdFromJwt(token: String): Int? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null
            val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))
            val obj = JSONObject(payloadJson)
            obj.optInt("id").takeIf { it != 0 }
        } catch (e: Exception) {
            null
        }
    }
}

