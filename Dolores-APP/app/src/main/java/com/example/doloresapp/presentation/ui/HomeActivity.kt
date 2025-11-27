package com.example.doloresapp.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.doloresapp.LoginActivity
import com.example.doloresapp.R
import com.example.doloresapp.data.local.TokenStore
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.UserApi
import com.example.doloresapp.utils.ApiConstants
import com.example.doloresapp.utils.RoleManager
import com.example.doloresapp.utils.UserRole
import kotlinx.coroutines.launch

/**
 * Activity principal que muestra opciones según el rol del usuario
 */
class HomeActivity : AppCompatActivity() {
    
    private lateinit var tvBienvenida: TextView
    private lateinit var tvRol: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Asegurar que NetworkClient y CartRepository estén inicializados
        TokenStore.init(applicationContext)
        NetworkClient.init(applicationContext)
        com.example.doloresapp.data.cart.CartRepository.init(applicationContext)
        
        val userRole = RoleManager.getUserRole(this)
        
        // Cargar layout según rol
        when (userRole) {
            UserRole.CLIENTE -> setupClienteUI()
            UserRole.REPARTIDOR -> setupRepartidorUI()
            UserRole.ADMIN -> setupAdminUI()
            UserRole.FARMACEUTICO -> setupFarmaceuticoUI()
        }
        
