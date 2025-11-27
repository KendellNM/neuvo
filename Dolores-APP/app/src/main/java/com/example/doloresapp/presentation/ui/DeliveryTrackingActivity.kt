package com.example.doloresapp.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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

class DeliveryTrackingActivity : AppCompatActivity() {
    
    private var mapView: MapView? = null
    private var webSocketClient: DeliveryWebSocketClient? = null
    private var database: AppDatabase? = null
    private var tvTiempo: TextView? = null
    private var tvDireccion: TextView? = null
    private var tvRepartidorNombre: TextView? = null
    private var tvConnection: TextView? = null
    private var progressBar: View? = null
    
    private var pedidoId: Long = 0
    private var repartidorMarker: Marker? = null
    private var destinoMarker: Marker? = null
    private var destinoGeoPoint: GeoPoint? = null
    private var currentProgress = 10 // Porcentaje inicial
    private var isMapInitialized = false
    
    companion object {
        private const val LOCATION_PERMISSION_CODE = 101
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Configurar OSMDroid
            Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
            Configuration.getInstance().userAgentValue = packageName
            
            setContentView(R.layout.activity_delivery_tracking)
            
            pedidoId = intent.getLongExtra("pedido_id", 0)
            
            // Obtener coordenadas de destino
            val destinoLat = intent.getDoubleExtra("destino_lat", -12.0464)
            val destinoLng = intent.getDoubleExtra("destino_lng", -77.0428)
            destinoGeoPoint = GeoPoint(destinoLat, destinoLng)
            
            // Obtener dirección si viene en el intent
            val direccion = intent.getStringExtra("direccion") ?: "Dirección de entrega"
            
            initViews()
            setupClickListeners()
            
            tvDireccion?.text = direccion
            
            database = AppDatabase.getDatabase(this)
            webSocketClient = DeliveryWebSocketClient(Constants.BASE_URL)
            
            setupMap()
            checkLocationPermission()
            
            // Animar barra de progreso inicial
            animateProgress(65) // 65% = En camino
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al iniciar seguimiento: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun initViews() {
        tvTiempo = findViewById(R.id.tv_tiempo)
        tvDireccion = findViewById(R.id.tvDireccionEntrega)
        tvRepartidorNombre = findViewById(R.id.tvRepartidorNombre)
        tvConnection = findViewById(R.id.tvConnection)
        progressBar = findViewById(R.id.progressBar)
        
        // Valores por defecto - se actualizará con tiempo real
        tvTiempo?.text = "..."
        tvRepartidorNombre?.text = "Repartidor"
        
        // Cargar info del repartidor y calcular tiempo inicial
        cargarInfoPedido()
    }
    
    private fun cargarInfoPedido() {
        lifecycleScope.launch {
            try {
                // Calcular tiempo estimado inicial basado en distancia
                destinoGeoPoint?.let { destino ->
                    // Usar ubicación por defecto de la farmacia como origen inicial
                    val origenFarmacia = GeoPoint(-12.0464, -77.0428)
                    
                    val results = FloatArray(1)
                    android.location.Location.distanceBetween(
                        origenFarmacia.latitude, origenFarmacia.longitude,
                        destino.latitude, destino.longitude,
                        results
                    )
                    val distanciaKm = results[0] / 1000
                    
                    // Estimar tiempo: ~3 min por km en ciudad (tráfico moderado)
                    val tiempoEstimado = (distanciaKm * 3).toInt().coerceAtLeast(5)
                    
                    runOnUiThread {
                        tvTiempo?.text = "$tiempoEstimado-${tiempoEstimado + 5}"
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    tvTiempo?.text = "10-15"
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        // Botón volver
        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }
        
        // Botón llamar
        findViewById<View>(R.id.btnLlamar)?.setOnClickListener {
            Toast.makeText(this, "Llamando al repartidor...", Toast.LENGTH_SHORT).show()
        }
        
        // Botón navegar - buscar por recursos
        try {
            val btnNavegarId = resources.getIdentifier("btnNavegar", "id", packageName)
            if (btnNavegarId != 0) {
                findViewById<View>(btnNavegarId)?.setOnClickListener {
                    destinoGeoPoint?.let { destino ->
                        mapView?.controller?.animateTo(destino)
                        mapView?.controller?.setZoom(17.0)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // FAB centrar
        findViewById<View>(R.id.fabCentrar)?.setOnClickListener {
            repartidorMarker?.position?.let { pos ->
                mapView?.controller?.animateTo(pos)
            }
        }
    }
    
    private fun animateProgress(targetPercent: Int) {
        val bar = progressBar ?: return
        val parent = bar.parent as? ViewGroup ?: return
        val totalWidth = parent.width
        
        // Si el parent aún no tiene width, esperar
        if (totalWidth == 0) {
            bar.post { animateProgress(targetPercent) }
            return
        }
        
        val targetWidth = (totalWidth * targetPercent / 100)
        
        bar.animate()
            .setDuration(1000)
            .withStartAction {
                val params = bar.layoutParams
                params.width = (totalWidth * currentProgress / 100)
                bar.layoutParams = params
            }
            .withEndAction {
                val params = bar.layoutParams
                params.width = targetWidth
                bar.layoutParams = params
                currentProgress = targetPercent
            }
            .start()
        
        // Actualizar labels según progreso
        updateProgressLabels(targetPercent)
    }
    
    private fun updateProgressLabels(percent: Int) {
        val tvStep1 = findViewById<TextView>(R.id.tvStep1)
        val tvStep2 = findViewById<TextView>(R.id.tvStep2)
        val tvStep3 = findViewById<TextView>(R.id.tvStep3)
        
        when {
            percent >= 100 -> {
                tvStep1?.setTextColor(getColor(R.color.teal_900))
                tvStep2?.setTextColor(getColor(R.color.teal_900))
                tvStep3?.setTextColor(getColor(R.color.teal_900))
                tvConnection?.text = "Entregado"
            }
            percent >= 50 -> {
                tvStep1?.setTextColor(getColor(R.color.teal_900))
                tvStep2?.setTextColor(getColor(R.color.teal_900))
                tvStep3?.setTextColor(getColor(R.color.text_secondary))
                tvConnection?.text = "En camino"
            }
            else -> {
                tvStep1?.setTextColor(getColor(R.color.teal_900))
                tvStep2?.setTextColor(getColor(R.color.text_secondary))
                tvStep3?.setTextColor(getColor(R.color.text_secondary))
                tvConnection?.text = "Confirmado"
            }
        }
    }
    
    private fun setupMap() {
        try {
            mapView = findViewById(R.id.map_view)
            val map = mapView ?: return
            
            map.setTileSource(TileSourceFactory.MAPNIK)
            map.setMultiTouchControls(true)
            map.controller.setZoom(15.0)
            
            // Marcar destino
            destinoGeoPoint?.let { destino ->
                destinoMarker = Marker(map).apply {
                    position = destino
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "📍 Destino"
                    icon = ContextCompat.getDrawable(this@DeliveryTrackingActivity, R.drawable.ic_destination_marker)
                        ?: getDefaultMarkerDrawable()
                }
                map.overlays.add(destinoMarker)
                map.controller.setCenter(destino)
            }
            
            isMapInitialized = true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error inicializando mapa: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        
        loadLastLocation()
        connectWebSocket()
    }
    
    private fun getDefaultMarkerDrawable(): Drawable {
        return ContextCompat.getDrawable(this, android.R.drawable.ic_menu_mylocation)!!
    }
    
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        }
    }
    
    private fun loadLastLocation() {
        lifecycleScope.launch {
            try {
                val ultimaUbicacion = database?.ubicacionDeliveryDao()?.getUltimaUbicacion(pedidoId)
                ultimaUbicacion?.let {
                    updateMapLocation(it.latitud, it.longitud)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun connectWebSocket() {
        try {
            webSocketClient?.connect(
                onConnected = {
                    runOnUiThread {
                        findViewById<View>(R.id.dotConnection)?.setBackgroundResource(R.drawable.bg_dot_green)
                    }
                    subscribeToDelivery()
                },
                onError = { _ ->
                    runOnUiThread {
                        tvConnection?.text = "Sin conexión"
                    }
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun subscribeToDelivery() {
        webSocketClient?.subscribeToDelivery(pedidoId) { update ->
            runOnUiThread {
                updateMapLocation(update.latitud, update.longitud)
                
                update.estado?.let { estado ->
                    when (estado.uppercase()) {
                        "ENTREGADO" -> animateProgress(100)
                        "EN_CAMINO" -> animateProgress(65)
                        "ASIGNADO" -> animateProgress(30)
                        else -> animateProgress(10)
                    }
                }
                
                lifecycleScope.launch {
                    try {
                        database?.ubicacionDeliveryDao()?.insertUbicacion(
                            UbicacionDeliveryEntity(
                                pedidoId = pedidoId,
                                latitud = update.latitud,
                                longitud = update.longitud,
                                timestamp = Date()
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    
    private fun updateMapLocation(latitud: Double, longitud: Double) {
        val map = mapView ?: return
        if (!isMapInitialized) return
        
        val location = GeoPoint(latitud, longitud)
        
        repartidorMarker?.let { map.overlays.remove(it) }
        
        repartidorMarker = Marker(map).apply {
            position = location
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "🚚 Repartidor"
            icon = ContextCompat.getDrawable(this@DeliveryTrackingActivity, R.drawable.ic_delivery_marker)
                ?: getDefaultMarkerDrawable()
        }
        map.overlays.add(repartidorMarker)
        map.controller.animateTo(location)
        map.invalidate()
        
        // Calcular ruta y tiempo
        destinoGeoPoint?.let { destino ->
            lifecycleScope.launch {
                try {
                    val routeInfo = DirectionsHelper.drawRoute(map, location, destino)
                    routeInfo?.let { info ->
                        runOnUiThread {
                            tvTiempo?.text = "${info.durationMinutes}-${info.durationMinutes + 5}"
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }
    
    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            webSocketClient?.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
