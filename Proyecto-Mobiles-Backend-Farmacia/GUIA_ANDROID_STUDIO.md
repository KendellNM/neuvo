# 📱 Guía de Integración - Android Studio

## 🎯 INFORMACIÓN GENERAL

**Backend URL:** `http://10.0.2.2:8080` (para emulador)  
**Backend URL:** `http://TU_IP:8080` (para dispositivo físico)

**Autenticación:** Todas las peticiones (excepto login) requieren header:
```
Authorization: Bearer {token}
```

---

## 1️⃣ AUTENTICACIÓN CON DNI

### Endpoint de Login
```
POST /api/auth/login
Content-Type: application/json

Body:
{
  "username": "12345678",    // DNI del cliente
  "password": "password123"
}

Respuesta exitosa:
{
  "token": "eyJhbGc...",
  "roles": ["ROLE_CLIENTE"]
}
```

### Qué debe hacer tu app:
1. Crear pantalla de login con campos: DNI y contraseña
2. Enviar petición POST al endpoint
3. Guardar el token en SharedPreferences
4. Usar el token en todas las peticiones siguientes
5. Si el token expira (error 401), redirigir a login

---

## 2️⃣ ESCANEO QR DE PRODUCTOS

### Cómo funciona:
1. El QR del producto contiene solo el ID (ejemplo: "123")
2. Tu app escanea el QR y obtiene el ID
3. Llamas al endpoint con ese ID
4. Muestras la información del producto

### Endpoint
```
GET /api/productos/{id}/mobile
Authorization: Bearer {token}

Ejemplo: GET /api/productos/123/mobile

Respuesta:
{
  "id": 123,
  "nombre": "Aspirina 500mg",
  "descripcion": "Analgésico...",
  "precio": 5.50,
  "stock": 100,
  "disponible": true,
  "imagenUrl": "http://10.0.2.2:8080/uploads/productos/aspirina.jpg",
  "laboratorioNombre": "Bayer",
  "categoria": "Analgésicos",
  "requiereReceta": false
}
```

### Qué debe hacer tu app:
1. Implementar escáner QR usando CameraX + ML Kit
2. Extraer el ID del QR escaneado
3. Llamar al endpoint GET con el ID
4. Mostrar la información del producto en una pantalla
5. Permitir agregar al carrito o ver más detalles

### Librerías necesarias:
- `androidx.camera:camera-camera2` (CameraX)
- `com.google.mlkit:barcode-scanning` (Escaneo QR)

---

## 3️⃣ RECETAS DIGITALES CON OCR

### Cómo funciona:
1. Usuario toma foto de la receta médica
2. Tu app sube la imagen al servidor
3. El servidor procesa con OCR y detecta medicamentos
4. Retorna el texto extraído y medicamentos encontrados

### Endpoint para subir receta
```
POST /api/recetas-digitales/procesar
Content-Type: multipart/form-data
Authorization: Bearer {token}

Body (FormData):
- imagen: [archivo de imagen]
- clienteId: 1

Respuesta:
{
  "success": true,
  "message": "Receta procesada exitosamente",
  "data": {
    "id": 1,
    "imagenUrl": "http://10.0.2.2:8080/uploads/recetas/receta_123.jpg",
    "textoExtraido": "Aspirina 500mg\nTomar 1 cada 8 horas...",
    "estado": "PENDIENTE",
    "detalles": [
      {
        "medicamentoTexto": "Aspirina 500mg",
        "productoId": 123,
        "productoNombre": "Aspirina 500mg",
        "validado": false
      }
    ]
  }
}
```

### Endpoint para ver recetas del cliente
```
GET /api/recetas-digitales/cliente/{clienteId}
Authorization: Bearer {token}

Respuesta: Lista de todas las recetas del cliente
```

### Estados de receta:
- **PENDIENTE**: Recién subida
- **PROCESADA**: OCR completado
- **VALIDADA**: Aprobada por farmacéutico
- **RECHAZADA**: Rechazada

