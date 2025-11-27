package com.example.doloresapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doloresapp.R
import com.example.doloresapp.domain.model.ItemVenta
import com.example.doloresapp.utils.Constants

class VentaProductoAdapter(
    private val items: MutableList<ItemVenta>,
    private val onListChanged: () -> Unit
) : RecyclerView.Adapter<VentaProductoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_venta, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvPrecioUnitario: TextView = itemView.findViewById(R.id.tvPrecioUnitario)
        private val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        private val tvSubtotal: TextView = itemView.findViewById(R.id.tvSubtotal)
        private val btnMenos: ImageButton = itemView.findViewById(R.id.btnMenos)
        private val btnMas: ImageButton = itemView.findViewById(R.id.btnMas)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

        fun bind(item: ItemVenta, position: Int) {
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
            } else {
                imgProducto.setImageResource(R.drawable.ic_producto_placeholder)
            }

            btnMenos.setOnClickListener {
                if (item.cantidad > 1) {
                    items[position] = item.decrementar()
                    notifyItemChanged(position)
                    onListChanged()
                }
            }

            btnMas.setOnClickListener {
                if (item.puedeIncrementar()) {
                    items[position] = item.incrementar()
                    notifyItemChanged(position)
                    onListChanged()
                } else {
                    android.widget.Toast.makeText(
                        itemView.context,
                        "Stock máximo: ${item.stockDisponible}",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnEliminar.setOnClickListener {
                items.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, items.size)
                onListChanged()
            }
        }
    }
}