        // Cargar nombre del usuario desde el backend
        loadUserName()
    }
    
    private fun loadUserName() {
        lifecycleScope.launch {
            try {
                val api = NetworkClient.createService(UserApi::class.java)
                val user = api.getCurrentUser()
                
                // Obtener el nombre a mostrar
                val displayName = when {
                    !user.nombres.isNullOrBlank() && !user.apellidos.isNullOrBlank() -> 
                        "${user.nombres} ${user.apellidos}"
                    !user.nombres.isNullOrBlank() -> user.nombres
                    !user.usuario.isNullOrBlank() -> user.usuario.replaceFirstChar { it.uppercase() }
                    !user.username.isNullOrBlank() -> user.username.replaceFirstChar { it.uppercase() }
                    else -> null
                }
                
                displayName?.let {
                    tvBienvenida.text = "Hola, $it"
                }
            } catch (e: Exception) {
                // Si falla, mantener el nombre extraído del email
            }
        }
    }
    
    private fun setupClienteUI() {
        setContentView(R.layout.activity_home_cliente)
        setupEdgeToEdge(R.id.cliente_root)
        
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        tvRol = findViewById(R.id.tv_rol)
        
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        val userEmail = prefs.getString(ApiConstants.Prefs.USER_EMAIL, "Usuario") ?: "Usuario"
        val displayName = extractNameFromEmail(userEmail)
        
        tvBienvenida.text = "Farmacia Dolores"
        
        // Categorías
        findViewById<View>(R.id.cat_productos)?.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }
        findViewById<View>(R.id.cat_carrito)?.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }
        findViewById<View>(R.id.cat_pedidos)?.setOnClickListener {
            startActivity(Intent(this, MisPedidosActivity::class.java))
        }
        findViewById<View>(R.id.cat_perfil)?.setOnClickListener {
            showLogoutDialog()
        }
        
        // Banner escanear
        findViewById<View>(R.id.btn_escanear)?.setOnClickListener {
            startActivity(Intent(this, QRScannerActivity::class.java))
        }
        
        // Barra de búsqueda
        findViewById<View>(R.id.card_buscar)?.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }
        
        // Carrito header
        findViewById<View>(R.id.btn_carrito)?.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }
        
        // Ver todos productos
        findViewById<View>(R.id.btn_ver_todos)?.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }
        
        // Bottom navigation
        findViewById<View>(R.id.tab_tienda)?.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }
        findViewById<View>(R.id.tab_pedidos)?.setOnClickListener {
            startActivity(Intent(this, MisPedidosActivity::class.java))
        }
        findViewById<View>(R.id.fab_escanear)?.setOnClickListener {
            startActivity(Intent(this, QRScannerActivity::class.java))
        }
        findViewById<View>(R.id.tab_carrito)?.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }
        findViewById<View>(R.id.tab_logout)?.setOnClickListener {
            showLogoutDialog()
        }
        
        // Cargar productos
        cargarProductosCliente()
    }
    
    private fun cargarProductosCliente() {
        val recyclerDestacados = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_destacados)
        val recyclerEsenciales = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_esenciales)
        
        if (recyclerDestacados == null || recyclerEsenciales == null) return
        
        // Adapters
        val adapterDestacados = com.example.doloresapp.presentation.adapter.ProductoDestacadoAdapter(
            onProductoClick = { producto ->
                val intent = Intent(this, ProductoDetalleActivity::class.java)
                intent.putExtra("PRODUCTO_ID", producto.id)
                startActivity(intent)
            },
            onAgregarClick = { producto ->
                agregarAlCarrito(producto)
            }
        )
        
        val adapterEsenciales = com.example.doloresapp.presentation.adapter.ProductoEsencialAdapter(
            onProductoClick = { producto ->
                val intent = Intent(this, ProductoDetalleActivity::class.java)
                intent.putExtra("PRODUCTO_ID", producto.id)
                startActivity(intent)
            },
            onAgregarClick = { producto ->
                agregarAlCarrito(producto)
            }
        )
        
        recyclerDestacados.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false
        )
        recyclerDestacados.adapter = adapterDestacados
        
        recyclerEsenciales.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerEsenciales.adapter = adapterEsenciales
        
        // Cargar productos desde API
        lifecycleScope.launch {
            try {
                val api = NetworkClient.createService(com.example.doloresapp.data.remote.service.ProductoApiService::class.java)
                val productos = api.getAllProductos()
                
                // Primeros 5 para destacados
                adapterDestacados.submitList(productos.take(5))
                // Siguientes 5 para esenciales
                adapterEsenciales.submitList(productos.drop(5).take(5))
            } catch (e: Exception) {
                // Silenciar error
            }
        }
    }
    
    private fun agregarAlCarrito(producto: com.example.doloresapp.data.remote.dto.ProductoDTO) {
        lifecycleScope.launch {
            try {
                val database = com.example.doloresapp.data.local.database.AppDatabase.getDatabase(this@HomeActivity)
                val existente = database.carritoDao().getItemByProductoId(producto.id)
                
                if (existente != null) {
                    database.carritoDao().updateItem(existente.copy(cantidad = existente.cantidad + 1))
                } else {
                    database.carritoDao().insertItem(
                        com.example.doloresapp.data.local.entity.CarritoItem(
                            productoId = producto.id,
                            nombre = producto.nombre,
                            precio = producto.precio,
                            cantidad = 1,
                            imagenUrl = producto.imagen_url
                        )
                    )
                }
                android.widget.Toast.makeText(this@HomeActivity, "✅ Agregado al carrito", android.widget.Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                android.widget.Toast.makeText(this@HomeActivity, "Error al agregar", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupRepartidorUI() {
        setContentView(R.layout.activity_home_repartidor)
        setupEdgeToEdge(R.id.repartidor_root)
        
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        tvRol = findViewById(R.id.tv_rol)
        
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        val userEmail = prefs.getString(ApiConstants.Prefs.USER_EMAIL, "Repartidor") ?: "Repartidor"
        val displayName = extractNameFromEmail(userEmail)
        
        tvBienvenida.text = "Hola, $displayName"
        tvRol.text = "Farmacia Dolores"
        
        // Botón logout
        findViewById<View>(R.id.btn_logout)?.setOnClickListener { showLogoutDialog() }
        
        // Cards del grid - Pedidos asignados
        findViewById<View>(R.id.card_asignados)?.setOnClickListener {
            startActivity(Intent(this, PedidosAsignadosActivity::class.java))
        }
        
        // Entrega activa - Ver pedidos en camino
        findViewById<View>(R.id.card_en_camino)?.setOnClickListener {
            val intent = Intent(this, PedidosAsignadosActivity::class.java)
            intent.putExtra("tab_inicial", 1) // Tab "En Camino"
            startActivity(intent)
        }
        
        // Historial de entregas
        findViewById<View>(R.id.card_historial)?.setOnClickListener {
            val intent = Intent(this, PedidosAsignadosActivity::class.java)
            intent.putExtra("tab_inicial", 2) // Tab "Entregados"
            startActivity(intent)
        }
        
        // Cargar contadores de pedidos
        cargarContadoresRepartidor()
    }
    
    private fun cargarContadoresRepartidor() {
        val tvAsignados = findViewById<TextView>(R.id.tv_count_asignados)
        val tvEnCamino = findViewById<TextView>(R.id.tv_count_en_camino)
        val tvEntregados = findViewById<TextView>(R.id.tv_count_entregados)
        
        lifecycleScope.launch {
            try {
                val userApi = NetworkClient.createService(UserApi::class.java)
                val currentUser = userApi.getCurrentUser()
                val repartidorId = currentUser.repartidorId
                
                if (repartidorId != null) {
                    val pedidosApi = NetworkClient.createService(com.example.doloresapp.data.remote.PedidosApi::class.java)
                    val pedidos = pedidosApi.getPedidosByRepartidor(repartidorId)
                    
                    val asignados = pedidos.count { it.estado?.uppercase() == "ASIGNADO" }
                    val enCamino = pedidos.count { it.estado?.uppercase() == "EN_CAMINO" }
                    val entregados = pedidos.count { it.estado?.uppercase() == "ENTREGADO" }
                    
                    runOnUiThread {
                        tvAsignados?.text = asignados.toString()
                        tvEnCamino?.text = enCamino.toString()
                        tvEntregados?.text = entregados.toString()
                    }
                }
            } catch (e: Exception) {
                // Si falla, mantener los valores en 0
                e.printStackTrace()
            }
        }
    }
    
    private fun setupAdminUI() {
        setContentView(R.layout.activity_home_admin)
        setupEdgeToEdge(R.id.admin_root)
        
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        tvRol = findViewById(R.id.tv_rol)
        
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        val userEmail = prefs.getString(ApiConstants.Prefs.USER_EMAIL, "Admin") ?: "Admin"
        val displayName = extractNameFromEmail(userEmail)
        
        tvBienvenida.text = "Hola, $displayName"
        tvRol.text = "Farmacia Dolores"
        
        // Botón logout
        findViewById<View>(R.id.btn_logout)?.setOnClickListener { showLogoutDialog() }
        
        // Cards del grid - Gestionar productos
        findViewById<View>(R.id.card_productos)?.setOnClickListener {
            startActivity(Intent(this, GestionProductosActivity::class.java))
        }
        
        // Ver pedidos
        findViewById<View>(R.id.card_pedidos)?.setOnClickListener {
            startActivity(Intent(this, GestionPedidosActivity::class.java))
        }
        
        // Gestionar usuarios
        findViewById<View>(R.id.card_usuarios)?.setOnClickListener {
            startActivity(Intent(this, GestionUsuariosActivity::class.java))
        }
        
        // Control de stock
        findViewById<View>(R.id.card_stock)?.setOnClickListener {
            startActivity(Intent(this, GestionStockActivity::class.java))
        }
        
        // Cargar contadores
        cargarContadoresAdmin()
    }
    
    private fun cargarContadoresAdmin() {
        val tvProductos = findViewById<TextView>(R.id.tv_count_productos)
        val tvPedidos = findViewById<TextView>(R.id.tv_count_pedidos)
        val tvUsuarios = findViewById<TextView>(R.id.tv_count_usuarios)
        val tvStockBajo = findViewById<TextView>(R.id.tv_count_stock_bajo)
        
        lifecycleScope.launch {
            try {
                val productosApi = NetworkClient.createService(com.example.doloresapp.data.remote.service.ProductoApiService::class.java)
                val productos = productosApi.getAllProductos()
                val stockBajo = productos.count { (it.stock ?: 0) < (it.stockMin ?: 10) }
                
                runOnUiThread {
                    tvProductos?.text = productos.size.toString()
                    tvStockBajo?.text = stockBajo.toString()
                }
            } catch (e: Exception) { e.printStackTrace() }
            
            try {
                val pedidosApi = NetworkClient.createService(com.example.doloresapp.data.remote.PedidosApi::class.java)
                val pedidos = pedidosApi.getAllPedidos()
                val pendientes = pedidos.count { it.estado?.uppercase() in listOf("PENDIENTE", "EN_PREPARACION", "LISTO") }
                
                runOnUiThread {
                    tvPedidos?.text = pendientes.toString()
                }
            } catch (e: Exception) { e.printStackTrace() }
            
            try {
                val adminApi = NetworkClient.createService(com.example.doloresapp.data.remote.AdminApi::class.java)
                val usuarios = adminApi.getAllUsuarios()
                
                runOnUiThread {
                    tvUsuarios?.text = usuarios.size.toString()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
    
    private fun setupFarmaceuticoUI() {
        setContentView(R.layout.activity_home_farmaceutico)
        setupEdgeToEdge(R.id.farmaceutico_root)
        
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        tvRol = findViewById(R.id.tv_rol)
        
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        val userEmail = prefs.getString(ApiConstants.Prefs.USER_EMAIL, "Farmacéutico") ?: "Farmacéutico"
        val displayName = extractNameFromEmail(userEmail)
        
        tvBienvenida.text = "Hola, $displayName"
        tvRol.text = "Farmacia Dolores"
        
        // Botón logout
        findViewById<View>(R.id.btn_logout)?.setOnClickListener { showLogoutDialog() }
        
        // Cards del grid - Venta presencial
        findViewById<View>(R.id.card_venta)?.setOnClickListener {
            startActivity(Intent(this, VentaPresencialActivity::class.java))
        }
        
        // Validar recetas pendientes
        findViewById<View>(R.id.card_recetas)?.setOnClickListener {
            startActivity(Intent(this, RecetasPendientesActivity::class.java))
        }
        
        // Ver inventario de productos
        findViewById<View>(R.id.card_inventario)?.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }
        
        // Control de stock
        findViewById<View>(R.id.card_stock)?.setOnClickListener {
            startActivity(Intent(this, GestionStockActivity::class.java))
        }
        
        // Cargar contadores
        cargarContadoresFarmaceutico()
    }
    
    private fun cargarContadoresFarmaceutico() {
        val tvRecetas = findViewById<TextView>(R.id.tv_count_recetas)
        val badgeRecetas = findViewById<TextView>(R.id.badge_recetas)
        
        lifecycleScope.launch {
            try {
                val recetasApi = NetworkClient.createService(com.example.doloresapp.data.remote.RecetasDigitalesApi::class.java)
                val response = recetasApi.getRecetasPendientes()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val recetas = response.body()?.data ?: emptyList()
                    val count = recetas.size
                    
                    runOnUiThread {
                        tvRecetas?.text = count.toString()
                        
                        // Mostrar badge si hay recetas pendientes
                        if (count > 0) {
                            badgeRecetas?.visibility = View.VISIBLE
                            badgeRecetas?.text = if (count > 9) "9+" else count.toString()
                        } else {
                            badgeRecetas?.visibility = View.GONE
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun setupEdgeToEdge(rootViewId: Int) {
        val rootView = findViewById<View>(rootViewId)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Solo aplicar padding inferior para la barra de navegación
            // El header se extiende hasta arriba
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }
    }
    
    private fun extractNameFromEmail(email: String): String {
        // Extraer la parte antes del @ y capitalizar
        val namePart = email.substringBefore("@")
        return namePart.replaceFirstChar { it.uppercase() }
    }
    
    private fun showLogoutDialog() {
        val dialogView = layoutInflater.inflate(android.R.layout.simple_list_item_1, null)
        
        AlertDialog.Builder(this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)
            .setTitle("👋 ¿Cerrar sesión?")
            .setMessage("Volverás a la pantalla de inicio de sesión")
            .setPositiveButton("Cerrar sesión") { _, _ -> logout() }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun logout() {
        // Limpiar token
        TokenStore.clear()
        
        // Limpiar SharedPreferences
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        prefs.edit().clear().apply()
        
        // Limpiar rol
        RoleManager.clearUserRole(this)
        
        // Redirigir al login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
