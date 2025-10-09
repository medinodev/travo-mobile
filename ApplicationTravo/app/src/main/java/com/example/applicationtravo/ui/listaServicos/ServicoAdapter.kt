package com.example.applicationtravo.ui.listaServicos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.applicationtravo.R
import com.example.applicationtravo.models.ServicoListagemResponse

class ServicoAdapter : RecyclerView.Adapter<ServicoAdapter.VH>() {

    private var itens: List<ServicoListagemResponse> = emptyList()

    fun submit(lista: List<ServicoListagemResponse>) {
        itens = lista
        notifyDataSetChanged()
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val imgThumb: ImageView = view.findViewById(R.id.imgPlace)
        val tvNome: TextView = view.findViewById(R.id.tvTitle)
        val tvEndereco: TextView = view.findViewById(R.id.tvSubtitle)
        val ivArrow: ImageView = view.findViewById(R.id.btnChevron)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_favorito, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = itens[position]
        holder.tvNome.text = item.nome
        holder.tvEndereco.text = item.endereco ?: ""

        holder.imgThumb.load(item.imagemCapaUrl) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_report_image)
            error(android.R.drawable.ic_menu_report_image)
        }

    }

    override fun getItemCount(): Int = itens.size
}
