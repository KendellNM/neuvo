# 🚚 Tracking en Tiempo Real - Guía Completa

## ✅ Implementación Completa

### 🎯 Dos Roles Implementados:

1. **👤 Cliente** - `DeliveryTrackingActivity` - VE la ubicación del repartidor
2. **🚚 Repartidor** - `RepartidorActivity` - ENVÍA su ubicación

---

## 🗺️ Rutas en el Mapa - SIN API KEY

### OSRM (OpenStreetMap Routing Machine)

**✅ 100% GRATIS - SIN API KEY**

#### Características:

- Servidor público: `https://router.project-osrm.org`
- Sin límites de requests
- Sin registro necesario
- Basado en OpenStreetMap (datos abiertos)
- Calcula rutas optimizadas
- Devuelve distancia y tiempo estimado

#### Implementación:

**Archivo:** `DirectionsHelper.kt`

```kotlin
// Uso simple - NO requiere API Key
val routeInfo = DirectionsHelper.drawRoute(
    googleMap,
    origenLatLng,
    destinoLatLng
)

// Retorna distancia (km) y tiempo (minutos)
routeInfo?.let {
    println("Distancia: ${it.distanceKm} km")
    println("Tiempo: ${it.durationMinutes} min")
}
```

---

## 🚚 Modo Repartidor

### RepartidorActivity

#### Funcionalidades:

- ✅ Obtiene ubicación GPS cada 5 segundos
- ✅ Envía ubicación al backend vía WebSocket
- ✅ Muestra mapa con destino marcado
- ✅ Calcula distancia al destino
- ✅ Botones Iniciar/Detener tracking

#### Flujo:

1. Repartidor abre la app
2. Presiona "Iniciar"
3. App obtiene ubicación GPS
4. Envía ubicación por WebSocket: `/app/delivery/location`
5. Backend distribuye a clientes suscritos: `/topic/delivery/{pedidoId}`

#### Código de Ejemplo:

```kotlin
// Abrir modo repartidor
val intent = Intent(this, RepartidorActivity::class.java)
intent.putExtra("pedido_id", 123L)
intent.putExtra("destino_lat", -12.0464)
intent.putExtra("destino_lng", -77.0428)
startActivity(intent)
```

#### Permisos Necesarios:

- `ACCESS_FINE_LOCATION` - Para GPS
- `INTERNET` - Para WebSocket

---

## 👤 Modo Cliente

### DeliveryTrackingActivity

#### Funcionalidades:

- ✅ Se conecta a WebSocket
- ✅ Se suscribe a `/topic/delivery/{pedidoId}`
- ✅ Recibe ubicación del repartidor en tiempo real
- ✅ Muestra marcador del repartidor en el mapa
- ✅ Dibuja ruta desde repartidor hasta destino
- ✅ Muestra distancia y tiempo estimado
- ✅ Guarda historial en SQLite

#### Flujo:

1. Cliente abre tracking de su pedido
2. App se conecta a WebSocket
3. Se suscribe al pedido específico
4. Recibe actualizaciones cada 5 segundos
5. Actualiza mapa y calcula ruta

#### Código de Ejemplo:

```kotlin
// Abrir tracking como cliente
val intent = Intent(this, DeliveryTrackingActivity::class.java)
intent.putExtra("pedido_id", 123L)
intent.putExtra("destino_lat", -12.0464) // Dirección del cliente
intent.putExtra("destino_lng", -77.0428)
startActivity(intent)
```

---

## 🔄 Flujo Completo WebSocket

### 1. Repartidor Envía Ubicación

```kotlin
// RepartidorActivity
webSocketClient.sendLocation(pedidoId, latitud, longitud)
```

**Mensaje enviado:**

```json
SEND
destination:/app/delivery/location

{
  "pedidoId": 123,
  "latitud": -12.0464,
  "longitud": -77.0428
}
```

### 2. Backend Procesa y Distribuye

