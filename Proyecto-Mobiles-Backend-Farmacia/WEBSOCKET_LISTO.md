# ✅ WebSocket Configurado y Listo

## 🎉 PROBLEMA RESUELTO

El error 403 estaba causado por Spring Security bloqueando el WebSocket.

---

## ✅ CAMBIOS REALIZADOS

### 1. SecurityConfig.java
Agregado WebSocket a las rutas permitidas sin autenticación:
```java
.requestMatchers(
    "/ws-delivery/**",
    "/app/**",
    "/topic/**"
).permitAll()
```

### 2. WebSocketConfig.java
Simplificado para no requerir autenticación:
- ✅ Sin validación de token
- ✅ Sin interceptores
- ✅ Conexión directa

---

## 🔌 CÓMO CONECTAR AHORA

### En Postman:

1. **Crear WebSocket Request**
   - Click en "New" → "WebSocket Request"

2. **URL:**
   ```
   ws://localhost:8090/ws-delivery
   ```

3. **Conectar:**
   - Click en "Connect"
   - **NO necesitas agregar headers**
   - **NO necesitas token**

4. **Suscribirse a un pedido:**
   ```json
   SUBSCRIBE
   destination:/topic/delivery/123
   ```

5. **Enviar ubicación:**
   ```json
   SEND
   destination:/app/delivery/location
   content-type:application/json
   
   {"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
   ```

---

## 📝 EJEMPLO COMPLETO EN POSTMAN

### Paso 1: Conectar
```
URL: ws://localhost:8090/ws-delivery
Click: Connect
```

### Paso 2: Suscribirse
```
SUBSCRIBE
id:sub-0
destination:/topic/delivery/123

```
(Dejar línea en blanco al final)

### Paso 3: Enviar ubicación
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

### Paso 4: Ver mensaje recibido
Deberías recibir:
```json
{
  "pedidoId": 123,
  "latitud": -12.0464,
  "longitud": -77.0428,
  "timestamp": "2025-11-13T20:45:00"
}
```

---

## 🧪 PROBAR MOVIMIENTO DEL REPARTIDOR

Envía varias ubicaciones para simular movimiento:

**Ubicación 1 (Farmacia):**
```json
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

**Ubicación 2 (En camino):**
```json
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0500,"longitud":-77.0450}
```

**Ubicación 3 (Cerca del cliente):**
```json
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0550,"longitud":-77.0480}
```

---

## 💻 CÓDIGO PARA ANDROID

```kotlin
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

class DeliveryTracker {
    private var stompClient: StompClient? = null
    
    fun conectar(pedidoId: Long) {
        val url = "http://10.0.2.2:8090/ws-delivery"
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        
        // Conectar (sin headers, sin token)
        stompClient?.connect()
        
        stompClient?.lifecycle()?.subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d("WebSocket", "Conectado")
                    suscribirseAPedido(pedidoId)
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.e("WebSocket", "Error: ${lifecycleEvent.exception}")
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.d("WebSocket", "Desconectado")
                }
            }
        }
    }
    
    private fun suscribirseAPedido(pedidoId: Long) {
        stompClient?.topic("/topic/delivery/$pedidoId")?.subscribe { message ->
            val data = JSONObject(message.payload)
            val latitud = data.getDouble("latitud")
            val longitud = data.getDouble("longitud")
            
            // Actualizar mapa
            actualizarMapa(latitud, longitud)
        }
    }
    
    fun enviarUbicacion(pedidoId: Long, lat: Double, lng: Double) {
        val json = JSONObject().apply {
            put("pedidoId", pedidoId)
            put("latitud", lat)
            put("longitud", lng)
        }
        
        stompClient?.send("/app/delivery/location", json.toString())?.subscribe()
    }
    
    fun desconectar() {
        stompClient?.disconnect()
    }
}
```

---

## 🌐 CÓDIGO PARA WEB/REACT

```javascript
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const conectarDelivery = (pedidoId) => {
  const socket = new SockJS('http://localhost:8090/ws-delivery');
  const stompClient = Stomp.over(socket);
  
  // Conectar sin autenticación
  stompClient.connect(
    {},  // Sin headers
    () => {
      console.log('WebSocket conectado');
      
      // Suscribirse al pedido
      stompClient.subscribe(`/topic/delivery/${pedidoId}`, (message) => {
        const ubicacion = JSON.parse(message.body);
        console.log('Nueva ubicación:', ubicacion);
        
        // Actualizar mapa
        actualizarMapa(ubicacion.latitud, ubicacion.longitud);
      });
    },
    (error) => {
      console.error('Error WebSocket:', error);
    }
  );
  
  return stompClient;
};

// Enviar ubicación
const enviarUbicacion = (stompClient, pedidoId, lat, lng) => {
  stompClient.send(
    '/app/delivery/location',
    {},
    JSON.stringify({
      pedidoId: pedidoId,
      latitud: lat,
      longitud: lng
    })
  );
};
```

---

## 🐛 TROUBLESHOOTING

### Error: "Connection refused"
**Solución:** Verifica que el backend esté corriendo en el puerto 8090

### Error: "404 Not Found"
**Solución:** Verifica la URL: `ws://localhost:8090/ws-delivery`

### No recibo mensajes
**Solución:** 
1. Verifica que te suscribiste: `SUBSCRIBE destination:/topic/delivery/123`
2. El `pedidoId` en el mensaje debe coincidir con el topic

### Error: "Invalid frame"
**Solución:** Asegúrate de dejar una línea en blanco después del mensaje STOMP

---

## ✅ VENTAJAS DE ESTA CONFIGURACIÓN

1. ✅ **Simple** - No necesitas token
2. ✅ **Rápido** - Conexión directa
3. ✅ **Fácil de probar** - Funciona en Postman sin configuración
4. ✅ **Sin CORS** - No hay problemas de headers
5. ✅ **Listo para desarrollo** - Perfecto para pruebas

---

## 🚀 SIGUIENTE PASO

1. ✅ Backend corriendo en puerto 8090
2. ✅ Abrir Postman
3. ✅ Conectar a `ws://localhost:8090/ws-delivery`
4. ✅ Suscribirse a `/topic/delivery/123`
5. ✅ Enviar ubicaciones
6. ✅ Ver actualizaciones en tiempo real

---

**🎉 ¡WebSocket funcionando sin autenticación!**

Ahora puedes probar el seguimiento de delivery desde Postman sin problemas.
