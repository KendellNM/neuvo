package com.example.doloresapp.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.doloresapp.R
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
        
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        tvRol = findViewById(R.id.tv_rol)
        
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        val userName = prefs.getString(ApiConstants.Prefs.USER_EMAIL, "Usuario")
        
        tvBienvenida.text = "Bienvenido, $userName"
        tvRol.text = "👤 Cliente"
        
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
        
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        tvRol = findViewById(R.id.tv_rol)
        
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        val userName = prefs.getString(ApiConstants.Prefs.USER_EMAIL, "Repartidor")
        
        tvBienvenida.text = "Bienvenido, $userName"
        tvRol.text = "🚚 Repartidor"
        
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
        
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        tvRol = findViewById(R.id.tv_rol)
        
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        val userName = prefs.getString(ApiConstants.Prefs.USER_EMAIL, "Admin")
        
        tvBienvenida.text = "Bienvenido, $userName"
        tvRol.text = "👨‍💼 Administrador"
        
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
        
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        tvRol = findViewById(R.id.tv_rol)
        
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        val userName = prefs.getString(ApiConstants.Prefs.USER_EMAIL, "Farmacéutico")
        
        tvBienvenida.text = "Bienvenido, $userName"
        tvRol.text = "💊 Farmacéutico"
        
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
}
