package com.example.doloresapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.dto.PedidoDTO

class PedidosAdapter(
    private val onPedidoClick: (PedidoDTO) -> Unit
) : ListAdapter<PedidoDTO, PedidosAdapter.PedidoViewHolder>(PedidoDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view, onPedidoClick)
    }
    
    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class PedidoViewHolder(
        itemView: View,
        private val onPedidoClick: (PedidoDTO) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val tvNumero: TextView = itemView.findViewById(R.id.tvNumero)
        private val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)
        
        fun bind(pedido: PedidoDTO) {
            tvNumero.text = "Pedido #${pedido.id}"
            tvTotal.text = "S/ %.2f".format(pedido.total)
            tvFecha.text = pedido.fechaCreacion ?: ""
            tvDireccion.text = pedido.direccionEntrega ?: "Sin dirección"
            
            // Estado con emoji
            val (emoji, color) = when (pedido.estado) {
                "PENDIENTE" -> "⏳" to android.R.color.holo_orange_dark
                "PREPARANDO" -> "👨‍🍳" to android.R.color.holo_blue_dark
                "LISTO" -> "✅" to android.R.color.holo_green_dark
                "EN_CAMINO" -> "🚚" to android.R.color.holo_blue_light
                "ENTREGADO" -> "📦" to android.R.color.holo_green_light
                "CANCELADO" -> "❌" to android.R.color.holo_red_dark
                else -> "📋" to android.R.color.darker_gray
            }
            tvEstado.text = "$emoji ${pedido.estado}"
            tvEstado.setTextColor(itemView.context.getColor(color))
            
            itemView.setOnClickListener { onPedidoClick(pedido) }
        }
    }
    
    class PedidoDiffCallback : DiffUtil.ItemCallback<PedidoDTO>() {
        override fun areItemsTheSame(oldItem: PedidoDTO, newItem: PedidoDTO) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PedidoDTO, newItem: PedidoDTO) = oldItem == newItem
    }
}
