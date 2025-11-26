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
import com.example.doloresapp.LoginActivity
import com.example.doloresapp.R
import com.example.doloresapp.data.local.TokenStore
import com.example.doloresapp.utils.ApiConstants
import com.example.doloresapp.utils.RoleManager
import com.example.doloresapp.utils.UserRole

/**
 * Activity principal que muestra opciones según el rol del usuario
 */
class HomeActivity : AppCompatActivity() {
    
    private lateinit var tvBienvenida: TextView
    private lateinit var tvRol: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val userRole = RoleManager.getUserRole(this)
        
        // Cargar layout según rol
        when (userRole) {
            UserRole.CLIENTE -> setupClienteUI()
            UserRole.REPARTIDOR -> setupRepartidorUI()
            UserRole.ADMIN -> setupAdminUI()
            UserRole.FARMACEUTICO -> setupFarmaceuticoUI()
        }
    }
    
    private fun setupClienteUI() {
        setContentView(R.layout.activity_home_cliente)
        setupEdgeToEdge(R.id.cliente_root)
        
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        tvRol = findViewById(R.id.tv_rol)
        
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        val userName = prefs.getString(ApiConstants.Prefs.USER_EMAIL, "Usuario")
        
        tvBienvenida.text = "Bienvenido, $userName"
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
        
        findViewById<Button>(R.id.btn_tracking).setOnClickListener {
            // Abrir tracking como CLIENTE
            val intent = Intent(this, DeliveryTrackingActivity::class.java)
            intent.putExtra("pedido_id", 123L) // Obtener de pedido real
            intent.putExtra("destino_lat", -12.0464)
            intent.putExtra("destino_lng", -77.0428)
            startActivity(intent)
        }
        
        findViewById<Button>(R.id.btn_notificaciones).setOnClickListener {
            startActivity(Intent(this, NotificacionesActivity::class.java))
        }
        
        // Carrito
        findViewById<Button>(R.id.btn_carrito)?.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }
        
        // Mis Pedidos
        findViewById<Button>(R.id.btn_mis_pedidos)?.setOnClickListener {
            startActivity(Intent(this, MisPedidosActivity::class.java))
        }
    }
    
    private fun setupRepartidorUI() {
        setContentView(R.layout.activity_home_repartidor)
        setupEdgeToEdge(R.id.repartidor_root)
        
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        tvRol = findViewById(R.id.tv_rol)
        
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        val userName = prefs.getString(ApiConstants.Prefs.USER_EMAIL, "Repartidor")
        
        tvBienvenida.text = "Panel de Entregas"
        tvRol.text = "🚚 Repartidor"
        
        // Botón logout
        findViewById<View>(R.id.btn_logout)?.setOnClickListener { showLogoutDialog() }
        
        // Iniciar entrega - Abre el modo repartidor con GPS en tiempo real
        findViewById<Button>(R.id.btn_iniciar_delivery).setOnClickListener {
            val intent = Intent(this, RepartidorActivity::class.java)
            // En producción, estos datos vendrían del pedido asignado
            intent.putExtra("pedido_id", 123L)
            intent.putExtra("destino_lat", -12.0464) // Lima, Perú
            intent.putExtra("destino_lng", -77.0428)
            startActivity(intent)
        }
        
        // Mis entregas pendientes
        findViewById<Button>(R.id.btn_mis_entregas).setOnClickListener {
            android.widget.Toast.makeText(this, "Próximamente: Lista de entregas pendientes", android.widget.Toast.LENGTH_SHORT).show()
        }
        
        // Historial de entregas
        findViewById<Button>(R.id.btn_historial).setOnClickListener {
            android.widget.Toast.makeText(this, "Próximamente: Historial de entregas", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupAdminUI() {
        setContentView(R.layout.activity_home_admin)
        setupEdgeToEdge(R.id.admin_root)
        
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        tvRol = findViewById(R.id.tv_rol)
        
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        val userName = prefs.getString(ApiConstants.Prefs.USER_EMAIL, "Admin")
        
        tvBienvenida.text = "Panel de Administración"
        tvRol.text = "👨‍💼 Administrador"
        
        // Botón logout
        findViewById<View>(R.id.btn_logout)?.setOnClickListener { showLogoutDialog() }
        
        // Gestionar productos
        findViewById<Button>(R.id.btn_productos).setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
        }
        
        // Ver pedidos (TODO)
        findViewById<Button>(R.id.btn_pedidos).setOnClickListener {
            android.widget.Toast.makeText(this, "Próximamente: Gestión de pedidos", android.widget.Toast.LENGTH_SHORT).show()
        }
        
        // Gestionar usuarios (TODO)
        findViewById<Button>(R.id.btn_usuarios).setOnClickListener {
            android.widget.Toast.makeText(this, "Próximamente: Gestión de usuarios", android.widget.Toast.LENGTH_SHORT).show()
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
        val userName = prefs.getString(ApiConstants.Prefs.USER_EMAIL, "Farmacéutico")
        
        tvBienvenida.text = "Panel de Farmacéutico"
        tvRol.text = "💊 Farmacéutico"
        
        // Botón logout
        findViewById<View>(R.id.btn_logout)?.setOnClickListener { showLogoutDialog() }
        
        // Escanear QR de productos
        findViewById<Button>(R.id.btn_escanear_qr).setOnClickListener {
            startActivity(Intent(this, QRScannerActivity::class.java))
        }
        
        // Validar recetas pendientes
        findViewById<Button>(R.id.btn_validar_recetas).setOnClickListener {
            startActivity(Intent(this, RecetaDigitalActivity::class.java))
        }
        
        // Ver inventario de productos
        findViewById<Button>(R.id.btn_ver_productos).setOnClickListener {
            startActivity(Intent(this, ProductosActivity::class.java))
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
            v.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }
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
