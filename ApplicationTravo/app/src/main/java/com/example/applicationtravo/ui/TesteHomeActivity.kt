package com.example.applicationtravo.ui


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.applicationtravo.R
import com.example.applicationtravo.ui.cadastro.CadastroActivity
import com.example.applicationtravo.ui.configuracoes.Configuracoes
import com.example.applicationtravo.ui.detalhesLocal.DetalhesLocal
import com.example.applicationtravo.ui.favoritos.Favoritos
import com.example.applicationtravo.ui.listaCupons.ListaCupons
import com.example.applicationtravo.ui.listaServicos.ListaServicos
import com.example.applicationtravo.ui.login.LoginActivity
import com.example.applicationtravo.ui.perfil.PerfilActivity
import com.example.applicationtravo.ui.perfil.PerfilEditarActivity
import com.example.applicationtravo.ui.recuperarSenha.RecuperarSenhaActivity

class TesteHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teste_home)

        findViewById<Button>(R.id.btnCadastro).setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }

        findViewById<Button>(R.id.btnDetalhesLocal).setOnClickListener {
            startActivity(Intent(this, DetalhesLocal::class.java))
        }

        findViewById<Button>(R.id.btnFavoritos).setOnClickListener {
            startActivity(Intent(this, Favoritos::class.java))
        }

        findViewById<Button>(R.id.btnListaCupons).setOnClickListener {
            startActivity(Intent(this, ListaCupons::class.java))
        }

        findViewById<Button>(R.id.btnListaServicos).setOnClickListener {
            startActivity(Intent(this, ListaServicos::class.java))
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<Button>(R.id.btnPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        findViewById<Button>(R.id.btnPerfilEditar).setOnClickListener {
            startActivity(Intent(this, Configuracoes::class.java))
        }

        findViewById<Button>(R.id.btnRecuperarSenha).setOnClickListener {
            startActivity(Intent(this, RecuperarSenhaActivity::class.java))
        }
    }
}
