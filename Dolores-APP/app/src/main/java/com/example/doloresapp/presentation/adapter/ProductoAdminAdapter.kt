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
import com.example.doloresapp.data.remote.dto.ProductoDTO
import com.example.doloresapp.utils.Constants

class ProductoAdminAdapter(
    private var productos: List<ProductoDTO>,
    private val onEditar: (ProductoDTO) -> Unit,
    private val onEliminar: (ProductoDTO) -> Unit,
    private val onVerQR: (ProductoDTO) -> Unit
) : RecyclerView.Adapter<ProductoAdminAdapter.ViewHolder>() {

    fun updateList(newList: List<ProductoDTO>) {
        productos = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productos[position])
    }

    override fun getItemCount() = productos.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgQR: ImageView = itemView.findViewById(R.id.imgQR)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val tvStock: TextView = itemView.findViewById(R.id.tvStock)
        private val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
        private val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

        fun bind(producto: ProductoDTO) {
            tvNombre.text = producto.nombre
            tvPrecio.text = "S/ ${String.format("%.2f", producto.precio)}"
            tvStock.text = "Stock: ${producto.stock}"
            tvCategoria.text = producto.categoria?.nombre ?: "Sin categoría"

            // Cargar QR si existe
            if (!producto.qrImageUrl.isNullOrEmpty()) {
                val fullUrl = if (producto.qrImageUrl.startsWith("http")) producto.qrImageUrl
                    else Constants.BASE_URL.trimEnd('/') + producto.qrImageUrl
                Glide.with(itemView.context)
                    .load(fullUrl)
                    .placeholder(R.drawable.ic_producto_placeholder)
                    .into(imgQR)
            } else {
                imgQR.setImageResource(R.drawable.ic_producto_placeholder)
            }

            // Click en QR para ver ampliado
            imgQR.setOnClickListener { onVerQR(producto) }
            
            btnEditar.setOnClickListener { onEditar(producto) }
            btnEliminar.setOnClickListener { onEliminar(producto) }
        }
    }
}