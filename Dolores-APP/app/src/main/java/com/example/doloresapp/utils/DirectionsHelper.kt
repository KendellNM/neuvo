package com.example.doloresapp.utils

import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

/**
 * Helper para obtener rutas usando OSRM (OpenStreetMap)
 * 100% GRATIS - SIN API KEY NECESARIA
 * Servidor público: https://router.project-osrm.org
 */
object DirectionsHelper {
    
    // OSRM - Totalmente gratis, sin API Key
    private const val OSRM_API_URL = "https://router.project-osrm.org/route/v1/driving"
    
    private val client = OkHttpClient()
    
    /**
     * Obtiene y dibuja la ruta entre dos puntos usando OSRM
     * NO REQUIERE API KEY - 100% GRATIS
     * 
     * @param mapView Mapa OSMDroid donde dibujar
     * @param origin Punto de origen (repartidor)
     * @param destination Punto de destino (cliente)
     * @return RouteInfo con distancia y duración, o null si falla
     */
    suspend fun drawRoute(
        mapView: MapView,
        origin: GeoPoint,
        destination: GeoPoint
    ): RouteInfo? = withContext(Dispatchers.IO) {
        try {
            // OSRM usa formato: lng,lat
            val url = "$OSRM_API_URL/${origin.longitude},${origin.latitude};" +
                    "${destination.longitude},${destination.latitude}?" +
                    "overview=full&geometries=polyline"
            
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val jsonData = response.body?.string() ?: return@withContext null
            
            val json = JSONObject(jsonData)
            val code = json.getString("code")
            
            if (code == "Ok") {
                val routes = json.getJSONArray("routes")
                if (routes.length() > 0) {
                    val route = routes.getJSONObject(0)
                    
                    // Obtener geometría (polyline)
                    val geometry = route.getString("geometry")
                    val points = decodePolyline(geometry)
                    
                    // Obtener distancia y duración
                    val distance = route.getDouble("distance") / 1000.0 // metros a km
                    val duration = route.getDouble("duration") / 60.0 // segundos a minutos
                    
                    // Dibujar ruta en el mapa
                    withContext(Dispatchers.Main) {
                        val polyline = Polyline()
                        polyline.setPoints(points)
                        polyline.outlinePaint.color = Color.BLUE
                        polyline.outlinePaint.strokeWidth = 8f
                        
                        // Remover polylines anteriores
                        mapView.overlays.removeAll { it is Polyline }
                        mapView.overlays.add(polyline)
                        mapView.invalidate()
                    }
                    
                    return@withContext RouteInfo(distance, duration.toInt())
                }
            }
            
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Información de la ruta
     */
    data class RouteInfo(
        val distanceKm: Double,
        val durationMinutes: Int
    )
    
    /**
     * Decodifica polyline de Google/OSRM
     */
    private fun decodePolyline(encoded: String): List<GeoPoint> {
        val poly = ArrayList<GeoPoint>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            
            poly.add(GeoPoint(lat.toDouble() / 1E5, lng.toDouble() / 1E5))
        }
        
        return poly
    }
    
    /**
     * Calcula distancia entre dos puntos en kilómetros
     */
    fun calculateDistance(point1: GeoPoint, point2: GeoPoint): Double {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            point1.latitude,
            point1.longitude,
            point2.latitude,
            point2.longitude,
            results
        )
        return results[0] / 1000.0 // Convertir a km
    }
    
    /**
     * Calcula tiempo estimado de llegada (minutos)
     * Asume velocidad promedio de 30 km/h en ciudad
     */
    fun calculateETA(distanceKm: Double, speedKmh: Double = 30.0): Int {
        return ((distanceKm / speedKmh) * 60).toInt()
    }
}