### Qué debe hacer tu app:
1. Permitir tomar foto o seleccionar de galería
2. Subir imagen usando multipart/form-data
3. Mostrar progreso mientras procesa
4. Mostrar resultado: texto extraído y medicamentos detectados
5. Listar todas las recetas del cliente
6. Mostrar estado de cada receta (pendiente, validada, etc.)

### Librerías necesarias:
- `com.squareup.retrofit2:retrofit` (para multipart)
- CameraX o Intent de cámara nativa

---

## 4️⃣ PROGRAMA DE FIDELIZACIÓN Y PUNTOS

### Cómo funciona:
- Cada cliente tiene puntos acumulados
- Los puntos se ganan con compras
- Hay 4 niveles: BRONCE, PLATA, ORO, PLATINO
- Los puntos se pueden canjear por cupones

### Niveles:
- BRONCE: 0 - 1,999 puntos
- PLATA: 2,000 - 4,999 puntos
- ORO: 5,000 - 9,999 puntos
- PLATINO: 10,000+ puntos

### Endpoint para obtener puntos
```
GET /api/fidelizacion/cliente/{clienteId}
Authorization: Bearer {token}

Respuesta:
{
  "id": 1,
  "clienteId": 1,
  "puntosActuales": 3500,
  "puntosAcumulados": 3500,
  "nivelMembresia": "PLATA",
  "siguienteNivel": "ORO",
  "puntosParaSiguienteNivel": 1500
}
```

### Endpoint para canjear puntos
```
POST /api/fidelizacion/canjear
Content-Type: application/json
Authorization: Bearer {token}

Body:
{
  "clienteId": 1,
  "puntosACanjear": 500,
  "descripcionCupon": "Descuento 10%"
}

Respuesta:
{
  "success": true,
  "message": "Puntos canjeados exitosamente",
  "data": {
    "cuponId": 1,
    "codigo": "CUPON-ABC123",
    "descuento": 10.0,
    "puntosRestantes": 3000
  }
}
```

### Endpoint para historial
```
GET /api/fidelizacion/historial/{clienteId}
Authorization: Bearer {token}

Respuesta: Lista de movimientos (acumulaciones y canjes)
```

### Qué debe hacer tu app:
1. Mostrar pantalla con:
   - Puntos actuales del cliente
   - Nivel de membresía (con icono/color)
   - Barra de progreso al siguiente nivel
   - Puntos que faltan para siguiente nivel
2. Botón para canjear puntos
3. Mostrar historial de movimientos
4. Actualizar puntos después de cada compra

---

## 5️⃣ NOTIFICACIONES PUSH (FIREBASE)

### Cómo funciona:
1. Tu app obtiene el token FCM de Firebase
2. Registras el token en el backend
3. El backend envía notificaciones cuando:
   - Un pedido está listo
   - Hay promociones
   - Recordatorios de medicamentos

### Endpoint para registrar dispositivo
```
POST /api/notificaciones/registrar-dispositivo
Content-Type: application/json
Authorization: Bearer {token}

Body:
{
  "clienteId": 1,
  "fcmToken": "fGHj8K9L0mN1oP2qR3sT4uV5wX6yZ7...",
  "plataforma": "ANDROID"
}
```

### Endpoint para obtener notificaciones
```
GET /api/notificaciones/cliente/{clienteId}
Authorization: Bearer {token}

Respuesta: Lista de notificaciones
[
  {
    "id": 1,
    "titulo": "Pedido Listo",
    "mensaje": "Tu pedido #123 está listo",
    "tipo": "PEDIDO",
    "leida": false,
    "fecha": "2025-11-13T10:30:00"
  }
]
```

### Endpoint para marcar como leída
```
PUT /api/notificaciones/{id}/marcar-leida
Authorization: Bearer {token}
```

