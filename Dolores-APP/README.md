# 📱 Dolores APP - Farmacia Móvil

Aplicación Android para Farmacia Dolores con funcionalidades avanzadas.

## ✨ Funcionalidades Implementadas

### 1. 📷 Escaneo QR de Productos

- Escanea códigos QR de productos
- Obtiene información completa del producto desde el backend
- Integración con ZXing para escaneo rápido

### 2. 📋 Recetas Digitales con OCR

- Captura fotos de recetas médicas
- Procesamiento OCR en el backend (Tesseract)
- Detección automática de medicamentos
- Estados: PENDIENTE, PROCESADA, VALIDADA, RECHAZADA

### 3. 🎁 Programa de Fidelización

- Acumulación de puntos por compras
- 4 niveles: BRONCE, PLATA, ORO, PLATINO
- Canje de puntos por cupones
- Historial completo de movimientos

### 4. 🚚 Seguimiento en Tiempo Real

- WebSocket para tracking de delivery
- Mapa con ubicación del repartidor
- Actualización en tiempo real
- Historial de ubicaciones guardado localmente

### 5. 💾 Base de Datos Local (SQLite)

- Room Database para caché offline
- Sincronización con backend
- Productos, pedidos, recetas, notificaciones

## 🏗️ Arquitectura

### Clean Architecture

```
presentation/
  ├── ui/          # Activities y Fragments
  ├── viewmodel/   # ViewModels
  └── adapters/    # RecyclerView Adapters

domain/
  ├── model/       # Modelos de dominio
  ├── repository/  # Interfaces de repositorios
  └── usecase/     # Casos de uso

data/
  ├── local/       # Room Database
  │   ├── dao/
  │   ├── entity/
  │   └── database/
  ├── remote/      # Retrofit API
  │   ├── api/
  │   ├── dto/
  │   └── websocket/
  └── repository/  # Implementación de repositorios
```

### Componentes Reutilizables

#### ApiConstants.kt

Centraliza todas las constantes de la API:

```kotlin
ApiConstants.BASE_URL
ApiConstants.Productos.GET_BY_QR
ApiConstants.EstadoPedido.EN_CAMINO
```

#### Result.kt

Manejo de estados de operaciones:

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T)
    data class Error(val message: String)
    object Loading
}
```

#### ServiceLocator.kt

Inyección de dependencias manual:

```kotlin
ServiceLocator.provideQRApiService()
ServiceLocator.provideRecetaApiService()
```

## 🔧 Configuración

### 1. Backend

Asegúrate de que el backend esté corriendo en:

```
http://localhost:8090
```

### 2. URL de Conexión

En `ApiConstants.kt`, la URL está configurada para:

- **Emulador Android**: `http://10.0.2.2:8090/`
- **Dispositivo físico**: Cambiar a IP de tu PC (ej: `http://192.168.1.100:8090/`)

### 3. Google Maps API Key

1. Obtén una API Key de Google Maps
2. En `AndroidManifest.xml`, reemplaza:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="TU_API_KEY_AQUI" />
```

### 4. Permisos

La app solicita automáticamente:

- ✅ CAMERA (para QR y recetas)
- ✅ INTERNET (para API)
- ✅ ACCESS_FINE_LOCATION (para tracking)

## 📦 Dependencias Principales

```gradle
// Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")

// Database
implementation("androidx.room:room-runtime:2.6.0")
kapt("androidx.room:room-compiler:2.6.0")

// QR Scanner
implementation("com.journeyapps:zxing-android-embedded:4.3.0")

// Maps
implementation("com.google.android.gms:play-services-maps:18.2.0")

// WebSocket
implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")

// Image Loading
implementation("com.github.bumptech.glide:glide:4.16.0")
```

## 🚀 Uso

### Escanear QR

```kotlin
val intent = Intent(this, QRScannerActivity::class.java)
startActivityForResult(intent, REQUEST_QR_SCANNER)
```

### Procesar Receta

```kotlin
val intent = Intent(this, RecetaDigitalActivity::class.java)
startActivity(intent)
```

### Ver Puntos

```kotlin
val intent = Intent(this, FidelizacionActivity::class.java)
startActivity(intent)
```

### Tracking de Pedido

```kotlin
val intent = Intent(this, DeliveryTrackingActivity::class.java)
intent.putExtra("pedido_id", pedidoId)
startActivity(intent)
```

## 🗄️ Base de Datos Local

### Tablas

- `productos` - Caché de productos
- `pedidos` - Pedidos del cliente
- `notificaciones` - Historial de notificaciones
- `recetas_digitales` - Recetas procesadas
- `ubicaciones_delivery` - Tracking de entregas

### Acceso

```kotlin
val database = AppDatabase.getDatabase(context)
val productos = database.productoDao().getAllProductos()
```

## 🔐 Autenticación

### Login

```kotlin
val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
prefs.edit()
    .putString(ApiConstants.Prefs.TOKEN, token)
    .putLong(ApiConstants.Prefs.USER_ID, userId)
    .putBoolean(ApiConstants.Prefs.IS_LOGGED_IN, true)
    .apply()
```

### Obtener Token

```kotlin
val token = prefs.getString(ApiConstants.Prefs.TOKEN, null)
```

## 📱 Pantallas Implementadas

1. **QRScannerActivity** - Escaneo de productos
2. **RecetaDigitalActivity** - Subir y procesar recetas
3. **FidelizacionActivity** - Puntos y canje
4. **DeliveryTrackingActivity** - Seguimiento en tiempo real

## 🧪 Testing

### Credenciales de Prueba

```
Cliente:
- DNI: 12345678
- Password: password123

Admin:
- Email: admin@test.com
- Password: password123
```

### Pedido de Prueba

- ID: 123
- Estado: EN_CAMINO
- WebSocket: `/topic/delivery/123`

## 📝 Notas Importantes

1. **Sin Firebase**: Esta versión NO usa Firebase, ya que el backend no lo requiere
2. **WebSocket**: Conecta directamente sin autenticación (según backend)
3. **OCR**: El procesamiento OCR se hace en el backend con Tesseract
4. **Offline First**: Los datos se guardan localmente y se sincronizan

## 🐛 Troubleshooting

### Error de conexión

- Verifica que el backend esté corriendo
- Revisa la URL en `ApiConstants.kt`
- Para dispositivo físico, usa la IP de tu PC

### QR no escanea

- Verifica permisos de cámara
- Asegúrate de que el QR contenga solo el ID del producto

### WebSocket no conecta

- Verifica que el backend tenga WebSocket habilitado
- URL correcta: `ws://10.0.2.2:8090/ws-delivery`

## 📚 Documentación del Backend

Ver Swagger UI del backend:

```
http://localhost:8090/swagger-ui.html
```

## 🎯 Próximos Pasos

- [ ] Implementar notificaciones locales
- [ ] Agregar más filtros de productos
- [ ] Mejorar UI/UX
- [ ] Agregar tests unitarios
- [ ] Implementar paginación

## 👨‍💻 Desarrollo

```bash
# Compilar
./gradlew build

# Instalar en dispositivo
./gradlew installDebug

# Limpiar
./gradlew clean
```

---

**Versión**: 1.0.0  
**Última actualización**: 2025-01-25
