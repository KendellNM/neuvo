package com.example.doloresapp.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.doloresapp.R
import com.example.doloresapp.data.local.database.AppDatabase
import com.example.doloresapp.data.local.entity.CarritoItem
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.dto.ProductoDTO
import com.example.doloresapp.data.remote.service.ProductoApiService
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ProductoDetalleActivity : AppCompatActivity() {
    
    private lateinit var apiService: ProductoApiService
    private lateinit var database: AppDatabase
    private var productoId: Long = 0
    private var cantidad: Int = 1
    private var productoActual: ProductoDTO? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto_detalle)
        
        productoId = intent.getLongExtra("PRODUCTO_ID", 0)
        
        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalle del Producto"
        
        apiService = NetworkClient.createService(ProductoApiService::class.java)
        database = AppDatabase.getDatabase(this)
        
        cargarProducto()
        setupCantidadControls()
    }
    
    private fun cargarProducto() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val producto = apiService.getProductoById(productoId)
                
                // Mostrar datos
                findViewById<TextView>(R.id.tvNombre).text = producto.nombre
                findViewById<TextView>(R.id.tvDescripcion).text = producto.descripcion
                findViewById<TextView>(R.id.tvPrecio).text = "S/ ${producto.precio}"
                findViewById<TextView>(R.id.tvStock).text = "Stock disponible: ${producto.stock}"
                findViewById<TextView>(R.id.tvCategoria).text = "Categoría: ${producto.categoria?.nombre ?: "Sin categoría"}"
                
                // Imagen
                Glide.with(this@ProductoDetalleActivity)
                    .load(producto.imagen_url)
                    .placeholder(R.drawable.ic_producto_placeholder)
                    .into(findViewById(R.id.imgProducto))
                
                productoActual = producto
                
                // Botón agregar al carrito
                findViewById<MaterialButton>(R.id.btnAgregarCarrito).setOnClickListener {
                    agregarAlCarrito()
                }
                
                progressBar.visibility = View.GONE
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@ProductoDetalleActivity, 
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupCantidadControls() {
        val tvCantidad = findViewById<TextView>(R.id.tvCantidad)
        val btnMenos = findViewById<ImageButton>(R.id.btnMenos)
        val btnMas = findViewById<ImageButton>(R.id.btnMas)
        
        tvCantidad.text = cantidad.toString()
        
        btnMenos.setOnClickListener {
            if (cantidad > 1) {
                cantidad--
                tvCantidad.text = cantidad.toString()
            }
        }
        
        btnMas.setOnClickListener {
            cantidad++
            tvCantidad.text = cantidad.toString()
        }
    }
    
    private fun agregarAlCarrito() {
        val producto = productoActual ?: return
        
        lifecycleScope.launch {
            try {
                val existente = database.carritoDao().getItemByProductoId(producto.id)
                
                if (existente != null) {
                    // Actualizar cantidad
                    database.carritoDao().updateItem(
                        existente.copy(cantidad = existente.cantidad + cantidad)
                    )
                } else {
                    // Agregar nuevo
                    database.carritoDao().insertItem(
                        CarritoItem(
                            productoId = producto.id,
                            nombre = producto.nombre,
                            precio = producto.precio,
                            cantidad = cantidad,
                            imagenUrl = producto.imagen_url
                        )
                    )
                }
                
                Toast.makeText(this@ProductoDetalleActivity, 
                    "✅ Agregado al carrito: $cantidad unidad(es)", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ProductoDetalleActivity, 
                    "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
