package com.example.applicationtravo.ui.detalhesLocal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationtravo.R
import com.example.applicationtravo.models.CupomResponse

class CupomAdapter : RecyclerView.Adapter<CupomAdapter.VH>() {

    private var itens: List<CupomResponse> = emptyList()

    fun submit(novos: List<CupomResponse>) {
        itens = novos
        notifyDataSetChanged()
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.txtDesconto)
        val tvDescricao: TextView = view.findViewById(R.id.txtLoja)
        val tvCodigo: TextView = view.findViewById(R.id.txtValidade)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_cupom, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = itens[position]
        holder.tvTitulo.text = item.nome ?: "Cupom"
        holder.tvDescricao.text = item.descricao ?: ""
        holder.tvCodigo.text = item.codigo ?: ""
    }

    override fun getItemCount(): Int = itens.size
}
