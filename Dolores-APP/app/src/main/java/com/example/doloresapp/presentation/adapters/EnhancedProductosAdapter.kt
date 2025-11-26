package com.example.doloresapp.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doloresapp.R
import com.example.doloresapp.domain.model.Producto

class EnhancedProductosAdapter(
    private var productos: List<Producto>,
    private val onItemClick: (Producto) -> Unit = {},
    private val onAddToCartClick: (Producto) -> Unit = {}
) : RecyclerView.Adapter<EnhancedProductosAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto, onItemClick, onAddToCartClick)
    }

    override fun getItemCount(): Int = productos.size

    fun submitList(newProductos: List<Producto>) {
        productos = newProductos
        notifyDataSetChanged()
    }

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.product_name)
        private val productPrice: TextView = itemView.findViewById(R.id.product_price)
        private val infoIcon: View? = itemView.findViewById(R.id.product_info_icon)
        private val addIcon: View? = itemView.findViewById(R.id.add_to_cart_icon)

        fun bind(producto: Producto, onItemClick: (Producto) -> Unit, onAddToCartClick: (Producto) -> Unit) {
            productName.text = producto.nombre
            productPrice.text = "${'$'}${producto.precio}"

            itemView.setOnClickListener { onItemClick(producto) }
            infoIcon?.setOnClickListener { onItemClick(producto) }
            addIcon?.setOnClickListener { onAddToCartClick(producto) }
        }
    }
}
