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
        private val tvRequiereReceta: TextView? = itemView.findViewById(R.id.tvRequiereReceta)
        
        fun bind(producto: ProductoDTO) {
            nombre.text = producto.nombre
            precio.text = "S/ %.2f".format(producto.precio)
            stock.text = if (producto.stock > 0) {
                "Stock: ${producto.stock}"
            } else {
                "Agotado"
            }
            
            // Mostrar badge si requiere receta
            tvRequiereReceta?.visibility = if (producto.requerireReceta == true) {
                View.VISIBLE
            } else {
                View.GONE
            }
            
            // Cargar imagen con Glide o usar placeholder
            if (!producto.imagen_url.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(producto.imagen_url)
                    .placeholder(R.drawable.ic_medication)
                    .error(R.drawable.ic_medication)
                    .into(imagen)
            } else {
                imagen.setImageResource(R.drawable.ic_medication)
                imagen.setColorFilter(itemView.context.getColor(R.color.primary))
            }
            
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
