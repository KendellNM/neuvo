package com.example.doloresapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doloresapp.R
import com.example.doloresapp.domain.model.ItemVenta
import com.example.doloresapp.utils.Constants
import java.text.NumberFormat
import java.util.Locale

class VentaProductoAdapter(
    private val onCantidadChanged: (ItemVenta) -> Unit,
    private val onItemRemoved: (ItemVenta) -> Unit
) : ListAdapter<ItemVenta, VentaProductoAdapter.ViewHolder>(DiffCallback()) {

    private val numberFormat = NumberFormat.getCurrencyInstance(Locale("es", "PE")).apply {
        maximumFractionDigits = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_venta, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvPrecioUnitario: TextView = itemView.findViewById(R.id.tvPrecioUnitario)
        private val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        private val tvSubtotal: TextView = itemView.findViewById(R.id.tvSubtotal)
        private val btnMenos: ImageButton = itemView.findViewById(R.id.btnMenos)
        private val btnMas: ImageButton = itemView.findViewById(R.id.btnMas)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

        fun bind(item: ItemVenta) {
            tvNombre.text = item.nombre
            tvPrecioUnitario.text = "S/ ${String.format("%.2f", item.precioUnitario)} c/u"
            tvCantidad.text = item.cantidad.toString()
            tvSubtotal.text = "S/ ${String.format("%.2f", item.subtotal)}"

            // Cargar imagen
            if (!item.imagenUrl.isNullOrEmpty()) {
                val fullUrl = if (item.imagenUrl.startsWith("http")) item.imagenUrl
                    else Constants.BASE_URL.trimEnd('/') + item.imagenUrl
                Glide.with(itemView.context)
                    .load(fullUrl)
                    .placeholder(R.drawable.ic_producto_placeholder)
                    .into(imgProducto)
            }

            btnMenos.setOnClickListener {
                if (item.cantidad > 1) {
                    item.cantidad--
                    tvCantidad.text = item.cantidad.toString()
                    tvSubtotal.text = "S/ ${String.format("%.2f", item.subtotal)}"
                    onCantidadChanged(item)
                }
            }

            btnMas.setOnClickListener {
                item.cantidad++
                tvCantidad.text = item.cantidad.toString()
                tvSubtotal.text = "S/ ${String.format("%.2f", item.subtotal)}"
                onCantidadChanged(item)
            }

            btnEliminar.setOnClickListener {
                onItemRemoved(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ItemVenta>() {
        override fun areItemsTheSame(oldItem: ItemVenta, newItem: ItemVenta) =
            oldItem.productoId == newItem.productoId

        override fun areContentsTheSame(oldItem: ItemVenta, newItem: ItemVenta) =
            oldItem == newItem
    }
}