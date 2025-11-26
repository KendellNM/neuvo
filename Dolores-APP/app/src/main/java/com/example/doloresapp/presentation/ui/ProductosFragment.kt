package com.example.doloresapp.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doloresapp.R
import com.example.doloresapp.presentation.viewmodel.ProductosViewModel
import com.example.doloresapp.presentation.adapters.EnhancedProductosAdapter
import com.example.doloresapp.di.ServiceLocator
import com.example.doloresapp.presentation.viewmodel.ProductosViewModelFactory
import android.util.Log
import android.widget.Toast
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.doloresapp.domain.model.Producto
import android.view.ViewGroup
import com.example.doloresapp.data.cart.CartRepository

class ProductosFragment : Fragment(R.layout.listproducts_layout) {
    private val productosViewModel: ProductosViewModel by viewModels {
        ProductosViewModelFactory(
            ServiceLocator.getProductosUseCase(),
            ServiceLocator.getCategoriasUseCase()
        )
    }


    private fun openProductDetail(producto: Producto) {
        val fragment = ProductDetailFragment.newInstance(producto)
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private lateinit var adapter: EnhancedProductosAdapter
    private var allProducts: List<Producto> = emptyList()
    private var selectedCategoryId: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Log.d("ProductosFragment", "onViewCreated(): iniciado")
        // Configuración del RecyclerView
        adapter = EnhancedProductosAdapter(
            productos = emptyList(),
            onItemClick = { producto -> openProductDetail(producto) },
            onAddToCartClick = { producto ->
                CartRepository.add(producto)
                Toast.makeText(requireContext(), "Añadido al carrito", Toast.LENGTH_SHORT).show()
            }
        )
        val recyclerView: RecyclerView = view.findViewById(R.id.products_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Back arrow: regresar al Home
        view.findViewById<View>(R.id.back_arrow)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Search real-time
        val searchEditText: EditText = view.findViewById(R.id.search_edittext)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAndUpdate()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Observar los productos
        productosViewModel.productos.observe(viewLifecycleOwner) { productos ->
            Log.d("ProductosFragment", "observer productos: tamaño=${'$'}{productos.size}")
            allProducts = productos
            filterAndUpdate() // actualizar lista + contador
        }

        // Observar las categorías si es necesario
        productosViewModel.categorias.observe(viewLifecycleOwner) { categorias ->
            // Aquí puedes manejar las categorías si las estás utilizando para algún filtro
            Log.d("ProductosFragment", "observer categorias: tamaño=${'$'}{categorias.size}")
            val container: LinearLayout? = view.findViewById(R.id.category_container)
            container?.let { ll ->
                ll.removeAllViews()

                // Chip "Todos"
                ll.addView(createCategoryChip(label = "Todos") {
                    selectedCategoryId = null
                    filterAndUpdate()
                })

                categorias.forEach { cat ->
                    ll.addView(createCategoryChip(label = cat.nombre) {
                        selectedCategoryId = cat.id
                        filterAndUpdate()
                    })
                }
            }
        }

        // Click en "Mis Pedidos" -> navegar al CartFragment (carrito)
        view.findViewById<View>(R.id.cart_fab)?.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, CartFragment())
                .addToBackStack(null)
                .commit()
        }

        // Llamar a los métodos para cargar productos y categorías
        Log.d("ProductosFragment", "Solicitando carga de productos y categorias...")
        productosViewModel.loadProductos()  // Cargar productos
        productosViewModel.loadCategorias()  // Cargar categorías
    }

    private fun createCategoryChip(label: String, onClick: () -> Unit): TextView {
        val tv = TextView(requireContext())
        val padH = dp(12)
        val padV = dp(8)
        tv.text = label
        tv.setPadding(padH, padV, padH, padV)
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.dolores))
        tv.textSize = 14f
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(36))
        lp.setMargins(0, 0, dp(8), 0)
        tv.layoutParams = lp
        // Se puede añadir un fondo custom más adelante; por ahora, sin fondo para evitar dependencias
        tv.setOnClickListener { onClick() }
        return tv
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun filterAndUpdate() {
        val countView: TextView? = view?.findViewById(R.id.products_count)
        val query = view?.findViewById<EditText>(R.id.search_edittext)?.text?.toString()?.trim().orEmpty()

        val filtered = allProducts.filter { p ->
            val byText = if (query.isBlank()) true else p.nombre.contains(query, ignoreCase = true) || (p.descripcion?.contains(query, ignoreCase = true) ?: false)
            val byCategory = selectedCategoryId?.let { cid -> p.categoriaId == cid } ?: true
            byText && byCategory
        }

        adapter.submitList(filtered)
        countView?.text = "${filtered.size} Productos Encontrados"

        if (allProducts.isNotEmpty() && filtered.isEmpty()) {
            // No mostrar Toast si prefieres solo el contador; se deja comentado.
            // Toast.makeText(requireContext(), "No se encontraron productos", Toast.LENGTH_SHORT).show()
            Log.d("ProductosFragment", "filterAndUpdate(): sin resultados para query='$query' categoria=${selectedCategoryId}")
        }
    }
}