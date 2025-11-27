package com.example.doloresapp.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.service.ProductoApiService
import com.example.doloresapp.presentation.adapter.ProductosAdapter
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class ProductosActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: ProductosAdapter
    private lateinit var apiService: ProductoApiService
    private var allProductos = listOf<com.example.doloresapp.data.remote.dto.ProductoDTO>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)
        
        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Productos"
        toolbar.setNavigationOnClickListener { onBackPressed() }
        
        // Views
        recyclerView = findViewById(R.id.recyclerProductos)
        progressBar = findViewById(R.id.progressBar)
        
        // FAB Carrito
        findViewById<com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton>(R.id.fabCarrito)?.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }
        
        // Búsqueda
        val etBuscar = findViewById<android.widget.EditText>(R.id.etBuscar)
        etBuscar?.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                filtrarProductos(s?.toString() ?: "")
            }
        })
        
        // Setup RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = ProductosAdapter { producto ->
            val intent = Intent(this, ProductoDetalleActivity::class.java)
            intent.putExtra("PRODUCTO_ID", producto.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        
        // API Service
        apiService = NetworkClient.createService(ProductoApiService::class.java)
        
        cargarProductos()
    }
    
    private fun filtrarProductos(query: String) {
        val filtered = if (query.isEmpty()) {
            allProductos
        } else {
            allProductos.filter { 
                it.nombre.contains(query, ignoreCase = true) ||
                it.descripcion?.contains(query, ignoreCase = true) == true
            }
        }
        adapter.submitList(filtered)
    }
    
    private fun cargarProductos() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val productos = apiService.getAllProductos()
                allProductos = productos
                adapter.submitList(productos)
                progressBar.visibility = View.GONE
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ProductosActivity, 
                    "Error al cargar productos", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
