package com.example.doloresapp.presentation.ui

import android.Manifest
import android.content.Intent
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
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
    private lateinit var btnSeleccionarDireccion: MaterialButton
    private lateinit var btnUsarUbicacion: MaterialButton
    
    // Nuevos elementos para receta y tipo de entrega
    private var cardRecetaRequerida: MaterialCardView? = null
    private var btnSubirReceta: MaterialButton? = null
    private var btnLlevarReceta: MaterialButton? = null
    private var layoutRecetaEstado: View? = null
    private var tvRecetaEstado: TextView? = null
    private var btnDelivery: MaterialCardView? = null
    private var btnRecojo: MaterialCardView? = null
    private var layoutDireccion: MaterialCardView? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var direccionesCliente: List<DireccionResponse> = emptyList()
    private var direccionSeleccionada: DireccionResponse? = null
    private var clienteId: Long? = null
    private var latitudSeleccionada: Double? = null
    private var longitudSeleccionada: Double? = null

    companion object {
        private const val LOCATION_PERMISSION_CODE = 1001
        private const val REQUEST_ENVIAR_RECETA = 100
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

        // Views básicos
        tvResumen = findViewById(R.id.tvResumen)
        tvTotal = findViewById(R.id.tvTotal)
        etDireccion = findViewById(R.id.etDireccion)
        etTelefono = findViewById(R.id.etTelefono)
        etNotas = findViewById(R.id.etNotas)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        btnSeleccionarDireccion = findViewById(R.id.btnSeleccionarDireccion)
        btnUsarUbicacion = findViewById(R.id.btnUsarUbicacion)
        
        // Nuevos elementos
        cardRecetaRequerida = findViewById(R.id.cardRecetaRequerida)
        btnSubirReceta = findViewById(R.id.btnSubirReceta)
        btnLlevarReceta = findViewById(R.id.btnLlevarReceta)
        layoutRecetaEstado = findViewById(R.id.layoutRecetaEstado)
        tvRecetaEstado = findViewById(R.id.tvRecetaEstado)
        btnDelivery = findViewById(R.id.btnDelivery)
        btnRecojo = findViewById(R.id.btnRecojo)
        layoutDireccion = findViewById(R.id.layoutDireccion)

        setupListeners()
        cargarResumen()
        cargarClienteYDirecciones()
    }
    
    private fun setupListeners() {
        btnSeleccionarDireccion.setOnClickListener {
            mostrarDialogoDirecciones()
        }

        btnUsarUbicacion.setOnClickListener {
            obtenerUbicacionActual()
        }

        btnConfirmar.setOnClickListener {
            confirmarPedido()
        }
        
        // Listeners para receta
        btnSubirReceta?.setOnClickListener {
            val intent = Intent(this, EnviarRecetaActivity::class.java)
            intent.putExtra("desde_checkout", true)
            startActivityForResult(intent, REQUEST_ENVIAR_RECETA)
        }
        
        btnLlevarReceta?.setOnClickListener {
            tipoEntrega = "RECOJO"
            actualizarVistaEntrega()
            layoutRecetaEstado?.visibility = View.VISIBLE
            tvRecetaEstado?.text = "Deberás presentar tu receta física al recoger"
            Toast.makeText(this, "Deberás presentar tu receta al recoger el pedido", Toast.LENGTH_LONG).show()
        }
        
        // Listeners para tipo de entrega
        btnDelivery?.setOnClickListener {
            tipoEntrega = "DELIVERY"
            actualizarVistaEntrega()
        }
        
        btnRecojo?.setOnClickListener {
            tipoEntrega = "RECOJO"
            actualizarVistaEntrega()
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

    private var requiereReceta = false
    private var tipoEntrega = "DELIVERY" // DELIVERY o RECOJO
    private var recetaEnviada = false
    
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
            
            // Verificar si algún producto requiere receta
            verificarProductosConReceta(items.map { it.productoId })
        }
    }
    
    private fun verificarProductosConReceta(productoIds: List<Long>) {
        lifecycleScope.launch {
            try {
                val api = NetworkClient.createService(com.example.doloresapp.data.remote.service.ProductoApiService::class.java)
                val productos = api.getAllProductos()
                
                val productosEnCarrito = productos.filter { it.id in productoIds }
                requiereReceta = productosEnCarrito.any { it.requerireReceta == true }
                
                runOnUiThread {
                    if (requiereReceta) {
                        cardRecetaRequerida?.visibility = View.VISIBLE
                    } else {
                        cardRecetaRequerida?.visibility = View.GONE
                    }
                    actualizarVistaEntrega()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun actualizarVistaEntrega() {
        if (tipoEntrega == "DELIVERY") {
            // Estilo seleccionado para Delivery
            btnDelivery?.strokeColor = getColor(R.color.primary)
            btnDelivery?.strokeWidth = 2
            btnRecojo?.strokeColor = getColor(android.R.color.darker_gray)
            btnRecojo?.strokeWidth = 1
            
            layoutDireccion?.visibility = View.VISIBLE
            etDireccion.setText("")
            etDireccion.hint = "Ingresa tu dirección de entrega"
        } else {
            // Estilo seleccionado para Recojo
            btnRecojo?.strokeColor = getColor(R.color.primary)
            btnRecojo?.strokeWidth = 2
            btnDelivery?.strokeColor = getColor(android.R.color.darker_gray)
            btnDelivery?.strokeWidth = 1
            
            layoutDireccion?.visibility = View.GONE
            etDireccion.setText("Recoger en tienda - Farmacia Dolores")
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENVIAR_RECETA && resultCode == RESULT_OK) {
            recetaEnviada = true
            layoutRecetaEstado?.visibility = View.VISIBLE
            tvRecetaEstado?.text = "✅ Receta enviada. El farmacéutico la revisará."
            Toast.makeText(this, "✅ Receta enviada correctamente", Toast.LENGTH_LONG).show()
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
                
                if (clienteId == null && com.example.doloresapp.utils.NetworkUtils.isNetworkAvailable(this@CheckoutActivity)) {
                    // Intentar desde el endpoint /me solo si hay conexión
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
                
                val total = items.sumOf { it.subtotal }

                // Verificar si hay conexión a internet
                if (com.example.doloresapp.utils.NetworkUtils.isNetworkAvailable(this@CheckoutActivity)) {
                    // CON INTERNET: Enviar pedido al servidor
                    enviarPedidoOnline(direccion, telefono, notas, detalles)
                } else {
                    // SIN INTERNET: Guardar pedido localmente para sincronizar después
                    guardarPedidoOffline(direccion, telefono, notas, detalles, total)
                }

            } catch (e: Exception) {
                Toast.makeText(this@CheckoutActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                btnConfirmar.isEnabled = true
            }
        }
    }
    
    private suspend fun enviarPedidoOnline(
        direccion: String,
        telefono: String,
        notas: String,
        detalles: List<PedidoDetalleRequest>
    ) {
        try {
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
    
    private suspend fun guardarPedidoOffline(
        direccion: String,
        telefono: String,
        notas: String,
        detalles: List<PedidoDetalleRequest>,
        total: Double
    ) {
        try {
            val syncManager = com.example.doloresapp.data.sync.SyncManager.getInstance(this@CheckoutActivity)
            
            val pedidoLocalId = syncManager.savePendingOrder(
                clienteId = clienteId!!,
                direccionId = direccionSeleccionada?.idDirecciones,
                direccionEntrega = direccion,
                telefono = telefono,
                notas = notas,
                metodoPago = "EFECTIVO",
                latitud = latitudSeleccionada,
                longitud = longitudSeleccionada,
                detalles = detalles,
                total = total
            )

            // Limpiar carrito
            database.carritoDao().clearCarrito()

            // Mostrar mensaje de pedido guardado offline
            AlertDialog.Builder(this@CheckoutActivity)
                .setTitle("📱 Pedido Guardado")
                .setMessage("Tu pedido ha sido guardado localmente.\n\n" +
                    "⚠️ Sin conexión a internet\n\n" +
                    "El pedido se enviará automáticamente cuando tengas conexión.\n\n" +
                    "Total: S/ %.2f".format(total))
                .setPositiveButton("Entendido") { _, _ ->
                    startActivity(android.content.Intent(this@CheckoutActivity, HomeActivity::class.java))
                    finishAffinity()
                }
                .setCancelable(false)
                .show()
                
        } catch (e: Exception) {
            Toast.makeText(this@CheckoutActivity, "Error guardando pedido: ${e.message}", Toast.LENGTH_LONG).show()
            btnConfirmar.isEnabled = true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
