package com.example.doloresapp.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.doloresapp.R
import com.example.doloresapp.data.local.database.AppDatabase
import com.example.doloresapp.data.local.entity.UbicacionDeliveryEntity
import com.example.doloresapp.data.remote.websocket.DeliveryWebSocketClient
import com.example.doloresapp.utils.Constants
import com.example.doloresapp.utils.DirectionsHelper
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Date

/**
 * Activity para tracking de delivery usando OSMDroid (OpenStreetMap)
 * 100% GRATIS - SIN API KEY
 */
class DeliveryTrackingActivity : AppCompatActivity() {
    
    private lateinit var mapView: MapView
    private lateinit var webSocketClient: DeliveryWebSocketClient
    private lateinit var database: AppDatabase
    private lateinit var estadoTextView: TextView
    private lateinit var tvDistancia: TextView
    private lateinit var tvTiempo: TextView
    
    private var pedidoId: Long = 0
    private var repartidorMarker: Marker? = null
    private var destinoMarker: Marker? = null
    
    // Coordenadas de destino (dirección del cliente)
    private var destinoGeoPoint: GeoPoint? = null
    
    companion object {
        private const val LOCATION_PERMISSION_CODE = 101
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configurar OSMDroid
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = packageName
        
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
        destinoGeoPoint = GeoPoint(destinoLat, destinoLng)
        
        estadoTextView = findViewById(R.id.tv_estado)
        tvDistancia = findViewById(R.id.tv_distancia)
        tvTiempo = findViewById(R.id.tv_tiempo)
        
        database = AppDatabase.getDatabase(this)
        webSocketClient = DeliveryWebSocketClient(Constants.BASE_URL)
        
        setupMap()
        checkLocationPermission()
    }
    
    private fun setupMap() {
        try {
            mapView = findViewById(R.id.map_view)
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)
            
            // Configurar zoom
            mapView.controller.setZoom(15.0)
            
            // Marcar destino (dirección del cliente)
            destinoGeoPoint?.let { destino ->
                destinoMarker = Marker(mapView).apply {
                    position = destino
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "📍 Destino de Entrega"
                    icon = ContextCompat.getDrawable(this@DeliveryTrackingActivity, R.drawable.ic_destination_marker)
                        ?: getDefaultMarkerDrawable()
                }
                mapView.overlays.add(destinoMarker)
                mapView.controller.setCenter(destino)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error inicializando mapa: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
        
        // Cargar última ubicación guardada
        loadLastLocation()
        
        // Conectar WebSocket
        connectWebSocket()
    }
    
    private fun getDefaultMarkerDrawable(): Drawable {
        return ContextCompat.getDrawable(this, android.R.drawable.ic_menu_mylocation)!!
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
    
    private fun loadLastLocation() {
        lifecycleScope.launch {
            val ultimaUbicacion = database.ubicacionDeliveryDao().getUltimaUbicacion(pedidoId)
            ultimaUbicacion?.let {
                updateMapLocation(it.latitud, it.longitud)
            }
        }
    }
    
    private fun connectWebSocket() {
        try {
            webSocketClient.connect(
                onConnected = {
                    runOnUiThread {
                        Toast.makeText(this, "✅ Conectado al seguimiento", Toast.LENGTH_SHORT).show()
                    }
                    subscribeToDelivery()
                },
                onError = { error ->
                    runOnUiThread {
                        // No mostrar error, el tracking puede funcionar sin WebSocket
                        estadoTextView.text = "Estado: Sin conexión en tiempo real"
                    }
                }
            )
        } catch (e: Exception) {
            // WebSocket es opcional
            e.printStackTrace()
        }
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
        val location = GeoPoint(latitud, longitud)
        
        // Actualizar marcador del repartidor
        repartidorMarker?.let { mapView.overlays.remove(it) }
        
        repartidorMarker = Marker(mapView).apply {
            position = location
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "🚚 Repartidor"
            icon = ContextCompat.getDrawable(this@DeliveryTrackingActivity, R.drawable.ic_delivery_marker)
                ?: getDefaultMarkerDrawable()
        }
        mapView.overlays.add(repartidorMarker)
        
        mapView.controller.animateTo(location)
        mapView.invalidate()
        
        // Dibujar ruta desde repartidor hasta destino
        destinoGeoPoint?.let { destino ->
            lifecycleScope.launch {
                val routeInfo = DirectionsHelper.drawRoute(
                    mapView,
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
    
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
    
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        webSocketClient.disconnect()
    }
}
