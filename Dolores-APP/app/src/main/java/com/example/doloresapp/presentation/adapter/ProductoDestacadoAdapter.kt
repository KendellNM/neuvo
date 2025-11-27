package com.example.doloresapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.dto.ProductoDTO

class ProductoDestacadoAdapter(
    private val onProductoClick: (ProductoDTO) -> Unit,
    private val onAgregarClick: (ProductoDTO) -> Unit
) : ListAdapter<ProductoDTO, ProductoDestacadoAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_destacado, parent, false)
        return ViewHolder(view, onProductoClick, onAgregarClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        itemView: View,
        private val onProductoClick: (ProductoDTO) -> Unit,
        private val onAgregarClick: (ProductoDTO) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val tvReceta: TextView = itemView.findViewById(R.id.tvReceta)
        private val btnAgregar: FrameLayout = itemView.findViewById(R.id.btnAgregar)

        fun bind(producto: ProductoDTO) {
            tvNombre.text = producto.nombre
            tvCategoria.text = producto.categoria?.nombre ?: "Medicamento"
            tvPrecio.text = "S/ %.2f".format(producto.precio)

            // Badge de receta
            tvReceta.visibility = if (producto.requerireReceta == true) View.VISIBLE else View.GONE

            // Imagen
            if (!producto.imagen_url.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(producto.imagen_url)
                    .placeholder(R.drawable.ic_medication)
                    .error(R.drawable.ic_medication)
                    .into(imgProducto)
            } else {
                imgProducto.setImageResource(R.drawable.ic_medication)
                imgProducto.setColorFilter(itemView.context.getColor(R.color.primary))
            }

            itemView.setOnClickListener { onProductoClick(producto) }
            btnAgregar.setOnClickListener { onAgregarClick(producto) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductoDTO>() {
        override fun areItemsTheSame(oldItem: ProductoDTO, newItem: ProductoDTO) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ProductoDTO, newItem: ProductoDTO) = oldItem == newItem
    }
}
