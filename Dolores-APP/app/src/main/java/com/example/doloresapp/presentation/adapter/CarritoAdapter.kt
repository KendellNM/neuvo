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
import com.example.doloresapp.data.local.entity.CarritoItem

class CarritoAdapter(
    private val onUpdateCantidad: (CarritoItem, Int) -> Unit,
    private val onEliminar: (CarritoItem) -> Unit
) : ListAdapter<CarritoItem, CarritoAdapter.CarritoViewHolder>(CarritoDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view, onUpdateCantidad, onEliminar)
    }
    
    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class CarritoViewHolder(
        itemView: View,
        private val onUpdateCantidad: (CarritoItem, Int) -> Unit,
        private val onEliminar: (CarritoItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        private val tvSubtotal: TextView = itemView.findViewById(R.id.tvSubtotal)
        private val btnMenos: ImageButton = itemView.findViewById(R.id.btnMenos)
        private val btnMas: ImageButton = itemView.findViewById(R.id.btnMas)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)
        
        fun bind(item: CarritoItem) {
            tvNombre.text = item.nombre
            tvPrecio.text = "S/ %.2f".format(item.precio)
            tvCantidad.text = item.cantidad.toString()
            tvSubtotal.text = "S/ %.2f".format(item.subtotal)
            
            Glide.with(itemView.context)
                .load(item.imagenUrl)
                .placeholder(R.drawable.ic_producto_placeholder)
                .into(imgProducto)
            
            btnMenos.setOnClickListener { onUpdateCantidad(item, item.cantidad - 1) }
            btnMas.setOnClickListener { onUpdateCantidad(item, item.cantidad + 1) }
            btnEliminar.setOnClickListener { onEliminar(item) }
        }
    }
    
    class CarritoDiffCallback : DiffUtil.ItemCallback<CarritoItem>() {
        override fun areItemsTheSame(oldItem: CarritoItem, newItem: CarritoItem) = 
            oldItem.productoId == newItem.productoId
        override fun areContentsTheSame(oldItem: CarritoItem, newItem: CarritoItem) = 
            oldItem == newItem
    }
}
