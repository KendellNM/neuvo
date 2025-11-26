# 🔴 Prueba de WebSocket en Tiempo Real

## 🎯 OBJETIVO

Simular que un **repartidor** envía su ubicación y un **cliente** la ve en tiempo real.

---

## 👥 CONFIGURACIÓN: 2 POSTMAN

### Postman 1 - CLIENTE (Observador)
Este solo **recibe** mensajes, simula la app del cliente viendo el mapa.

### Postman 2 - REPARTIDOR (Emisor)
Este **envía** ubicaciones, simula el repartidor moviéndose.

---

## 📱 POSTMAN 1 - CLIENTE

### Paso 1: Conectar
```
URL: ws://localhost:8090/ws-delivery
Click: Connect
```

### Paso 2: Suscribirse al pedido 123
```
SUBSCRIBE
id:sub-cliente
destination:/topic/delivery/123

```

### Paso 3: Esperar y observar
**NO envíes nada más**, solo observa la consola.

Verás los mensajes que lleguen en tiempo real:
```json
{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428,"timestamp":"..."}
{"pedidoId":123,"latitud":-12.0500,"longitud":-77.0450,"timestamp":"..."}
{"pedidoId":123,"latitud":-12.0550,"longitud":-77.0480,"timestamp":"..."}
```

---

## 🚚 POSTMAN 2 - REPARTIDOR

### Paso 1: Conectar
```
URL: ws://localhost:8090/ws-delivery
Click: Connect
```

### Paso 2: Suscribirse (opcional, para ver confirmación)
```
SUBSCRIBE
id:sub-repartidor
destination:/topic/delivery/123

```

### Paso 3: Enviar ubicaciones
Envía estas ubicaciones **una por una** (espera 5 segundos entre cada una):

**Ubicación 1 - Farmacia:**
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

**Espera 5 segundos...**

**Ubicación 2 - Saliendo:**
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0480,"longitud":-77.0440}
```

**Espera 5 segundos...**

**Ubicación 3 - En camino:**
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0500,"longitud":-77.0450}
```

**Espera 5 segundos...**

**Ubicación 4 - Cerca:**
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0530,"longitud":-77.0470}
```

**Espera 5 segundos...**

**Ubicación 5 - Llegando:**
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0550,"longitud":-77.0480}
```

---

## 👀 QUÉ VERÁS

### En POSTMAN 1 (Cliente):
Cada vez que el repartidor envíe una ubicación, verás aparecer el mensaje **INMEDIATAMENTE**:

```
[Recibido] {"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428,"timestamp":"2025-11-13T15:50:00"}
[Recibido] {"pedidoId":123,"latitud":-12.0480,"longitud":-77.0440,"timestamp":"2025-11-13T15:50:05"}
[Recibido] {"pedidoId":123,"latitud":-12.0500,"longitud":-77.0450,"timestamp":"2025-11-13T15:50:10"}
[Recibido] {"pedidoId":123,"latitud":-12.0530,"longitud":-77.0470,"timestamp":"2025-11-13T15:50:15"}
[Recibido] {"pedidoId":123,"latitud":-12.0550,"longitud":-77.0480,"timestamp":"2025-11-13T15:50:20"}
```

### En POSTMAN 2 (Repartidor):
Si te suscribiste, también verás tus propios mensajes (confirmación de que se enviaron).

---

## 🎬 ESCENARIO COMPLETO

### Simulación realista:

1. **Cliente abre la app** → Postman 1 conecta y se suscribe
2. **Repartidor sale de la farmacia** → Postman 2 envía ubicación 1
3. **Cliente ve en el mapa** → Postman 1 recibe ubicación 1
4. **Repartidor avanza** → Postman 2 envía ubicación 2
5. **Cliente ve actualización** → Postman 1 recibe ubicación 2
6. **Y así sucesivamente...**

---

## 🔄 PRUEBA CON MÚLTIPLES CLIENTES

Puedes abrir **3 o más Postman**:

### Postman 1 - Cliente A
```
SUBSCRIBE
id:cliente-a
destination:/topic/delivery/123

```

### Postman 2 - Cliente B
```
SUBSCRIBE
id:cliente-b
destination:/topic/delivery/123

```

### Postman 3 - Repartidor
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

**Resultado:** Ambos clientes (Postman 1 y 2) recibirán el mensaje al mismo tiempo.

---

## 📊 PRUEBA CON MÚLTIPLES PEDIDOS

### Postman 1 - Cliente del pedido 123
```
SUBSCRIBE
id:cliente-123
destination:/topic/delivery/123

```

### Postman 2 - Cliente del pedido 456
```
SUBSCRIBE
id:cliente-456
destination:/topic/delivery/456

```

### Postman 3 - Repartidor del pedido 123
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

### Postman 4 - Repartidor del pedido 456
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":456,"latitud":-12.0600,"longitud":-77.0500}
```

**Resultado:**
- Postman 1 solo recibe mensajes del pedido 123
- Postman 2 solo recibe mensajes del pedido 456
- Cada cliente ve solo su pedido

---

## 🎯 EJERCICIO PRÁCTICO

### Objetivo: Simular delivery completo

**Tiempo estimado:** 2 minutos

#### Postman 1 (Cliente):
1. Conectar a `ws://localhost:8090/ws-delivery`
2. Suscribirse a `/topic/delivery/123`
3. Dejar abierto y observar

#### Postman 2 (Repartidor):
1. Conectar a `ws://localhost:8090/ws-delivery`
2. Enviar 5 ubicaciones (una cada 10 segundos)
3. Observar que Postman 1 las recibe

#### Verificación:
- ✅ Postman 1 recibe todas las ubicaciones
- ✅ Los mensajes llegan en menos de 1 segundo
- ✅ El timestamp es diferente en cada mensaje

---

## 🐛 TROUBLESHOOTING

### No veo mensajes en Postman 1
**Causa:** No estás suscrito o el pedidoId no coincide

**Solución:**
1. Verifica que te suscribiste: `destination:/topic/delivery/123`
2. Verifica que el mensaje tiene: `"pedidoId":123`
3. Los números deben coincidir

### Veo mensajes duplicados
**Causa:** Te suscribiste dos veces

**Solución:** Desconecta y vuelve a conectar

### Los mensajes llegan con retraso
**Causa:** Problema de red o backend lento

**Solución:** 
1. Verifica que el backend esté corriendo
2. Revisa los logs del backend
3. Usa `localhost` en lugar de IP

---

## ✅ CHECKLIST DE PRUEBA

- [ ] Backend corriendo en puerto 8090
- [ ] Postman 1 conectado y suscrito (Cliente)
- [ ] Postman 2 conectado (Repartidor)
- [ ] Enviar ubicación desde Postman 2
- [ ] Ver mensaje en Postman 1 en tiempo real
- [ ] Enviar 5 ubicaciones diferentes
- [ ] Verificar que todas llegan a Postman 1
- [ ] Probar con diferentes pedidoId

---

## 🎉 RESULTADO ESPERADO

Cuando envíes una ubicación desde Postman 2, deberías ver **INMEDIATAMENTE** (menos de 1 segundo) el mensaje aparecer en Postman 1.

Esto simula exactamente lo que pasará en la app Android:
- **App del cliente** = Postman 1 (recibe y muestra en mapa)
- **App del repartidor** = Postman 2 (envía ubicación GPS)

---

## 📱 SIGUIENTE PASO

Una vez que funcione en Postman, implementar en Android será fácil:

1. **Cliente Android:**
   - Conectar WebSocket
   - Suscribirse a `/topic/delivery/{pedidoId}`
   - Actualizar marcador en MapLibre cuando llegue mensaje

2. **Repartidor Android:**
   - Conectar WebSocket
   - Obtener ubicación GPS cada 5 segundos
   - Enviar a `/app/delivery/location`

---

**🚀 ¡Prueba ahora con 2 Postman y verás la magia del tiempo real!**
