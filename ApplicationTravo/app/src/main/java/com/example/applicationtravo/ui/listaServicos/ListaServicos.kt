package com.example.applicationtravo.ui.listaServicos

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationtravo.R

class ListaServicos : AppCompatActivity() {

    private lateinit var cardFiltros: View
    private lateinit var btnToggleFiltros: ImageButton

    companion object {
        private const val STATE_FILTROS_VISIVEIS = "STATE_FILTROS_VISIVEIS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_servicos)

        cardFiltros = findViewById(R.id.cardFiltros)
        btnToggleFiltros = findViewById(R.id.btnToggleFiltros)

        // Restaura estado (default: GONE como está no XML)
        val estavaVisivel = savedInstanceState?.getBoolean(STATE_FILTROS_VISIVEIS, false) ?: false
        cardFiltros.visibility = if (estavaVisivel) View.VISIBLE else View.GONE
        btnToggleFiltros.setImageResource(
            if (estavaVisivel) R.drawable.ic_chevron_left_24 else R.drawable.ic_chevron_left_24
        )
        btnToggleFiltros.contentDescription = getString(
            if (estavaVisivel) R.string.fechar_filtros else R.string.abrir_filtros
        )

        btnToggleFiltros.setOnClickListener { alternarFiltros() }
    }

    private fun alternarFiltros() {
        val abrir = cardFiltros.visibility != View.VISIBLE
        if ( abrir ) {
            // Mostra com leve slide da direita
            cardFiltros.visibility = View.VISIBLE
            cardFiltros.translationX = cardFiltros.width.toFloat()
            cardFiltros.alpha = 0f
            cardFiltros.animate()
                .translationX(0f)
                .alpha(1f)
                .setDuration(180)
                .start()

            btnToggleFiltros.setImageResource(R.drawable.ic_chevron_left_24)
            btnToggleFiltros.contentDescription = getString(R.string.fechar_filtros)
        } else {
            // Esconde com slide para a direita
            cardFiltros.animate()
                .translationX(cardFiltros.width.toFloat())
                .alpha(0f)
                .setDuration(180)
                .withEndAction {
                    cardFiltros.visibility = View.GONE
                    btnToggleFiltros.setImageResource(R.drawable.ic_chevron_left_24)
                    btnToggleFiltros.contentDescription = getString(R.string.abrir_filtros)
                    // Reseta props pra próxima abertura
                    cardFiltros.translationX = 0f
                    cardFiltros.alpha = 1f
                }
                .start()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_FILTROS_VISIVEIS, cardFiltros.visibility == View.VISIBLE)
        super.onSaveInstanceState(outState)
    }
}
