package com.example.doloresapp.presentation.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.PedidoResponse
import com.google.android.material.button.MaterialButton

class PedidosAdminAdapter(
    private val onCambiarEstado: (PedidoResponse) -> Unit,
    private val onAsignar: (PedidoResponse) -> Unit
) : ListAdapter<PedidoResponse, PedidosAdminAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNumeroPedido: TextView = itemView.findViewById(R.id.tv_numero_pedido)
        private val tvEstado: TextView = itemView.findViewById(R.id.tv_estado)
        private val tvCliente: TextView = itemView.findViewById(R.id.tv_cliente)
        private val tvTotal: TextView = itemView.findViewById(R.id.tv_total)
        private val tvFecha: TextView = itemView.findViewById(R.id.tv_fecha)
        private val btnCambiarEstado: MaterialButton = itemView.findViewById(R.id.btn_cambiar_estado)
        private val btnAsignar: MaterialButton = itemView.findViewById(R.id.btn_asignar)

        fun bind(pedido: PedidoResponse) {
            tvNumeroPedido.text = "Pedido #${pedido.numeroPedido ?: pedido.idPedidos}"
            tvEstado.text = pedido.estado ?: "PENDIENTE"
            
            // Color del estado
            val estadoColor = when (pedido.estado?.uppercase()) {
                "PENDIENTE" -> Color.parseColor("#9E9E9E")
                "CONFIRMADO" -> Color.parseColor("#2196F3")
                "PREPARANDO" -> Color.parseColor("#FF9800")
                "LISTO" -> Color.parseColor("#8BC34A")
                "ASIGNADO" -> Color.parseColor("#00BCD4")
                "EN_CAMINO" -> Color.parseColor("#3F51B5")
                "ENTREGADO" -> Color.parseColor("#4CAF50")
                "CANCELADO" -> Color.parseColor("#F44336")
                else -> Color.parseColor("#9E9E9E")
            }
            tvEstado.background.setTint(estadoColor)
            
            // Cliente
            val clienteNombre = pedido.clientes?.let {
                "${it.nombres ?: ""} ${it.apellidos ?: ""}".trim()
            } ?: "Sin cliente"
            tvCliente.text = "👤 $clienteNombre"
            
            // Total
            tvTotal.text = "💰 S/ ${String.format("%.2f", pedido.total ?: 0.0)}"
            
            // Fecha
            tvFecha.text = "📅 ${pedido.fechaPedido ?: "Sin fecha"}"
            
            // Mostrar/ocultar botón asignar según estado
            val puedeAsignar = pedido.estado?.uppercase() in listOf("LISTO", "PREPARADO") && 
                               pedido.repartidor == null
            btnAsignar.visibility = if (puedeAsignar) View.VISIBLE else View.GONE
            
            btnCambiarEstado.setOnClickListener { onCambiarEstado(pedido) }
            btnAsignar.setOnClickListener { onAsignar(pedido) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PedidoResponse>() {
        override fun areItemsTheSame(oldItem: PedidoResponse, newItem: PedidoResponse) =
            oldItem.idPedidos == newItem.idPedidos

        override fun areContentsTheSame(oldItem: PedidoResponse, newItem: PedidoResponse) =
            oldItem == newItem
    }
}
