package com.example.doloresapp.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.websocket.DeliveryWebSocketClient
import com.example.doloresapp.utils.ApiConstants
import com.example.doloresapp.utils.DirectionsHelper
import com.google.android.gms.location.*
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * Activity para REPARTIDOR - Usa OSMDroid (100% GRATIS)
 * Envía ubicación en tiempo real al backend vía WebSocket
 */
class RepartidorActivity : AppCompatActivity() {
    
    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var webSocketClient: DeliveryWebSocketClient
    private var myLocationOverlay: MyLocationNewOverlay? = null
    
    private lateinit var btnIniciar: Button
    private lateinit var btnDetener: Button
    private lateinit var tvEstado: TextView
    private lateinit var tvUbicacion: TextView
    
    private var pedidoId: Long = 0
    private var isTracking = false
    private var destinoGeoPoint: GeoPoint? = null
    private var destinoMarker: Marker? = null
    private var currentLocationMarker: Marker? = null
    
    companion object {
        private const val LOCATION_PERMISSION_CODE = 101
        private const val UPDATE_INTERVAL = 5000L // 5 segundos
        private const val FASTEST_INTERVAL = 3000L // 3 segundos
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configurar OSMDroid
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = packageName
        
        // Verificar que el usuario sea repartidor
        if (!com.example.doloresapp.utils.RoleManager.isRepartidor(this)) {
            Toast.makeText(this, "⚠️ Solo repartidores pueden acceder a esta función", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        
        setContentView(R.layout.activity_repartidor)
        
        pedidoId = intent.getLongExtra("pedido_id", 123) // Default para testing
        
        // Coordenadas de destino
        val destinoLat = intent.getDoubleExtra("destino_lat", -12.0464)
        val destinoLng = intent.getDoubleExtra("destino_lng", -77.0428)
        destinoGeoPoint = GeoPoint(destinoLat, destinoLng)
        
        initViews()
        initMap()
        initLocationClient()
        initWebSocket()
        
        checkLocationPermission()
    }
    
    private fun initViews() {
        btnIniciar = findViewById(R.id.btn_iniciar_tracking)
        btnDetener = findViewById(R.id.btn_detener_tracking)
        tvEstado = findViewById(R.id.tv_estado)
        tvUbicacion = findViewById(R.id.tv_ubicacion)
        
        btnDetener.isEnabled = false
        
        btnIniciar.setOnClickListener {
            startTracking()
        }
        
        btnDetener.setOnClickListener {
            stopTracking()
        }
    }
    
    private fun initMap() {
        try {
            mapView = findViewById(R.id.map_view)
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)
            mapView.controller.setZoom(15.0)
            
            // Agregar overlay de ubicación
            try {
                myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
                myLocationOverlay?.enableMyLocation()
                mapView.overlays.add(myLocationOverlay)
            } catch (e: Exception) {
                // Si falla el overlay de ubicación, continuar sin él
                e.printStackTrace()
            }
            
            // Marcar destino
            destinoGeoPoint?.let { destino ->
                destinoMarker = Marker(mapView).apply {
                    position = destino
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "📍 Destino de Entrega"
                    icon = ContextCompat.getDrawable(this@RepartidorActivity, R.drawable.ic_destination_marker)
                        ?: ContextCompat.getDrawable(this@RepartidorActivity, android.R.drawable.ic_menu_mylocation)
                }
                mapView.overlays.add(destinoMarker)
                mapView.controller.setCenter(destino)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error inicializando mapa: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
    
    private fun initLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    onLocationUpdate(location)
                }
            }
        }
        
        // Obtener ubicación inicial y dibujar ruta
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val origen = GeoPoint(it.latitude, it.longitude)
                    destinoGeoPoint?.let { destino ->
                        dibujarRuta(origen, destino)
                    }
                }
            }
        }
    }
    
    private fun initWebSocket() {
        try {
            webSocketClient = DeliveryWebSocketClient(ApiConstants.BASE_URL)
            webSocketClient.connect(
                onConnected = {
                    runOnUiThread {
                        Toast.makeText(this, "✅ WebSocket conectado", Toast.LENGTH_SHORT).show()
                    }
                },
                onError = { error ->
                    runOnUiThread {
                        // No mostrar error si es solo problema de conexión
                        // Toast.makeText(this, "❌ Error WebSocket: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        } catch (e: Exception) {
            // WebSocket es opcional, continuar sin él
            e.printStackTrace()
        }
    }
    
    private fun dibujarRuta(origen: GeoPoint, destino: GeoPoint) {
        lifecycleScope.launch {
            try {
                val routeInfo = DirectionsHelper.drawRoute(mapView, origen, destino)
                
                routeInfo?.let { info ->
                    runOnUiThread {
                        tvEstado.text = "📍 Distancia: ${String.format("%.2f", info.distanceKm)} km\n⏱️ Tiempo: ${info.durationMinutes} min"
                    }
                }
            } catch (e: Exception) {
                // Si falla, solo mostrar los marcadores
            }
        }
    }
    
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                myLocationOverlay?.enableMyLocation()
            } else {
                Toast.makeText(this, "Permiso de ubicación requerido", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun startTracking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission()
            return
        }
        
        val locationRequest = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL
            fastestInterval = FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        
        isTracking = true
        btnIniciar.isEnabled = false
        btnDetener.isEnabled = true
        tvEstado.text = "Estado: Enviando ubicación..."
        tvEstado.setTextColor(Color.GREEN)
        
        Toast.makeText(this, "🚚 Tracking iniciado", Toast.LENGTH_SHORT).show()
    }
    
    private fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        
        isTracking = false
        btnIniciar.isEnabled = true
        btnDetener.isEnabled = false
        tvEstado.text = "Estado: Detenido"
        tvEstado.setTextColor(Color.RED)
        
        Toast.makeText(this, "⏹️ Tracking detenido", Toast.LENGTH_SHORT).show()
    }
    
    private var lastRouteUpdate: Long = 0
    
    private fun onLocationUpdate(location: Location) {
        val geoPoint = GeoPoint(location.latitude, location.longitude)
        
        // Actualizar UI
        tvUbicacion.text = "📍 ${String.format("%.6f", location.latitude)}, ${String.format("%.6f", location.longitude)}"
        
        // Actualizar marcador de ubicación actual
        currentLocationMarker?.let { mapView.overlays.remove(it) }
        currentLocationMarker = Marker(mapView).apply {
            position = geoPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "🚚 Mi ubicación"
        }
        mapView.overlays.add(currentLocationMarker)
        
        // Mover cámara
        mapView.controller.animateTo(geoPoint)
        mapView.invalidate()
        
        // Enviar ubicación por WebSocket
        webSocketClient.sendLocation(pedidoId, location.latitude, location.longitude)
        
        // Calcular distancia al destino y actualizar ruta cada 30 segundos
        destinoGeoPoint?.let { destino ->
            val results = FloatArray(1)
            Location.distanceBetween(location.latitude, location.longitude, destino.latitude, destino.longitude, results)
            val distanceInKm = results[0] / 1000
            
            // Actualizar ruta cada 30 segundos
            val now = System.currentTimeMillis()
            if (now - lastRouteUpdate > 30000) {
                lastRouteUpdate = now
                dibujarRuta(geoPoint, destino)
            } else {
                tvEstado.text = "📍 Distancia: ${String.format("%.2f", distanceInKm)} km"
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        mapView.onResume()
        myLocationOverlay?.enableMyLocation()
    }
    
    override fun onPause() {
        super.onPause()
        mapView.onPause()
        myLocationOverlay?.disableMyLocation()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (isTracking) {
            stopTracking()
        }
        webSocketClient.disconnect()
    }
}
