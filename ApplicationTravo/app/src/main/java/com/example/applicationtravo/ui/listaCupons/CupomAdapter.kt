package com.example.applicationtravo.ui.listaCupons

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationtravo.R
import com.example.applicationtravo.models.CupomResponse

class CupomAdapter(private var lista: List<CupomResponse>) :
    RecyclerView.Adapter<CupomAdapter.CupomViewHolder>() {

    private var listaOriginal: List<CupomResponse> = lista.toList()

    inner class CupomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.txtDesconto)
        val descricao: TextView = view.findViewById(R.id.txtLoja)
        val validade: TextView = view.findViewById(R.id.txtValidade)
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
}
