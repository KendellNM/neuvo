package com.example.doloresapp.presentation.ui

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.doloresapp.R
import com.example.doloresapp.data.cart.CartRepository

class CartFragment : Fragment(R.layout.cart_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.back_arrow)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        renderCart(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let { renderCart(it) }
    }

    private fun renderCart(root: View) {
        val container: LinearLayout = root.findViewById(R.id.cart_items_container)
        val totalText: TextView = root.findViewById(R.id.totalText)

        container.removeAllViews()

        var total = 0.0
        for ((product, qty) in CartRepository.getItems()) {
            total += (product.precioOferta ?: product.precio) * qty

            // Inflate a simple item row using existing structure from layout as a template replacement
            val row = layoutInflater.inflate(R.layout.item_cart_row, container, false)
            // If you don't have item_cart_row.xml, we will fallback to building simple views dynamically
            // But for now, try to bind common IDs present in cart_fragment.xml's sample card
            val nameView = row.findViewById<TextView?>(R.id.productName)
            val oldView = row.findViewById<TextView?>(R.id.oldPrice)
            val priceView = row.findViewById<TextView?>(R.id.price)
            val imageView = row.findViewById<ImageView?>(R.id.productImage)
            val qtyText = row.findViewById<TextView?>(R.id.quantityText)
            val decBtn = row.findViewById<View?>(R.id.decreaseButton)
            val incBtn = row.findViewById<View?>(R.id.increaseButton)

            nameView?.text = product.nombre
            if (product.precioOferta != null) {
                oldView?.apply {
                    text = "Antes: $${product.precio}"
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    visibility = View.VISIBLE
                }
                priceView?.text = "$${product.precioOferta}"
            } else {
                oldView?.visibility = View.GONE
                priceView?.text = "$${product.precio}"
            }
            imageView?.setImageResource(R.drawable.ic_medication)
            qtyText?.text = qty.toString()

            decBtn?.setOnClickListener {
                CartRepository.remove(product.id)
                renderCart(root)
            }
            incBtn?.setOnClickListener {
                CartRepository.add(product)
                renderCart(root)
            }

            container.addView(row)
        }

        totalText.text = "$${String.format("%.2f", total)}"
    }
}
