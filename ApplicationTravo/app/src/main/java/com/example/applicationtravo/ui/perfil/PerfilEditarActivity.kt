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
import android.content.Intent
import com.example.applicationtravo.retrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Base64
import org.json.JSONObject
import com.example.applicationtravo.models.UsuarioUpdateRequest
import com.example.applicationtravo.models.ChangePasswordRequest
import com.example.applicationtravo.ui.TesteHomeActivity
import com.example.applicationtravo.ui.configuracoes.Configuracoes
import com.example.applicationtravo.ui.listaCupons.ListaCupons

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
                                    // Preencher com nome completo (obrigatório) ou nome de usuário (opcional)
                                    editNome.setText(u.nomeCompleto.ifEmpty { u.nomeUsuario })
                                    editBio.setText(u.sobre ?: "")
                                    editTelefone.setText(u.telefone ?: "")
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
            val telefone = editTelefone.text.toString()
            
            if (nome.isBlank()) {
                Toast.makeText(this, "Nome é obrigatório", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
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
            
            // Buscar dados atuais do usuário para mesclar com o update
            // (necessário porque o backend exige todos os campos obrigatórios)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val usuarioAtual = api.getUserById(userId)
                    if (!usuarioAtual.isSuccessful || usuarioAtual.body() == null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@PerfilEditarActivity, "Erro ao buscar dados do usuário", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }
                    
                    val u = usuarioAtual.body()!!
                    val request = UsuarioUpdateRequest(
                        email = u.email, // Manter email atual
                        nomeCompleto = nome, // nomeCompleto é obrigatório
                        nomeUsuario = if (nome != u.nomeCompleto) nome.takeIf { it.isNotEmpty() } else u.nomeUsuario,
                        sobre = bio.ifEmpty { null },
                        telefone = telefone.ifEmpty { null }
                    )
                    
                    val response = api.updateUser(userId, request)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@PerfilEditarActivity, "Perfil salvo!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val errorMessage = try {
                                val jsonObject = org.json.JSONObject(errorBody)
                                if (jsonObject.has("errors")) {
                                    val errorsArray = jsonObject.getJSONArray("errors")
                                    errorsArray.getString(0)
                                } else {
                                    jsonObject.optString("error", "Erro ao salvar perfil")
                                }
                            } catch (e: Exception) {
                                "Erro ao salvar perfil: ${errorBody ?: e.message}"
                            }
                            Toast.makeText(this@PerfilEditarActivity, errorMessage, Toast.LENGTH_LONG).show()
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

            if (antiga.isBlank() || nova.isBlank() || confirmar.isBlank()) {
                Toast.makeText(this, "Todos os campos de senha são obrigatórios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nova != confirmar) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nova.length < 6) {
                Toast.makeText(this, "Nova senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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
            val request = ChangePasswordRequest(
                senhaAtual = antiga,
                novaSenha = nova
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = api.changePassword(userId, request)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@PerfilEditarActivity, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
                            // Limpar campos
                            editSenhaAntiga.text.clear()
                            editSenhaNova.text.clear()
                            editSenhaConfirmar.text.clear()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(this@PerfilEditarActivity, "Erro ao alterar senha: $errorBody", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@PerfilEditarActivity, "Falha: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_descontos -> {
                    startActivity(Intent(this, ListaCupons::class.java))
                    true
                }

                R.id.nav_home -> {
                    startActivity(Intent(this, TesteHomeActivity::class.java))
                    true
                }

                R.id.nav_config -> {
                    startActivity(Intent(this, Configuracoes::class.java))
                    true
                }

                else -> false
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