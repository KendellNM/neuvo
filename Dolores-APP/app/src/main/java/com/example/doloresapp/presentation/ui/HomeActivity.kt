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
    }
    
    private fun setupRepartidorUI() {
        setContentView(R.layout.activity_home_repartidor)
        
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        tvRol = findViewById(R.id.tv_rol)
        
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        val userName = prefs.getString(ApiConstants.Prefs.USER_EMAIL, "Repartidor")
        
        tvBienvenida.text = "Bienvenido, $userName"
        tvRol.text = "🚚 Repartidor"
        
        // Botones para repartidor
        findViewById<Button>(R.id.btn_iniciar_delivery).setOnClickListener {
            // Abrir modo REPARTIDOR
            val intent = Intent(this, RepartidorActivity::class.java)
            intent.putExtra("pedido_id", 123L) // Obtener de pedido asignado
            intent.putExtra("destino_lat", -12.0464)
            intent.putExtra("destino_lng", -77.0428)
            startActivity(intent)
        }
        
        findViewById<Button>(R.id.btn_mis_entregas).setOnClickListener {
            // TODO: Implementar lista de entregas
        }
        
        findViewById<Button>(R.id.btn_historial).setOnClickListener {
            // TODO: Implementar historial de entregas
        }
    }
    
    private fun setupAdminUI() {
        setContentView(R.layout.activity_home_admin)
        
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        tvRol = findViewById(R.id.tv_rol)
        
        tvBienvenida.text = "Panel de Administración"
        tvRol.text = "👨‍💼 Administrador"
        
        // TODO: Implementar opciones de admin
    }
    
    private fun setupFarmaceuticoUI() {
        setContentView(R.layout.activity_home_farmaceutico)
        
        tvBienvenida = findViewById(R.id.tv_bienvenida)
        tvRol = findViewById(R.id.tv_rol)
        
        tvBienvenida.text = "Panel de Farmacéutico"
        tvRol.text = "💊 Farmacéutico"
        
        // TODO: Implementar opciones de farmacéutico
    }
}
