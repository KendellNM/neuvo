# 🔌 WebSocket - Conexión Simple

## ✅ CONFIGURACIÓN ACTUALIZADA

El WebSocket está configurado **SIN autenticación** para facilitar pruebas y desarrollo.

**No necesitas token para conectar.**

---

## 🔌 CÓMO CONECTAR (SIN TOKEN)

### 1️⃣ En Postman

#### Conectar WebSocket
1. Crear **"New WebSocket Request"**
2. URL: `ws://localhost:8080/ws-delivery`
3. Click **"Connect"** (sin headers, sin token)

#### Paso 3: Suscribirse a un pedido
Enviar mensaje:
```json
{
  "type": "SUBSCRIBE",
  "destination": "/topic/delivery/123"
}
```

#### Paso 4: Enviar ubicación
```json
{
  "type": "MESSAGE",
  "destination": "/app/delivery/location",
  "body": {
    "pedidoId": 123,
    "latitud": -12.0464,
    "longitud": -77.0428
  }
}
```

---

### 2️⃣ En JavaScript (Web)

```javascript
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

// Conectar (sin token)
const socket = new SockJS('http://localhost:8080/ws-delivery');
const stompClient = Stomp.over(socket);

// Conectar sin autenticación
stompClient.connect(
  {},  // Sin headers
  function(frame) {
    console.log('Conectado:', frame);
    
    // Suscribirse a un pedido
    stompClient.subscribe('/topic/delivery/123', function(message) {
      const data = JSON.parse(message.body);
      console.log('Mensaje recibido:', data);
    });
  },
  function(error) {
    console.error('Error de conexión:', error);
  }
);
```

---

### 3️⃣ En Android (Kotlin)

```kotlin
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

class DeliveryTracker {
    private var stompClient: StompClient? = null
    
    fun conectar(pedidoId: Long) {
        val url = "http://10.0.2.2:8080/ws-delivery"
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        
        // Conectar sin headers
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
            Log.d("WebSocket", "Ubicación recibida: $data")
            
            // Actualizar UI con la nueva ubicación
            val latitud = data.getDouble("latitud")
            val longitud = data.getDouble("longitud")
            actualizarMapa(latitud, longitud)
        }
    }
    
    fun desconectar() {
        stompClient?.disconnect()
    }
}
```

---

### 4️⃣ En React Native

```javascript
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const conectarDelivery = (pedidoId) => {
  const socket = new SockJS('http://10.0.2.2:8080/ws-delivery');
  const stompClient = Stomp.over(socket);
  
  stompClient.connect(
    {},  // Sin headers
    () => {
      console.log('WebSocket conectado');
      
      // Suscribirse al pedido
      stompClient.subscribe(`/topic/delivery/${pedidoId}`, (message) => {
        const ubicacion = JSON.parse(message.body);
        console.log('Nueva ubicación:', ubicacion);
        
        // Actualizar estado
        setUbicacionRepartidor({
          latitude: ubicacion.latitud,
          longitude: ubicacion.longitud
        });
      });
    },
    (error) => {
      console.error('Error WebSocket:', error);
    }
  );
  
  return stompClient;
};
```

---

## � SEGGURIDAD

### Sin Autenticación

El WebSocket está configurado **sin autenticación** para:
- ✅ Facilitar pruebas con Postman
- ✅ Simplificar desarrollo
- ✅ Evitar problemas de CORS y headers

**Nota:** En producción, puedes agregar autenticación si es necesario, pero para pruebas y desarrollo funciona sin token.

---

## 🧪 PRUEBAS

### Probar conexión exitosa:
```
1. Login → Obtener token
2. Conectar WebSocket con token en header
3. Suscribirse a /topic/delivery/123
4. Enviar mensaje de ubicación
5. Verificar que recibes el mensaje
```

### Probar token inválido:
```
1. Conectar con token incorrecto
2. La conexión se establece pero sin autenticación
3. Los logs del backend mostrarán: "Token WebSocket inválido"
```

---

## 🐛 TROUBLESHOOTING

### Error: "WebSocket connection failed"
**Causa:** Backend no está corriendo o URL incorrecta
**Solución:** 
- Verifica que el backend esté en `http://localhost:8080`
- Usa `ws://` no `wss://`

### Error: "Token inválido"
**Causa:** Token expirado o incorrecto
**Solución:**
- Haz login nuevamente para obtener un token fresco
- Verifica que estás copiando el token completo

### No recibo mensajes
**Causa:** No estás suscrito al topic correcto
**Solución:**
- Verifica el topic: `/topic/delivery/{pedidoId}`
- El `pedidoId` en el mensaje debe coincidir con el topic

### Error: "Header 'token' not found"
**Causa:** No estás enviando el token en el header
**Solución:**
- En Postman: Agregar en pestaña "Headers"
- En código: Pasar como primer parámetro de `connect()`

---

## 📝 EJEMPLO COMPLETO DE PRUEBA

### 1. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@farmacia.com","password":"admin123"}'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkBmYXJtYWNpYS5jb20iLCJpYXQiOjE2OTk5MDAwMDAsImV4cCI6MTY5OTk4NjQwMH0.abc123...",
  "roles": ["ROLE_ADMIN"]
}
```

### 2. Conectar WebSocket en Postman
- URL: `ws://localhost:8080/ws-delivery`
- Header: `token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
- Connect

### 3. Suscribirse
```json
{
  "type": "SUBSCRIBE",
  "destination": "/topic/delivery/123"
}
```

### 4. Enviar ubicación
```json
{
  "type": "MESSAGE",
  "destination": "/app/delivery/location",
  "body": {
    "pedidoId": 123,
    "latitud": -12.0464,
    "longitud": -77.0428
  }
}
```

### 5. Recibir mensaje
```json
{
  "tipo": "UBICACION",
  "pedidoId": 123,
  "latitud": -12.0464,
  "longitud": -77.0428,
  "timestamp": "2025-11-13T10:30:00"
}
```

---

## ✅ VENTAJAS DE ESTA IMPLEMENTACIÓN

1. ✅ **Seguro** - Token JWT validado
2. ✅ **Simple** - Token en header, fácil de usar
3. ✅ **Compatible** - Funciona con Postman, web y móvil
4. ✅ **Flexible** - Permite conexiones sin token para pruebas
5. ✅ **Estándar** - Usa el patrón común de autenticación WebSocket

---

**🎉 ¡Ahora puedes conectar WebSocket con tu token JWT!**