### Tipos de notificaciones:
- **PEDIDO**: Estado de pedidos
- **PROMOCION**: Ofertas
- **RECORDATORIO**: Recordatorios
- **SISTEMA**: Notificaciones del sistema

### Qué debe hacer tu app:
1. Configurar Firebase en tu proyecto Android
2. Obtener token FCM al iniciar sesión
3. Registrar el token en el backend
4. Escuchar notificaciones push
5. Mostrar lista de notificaciones en la app
6. Marcar como leídas cuando el usuario las ve
7. Mostrar badge con cantidad de no leídas

### Configuración necesaria:
1. Agregar `google-services.json` a tu proyecto
2. Agregar dependencia: `com.google.firebase:firebase-messaging-ktx`
3. Crear servicio que extienda `FirebaseMessagingService`

---

## 6️⃣ SEGUIMIENTO EN TIEMPO REAL DE DELIVERY

### Cómo funciona:
- Conexión WebSocket para actualizaciones en tiempo real
- El repartidor envía su ubicación cada X segundos
- Tu app recibe las actualizaciones y muestra en mapa
- También recibe cambios de estado del pedido

### Conexión WebSocket
```
URL: ws://10.0.2.2:8080/ws-delivery
```

### Suscribirse a un pedido
```
Suscripción: /topic/delivery/{pedidoId}

Ejemplo: /topic/delivery/123
```

### Mensajes que recibirás:

**Actualización de ubicación:**
```json
{
  "tipo": "UBICACION",
  "pedidoId": 123,
  "latitud": -12.0464,
  "longitud": -77.0428,
  "timestamp": "2025-11-13T10:30:00"
}
```

**Actualización de estado:**
```json
{
  "tipo": "ESTADO",
  "pedidoId": 123,
  "estado": "EN_CAMINO",
  "mensaje": "El repartidor está en camino",
  "timestamp": "2025-11-13T10:30:00"
}
```

### Estados del pedido:
- **PENDIENTE**: Pedido creado
- **PREPARANDO**: En preparación
- **LISTO**: Listo para envío
- **EN_CAMINO**: Repartidor en camino
- **ENTREGADO**: Entregado
- **CANCELADO**: Cancelado

### Qué debe hacer tu app:
1. Conectar al WebSocket cuando el usuario vea el seguimiento
2. Suscribirse al topic del pedido específico
3. Mostrar MapLibre GL con:
   - Ubicación del cliente (destino) - marcador fijo
   - Ubicación del repartidor (actualizada en tiempo real) - marcador móvil
   - Ruta entre ambos puntos (opcional)
4. Mostrar estado actual del pedido
5. Actualizar posición del marcador cuando lleguen nuevas coordenadas
6. Desconectar WebSocket al salir de la pantalla

### Ventajas de MapLibre GL:
- ✅ **Gratuito** - Sin costos de API
- ✅ **Open Source** - Código abierto
- ✅ **Sin límites** - Sin restricciones de uso
- ✅ **Offline** - Puede funcionar sin internet con tiles descargados
- ✅ **Personalizable** - Estilos de mapa personalizados

### Librerías necesarias:
- `org.maplibre.gl:android-sdk` (MapLibre GL - Mapas gratuitos)
- `com.github.NaikSoftware:StompProtocolAndroid` (WebSocket/Stomp)
- `io.reactivex.rxjava2:rxjava` (para Stomp)

### Alternativa sin WebSocket:
Si WebSocket es complicado, puedes hacer polling cada 5-10 segundos:
```
GET /api/delivery/pedido/{pedidoId}/ubicacion
Authorization: Bearer {token}
```

### Configuración de MapLibre GL:

**1. Agregar dependencia:**
```
implementation("org.maplibre.gl:android-sdk:10.2.0")
```

**2. Configurar estilo de mapa (gratuito):**
Usar OpenStreetMap como fuente de tiles:
```
URL del estilo: https://demotiles.maplibre.org/style.json
```

