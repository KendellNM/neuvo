package com.example.doloresapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.dto.ProductoDTO

class ProductosAdapter(
    private val onProductoClick: (ProductoDTO) -> Unit
) : ListAdapter<ProductoDTO, ProductosAdapter.ProductoViewHolder>(ProductoDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view, onProductoClick)
    }
    
    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ProductoViewHolder(
        itemView: View,
        private val onProductoClick: (ProductoDTO) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val imagen: ImageView = itemView.findViewById(R.id.imgProducto)
        private val nombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val precio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val stock: TextView = itemView.findViewById(R.id.tvStock)
        
        fun bind(producto: ProductoDTO) {
            nombre.text = producto.nombre
            precio.text = "S/ ${producto.precio}"
            stock.text = if (producto.stock > 0) {
                "Stock: ${producto.stock}"
            } else {
                "Agotado"
            }
            
            // Cargar imagen con Glide
            Glide.with(itemView.context)
                .load(producto.imagen_url)
                .placeholder(R.drawable.ic_producto_placeholder)
                .error(R.drawable.ic_producto_placeholder)
                .into(imagen)
            
            itemView.setOnClickListener {
                onProductoClick(producto)
            }
        }
    }
    
    class ProductoDiffCallback : DiffUtil.ItemCallback<ProductoDTO>() {
        override fun areItemsTheSame(oldItem: ProductoDTO, newItem: ProductoDTO): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: ProductoDTO, newItem: ProductoDTO): Boolean {
            return oldItem == newItem
        }
    }
}
