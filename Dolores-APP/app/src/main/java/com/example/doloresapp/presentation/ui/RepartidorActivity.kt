package com.example.doloresapp.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.doloresapp.R
import com.example.doloresapp.data.remote.websocket.DeliveryWebSocketClient
import com.example.doloresapp.utils.ApiConstants
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import android.graphics.Color

/**
 * Activity para REPARTIDOR
 * Envía ubicación en tiempo real al backend vía WebSocket
 */
class RepartidorActivity : AppCompatActivity(), OnMapReadyCallback {
    
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var webSocketClient: DeliveryWebSocketClient
    
    private lateinit var btnIniciar: Button
    private lateinit var btnDetener: Button
    private lateinit var tvEstado: TextView
    private lateinit var tvUbicacion: TextView
    
    private var pedidoId: Long = 0
    private var isTracking = false
    private var destinoLatLng: LatLng? = null
    
    companion object {
        private const val LOCATION_PERMISSION_CODE = 101
        private const val UPDATE_INTERVAL = 5000L // 5 segundos
        private const val FASTEST_INTERVAL = 3000L // 3 segundos
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Verificar que el usuario sea repartidor
        if (!com.example.doloresapp.utils.RoleManager.isRepartidor(this)) {
            Toast.makeText(this, "⚠️ Solo repartidores pueden acceder a esta función", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        
        setContentView(R.layout.activity_repartidor)
        
        pedidoId = intent.getLongExtra("pedido_id", 123) // Default para testing
        
        // Coordenadas de destino (ejemplo: Lima, Perú)
        val destinoLat = intent.getDoubleExtra("destino_lat", -12.0464)
        val destinoLng = intent.getDoubleExtra("destino_lng", -77.0428)
        destinoLatLng = LatLng(destinoLat, destinoLng)
        
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
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
    }
    
    private fun initWebSocket() {
        webSocketClient = DeliveryWebSocketClient(ApiConstants.BASE_URL)
        webSocketClient.connect(
            onConnected = {
                runOnUiThread {
                    Toast.makeText(this, "WebSocket conectado", Toast.LENGTH_SHORT).show()
                }
            },
            onError = { error ->
                runOnUiThread {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
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
        
        // Marcar destino
        destinoLatLng?.let { destino ->
            googleMap.addMarker(
                MarkerOptions()
                    .position(destino)
                    .title("Destino de Entrega")
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, 14f))
        }
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
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    googleMap.isMyLocationEnabled = true
                }
            } else {
                Toast.makeText(this, "Permiso de ubicación requerido", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun startTracking() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkLocationPermission()
            return
        }
        
        val locationRequest = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL
            fastestInterval = FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        
        isTracking = true
        btnIniciar.isEnabled = false
        btnDetener.isEnabled = true
        tvEstado.text = "Estado: Enviando ubicación..."
        tvEstado.setTextColor(Color.GREEN)
        
        Toast.makeText(this, "Tracking iniciado", Toast.LENGTH_SHORT).show()
    }
    
    private fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        
        isTracking = false
        btnIniciar.isEnabled = true
        btnDetener.isEnabled = false
        tvEstado.text = "Estado: Detenido"
        tvEstado.setTextColor(Color.RED)
        
        Toast.makeText(this, "Tracking detenido", Toast.LENGTH_SHORT).show()
    }
    
    private fun onLocationUpdate(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        
        // Actualizar UI
        tvUbicacion.text = "Lat: ${location.latitude}\nLng: ${location.longitude}"
        
        // Mover cámara
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        
        // Enviar ubicación por WebSocket
        webSocketClient.sendLocation(pedidoId, location.latitude, location.longitude)
        
        // Calcular distancia al destino
        destinoLatLng?.let { destino ->
            val results = FloatArray(1)
            Location.distanceBetween(
                location.latitude,
                location.longitude,
                destino.latitude,
                destino.longitude,
                results
            )
            val distanceInMeters = results[0]
            val distanceInKm = distanceInMeters / 1000
            
            tvEstado.text = "Distancia al destino: ${String.format("%.2f", distanceInKm)} km"
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (isTracking) {
            stopTracking()
        }
        webSocketClient.disconnect()
    }
}
