package com.example.doloresapp.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.api.CategoriaApiService
import com.example.doloresapp.data.remote.dto.CategoriaDTO
import com.example.doloresapp.data.remote.dto.ProductoDTO
import com.example.doloresapp.data.remote.service.ProductoApiService
import com.example.doloresapp.presentation.adapter.ProductosAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ProductosActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: ProductosAdapter
    private lateinit var apiService: ProductoApiService
    private lateinit var categoriaApiService: CategoriaApiService
    private lateinit var etBuscar: EditText
    private lateinit var spinnerCategoria: AutoCompleteTextView
    private lateinit var btnLimpiarFiltro: MaterialButton
    
    private var allProductos = listOf<ProductoDTO>()
    private var categorias = listOf<CategoriaDTO>()
    private var categoriaSeleccionada: CategoriaDTO? = null
    
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
        etBuscar = findViewById(R.id.etBuscar)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        btnLimpiarFiltro = findViewById(R.id.btnLimpiarFiltro)
        
        // FAB Carrito
        findViewById<com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton>(R.id.fabCarrito)?.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }
        
        // Setup RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = ProductosAdapter { producto ->
            val intent = Intent(this, ProductoDetalleActivity::class.java)
            intent.putExtra("PRODUCTO_ID", producto.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        
        // API Services
        apiService = NetworkClient.createService(ProductoApiService::class.java)
        categoriaApiService = NetworkClient.createService(CategoriaApiService::class.java)
        
        setupSearch()
        setupCategoriaFilter()
        
        cargarCategorias()
        cargarProductos()
    }

    private fun setupSearch() {
        etBuscar.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                aplicarFiltros()
            }
        })
    }
    
    private fun setupCategoriaFilter() {
        spinnerCategoria.setOnItemClickListener { _, _, position, _ ->
            categoriaSeleccionada = if (position == 0) null else categorias[position - 1]
            aplicarFiltros()
        }
        
        btnLimpiarFiltro.setOnClickListener {
            etBuscar.text.clear()
            spinnerCategoria.setText("Todas", false)
            categoriaSeleccionada = null
            aplicarFiltros()
        }
    }
    
    private fun cargarCategorias() {
        lifecycleScope.launch {
            try {
                // Usar repositorio offline para categorías
                val offlineRepo = com.example.doloresapp.di.ServiceLocator.getOfflineRepository(this@ProductosActivity)
                val categoriasDomain = offlineRepo.getCategorias()
                categorias = categoriasDomain.map { cat ->
                    CategoriaDTO(id = cat.id, nombre = cat.nombre)
                }
                setupCategoriaSpinner()
            } catch (e: Exception) {
                // Si falla, continuar sin filtro de categorías
            }
        }
    }
    
    private fun setupCategoriaSpinner() {
        val nombres = mutableListOf("Todas")
        nombres.addAll(categorias.map { it.nombre })
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nombres)
        spinnerCategoria.setAdapter(adapter)
    }
    
    private fun aplicarFiltros() {
        val query = etBuscar.text.toString()
        
        val filtered = allProductos.filter { producto ->
            val matchesText = query.isEmpty() || 
                producto.nombre.contains(query, ignoreCase = true) ||
                producto.descripcion?.contains(query, ignoreCase = true) == true
            
            val matchesCategory = categoriaSeleccionada == null || 
                producto.categoria?.id == categoriaSeleccionada?.id
            
            matchesText && matchesCategory
        }
        adapter.submitList(filtered)
    }
    
    private fun cargarProductos() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // Usar repositorio offline para productos (con cache SQLite)
                val offlineRepo = com.example.doloresapp.di.ServiceLocator.getOfflineRepository(this@ProductosActivity)
                val productosDomain = offlineRepo.getProductos()
                
                // Convertir de domain a DTO
                allProductos = productosDomain.map { p ->
                    ProductoDTO(
                        id = p.id,
                        nombre = p.nombre,
                        descripcion = p.descripcion,
                        precio = p.precio,
                        concentracion = p.concentracion,
                        precioOferta = p.precioOferta,
                        imagen_url = p.imagenUrl,
                        stock = p.stock,
                        stockMin = null,
                        principioActivo = null,
                        requerireReceta = false,
                        codigoBarras = null,
                        qrImageUrl = null,
                        categoria = null
                    )
                }
                adapter.submitList(allProductos)
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
