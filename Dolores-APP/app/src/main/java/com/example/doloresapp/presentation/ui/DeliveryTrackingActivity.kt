package com.example.doloresapp.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.doloresapp.R
import com.example.doloresapp.data.local.database.AppDatabase
import com.example.doloresapp.data.local.entity.UbicacionDeliveryEntity
import com.example.doloresapp.data.remote.websocket.DeliveryWebSocketClient
import com.example.doloresapp.utils.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.util.Date

class DeliveryTrackingActivity : AppCompatActivity(), OnMapReadyCallback {
    
    private lateinit var googleMap: GoogleMap
    private lateinit var webSocketClient: DeliveryWebSocketClient
    private lateinit var database: AppDatabase
    private lateinit var estadoTextView: TextView
    private lateinit var tvDistancia: TextView
    private lateinit var tvTiempo: TextView
    
    private var pedidoId: Long = 0
    private var currentMarker: com.google.android.gms.maps.model.Marker? = null
    private var destinoMarker: com.google.android.gms.maps.model.Marker? = null
    
    // Coordenadas de destino (dirección del cliente)
    private var destinoLatLng: LatLng? = null
    
    companion object {
        private const val LOCATION_PERMISSION_CODE = 101
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Verificar que el usuario sea cliente
        if (!com.example.doloresapp.utils.RoleManager.isCliente(this)) {
            Toast.makeText(this, "⚠️ Solo clientes pueden ver el tracking de pedidos", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        
        setContentView(R.layout.activity_delivery_tracking)
        
        pedidoId = intent.getLongExtra("pedido_id", 0)
        
        // Obtener coordenadas de destino (dirección del cliente)
        val destinoLat = intent.getDoubleExtra("destino_lat", -12.0464)
        val destinoLng = intent.getDoubleExtra("destino_lng", -77.0428)
        destinoLatLng = LatLng(destinoLat, destinoLng)
        
        estadoTextView = findViewById(R.id.tv_estado)
        tvDistancia = findViewById(R.id.tv_distancia)
        tvTiempo = findViewById(R.id.tv_tiempo)
        
        database = AppDatabase.getDatabase(this)
        webSocketClient = DeliveryWebSocketClient(Constants.BASE_URL)
        
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        checkLocationPermission()
    }
    
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        }
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Habilitar ubicación si hay permiso
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        }
        
        // Marcar destino (dirección del cliente)
        destinoLatLng?.let { destino ->
            destinoMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(destino)
                    .title("Destino de Entrega")
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, 14f))
        }
        
        // Cargar última ubicación guardada
        loadLastLocation()
        
        // Conectar WebSocket
        connectWebSocket()
    }
    
    private fun loadLastLocation() {
        lifecycleScope.launch {
            val ultimaUbicacion = database.ubicacionDeliveryDao().getUltimaUbicacion(pedidoId)
            ultimaUbicacion?.let {
                updateMapLocation(it.latitud, it.longitud)
            }
        }
    }
    
    private fun connectWebSocket() {
        webSocketClient.connect(
            onConnected = {
                runOnUiThread {
                    Toast.makeText(this, "Conectado al seguimiento", Toast.LENGTH_SHORT).show()
                }
                subscribeToDelivery()
            },
            onError = { error ->
                runOnUiThread {
                    Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
    
    private fun subscribeToDelivery() {
        webSocketClient.subscribeToDelivery(pedidoId) { update ->
            runOnUiThread {
                updateMapLocation(update.latitud, update.longitud)
                
                update.estado?.let { estado ->
                    estadoTextView.text = "Estado: $estado"
                }
                
                // Guardar en base de datos local
                lifecycleScope.launch {
                    database.ubicacionDeliveryDao().insertUbicacion(
                        UbicacionDeliveryEntity(
                            pedidoId = pedidoId,
                            latitud = update.latitud,
                            longitud = update.longitud,
                            timestamp = Date()
                        )
                    )
                }
            }
        }
    }
    
    private fun updateMapLocation(latitud: Double, longitud: Double) {
        val location = LatLng(latitud, longitud)
        
        // Actualizar marcador del repartidor
        currentMarker?.remove()
        currentMarker = googleMap.addMarker(
            MarkerOptions()
                .position(location)
                .title("🚚 Repartidor")
        )
        
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        
        // Dibujar ruta desde repartidor hasta destino
        destinoLatLng?.let { destino ->
            lifecycleScope.launch {
                val routeInfo = com.example.doloresapp.utils.DirectionsHelper.drawRoute(
                    googleMap,
                    location,
                    destino
                )
                
                routeInfo?.let { info ->
                    runOnUiThread {
                        tvDistancia.text = "📍 Distancia: ${String.format("%.2f", info.distanceKm)} km"
                        tvTiempo.text = "⏱️ Tiempo estimado: ${info.durationMinutes} min"
                    }
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        webSocketClient.disconnect()
    }
}
