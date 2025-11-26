# 📡 Cómo Usar WebSocket en Postman - Guía Paso a Paso

## ⚠️ IMPORTANTE: Formato STOMP

WebSocket usa el protocolo **STOMP**, NO JSON directo.

---

## 🔌 PASO 1: CONECTAR

### En Postman:
1. Click en **"New"** → **"WebSocket Request"**
2. URL: `ws://localhost:8090/ws-delivery`
3. Click en **"Connect"**
4. Espera a que diga "Connected"

---

## 📨 PASO 2: SUSCRIBIRSE A UN PEDIDO

### Formato correcto (STOMP):
En el campo de mensaje de Postman, escribe **EXACTAMENTE** esto:

```
SUBSCRIBE
id:sub-0
destination:/topic/delivery/123

```

**IMPORTANTE:**
- ✅ Escribe línea por línea
- ✅ Deja una línea en blanco al final
- ✅ NO uses JSON
- ✅ NO uses comillas

### Presiona "Send"

Deberías ver en la consola:
```
Subscribed to /topic/delivery/123
```

---

## 📍 PASO 3: ENVIAR UBICACIÓN

### Formato correcto (STOMP):
En el campo de mensaje, escribe:

```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

**IMPORTANTE:**
- ✅ Primera línea: `SEND`
- ✅ Segunda línea: `destination:/app/delivery/location`
- ✅ Tercera línea: `content-type:application/json`
- ✅ Cuarta línea: **VACÍA** (muy importante)
- ✅ Quinta línea: El JSON con los datos

### Presiona "Send"

---

## ✅ PASO 4: VER EL MENSAJE RECIBIDO

Deberías recibir en la consola de Postman:

```json
MESSAGE
destination:/topic/delivery/123
content-type:application/json
subscription:sub-0
message-id:...

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428,"timestamp":"2025-11-13T15:50:00"}
```

---

## 🎯 EJEMPLO COMPLETO

### 1. Conectar
```
URL: ws://localhost:8090/ws-delivery
Click: Connect
```

### 2. Suscribirse
```
SUBSCRIBE
id:sub-0
destination:/topic/delivery/123

```
(línea en blanco)

### 3. Enviar ubicación 1
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

### 4. Enviar ubicación 2 (simular movimiento)
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0500,"longitud":-77.0450}
```

### 5. Enviar ubicación 3
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0550,"longitud":-77.0480}
```

---

## ❌ ERRORES COMUNES

### Error 1: "No enum constant STOMP Command"
**Causa:** Enviaste JSON en lugar de formato STOMP

**Incorrecto:**
```json
{"type": "SUBSCRIBE", "destination": "/topic/delivery/123"}
```

**Correcto:**
```
SUBSCRIBE
id:sub-0
destination:/topic/delivery/123

```

### Error 2: "Invalid frame"
**Causa:** Falta la línea en blanco al final

**Correcto:**
```
SUBSCRIBE
id:sub-0
destination:/topic/delivery/123
← (línea vacía aquí)
```

### Error 3: No recibo mensajes
**Causa:** El `pedidoId` no coincide

**Solución:**
- Suscripción: `/topic/delivery/123`
- Mensaje: `"pedidoId":123`
- Deben ser el mismo número

---

## 🧪 PROBAR DIFERENTES PEDIDOS

### Pedido 123:
```
SUBSCRIBE
id:sub-1
destination:/topic/delivery/123

```

### Pedido 456:
```
SUBSCRIBE
id:sub-2
destination:/topic/delivery/456

```

Puedes suscribirte a múltiples pedidos al mismo tiempo.

---

## 📱 SIMULAR REPARTIDOR EN MOVIMIENTO

### Ruta completa (envía uno por uno):

**Ubicación 1 - Farmacia:**
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

**Ubicación 2 - Saliendo:**
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0470,"longitud":-77.0435}
```

**Ubicación 3 - En camino:**
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0500,"longitud":-77.0450}
```

**Ubicación 4 - Cerca:**
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0530,"longitud":-77.0470}
```

**Ubicación 5 - Llegando:**
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0550,"longitud":-77.0480}
```

---

## 🔄 CAMBIAR ESTADO DEL PEDIDO

### Estado: EN_CAMINO
```
SEND
destination:/app/delivery/status
content-type:application/json

{"pedidoId":123,"estado":"EN_CAMINO","mensaje":"El repartidor está en camino"}
```

### Estado: ENTREGADO
```
SEND
destination:/app/delivery/status
content-type:application/json

{"pedidoId":123,"estado":"ENTREGADO","mensaje":"Pedido entregado exitosamente"}
```

---

## 🎬 SCRIPT COMPLETO DE PRUEBA

Copia y pega estos mensajes uno por uno en Postman:

```
# 1. Conectar
ws://localhost:8090/ws-delivery

# 2. Suscribirse
SUBSCRIBE
id:sub-0
destination:/topic/delivery/123


# 3. Ubicación inicial
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}

# 4. Cambiar estado
SEND
destination:/app/delivery/status
content-type:application/json

{"pedidoId":123,"estado":"EN_CAMINO","mensaje":"Repartidor en camino"}

# 5. Ubicación en movimiento
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0500,"longitud":-77.0450}

# 6. Ubicación cerca
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0550,"longitud":-77.0480}

# 7. Estado entregado
SEND
destination:/app/delivery/status
content-type:application/json

{"pedidoId":123,"estado":"ENTREGADO","mensaje":"Pedido entregado"}
```

---

## 📋 CHECKLIST

- [ ] Backend corriendo en puerto 8090
- [ ] Postman abierto
- [ ] WebSocket conectado a `ws://localhost:8090/ws-delivery`
- [ ] Suscrito a `/topic/delivery/123` con formato STOMP
- [ ] Enviando ubicaciones con formato STOMP + JSON
- [ ] Recibiendo mensajes en la consola

---

## 🆘 AYUDA RÁPIDA

### Formato STOMP para SUSCRIBIRSE:
```
SUBSCRIBE
id:sub-0
destination:/topic/delivery/{pedidoId}

```

### Formato STOMP para ENVIAR:
```
SEND
destination:/app/delivery/location
content-type:application/json

{JSON aquí}
```

### Reglas:
1. ✅ Primera línea: Comando (SUBSCRIBE, SEND, etc.)
2. ✅ Siguientes líneas: Headers (key:value)
3. ✅ Línea en blanco
4. ✅ Body (si aplica)

---

**🎉 ¡Ahora sí puedes probar WebSocket correctamente en Postman!**

El error que tenías era porque enviabas JSON cuando debes usar el formato STOMP.
