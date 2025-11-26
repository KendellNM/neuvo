package com.example.doloresapp.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.doloresapp.R
import com.example.doloresapp.data.local.TokenStore
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.dto.ProductoDTO
import com.example.doloresapp.data.remote.service.ProductoApiService
import com.example.doloresapp.presentation.adapters.StockAdapter
import kotlinx.coroutines.launch

class GestionStockActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: View
    private lateinit var tvTotalProductos: TextView
    private lateinit var tvStockBajo: TextView
    private lateinit var tvSinStock: TextView
    private lateinit var adapter: StockAdapter
    private lateinit var apiService: ProductoApiService

    private var productos: List<ProductoDTO> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_stock)

        TokenStore.init(applicationContext)
        NetworkClient.init(applicationContext)

        setupToolbar()
        setupViews()
        setupRecyclerView()

        apiService = NetworkClient.createService(ProductoApiService::class.java)
        loadProductos()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recycler_productos)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        progressBar = findViewById(R.id.progress_bar)
        tvTotalProductos = findViewById(R.id.tv_total_productos)
        tvStockBajo = findViewById(R.id.tv_stock_bajo)
        tvSinStock = findViewById(R.id.tv_sin_stock)

        swipeRefresh.setOnRefreshListener { loadProductos() }
        swipeRefresh.setColorSchemeResources(R.color.dolores)
    }

    private fun setupRecyclerView() {
        adapter = StockAdapter { producto -> mostrarDialogoEditarStock(producto) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadProductos() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                productos = apiService.getAllProductos()
                
                // Ordenar por stock (menor primero)
                val ordenados = productos.sortedBy { it.stock }
                adapter.submitList(ordenados)
                
                // Actualizar resumen
                actualizarResumen()
                
            } catch (e: Exception) {
                Toast.makeText(this@GestionStockActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun actualizarResumen() {
        val total = productos.size
        val sinStock = productos.count { it.stock <= 0 }
        val stockBajo = productos.count { 
            val min = it.stockMin ?: 10
            it.stock in 1..min 
        }
        
        tvTotalProductos.text = total.toString()
        tvSinStock.text = sinStock.toString()
        tvStockBajo.text = stockBajo.toString()
    }

    private fun mostrarDialogoEditarStock(producto: ProductoDTO) {
        val input = EditText(this).apply {
            hint = "Nuevo stock"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(producto.stock.toString())
        }
        input.setPadding(50, 30, 50, 30)

        AlertDialog.Builder(this)
            .setTitle("Editar Stock")
            .setMessage("Producto: ${producto.nombre}\nStock actual: ${producto.stock}")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoStock = input.text.toString().toIntOrNull()
                if (nuevoStock != null && nuevoStock >= 0) {
                    actualizarStock(producto, nuevoStock)
                } else {
                    Toast.makeText(this, "Stock inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .setNeutralButton("+10") { _, _ ->
                actualizarStock(producto, producto.stock + 10)
            }
            .show()
    }

    private fun actualizarStock(producto: ProductoDTO, nuevoStock: Int) {
        lifecycleScope.launch {
            try {
                // Llamar al endpoint de actualización
                apiService.actualizarStock(producto.id, nuevoStock)
                Toast.makeText(this@GestionStockActivity, 
                    "✅ Stock actualizado: ${producto.nombre} → $nuevoStock", Toast.LENGTH_SHORT).show()
                loadProductos()
            } catch (e: Exception) {
                Toast.makeText(this@GestionStockActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
