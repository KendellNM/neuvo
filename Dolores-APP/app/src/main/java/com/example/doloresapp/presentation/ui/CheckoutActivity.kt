package com.example.doloresapp.presentation.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.doloresapp.R
import com.example.doloresapp.data.local.database.AppDatabase
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.api.PedidoApiService
import com.example.doloresapp.data.remote.dto.CrearPedidoRequest
import com.example.doloresapp.data.remote.dto.PedidoDetalleRequest
import com.example.doloresapp.utils.ApiConstants
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity() {
    
    private lateinit var database: AppDatabase
    private lateinit var tvResumen: TextView
    private lateinit var tvTotal: TextView
    private lateinit var etDireccion: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var etNotas: TextInputEditText
    private lateinit var btnConfirmar: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        
        database = AppDatabase.getDatabase(this)
        
        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Confirmar Pedido"
        
        // Views
        tvResumen = findViewById(R.id.tvResumen)
        tvTotal = findViewById(R.id.tvTotal)
        etDireccion = findViewById(R.id.etDireccion)
        etTelefono = findViewById(R.id.etTelefono)
        etNotas = findViewById(R.id.etNotas)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        
        cargarResumen()
        
        btnConfirmar.setOnClickListener {
            confirmarPedido()
        }
    }

    private fun cargarResumen() {
        lifecycleScope.launch {
            val items = database.carritoDao().getAllItemsList()
            val total = items.sumOf { it.subtotal }
            
            val resumen = StringBuilder()
            items.forEach { item ->
                resumen.append("• ${item.nombre} x${item.cantidad} - S/ %.2f\n".format(item.subtotal))
            }
            
            tvResumen.text = resumen.toString()
            tvTotal.text = "Total: S/ %.2f".format(total)
        }
    }
    
    private fun confirmarPedido() {
        val direccion = etDireccion.text?.toString()?.trim() ?: ""
        val telefono = etTelefono.text?.toString()?.trim() ?: ""
        val notas = etNotas.text?.toString()?.trim() ?: ""
        
        if (direccion.isEmpty()) {
            etDireccion.error = "Ingresa tu dirección"
            return
        }
        
        if (telefono.isEmpty()) {
            etTelefono.error = "Ingresa tu teléfono"
            return
        }
        
        btnConfirmar.isEnabled = false
        
        lifecycleScope.launch {
            try {
                val items = database.carritoDao().getAllItemsList()
                val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
                val userId = prefs.getLong(ApiConstants.Prefs.USER_ID, 1L)
                
                val detalles = items.map { item ->
                    PedidoDetalleRequest(
                        productoId = item.productoId,
                        cantidad = item.cantidad,
                        precioUnitario = item.precio
                    )
                }
                
                val request = CrearPedidoRequest(
                    clienteId = userId,
                    direccionEntrega = direccion,
                    telefono = telefono,
                    notas = notas,
                    detalles = detalles
                )
                
                val api = NetworkClient.createService(PedidoApiService::class.java)
                val response = api.crearPedido(request)
                
                // Limpiar carrito
                database.carritoDao().clearCarrito()
                
                // Mostrar éxito
                AlertDialog.Builder(this@CheckoutActivity)
                    .setTitle("✅ Pedido Confirmado")
                    .setMessage("Tu pedido #${response.id} ha sido creado exitosamente.\n\nEstado: ${response.estado}")
                    .setPositiveButton("Ver Mis Pedidos") { _, _ ->
                        startActivity(android.content.Intent(this@CheckoutActivity, MisPedidosActivity::class.java))
                        finish()
                    }
                    .setNegativeButton("Volver al Inicio") { _, _ ->
                        startActivity(android.content.Intent(this@CheckoutActivity, HomeActivity::class.java))
                        finishAffinity()
                    }
                    .setCancelable(false)
                    .show()
                    
            } catch (e: Exception) {
                Toast.makeText(this@CheckoutActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                btnConfirmar.isEnabled = true
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
