package com.example.applicationtravo.ui.perfil

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationtravo.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Context
import com.example.applicationtravo.retrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Base64
import org.json.JSONObject
import com.example.applicationtravo.models.UsuarioUpdateRequest

class PerfilEditarActivity : AppCompatActivity() {

    private lateinit var imgFoto: ImageView
    private lateinit var btnAlterarFoto: Button
    private lateinit var editNome: EditText
    private lateinit var editBio: EditText
    private lateinit var editTelefone: EditText
    private lateinit var btnSalvarPerfil: Button

    private lateinit var editSenhaAntiga: EditText
    private lateinit var editSenhaNova: EditText
    private lateinit var editSenhaConfirmar: EditText
    private lateinit var btnSalvarSenha: Button

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_editar)

        // Referências
        imgFoto = findViewById(R.id.imgFotoUsuarioEditar)
        btnAlterarFoto = findViewById(R.id.btnAlterarFoto)
        editNome = findViewById(R.id.editNome)
        editBio = findViewById(R.id.editBio)
        editTelefone = findViewById(R.id.editTelefone)
        btnSalvarPerfil = findViewById(R.id.btnSalvarPerfil)

        editSenhaAntiga = findViewById(R.id.editSenhaAntiga)
        editSenhaNova = findViewById(R.id.editSenhaNova)
        editSenhaConfirmar = findViewById(R.id.editSenhaConfirmar)
        btnSalvarSenha = findViewById(R.id.btnSalvarSenha)

        bottomNav = findViewById(R.id.bottomNav)

        // Prefill - buscar usuário atual
        val token = getSharedPreferences("TravoApp", Context.MODE_PRIVATE).getString("token", null)
        if (!token.isNullOrEmpty()) {
            val userId = extractUserIdFromJwt(token)
            if (userId != null) {
                val api = RetrofitService.getTravoServiceAPIWithToken(token)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val resp = api.getUserById(userId)
                        withContext(Dispatchers.Main) {
                            if (resp.isSuccessful) {
                                val u = resp.body()
                                if (u != null) {
                                    editNome.setText(u.nomeUsuario.ifEmpty { u.nomeCompleto })
                                    editBio.setText(u.sobre ?: "")
                                }
                            }
                        }
                    } catch (_: Exception) {
                        // Silencia, pois é prefill opcional
                    }
                }
            }
        }

        // Listener - alterar foto
        btnAlterarFoto.setOnClickListener {
            Toast.makeText(this, "Alterar foto (implementar lógica)", Toast.LENGTH_SHORT).show()
            // Exemplo: abrir galeria com Intent.ACTION_PICK
        }

        // Listener - salvar perfil
        btnSalvarPerfil.setOnClickListener {
            val nome = editNome.text.toString()
            val bio = editBio.text.toString()
            val token = getSharedPreferences("TravoApp", Context.MODE_PRIVATE).getString("token", null)
            if (token.isNullOrEmpty()) {
                Toast.makeText(this, "Sessão expirada.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val userId = extractUserIdFromJwt(token)
            if (userId == null) {
                Toast.makeText(this, "Usuário inválido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val api = RetrofitService.getTravoServiceAPIWithToken(token)
            val request = UsuarioUpdateRequest(
                nomeUsuario = nome,
                sobre = bio
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = api.updateUser(userId, request)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@PerfilEditarActivity, "Perfil salvo!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@PerfilEditarActivity, "Erro ao salvar perfil", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@PerfilEditarActivity, "Falha: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // Listener - salvar senha
        btnSalvarSenha.setOnClickListener {
            val antiga = editSenhaAntiga.text.toString()
            val nova = editSenhaNova.text.toString()
            val confirmar = editSenhaConfirmar.text.toString()

            if (nova == confirmar) {
                Toast.makeText(this, "Senha alterada!", Toast.LENGTH_SHORT).show()
                // Aqui você pode implementar a lógica de update de senha
            } else {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
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