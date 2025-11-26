# Guía Detallada para Probar WebSocket con STOMP

## 🔍 Diagnóstico del Problema

Actualmente solo ves logs de DISCONNECT, lo que significa que:

- ✅ La conexión WebSocket se establece
- ❌ El handshake STOMP NO se completa
- ❌ No se envía el frame CONNECT de STOMP

## 📋 Pasos para Conectar Correctamente

### Opción 1: Postman (Recomendado)

1. **Crear Nueva Request WebSocket**

   - Click en "New" → "WebSocket Request"
   - URL: `ws://localhost:8090/ws-delivery`
   - **NO uses `/websocket` al final**

2. **Conectar al WebSocket**

   - Click en "Connect"
   - Deberías ver "Connected" en verde

3. **Enviar Frame CONNECT de STOMP**

   ```
   CONNECT
   accept-version:1.1,1.2
   heart-beat:10000,10000


   ```

   **IMPORTANTE:**

   - Debe haber una línea en blanco al final
   - Usa el carácter NULL (`\0`) o simplemente Enter dos veces

4. **Deberías Recibir CONNECTED**

   ```
   CONNECTED
   version:1.2
   heart-beat:0,0


   ```

5. **Suscribirse a un Topic**

   ```
   SUBSCRIBE
   id:sub-0
   destination:/topic/delivery/123


   ```

6. **Enviar Ubicación**

   ```
   SEND
   destination:/app/delivery/location
   content-type:application/json

   {"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428,"repartidorId":1}
   ```

### Opción 2: Cliente JavaScript (Para Testing)

```javascript
// Usando SockJS + STOMP
const socket = new SockJS("http://localhost:8090/ws-delivery");
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
  console.log("Connected: " + frame);

  // Suscribirse
  stompClient.subscribe("/topic/delivery/123", function (message) {
    console.log("Mensaje recibido:", JSON.parse(message.body));
  });

  // Enviar ubicación
  stompClient.send(
    "/app/delivery/location",
    {},
    JSON.stringify({
      pedidoId: 123,
      latitud: -12.0464,
      longitud: -77.0428,
      repartidorId: 1,
    })
  );
});
```

### Opción 3: wscat (Línea de Comandos)

```bash
# Instalar wscat
npm install -g wscat

# Conectar
wscat -c ws://localhost:8090/ws-delivery/websocket

# Luego enviar manualmente:
CONNECT
accept-version:1.2
heart-beat:0,0

^@

# (^@ es Ctrl+@ para enviar NULL byte)
```

## 🐛 Problemas Comunes

### 1. Error: "Connection closed immediately"

**Causa:** No estás enviando el frame CONNECT de STOMP
**Solución:** Asegúrate de enviar CONNECT después de establecer la conexión WebSocket

### 2. Error: "404 Not Found"

**Causa:** URL incorrecta
**Solución:**

- Con SockJS: `ws://localhost:8090/ws-delivery`
- Sin SockJS: `ws://localhost:8090/ws-delivery/websocket`

### 3. No recibes mensajes

**Causa:** No estás suscrito al topic correcto
**Solución:** Verifica que el destination en SUBSCRIBE coincida con el topic del servidor

## 📊 Logs Esperados

Cuando todo funcione correctamente, deberías ver:

```
🔌 CONNECT recibido - Session: xxx
   Headers: {accept-version=[1.1,1.2], heart-beat=[10000,10000]}
✅ CONNECTED - Session: xxx
📡 SUBSCRIBE - Destination: /topic/delivery/123, Session: xxx
📨 SEND recibido - Destination: /app/delivery/location, Session: xxx
   Payload: {"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
📍 Ubicación recibida - Pedido: 123, Lat: -12.0464, Lng: -77.0428
📤 Enviando a topic: /topic/delivery/123
✅ Mensaje enviado exitosamente
```

## 🔧 Testing Rápido con cURL

Para verificar que el endpoint existe:

```bash
curl -i -N -H "Connection: Upgrade" -H "Upgrade: websocket" -H "Host: localhost:8090" -H "Origin: http://localhost:8090" http://localhost:8090/ws-delivery/info
```

Deberías recibir información sobre el servidor SockJS.

## 📝 Formato de Mensajes STOMP

### Frame CONNECT

```
CONNECT
accept-version:1.1,1.2
heart-beat:10000,10000

^@
```

### Frame SUBSCRIBE

```
SUBSCRIBE
id:sub-0
destination:/topic/delivery/123

^@
```

### Frame SEND

```
SEND
destination:/app/delivery/location
content-type:application/json
content-length:85

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428,"repartidorId":1}^@
```

### Frame DISCONNECT

```
DISCONNECT
receipt:77

^@
```

**Nota:** `^@` representa el byte NULL (carácter 0x00) que termina cada frame STOMP.

## 🎯 Siguiente Paso

Intenta conectarte siguiendo estos pasos y comparte los logs que veas. Si sigues sin ver el frame CONNECT, el problema está en cómo tu cliente está enviando los mensajes STOMP.
