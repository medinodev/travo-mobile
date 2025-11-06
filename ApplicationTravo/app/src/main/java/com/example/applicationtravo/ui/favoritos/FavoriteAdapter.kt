package com.example.applicationtravo.ui.favoritos

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationtravo.R

class FavoriteAdapter(
    private var items: List<FavoriteUi> = emptyList(),
    private val onClick: (FavoriteUi) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.VH>() {

    inner class VH(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_favorito, parent, false)
    ) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(parent)

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.title
        holder.tvSubtitle.text = item.subtitle ?: ""
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun submit(newItems: List<FavoriteUi>) {
        items = newItems
        notifyDataSetChanged()
    }

}
