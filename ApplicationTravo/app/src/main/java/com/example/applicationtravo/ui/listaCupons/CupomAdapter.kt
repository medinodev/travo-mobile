package com.example.applicationtravo.ui.listaCupons

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationtravo.R
import com.example.applicationtravo.models.CupomResponse
import android.widget.Button

class CupomAdapter(
    private var lista: List<CupomResponse>,
    private val onUseClick: (CupomResponse) -> Unit
) : RecyclerView.Adapter<CupomAdapter.CupomViewHolder>() {

    private var listaOriginal: List<CupomResponse> = lista.toList()
    private val favoritos = mutableSetOf<Int>()
    private var mostrandoFavoritos = false

    inner class CupomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.txtDesconto)
        val descricao: TextView = view.findViewById(R.id.txtLoja)
        val validade: TextView = view.findViewById(R.id.txtValidade)
        val btnFav: ImageButton = view.findViewById(R.id.btn_favorite)
        val btnUse: Button = view.findViewById(R.id.btn_use)
        val txtCodigo: TextView = view.findViewById(R.id.txtCodigo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CupomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cupom, parent, false)
        return CupomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CupomViewHolder, position: Int) {
        val cupom = lista[position]

        holder.nome.text = cupom.nome
        holder.descricao.text = cupom.descricao ?: "Sem descrição"
        holder.validade.text = "Válido até: ${cupom.expiration ?: "Indefinido"}"

        val idCupom = cupom.id ?: -1

        val icon = if (favoritos.contains(idCupom))
            android.R.drawable.btn_star_big_on
        else
            android.R.drawable.btn_star_big_off

        holder.btnFav.setImageResource(icon)

        holder.btnFav.setOnClickListener {
            if (favoritos.contains(idCupom)) {
                favoritos.remove(idCupom)
            } else {
                favoritos.add(idCupom)
            }
            notifyItemChanged(position)
        }
        if (!cupom.codigo.isNullOrBlank()) {
            holder.txtCodigo.visibility = View.VISIBLE
            holder.txtCodigo.text = "Código: ${cupom.codigo}"
            holder.btnUse.text = "Ver código"
        } else {
            holder.txtCodigo.visibility = View.GONE
            holder.btnUse.text = "Usar cupom"
        }

        holder.btnUse.setOnClickListener {
            onUseClick(cupom)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun filter(query: String) {
        lista = if (query.isEmpty()) {
            listaOriginal
        } else {
            listaOriginal.filter { it.nome.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    fun mostrarFavoritos() {
        mostrandoFavoritos = !mostrandoFavoritos
        lista = if (mostrandoFavoritos) {
            listaOriginal.filter { it.id != null && favoritos.contains(it.id) }
        } else {
            listaOriginal
        }
        notifyDataSetChanged()
    }
}
