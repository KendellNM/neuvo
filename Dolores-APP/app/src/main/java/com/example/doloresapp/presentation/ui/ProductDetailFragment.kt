package com.example.doloresapp.presentation.ui

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.fragment.app.Fragment
import com.example.doloresapp.R
import com.example.doloresapp.data.cart.CartRepository
import com.example.doloresapp.domain.model.Producto

class ProductDetailFragment : Fragment(R.layout.libraryproduct_layout) {

    private var producto: Producto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        producto = arguments?.getSerializable(ARG_PRODUCT) as? Producto
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val p = producto ?: return

        // Back
        view.findViewById<View>(R.id.back_arrow)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Bind basic fields
        view.findViewById<TextView>(R.id.productTitle)?.text = p.nombre
        view.findViewById<TextView>(R.id.descriptionText)?.text = p.descripcion
        view.findViewById<TextView>(R.id.concentrationText)?.text = p.concentracion
        view.findViewById<TextView>(R.id.stockText)?.text = "${p.stock} disponibles"
        // If you load images from URL, integrate Glide/Picasso here with p.imagenUrl
        view.findViewById<ImageView>(R.id.productImage)?.setImageResource(R.drawable.ic_medication)

        val oldPrice = view.findViewById<TextView>(R.id.oldPrice)
        val offerPrice = view.findViewById<TextView>(R.id.offerPrice)
        if (p.precioOferta != null) {
            oldPrice?.apply {
                text = "Precio anterior: $${p.precio}"
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                visibility = View.VISIBLE
            }
            offerPrice?.text = "  OFERTA: $${p.precioOferta}"
        } else {
            oldPrice?.visibility = View.GONE
            offerPrice?.text = "$${p.precio}"
        }

        view.findViewById<MaterialButton>(R.id.addToCartButton)?.setOnClickListener {
            CartRepository.add(p)
            // Optionally show a snackbar/toast
        }
    }

    companion object {
        private const val ARG_PRODUCT = "arg_product"
        fun newInstance(producto: Producto): ProductDetailFragment {
            val f = ProductDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_PRODUCT, producto)
            f.arguments = args
            return f
        }
    }
}