O usar Maptiler (gratuito hasta cierto límite):
```
URL: https://api.maptiler.com/maps/streets/style.json?key=TU_API_KEY
```

**3. Inicializar mapa en tu Activity/Fragment:**
- Crear MapView en el layout XML
- Inicializar con el estilo elegido
- Agregar marcadores para cliente y repartidor
- Actualizar posición del marcador del repartidor cuando lleguen coordenadas

**4. Marcadores:**
- **Marcador azul:** Ubicación del cliente (fijo)
- **Marcador verde/rojo:** Ubicación del repartidor (se mueve)
- Centrar cámara para mostrar ambos marcadores

**5. Recursos útiles:**
- Documentación: https://maplibre.org/maplibre-gl-native/android/
- Ejemplos: https://github.com/maplibre/maplibre-gl-native

---

## 📦 RESUMEN DE DEPENDENCIAS

Agregar en `build.gradle.kts` (Module):

```
dependencies {
    // Retrofit (API REST)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Firebase (Notificaciones)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    
    // CameraX + ML Kit (QR)
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    
    // MapLibre GL (Mapas gratuitos para Delivery)
    implementation("org.maplibre.gl:android-sdk:10.2.0")
    
    // WebSocket (Delivery en tiempo real)
    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    
    // Glide (Cargar imágenes)
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
```

---

## 🔐 PERMISOS NECESARIOS

Agregar en `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

**Nota:** MapLibre GL NO requiere API Key de Google Maps, es completamente gratuito.

---

## ✅ CHECKLIST DE IMPLEMENTACIÓN

### Básico (Obligatorio)
- [ ] Configurar Retrofit con la URL del backend
- [ ] Implementar pantalla de login con DNI
- [ ] Guardar token en SharedPreferences
- [ ] Agregar interceptor para incluir token en peticiones

### Escaneo QR
- [ ] Implementar escáner QR con CameraX
- [ ] Llamar endpoint de producto con ID escaneado
- [ ] Mostrar información del producto

### Recetas Digitales
- [ ] Permitir tomar foto o seleccionar de galería
- [ ] Subir imagen al servidor (multipart)
- [ ] Mostrar lista de recetas del cliente
- [ ] Mostrar detalle de cada receta

### Fidelización
- [ ] Mostrar puntos y nivel del cliente
- [ ] Mostrar barra de progreso
- [ ] Implementar canje de puntos
- [ ] Mostrar historial de movimientos

### Notificaciones Push
- [ ] Configurar Firebase en el proyecto
- [ ] Obtener token FCM
- [ ] Registrar dispositivo en backend
- [ ] Escuchar notificaciones
- [ ] Mostrar lista de notificaciones

### Seguimiento Delivery
- [ ] Integrar MapLibre GL
- [ ] Configurar estilo de mapa (OpenStreetMap)
- [ ] Conectar WebSocket
- [ ] Agregar marcadores (cliente y repartidor)
- [ ] Actualizar posición del repartidor en tiempo real
- [ ] Mostrar estado del pedido

---

## 🚀 ORDEN RECOMENDADO DE IMPLEMENTACIÓN

1. **Primero:** Login y autenticación (base para todo)
2. **Segundo:** Escaneo QR (más simple)
3. **Tercero:** Fidelización (no requiere librerías complejas)
4. **Cuarto:** Recetas digitales (requiere manejo de imágenes)
5. **Quinto:** Notificaciones push (requiere Firebase)
6. **Sexto:** Seguimiento delivery (más complejo, WebSocket + Maps)

---

## 📞 INFORMACIÓN ADICIONAL

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Documentación completa:** Ver `DOCUMENTACION_API.md`
- **Guía de login:** Ver `LOGIN_EXPLICACION.md`

---

**¡Listo para desarrollar!** 🎉

Comparte este documento con el desarrollador de Android.
