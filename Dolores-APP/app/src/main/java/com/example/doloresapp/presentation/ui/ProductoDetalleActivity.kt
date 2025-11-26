package com.example.doloresapp.presentation.ui

import android.graphics.Bitmap
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
import com.google.android.material.card.MaterialCardView
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
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
                
                // Mostrar QR del producto
                // Primero intentar cargar la imagen del backend, si no existe, generar localmente
                if (!producto.qrImageUrl.isNullOrEmpty()) {
                    mostrarQRDesdeUrl(producto.qrImageUrl, producto.codigoBarras)
                } else if (!producto.codigoBarras.isNullOrEmpty()) {
                    mostrarQR(producto.codigoBarras)
                }
                
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
    
    private fun mostrarQRDesdeUrl(qrUrl: String, codigoBarras: String?) {
        try {
            val cardQR = findViewById<MaterialCardView>(R.id.cardQR)
            val imgQR = findViewById<ImageView>(R.id.imgQR)
            val tvCodigoBarras = findViewById<TextView>(R.id.tvCodigoBarras)
            
            // Construir URL completa si es relativa
            val fullUrl = if (qrUrl.startsWith("http")) qrUrl 
                else com.example.doloresapp.utils.Constants.BASE_URL.trimEnd('/') + qrUrl
            
            // Cargar imagen del backend
            Glide.with(this)
                .load(fullUrl)
                .placeholder(R.drawable.ic_producto_placeholder)
                .into(imgQR)
            
            tvCodigoBarras.text = "Código: ${codigoBarras ?: "N/A"}"
            cardQR.visibility = View.VISIBLE
        } catch (e: Exception) {
            // Si falla, intentar generar localmente
            codigoBarras?.let { mostrarQR(it) }
        }
    }
    
    private fun mostrarQR(codigoBarras: String) {
        try {
            val cardQR = findViewById<MaterialCardView>(R.id.cardQR)
            val imgQR = findViewById<ImageView>(R.id.imgQR)
            val tvCodigoBarras = findViewById<TextView>(R.id.tvCodigoBarras)
            
            // Generar QR localmente
            val qrBitmap = generarQRCode(codigoBarras, 300)
            
            if (qrBitmap != null) {
                imgQR.setImageBitmap(qrBitmap)
                tvCodigoBarras.text = "Código: $codigoBarras"
                cardQR.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            // Si falla, simplemente no mostrar el QR
        }
    }
    
    private fun generarQRCode(contenido: String, size: Int): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(contenido, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
