# 📊 Logs de WebSocket - Qué Deberías Ver

## ✅ LOGS AGREGADOS

He agregado logs detallados para que puedas ver exactamente qué está pasando.

---

## 🔌 AL INICIAR EL BACKEND

Deberías ver:
```
✅ Message Broker configurado - Broker: /topic, App: /app
✅ WebSocket endpoints registrados en /ws-delivery
```

---

## 📡 CUANDO POSTMAN SE CONECTA

Deberías ver:
```
🔌 Nueva conexión WebSocket establecida
```

---

## 📨 CUANDO TE SUSCRIBES

En Postman envías:
```
SUBSCRIBE
id:sub-0
destination:/topic/delivery/123

```

En el backend deberías ver:
```
📡 Nueva suscripción a: /topic/delivery/123
```

---

## 📍 CUANDO ENVÍAS UBICACIÓN

En Postman envías:
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

En el backend deberías ver:
```
📍 Ubicación recibida - Pedido: 123, Lat: -12.0464, Lng: -77.0428
📤 Enviando a topic: /topic/delivery/123
✅ Mensaje enviado exitosamente
```

---

## 🔄 CUANDO CAMBIAS ESTADO

En Postman envías:
```
SEND
destination:/app/delivery/status
content-type:application/json

{"pedidoId":123,"estado":"EN_CAMINO","mensaje":"Repartidor en camino"}
```

En el backend deberías ver:
```
🔄 Estado recibido - Pedido: 123, Estado: EN_CAMINO
📤 Enviando a topic: /topic/delivery/123/status
✅ Estado enviado exitosamente
```

---

## 🔌 CUANDO POSTMAN SE DESCONECTA

Deberías ver:
```
🔌 Conexión WebSocket cerrada
```

---

## 🧪 PRUEBA COMPLETA CON LOGS

### Paso 1: Reinicia el backend
```bash
./gradlew bootRun
```

Espera a ver:
```
✅ Message Broker configurado - Broker: /topic, App: /app
✅ WebSocket endpoints registrados en /ws-delivery
```

### Paso 2: Conecta Postman 1 (Cliente)
```
ws://localhost:8090/ws-delivery
```

Deberías ver en logs:
```
🔌 Nueva conexión WebSocket establecida
```

### Paso 3: Suscríbete en Postman 1
```
SUBSCRIBE
id:sub-0
destination:/topic/delivery/123

```

Deberías ver en logs:
```
📡 Nueva suscripción a: /topic/delivery/123
```

### Paso 4: Conecta Postman 2 (Repartidor)
```
ws://localhost:8090/ws-delivery
```

Deberías ver en logs:
```
🔌 Nueva conexión WebSocket establecida
```

### Paso 5: Envía ubicación desde Postman 2
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

Deberías ver en logs:
```
📍 Ubicación recibida - Pedido: 123, Lat: -12.0464, Lng: -77.0428
📤 Enviando a topic: /topic/delivery/123
✅ Mensaje enviado exitosamente
```

### Paso 6: Verifica en Postman 1
Deberías recibir el mensaje en Postman 1 (Cliente).

---

## 🐛 TROUBLESHOOTING

### No veo "Nueva conexión WebSocket"
**Problema:** Postman no se está conectando
**Solución:**
1. Verifica la URL: `ws://localhost:8090/ws-delivery`
2. Verifica que el backend esté corriendo
3. Revisa si hay errores en los logs

### No veo "Nueva suscripción"
**Problema:** El mensaje SUBSCRIBE no está llegando
**Solución:**
1. Verifica el formato (sin espacios al inicio)
2. Debe tener línea en blanco al final
3. Copia exactamente:
```
SUBSCRIBE
id:sub-0
destination:/topic/delivery/123

```

### No veo "Ubicación recibida"
**Problema:** El mensaje SEND no está llegando
**Solución:**
1. Verifica el formato (sin espacios al inicio)
2. Debe tener línea en blanco antes del JSON
3. Copia exactamente:
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

### Veo "Ubicación recibida" pero no llega a Postman 1
**Problema:** El pedidoId no coincide
**Solución:**
1. Suscripción: `/topic/delivery/123`
2. Mensaje: `"pedidoId":123`
3. Deben ser el mismo número

---

## 📋 CHECKLIST DE LOGS

Cuando todo funciona correctamente, deberías ver esta secuencia:

- [ ] ✅ Message Broker configurado
- [ ] ✅ WebSocket endpoints registrados
- [ ] 🔌 Nueva conexión WebSocket (Postman 1)
- [ ] 📡 Nueva suscripción a: /topic/delivery/123
- [ ] 🔌 Nueva conexión WebSocket (Postman 2)
- [ ] 📍 Ubicación recibida - Pedido: 123
- [ ] 📤 Enviando a topic: /topic/delivery/123
- [ ] ✅ Mensaje enviado exitosamente

---

## 🎯 EJEMPLO DE LOGS COMPLETOS

```
2025-11-20 13:30:00 INFO  WebSocketConfig - ✅ Message Broker configurado - Broker: /topic, App: /app
2025-11-20 13:30:00 INFO  WebSocketConfig - ✅ WebSocket endpoints registrados en /ws-delivery
2025-11-20 13:30:15 INFO  WebSocketConfig - 🔌 Nueva conexión WebSocket establecida
2025-11-20 13:30:20 INFO  WebSocketConfig - 📡 Nueva suscripción a: /topic/delivery/123
2025-11-20 13:30:25 INFO  WebSocketConfig - 🔌 Nueva conexión WebSocket establecida
2025-11-20 13:30:30 INFO  DeliveryTrackingController - 📍 Ubicación recibida - Pedido: 123, Lat: -12.0464, Lng: -77.0428
2025-11-20 13:30:30 INFO  DeliveryTrackingController - 📤 Enviando a topic: /topic/delivery/123
2025-11-20 13:30:30 INFO  DeliveryTrackingController - ✅ Mensaje enviado exitosamente
2025-11-20 13:30:35 INFO  DeliveryTrackingController - 📍 Ubicación recibida - Pedido: 123, Lat: -12.0500, Lng: -77.0450
2025-11-20 13:30:35 INFO  DeliveryTrackingController - 📤 Enviando a topic: /topic/delivery/123
2025-11-20 13:30:35 INFO  DeliveryTrackingController - ✅ Mensaje enviado exitosamente
```

---

## 🚀 SIGUIENTE PASO

1. **Reinicia el backend** para que cargue los nuevos logs
2. **Conecta Postman** y observa los logs
3. **Suscríbete** y verifica que aparezca el log
4. **Envía ubicación** y verifica que aparezcan los 3 logs
5. **Verifica en Postman 1** que llegue el mensaje

---

**🎉 Con estos logs podrás ver exactamente qué está pasando!**

Si no ves ningún log, significa que el mensaje no está llegando al backend (problema de formato).
Si ves los logs pero no llega a Postman 1, significa que el pedidoId no coincide.
