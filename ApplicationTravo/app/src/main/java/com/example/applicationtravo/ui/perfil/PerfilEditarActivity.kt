package com.example.applicationtravo.ui.perfil

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationtravo.R
import com.google.android.material.bottomnavigation.BottomNavigationView

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

            // Aqui você pode validar e salvar no banco/API
            Toast.makeText(this, "Perfil salvo: $nome", Toast.LENGTH_SHORT).show()
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
}