El backend recibe en `/app/delivery/location` y envía a todos los suscritos:

```java
@MessageMapping("/delivery/location")
public void updateLocation(UbicacionRequest request) {
    messagingTemplate.convertAndSend(
        "/topic/delivery/" + request.getPedidoId(),
        request
    );
}
```

### 3. Cliente Recibe Actualización

```kotlin
// DeliveryTrackingActivity
webSocketClient.subscribeToDelivery(pedidoId) { update ->
    updateMapLocation(update.latitud, update.longitud)
    drawRoute(update.latitud, update.longitud)
}
```

**Mensaje recibido:**

```json
MESSAGE
destination:/topic/delivery/123

{
  "tipo": "UBICACION",
  "pedidoId": 123,
  "latitud": -12.0464,
  "longitud": -77.0428,
  "timestamp": "2025-01-25T10:30:00"
}
```

---

## 📍 Cálculo de Rutas con OSRM

### Request a OSRM:

```
GET https://router.project-osrm.org/route/v1/driving/
    -77.0428,-12.0464;-77.0500,-12.0500?
    overview=full&geometries=polyline
```

**Nota:** OSRM usa formato `lng,lat` (al revés de Google)

### Response:

```json
{
  "code": "Ok",
  "routes": [
    {
      "geometry": "encoded_polyline_string",
      "distance": 5234.5, // metros
      "duration": 420.3 // segundos
    }
  ]
}
```

### Decodificación de Polyline:

El helper `DirectionsHelper.decodePolyline()` convierte el string codificado en lista de `LatLng` para dibujar en el mapa.

---

## 🎨 Visualización en el Mapa

### Elementos Mostrados:

#### En Modo Repartidor:

- 🔵 Ubicación actual (punto azul)
- 📍 Marcador de destino
- 📏 Distancia al destino

#### En Modo Cliente:

- 🚚 Marcador del repartidor (actualizado en tiempo real)
- 🏠 Marcador del destino (dirección del cliente)
- 🛣️ Ruta dibujada (línea azul)
- 📍 Distancia restante
- ⏱️ Tiempo estimado de llegada

---

## 🔧 Configuración

### 1. Backend WebSocket

Asegúrate de que el backend tenga:

```
ws://localhost:8090/ws-delivery
```

### 2. Permisos en AndroidManifest

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

### 3. Google Maps API Key

Aunque OSRM no requiere API Key, Google Maps sí:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="TU_API_KEY_AQUI" />
```

**Obtener API Key:**

1. https://console.cloud.google.com/
2. Crear proyecto
3. Habilitar "Maps SDK for Android"
4. Crear credencial (API Key)
5. Copiar y pegar en AndroidManifest

---

## 🧪 Testing

### Simular Repartidor en Emulador:

1. **Abrir Extended Controls** (⋮ en emulador)
2. **Location** → Ingresar coordenadas
3. **Send** para simular movimiento

### Coordenadas de Prueba (Lima, Perú):

```
Inicio:  -12.0464, -77.0428
Punto 2: -12.0500, -77.0450
Punto 3: -12.0550, -77.0480
Destino: -12.0600, -77.0500
```

### Testing con 2 Dispositivos:

1. **Dispositivo 1 (Repartidor):**

   - Abrir `RepartidorActivity`
   - Iniciar tracking
   - Caminar o simular movimiento

2. **Dispositivo 2 (Cliente):**
   - Abrir `DeliveryTrackingActivity`
   - Ver ubicación del repartidor actualizarse
   - Ver ruta dibujada

---

## 📊 Frecuencia de Actualización

### Configuración Actual:

```kotlin
UPDATE_INTERVAL = 5000L      // 5 segundos
FASTEST_INTERVAL = 3000L     // 3 segundos mínimo
```

### Recomendaciones:

**Para Desarrollo:**

- 5 segundos (actual) ✅

**Para Producción:**

- 10-15 segundos (ahorra batería)
- Solo cuando pedido está "EN_CAMINO"

**Para Ahorro de Batería:**

```kotlin
UPDATE_INTERVAL = 15000L     // 15 segundos
FASTEST_INTERVAL = 10000L    // 10 segundos
```

---

## 💡 Optimizaciones

### 1. Actualizar Solo Cuando Necesario

```kotlin
// Solo enviar si se movió más de 10 metros
if (lastLocation.distanceTo(newLocation) > 10) {
    webSocketClient.sendLocation(...)
}
```

### 2. Detener Tracking Automáticamente

```kotlin
// Detener cuando llegue al destino (< 50 metros)
if (distanceToDestination < 50) {
    stopTracking()
    notifyArrival()
}
```

### 3. Reconexión Automática

```kotlin
// Si WebSocket se desconecta, reconectar
webSocketClient.connect(
    onError = {
        Handler().postDelayed({ reconnect() }, 5000)
    }
)
```

---

## 🐛 Troubleshooting

### WebSocket no conecta:

- ✅ Verificar que backend esté corriendo
- ✅ URL correcta: `ws://10.0.2.2:8090/ws-delivery` (emulador)
- ✅ Para dispositivo físico: usar IP de tu PC

