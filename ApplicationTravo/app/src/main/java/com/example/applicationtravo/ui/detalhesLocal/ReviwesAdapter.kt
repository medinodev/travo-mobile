// ReviewsAdapter.kt
package com.example.applicationtravo.ui.detalhesLocal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationtravo.R
import com.example.applicationtravo.models.ReviewResponse

class ReviewsAdapter : RecyclerView.Adapter<ReviewsAdapter.VH>() {

    private var itens: List<ReviewResponse> = emptyList()

    fun submit(novos: List<ReviewResponse>) {
        itens = novos
        notifyDataSetChanged()
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvAutorData: TextView = view.findViewById(R.id.tvAutor)

        val tvComentario: TextView = view.findViewById(R.id.tvComentario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = itens[position]
        holder.tvAutorData.text = r.dataComentario ?: r.createdAt ?: "—"
        holder.tvComentario.text = r.comentario ?: ""
    }

    override fun getItemCount(): Int = itens.size
}
