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
import com.example.doloresapp.data.remote.dto.ProductoDTO
import com.google.android.material.button.MaterialButton

class StockAdapter(
    private val onEditarStock: (ProductoDTO) -> Unit
) : ListAdapter<ProductoDTO, StockAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_stock, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tv_nombre)
        private val tvCodigo: TextView = itemView.findViewById(R.id.tv_codigo)
        private val tvStock: TextView = itemView.findViewById(R.id.tv_stock)
        private val btnEditar: MaterialButton = itemView.findViewById(R.id.btn_editar)

        fun bind(producto: ProductoDTO) {
            tvNombre.text = producto.nombre
            tvCodigo.text = "Código: ${producto.codigoBarras ?: producto.id}"
            tvStock.text = producto.stock.toString()
            
            // Color según nivel de stock
            val stockMin = producto.stockMin ?: 10
            val color = when {
                producto.stock <= 0 -> Color.parseColor("#F44336") // Rojo - sin stock
                producto.stock <= stockMin -> Color.parseColor("#FF9800") // Naranja - bajo
                else -> Color.parseColor("#4CAF50") // Verde - ok
            }
            tvStock.setTextColor(color)
            
            btnEditar.setOnClickListener { onEditarStock(producto) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductoDTO>() {
        override fun areItemsTheSame(oldItem: ProductoDTO, newItem: ProductoDTO) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ProductoDTO, newItem: ProductoDTO) =
            oldItem == newItem
    }
}
