package com.example.doloresapp.presentation.ui

import android.content.Intent
import android.os.Bundle
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
import com.google.android.material.search.SearchBar
import kotlinx.coroutines.launch

class ProductosActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: ProductosAdapter
    private lateinit var apiService: ProductoApiService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)
        
        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Productos"
        
        // Views
        recyclerView = findViewById(R.id.recyclerProductos)
        progressBar = findViewById(R.id.progressBar)
        
        // Setup RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = ProductosAdapter { producto ->
            // Click en producto
            val intent = Intent(this, ProductoDetalleActivity::class.java)
            intent.putExtra("PRODUCTO_ID", producto.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        
        // API Service
        apiService = NetworkClient.createService(ProductoApiService::class.java)
        
        // Cargar productos
        cargarProductos()
    }
    
    private fun cargarProductos() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val productos = apiService.getAllProductos()
                adapter.submitList(productos)
                progressBar.visibility = View.GONE
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ProductosActivity, 
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
