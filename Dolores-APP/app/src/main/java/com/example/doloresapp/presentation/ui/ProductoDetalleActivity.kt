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
        
        apiService = NetworkClient.createService(ProductoApiService::class.java)
        database = AppDatabase.getDatabase(this)
        
        setupUI()
        cargarProducto()
    }
    
    private fun setupUI() {
        // Botón volver
        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }
        
        // Botón favorito (solo visual por ahora)
        findViewById<View>(R.id.btnFavorito)?.setOnClickListener {
            Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
        }
        
        // Controles de cantidad
        val tvCantidad = findViewById<TextView>(R.id.tvCantidad)
        val btnMenos = findViewById<ImageButton>(R.id.btnMenos)
        val btnMas = findViewById<ImageButton>(R.id.btnMas)
        
        tvCantidad?.text = cantidad.toString()
        
        btnMenos?.setOnClickListener {
            if (cantidad > 1) {
                cantidad--
                tvCantidad?.text = cantidad.toString()
            }
        }
        
        btnMas?.setOnClickListener {
            cantidad++
            tvCantidad?.text = cantidad.toString()
        }
        
        // Botón agregar al carrito
        findViewById<MaterialButton>(R.id.btnAgregarCarrito)?.setOnClickListener {
            agregarAlCarrito()
        }
    }
    
    private fun cargarProducto() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar?.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                // Usar repositorio offline para obtener producto
                val offlineRepo = com.example.doloresapp.di.ServiceLocator.getOfflineRepository(this@ProductoDetalleActivity)
                val productoDomain = offlineRepo.getProductoById(productoId)
                
                if (productoDomain != null) {
                    // Convertir de domain a DTO
                    val producto = ProductoDTO(
                        id = productoDomain.id,
                        nombre = productoDomain.nombre,
                        descripcion = productoDomain.descripcion,
                        precio = productoDomain.precio,
                        concentracion = productoDomain.concentracion,
                        precioOferta = productoDomain.precioOferta,
                        imagen_url = productoDomain.imagenUrl,
                        stock = productoDomain.stock,
                        stockMin = null,
                        principioActivo = null,
                        requerireReceta = false,
                        codigoBarras = null,
                        qrImageUrl = null,
                        categoria = null
                    )
                    productoActual = producto
                    mostrarProducto(producto)
                    progressBar?.visibility = View.GONE
                } else {
                    progressBar?.visibility = View.GONE
                    Toast.makeText(this@ProductoDetalleActivity, 
                        "Producto no encontrado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                progressBar?.visibility = View.GONE
                Toast.makeText(this@ProductoDetalleActivity, 
                    "Error al cargar producto", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    
    private fun mostrarProducto(producto: ProductoDTO) {
        // Nombre
        findViewById<TextView>(R.id.tvNombre)?.text = producto.nombre
        
        // Precio
        findViewById<TextView>(R.id.tvPrecio)?.text = "S/ %.2f".format(producto.precio)
        
        // Categoría
        findViewById<TextView>(R.id.tvCategoria)?.text = 
            producto.categoria?.nombre?.uppercase() ?: "MEDICAMENTO"
        
        // Descripción
        findViewById<TextView>(R.id.tvDescripcion)?.text = 
            producto.descripcion ?: "Producto de alta calidad para el cuidado de tu salud."
        
        // Stock
        findViewById<TextView>(R.id.tvStock)?.text = "• Stock: ${producto.stock} unidades"
        
        // Rating (simulado)
        findViewById<TextView>(R.id.tvRating)?.text = "4.8"
        
        // Badge de receta
        val tvReceta = findViewById<TextView>(R.id.tvRequiereReceta)
        tvReceta?.visibility = if (producto.requerireReceta == true) View.VISIBLE else View.GONE
        
        // Imagen
        val imgProducto = findViewById<ImageView>(R.id.imgProducto)
        if (!producto.imagen_url.isNullOrEmpty()) {
            Glide.with(this)
                .load(producto.imagen_url)
                .placeholder(R.drawable.ic_medication)
                .error(R.drawable.ic_medication)
                .into(imgProducto)
        } else {
            imgProducto?.setImageResource(R.drawable.ic_medication)
            imgProducto?.setColorFilter(getColor(R.color.primary))
            imgProducto?.setPadding(80, 80, 80, 80)
        }
        
        // QR Code
        if (!producto.qrImageUrl.isNullOrEmpty() || !producto.codigoBarras.isNullOrEmpty()) {
            mostrarQR(producto)
        }
    }
    
    private fun mostrarQR(producto: ProductoDTO) {
        val cardQR = findViewById<MaterialCardView>(R.id.cardQR)
        val imgQR = findViewById<ImageView>(R.id.imgQR)
        val tvCodigo = findViewById<TextView>(R.id.tvCodigoBarras)
        
        if (!producto.qrImageUrl.isNullOrEmpty()) {
            // Cargar QR desde URL
            val fullUrl = if (producto.qrImageUrl.startsWith("http")) producto.qrImageUrl
                else com.example.doloresapp.utils.Constants.BASE_URL.trimEnd('/') + producto.qrImageUrl
            
            Glide.with(this)
                .load(fullUrl)
                .placeholder(R.drawable.ic_producto_placeholder)
                .into(imgQR!!)
            
            tvCodigo?.text = "Código: ${producto.codigoBarras ?: "N/A"}"
            cardQR?.visibility = View.VISIBLE
        } else if (!producto.codigoBarras.isNullOrEmpty()) {
            // Generar QR localmente
            val qrBitmap = generarQRCode(producto.codigoBarras, 300)
            if (qrBitmap != null) {
                imgQR?.setImageBitmap(qrBitmap)
                tvCodigo?.text = "Código: ${producto.codigoBarras}"
                cardQR?.visibility = View.VISIBLE
            }
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
    
    private fun agregarAlCarrito() {
        val producto = productoActual ?: return
        
        // Verificar si requiere receta
        if (producto.requerireReceta == true) {
            Toast.makeText(this, 
                "⚠️ Este producto requiere receta médica", 
                Toast.LENGTH_LONG).show()
        }
        
        lifecycleScope.launch {
            try {
                val existente = database.carritoDao().getItemByProductoId(producto.id)
                
                if (existente != null) {
                    database.carritoDao().updateItem(
                        existente.copy(cantidad = existente.cantidad + cantidad)
                    )
                } else {
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
                    "✅ Agregado al carrito: $cantidad unidad(es)", 
                    Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ProductoDetalleActivity, 
                    "Error al agregar al carrito", 
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}
