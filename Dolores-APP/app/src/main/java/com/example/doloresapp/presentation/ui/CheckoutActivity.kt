package com.example.doloresapp.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.doloresapp.R
import com.example.doloresapp.data.local.database.AppDatabase
import com.example.doloresapp.data.remote.DireccionResponse
import com.example.doloresapp.data.remote.DireccionesApi
import com.example.doloresapp.data.remote.NetworkClient
import com.example.doloresapp.data.remote.api.PedidoApiService
import com.example.doloresapp.data.remote.dto.CrearPedidoRequest
import com.example.doloresapp.data.remote.dto.PedidoDetalleRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var tvResumen: TextView
    private lateinit var tvTotal: TextView
    private lateinit var etDireccion: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var etNotas: TextInputEditText
    private lateinit var btnConfirmar: Button
    private lateinit var btnSeleccionarDireccion: Button
    private lateinit var btnUsarUbicacion: Button

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var direccionesCliente: List<DireccionResponse> = emptyList()
    private var direccionSeleccionada: DireccionResponse? = null
    private var clienteId: Long? = null
    private var latitudSeleccionada: Double? = null
    private var longitudSeleccionada: Double? = null

    companion object {
        private const val LOCATION_PERMISSION_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        database = AppDatabase.getDatabase(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

        // Botones opcionales (pueden no existir en el layout)
        btnSeleccionarDireccion = findViewById(R.id.btnSeleccionarDireccion) 
            ?: Button(this).also { it.visibility = View.GONE }
        btnUsarUbicacion = findViewById(R.id.btnUsarUbicacion) 
            ?: Button(this).also { it.visibility = View.GONE }

        cargarResumen()
        cargarClienteYDirecciones()

        btnSeleccionarDireccion.setOnClickListener {
            mostrarDialogoDirecciones()
        }

        btnUsarUbicacion.setOnClickListener {
            obtenerUbicacionActual()
        }

        btnConfirmar.setOnClickListener {
            confirmarPedido()
        }
    }

    private fun cargarClienteYDirecciones() {
        lifecycleScope.launch {
            try {
                val userApi = NetworkClient.createService(com.example.doloresapp.data.remote.UserApi::class.java)
                val currentUser = userApi.getCurrentUser()
                clienteId = currentUser.clienteId

                if (clienteId != null) {
                    // Cargar direcciones del cliente
                    try {
                        val direccionesApi = NetworkClient.createService(DireccionesApi::class.java)
                        direccionesCliente = direccionesApi.getDireccionesByCliente(clienteId!!)

                        if (direccionesCliente.isNotEmpty()) {
                            btnSeleccionarDireccion.visibility = View.VISIBLE
                            btnSeleccionarDireccion.text = "📍 Mis Direcciones (${direccionesCliente.size})"
                        }
                    } catch (e: Exception) {
                        // No hay direcciones guardadas
                    }

                    // Cargar teléfono del cliente si está disponible
                    currentUser.telefono?.let {
                        if (etTelefono.text.isNullOrEmpty()) {
                            etTelefono.setText(it)
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@CheckoutActivity, "Error cargando datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoDirecciones() {
        if (direccionesCliente.isEmpty()) {
            Toast.makeText(this, "No tienes direcciones guardadas", Toast.LENGTH_SHORT).show()
            return
        }

        val nombres = direccionesCliente.map { dir ->
            "${dir.nombre ?: "Dirección"}\n${dir.direccion ?: ""}"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Seleccionar Dirección")
            .setItems(nombres) { _, which ->
                val direccion = direccionesCliente[which]
                direccionSeleccionada = direccion
                etDireccion.setText(direccion.direccion ?: "")

                // Guardar coordenadas si existen
                direccion.latitud?.toDoubleOrNull()?.let { latitudSeleccionada = it }
                direccion.longitud?.toDoubleOrNull()?.let { longitudSeleccionada = it }

                Toast.makeText(this, "Dirección seleccionada: ${direccion.nombre}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .setNeutralButton("📍 Usar ubicación actual") { _, _ ->
                obtenerUbicacionActual()
            }
            .show()
    }

    private fun obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
            return
        }

        Toast.makeText(this, "Obteniendo ubicación...", Toast.LENGTH_SHORT).show()

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latitudSeleccionada = location.latitude
                longitudSeleccionada = location.longitude

                // Convertir coordenadas a dirección
                try {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val direccionTexto = address.getAddressLine(0) ?: 
                            "${address.thoroughfare ?: ""} ${address.subThoroughfare ?: ""}, ${address.locality ?: ""}"
                        etDireccion.setText(direccionTexto)
                        Toast.makeText(this, "✅ Ubicación obtenida", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    etDireccion.setText("Lat: ${location.latitude}, Lng: ${location.longitude}")
                }
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.isNotEmpty() 
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionActual()
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

                // Intentar obtener clienteId de múltiples fuentes
                if (clienteId == null) {
                    // Primero intentar desde SharedPreferences (guardado en registro)
                    val prefs = getSharedPreferences(com.example.doloresapp.utils.ApiConstants.Prefs.NAME, MODE_PRIVATE)
                    val savedClienteId = prefs.getLong("cliente_id", 0L)
                    if (savedClienteId > 0) {
                        clienteId = savedClienteId
                    }
                }
                
                if (clienteId == null) {
                    // Intentar desde el endpoint /me
                    try {
                        val userApi = NetworkClient.createService(com.example.doloresapp.data.remote.UserApi::class.java)
                        val currentUser = userApi.getCurrentUser()
                        clienteId = currentUser.clienteId
                        
                        // Guardar para futuras consultas
                        if (clienteId != null) {
                            val prefs = getSharedPreferences(com.example.doloresapp.utils.ApiConstants.Prefs.NAME, MODE_PRIVATE)
                            prefs.edit().putLong("cliente_id", clienteId!!).apply()
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("Checkout", "Error obteniendo usuario: ${e.message}")
                    }
                }

                if (clienteId == null || clienteId == 0L) {
                    Toast.makeText(this@CheckoutActivity, 
                        "No se encontró información del cliente. Por favor cierra sesión y vuelve a iniciar.", 
                        Toast.LENGTH_LONG).show()
                    btnConfirmar.isEnabled = true
                    return@launch
                }

                val detalles = items.map { item ->
                    PedidoDetalleRequest(
                        productoId = item.productoId,
                        cantidad = item.cantidad,
                        precioUnitario = item.precio
                    )
                }

                val request = CrearPedidoRequest(
                    clienteId = clienteId!!,
                    direccionId = direccionSeleccionada?.idDirecciones,
                    direccionEntrega = direccion,
                    telefono = telefono,
                    notas = notas,
                    metodoPago = "EFECTIVO",
                    latitud = latitudSeleccionada,
                    longitud = longitudSeleccionada,
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
