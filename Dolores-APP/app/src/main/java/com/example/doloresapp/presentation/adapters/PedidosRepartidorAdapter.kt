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

class PedidosRepartidorAdapter(
    private val onVerMapa: (PedidoResponse) -> Unit,
    private val onIniciarEntrega: (PedidoResponse) -> Unit,
    private val onMarcarEntregado: (PedidoResponse) -> Unit
) : ListAdapter<PedidoResponse, PedidosRepartidorAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_repartidor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNumeroPedido: TextView = itemView.findViewById(R.id.tv_numero_pedido)
        private val tvEstado: TextView = itemView.findViewById(R.id.tv_estado)
        private val tvCliente: TextView = itemView.findViewById(R.id.tv_cliente)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tv_direccion)
        private val tvTotal: TextView = itemView.findViewById(R.id.tv_total)
        private val btnVerMapa: MaterialButton = itemView.findViewById(R.id.btn_ver_mapa)
        private val btnIniciarEntrega: MaterialButton = itemView.findViewById(R.id.btn_iniciar_entrega)

        fun bind(pedido: PedidoResponse) {
            tvNumeroPedido.text = "Pedido #${pedido.numeroPedido ?: pedido.idPedidos}"
            tvEstado.text = pedido.estado ?: "PENDIENTE"
            
            // Color del estado (con null safety)
            val estadoColor = when (pedido.estado?.uppercase()) {
                "ASIGNADO" -> Color.parseColor("#FF9800")
                "EN_CAMINO" -> Color.parseColor("#2196F3")
                "ENTREGADO" -> Color.parseColor("#4CAF50")
                else -> Color.parseColor("#9E9E9E")
            }
            tvEstado.background?.setTint(estadoColor)
            
            // Cliente
            val clienteNombre = pedido.clientes?.let {
                "${it.nombres ?: ""} ${it.apellidos ?: ""}".trim()
            } ?: "Cliente"
            tvCliente.text = "👤 $clienteNombre"
            
            // Dirección
            val direccion = pedido.direcciones?.direccion ?: "Sin dirección"
            tvDireccion.text = "📍 $direccion"
            
            // Total
            tvTotal.text = "💰 Total: S/ ${String.format("%.2f", pedido.total ?: 0.0)}"
            
            // Resetear visibilidad del botón (importante para reciclaje de ViewHolder)
            btnIniciarEntrega.visibility = View.VISIBLE
            
            // Botones según estado
            when (pedido.estado?.uppercase()) {
                "ASIGNADO" -> {
                    btnIniciarEntrega.text = "🚚 Iniciar"
                    btnIniciarEntrega.setOnClickListener { onIniciarEntrega(pedido) }
                }
                "EN_CAMINO" -> {
                    btnIniciarEntrega.text = "✅ Entregado"
                    btnIniciarEntrega.setOnClickListener { onMarcarEntregado(pedido) }
                }
                "ENTREGADO" -> {
                    btnIniciarEntrega.visibility = View.GONE
                }
                else -> {
                    btnIniciarEntrega.text = "🚚 Iniciar"
                    btnIniciarEntrega.setOnClickListener { onIniciarEntrega(pedido) }
                }
            }
            
            btnVerMapa.setOnClickListener { onVerMapa(pedido) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PedidoResponse>() {
        override fun areItemsTheSame(oldItem: PedidoResponse, newItem: PedidoResponse) =
            oldItem.idPedidos == newItem.idPedidos

        override fun areContentsTheSame(oldItem: PedidoResponse, newItem: PedidoResponse) =
            oldItem == newItem
    }
}