### GPS no funciona:

- ✅ Verificar permisos en Settings
- ✅ Habilitar ubicación en dispositivo
- ✅ En emulador: usar Extended Controls

### Ruta no se dibuja:

- ✅ Verificar conexión a internet
- ✅ OSRM servidor público puede estar lento
- ✅ Verificar coordenadas válidas

### Mapa no carga:

- ✅ Verificar Google Maps API Key
- ✅ Habilitar "Maps SDK for Android" en Google Cloud
- ✅ Verificar permisos de ubicación

---

## 📱 Alternativas a OSRM

### Si OSRM no funciona:

#### 1. GraphHopper (Gratis 500 req/día)

```kotlin
private const val GRAPHHOPPER_URL =
    "https://graphhopper.com/api/1/route"
// Requiere API Key gratis
```

#### 2. Mapbox (50k req/mes gratis)

```kotlin
private const val MAPBOX_URL =
    "https://api.mapbox.com/directions/v5/mapbox/driving"
// Requiere API Key gratis
```

#### 3. Google Directions (Requiere billing)

```kotlin
private const val GOOGLE_URL =
    "https://maps.googleapis.com/maps/api/directions/json"
// Requiere API Key con billing habilitado
```

---

## 📚 Archivos Creados

1. `RepartidorActivity.kt` - Modo repartidor
2. `DeliveryTrackingActivity.kt` - Modo cliente (mejorado)
3. `DirectionsHelper.kt` - Helper para rutas con OSRM
4. `activity_repartidor.xml` - Layout repartidor
5. `activity_delivery_tracking.xml` - Layout cliente (mejorado)

---

## ✅ Checklist de Implementación

### Backend:

- [x] WebSocket endpoint `/ws-delivery`
- [x] Topic `/topic/delivery/{pedidoId}`
- [x] Endpoint `/app/delivery/location`

### App Android:

- [x] RepartidorActivity (envía ubicación)
- [x] DeliveryTrackingActivity (recibe ubicación)
- [x] WebSocket client (STOMP)
- [x] DirectionsHelper (OSRM)
- [x] Permisos de ubicación
- [x] Google Maps integrado
- [x] SQLite para historial

---

## 🎯 Resultado Final

### Cliente ve:

- 🚚 Ubicación del repartidor en tiempo real
- 🛣️ Ruta desde repartidor hasta su casa
- 📍 Distancia restante (ej: 2.5 km)
- ⏱️ Tiempo estimado (ej: 8 minutos)

### Repartidor ve:

- 📍 Su ubicación actual
- 🏠 Destino de entrega
- 📏 Distancia al destino
- ▶️ Botones para iniciar/detener

**¡Todo funciona sin API Key de rutas gracias a OSRM!** 🎉

---

**Última actualización:** 2025-01-25
