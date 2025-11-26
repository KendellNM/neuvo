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
        
        // Asegurar que NetworkClient esté inicializado
        TokenStore.init(applicationContext)
        NetworkClient.init(applicationContext)
        
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
        
        tvBienvenida.text = "Hola, $displayName"
        tvRol.text = "👤 Cliente"
        
        // Botón logout
        findViewById<View>(R.id.btn_logout)?.setOnClickListener { showLogoutDialog() }
        
        // Botón Ver Productos
        findViewById<Button>(R.id.btn_productos)?.setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }
        
        // Botones para cliente
        findViewById<Button>(R.id.btn_escanear_qr).setOnClickListener {
            startActivity(Intent(this, QRScannerActivity::class.java))
        }
        
        findViewById<Button>(R.id.btn_recetas).setOnClickListener {
            startActivity(Intent(this, RecetaDigitalActivity::class.java))
        }
        
        findViewById<Button>(R.id.btn_fidelizacion).setOnClickListener {
            startActivity(Intent(this, FidelizacionActivity::class.java))
        }
        
        // Carrito
        findViewById<Button>(R.id.btn_carrito)?.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }
        
        // Mis Pedidos y Seguimiento
        findViewById<Button>(R.id.btn_mis_pedidos)?.setOnClickListener {
            startActivity(Intent(this, MisPedidosActivity::class.java))
        }
        
        // Notificaciones
        findViewById<Button>(R.id.btn_notificaciones).setOnClickListener {
            startActivity(Intent(this, NotificacionesActivity::class.java))
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
        tvRol.text = "🚚 Repartidor"
        
        // Botón logout
        findViewById<View>(R.id.btn_logout)?.setOnClickListener { showLogoutDialog() }
        
        // Pedidos asignados - Ver lista de pedidos asignados al repartidor
        findViewById<Button>(R.id.btn_pedidos_asignados)?.setOnClickListener {
            startActivity(Intent(this, PedidosAsignadosActivity::class.java))
        }
        
        // Entrega activa - Ver pedidos en camino
        findViewById<Button>(R.id.btn_entrega_activa)?.setOnClickListener {
            val intent = Intent(this, PedidosAsignadosActivity::class.java)
            intent.putExtra("tab_inicial", 1) // Tab "En Camino"
            startActivity(intent)
        }
        
        // Historial de entregas
        findViewById<Button>(R.id.btn_historial)?.setOnClickListener {
            val intent = Intent(this, PedidosAsignadosActivity::class.java)
            intent.putExtra("tab_inicial", 2) // Tab "Entregados"
            startActivity(intent)
        }
        
        // Notificaciones
        findViewById<Button>(R.id.btn_notificaciones)?.setOnClickListener {
            startActivity(Intent(this, NotificacionesActivity::class.java))
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
        tvRol.text = "👨‍💼 Administrador"
        
        // Botón logout
        findViewById<View>(R.id.btn_logout)?.setOnClickListener { showLogoutDialog() }
        
        // Gestionar productos
        findViewById<Button>(R.id.btn_productos).setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }
        
        // Ver pedidos
        findViewById<Button>(R.id.btn_pedidos).setOnClickListener {
            startActivity(Intent(this, GestionPedidosActivity::class.java))
        }
        
        // Gestionar usuarios
        findViewById<Button>(R.id.btn_usuarios).setOnClickListener {
            startActivity(Intent(this, GestionUsuariosActivity::class.java))
        }
        
        // Control de stock
        findViewById<Button>(R.id.btn_stock).setOnClickListener {
            startActivity(Intent(this, GestionStockActivity::class.java))
        }
        
        // Recetas pendientes
        findViewById<Button>(R.id.btn_recetas).setOnClickListener {
            startActivity(Intent(this, RecetaDigitalActivity::class.java))
        }
        
        // Reportes (TODO)
        findViewById<Button>(R.id.btn_reportes).setOnClickListener {
            android.widget.Toast.makeText(this, "Próximamente: Reportes y estadísticas", android.widget.Toast.LENGTH_SHORT).show()
        }
        
        // Notificaciones
        findViewById<Button>(R.id.btn_notificaciones).setOnClickListener {
            startActivity(Intent(this, NotificacionesActivity::class.java))
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
        tvRol.text = "💊 Farmacéutico"
        
        // Botón logout
        findViewById<View>(R.id.btn_logout)?.setOnClickListener { showLogoutDialog() }
        
        // Escanear QR de productos (solo ver detalle)
        findViewById<Button>(R.id.btn_escanear_qr).setOnClickListener {
            startActivity(Intent(this, VentaPresencialActivity::class.java))
        }
        
        // Validar recetas pendientes
        findViewById<Button>(R.id.btn_validar_recetas).setOnClickListener {
            startActivity(Intent(this, RecetaDigitalActivity::class.java))
        }
        
        // Ver inventario de productos
        findViewById<Button>(R.id.btn_ver_productos).setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }
        
        // Control de stock
        findViewById<Button>(R.id.btn_stock).setOnClickListener {
            startActivity(Intent(this, GestionStockActivity::class.java))
        }
        
        // Consultas de clientes (TODO)
        findViewById<Button>(R.id.btn_consultas).setOnClickListener {
            android.widget.Toast.makeText(this, "Próximamente: Consultas de clientes", android.widget.Toast.LENGTH_SHORT).show()
        }
        
        // Notificaciones
        findViewById<Button>(R.id.btn_notificaciones).setOnClickListener {
            startActivity(Intent(this, NotificacionesActivity::class.java))
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
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ -> logout() }
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
