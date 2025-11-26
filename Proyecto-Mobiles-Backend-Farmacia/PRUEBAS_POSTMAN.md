# 🧪 Guía de Pruebas con Postman

## 📍 PROBAR SEGUIMIENTO DE UBICACIÓN (DELIVERY)

Puedes probar el seguimiento de delivery completamente desde Postman, **NO necesitas celular**.

---

## 🔧 CONFIGURACIÓN INICIAL

### 1. Verificar que el backend esté corriendo
```
http://localhost:8080
```

### 2. Obtener Token JWT

**Request:**
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

Body:
{
  "username": "admin@farmacia.com",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "roles": ["ROLE_ADMIN"]
}
```

**Guardar el token** para usarlo en las siguientes peticiones.

---

## 🔓 WEBSOCKET SIN AUTENTICACIÓN

El WebSocket está configurado **SIN autenticación** para facilitar las pruebas.

**Conexión simple:**

### En Postman:
1. Crear WebSocket Request
2. URL: `ws://localhost:8080/ws-delivery`
3. Click "Connect" (sin necesidad de token)

### En código JavaScript (para referencia):
```javascript
const socket = new SockJS('http://localhost:8080/ws-delivery');
const stompClient = Stomp.over(socket);

stompClient.connect(
  {},  // Sin headers, sin token
  function(frame) {
    console.log('Conectado:', frame);
  }
);
```

**Nota:** No necesitas token para conectar al WebSocket.

---

## 🗺️ PROBAR WEBSOCKET CON POSTMAN

### Opción 1: Usar Postman (Recomendado)

Postman tiene soporte nativo para WebSocket desde la versión 2023.

#### Paso 1: Crear nueva petición WebSocket
1. En Postman, click en **"New"** → **"WebSocket Request"**
2. URL: `ws://localhost:8080/ws-delivery`
3. Click en **"Connect"**

**Nota:** No necesitas token, la conexión es directa.

#### Paso 2: Suscribirse a un pedido
Una vez conectado, envía este mensaje:
```json
{
  "type": "SUBSCRIBE",
  "destination": "/topic/delivery/123"
}
```

#### Paso 3: Enviar actualización de ubicación
Envía este mensaje para simular que el repartidor se mueve:
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

#### Paso 4: Ver el mensaje recibido
Deberías recibir en Postman:
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

## 🔄 PROBAR CON ENDPOINTS REST (Alternativa)

Si no quieres usar WebSocket, puedes usar los endpoints REST:

### Endpoint para actualizar ubicación
```
POST http://localhost:8080/api/delivery/location
Authorization: Bearer {tu_token}
Content-Type: application/json

Body:
{
  "pedidoId": 123,
  "latitud": -12.0464,
  "longitud": -77.0428
}
```

### Endpoint para actualizar estado
```
POST http://localhost:8080/api/delivery/status
Authorization: Bearer {tu_token}
Content-Type: application/json

Body:
{
  "pedidoId": 123,
  "estado": "EN_CAMINO",
  "mensaje": "El repartidor está en camino"
}
```

---

## 🧪 PRUEBAS COMPLETAS PASO A PASO

### Escenario: Simular un delivery completo

#### 1. Login
```
POST http://localhost:8080/api/auth/login
Body: {"username": "admin@farmacia.com", "password": "admin123"}
```

#### 2. Crear un pedido (si no tienes uno)
```
POST http://localhost:8080/api/pedidos
Authorization: Bearer {token}
Body: {
  "clienteId": 1,
  "productos": [{"productoId": 1, "cantidad": 2}]
}
```

#### 3. Conectar WebSocket
```
ws://localhost:8080/ws-delivery
```

#### 4. Suscribirse al pedido
```json
{
  "type": "SUBSCRIBE",
  "destination": "/topic/delivery/123"
}
```

#### 5. Simular movimiento del repartidor
Envía varias ubicaciones para simular el movimiento:

**Ubicación 1 (Farmacia):**
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

**Ubicación 2 (En camino):**
```json
{
  "type": "MESSAGE",
  "destination": "/app/delivery/location",
  "body": {
    "pedidoId": 123,
    "latitud": -12.0500,
    "longitud": -77.0450
  }
}
```

**Ubicación 3 (Cerca del cliente):**
```json
{
  "type": "MESSAGE",
  "destination": "/app/delivery/location",
  "body": {
    "pedidoId": 123,
    "latitud": -12.0550,
    "longitud": -77.0480
  }
}
```

#### 6. Cambiar estado del pedido
```json
{
  "type": "MESSAGE",
  "destination": "/app/delivery/status",
  "body": {
    "pedidoId": 123,
    "estado": "ENTREGADO",
    "mensaje": "Pedido entregado exitosamente"
  }
}
```

---

## 🌐 PROBAR OTRAS FUNCIONALIDADES

### 1. Escaneo QR de Productos
```
GET http://localhost:8080/api/productos/1/mobile
Authorization: Bearer {token}
```

### 2. Subir Receta Digital
```
POST http://localhost:8080/api/recetas-digitales/procesar
Authorization: Bearer {token}
Content-Type: multipart/form-data

Form Data:
- imagen: [seleccionar archivo de imagen]
- clienteId: 1
```

### 3. Obtener Puntos de Fidelización
```
GET http://localhost:8080/api/fidelizacion/cliente/1
Authorization: Bearer {token}
```

### 4. Canjear Puntos
```
POST http://localhost:8080/api/fidelizacion/canjear
Authorization: Bearer {token}
Content-Type: application/json

Body:
{
  "clienteId": 1,
  "puntosACanjear": 500,
  "descripcionCupon": "Descuento 10%"
}
```

### 5. Registrar Dispositivo para Notificaciones
```
POST http://localhost:8080/api/notificaciones/registrar-dispositivo
Authorization: Bearer {token}
Content-Type: application/json

Body:
{
  "clienteId": 1,
  "fcmToken": "token_de_prueba_123",
  "plataforma": "ANDROID"
}
```

### 6. Obtener Notificaciones
```
GET http://localhost:8080/api/notificaciones/cliente/1
Authorization: Bearer {token}
```

---

## 📦 COLECCIÓN DE POSTMAN

### Crear colección con estas variables:

**Variables de entorno:**
```
base_url: http://localhost:8080
token: (se actualiza después del login)
client_id: 1
pedido_id: 123
```

### Requests organizados:

**1. Auth**
- Login
- Register

**2. Productos (QR)**
- Get Producto Mobile

**3. Recetas Digitales**
- Procesar Receta
- Listar Recetas Cliente
- Ver Detalle Receta

**4. Fidelización**
- Crear Programa
- Obtener Puntos
- Canjear Puntos
- Ver Historial

**5. Notificaciones**
- Registrar Dispositivo
- Listar Notificaciones
- Marcar como Leída

**6. Delivery (WebSocket)**
- Conectar WebSocket
- Actualizar Ubicación
- Actualizar Estado

---

## 🔍 HERRAMIENTAS ALTERNATIVAS

### 1. Postman (Recomendado)
- ✅ Soporte nativo para WebSocket
- ✅ Fácil de usar
- ✅ Guarda colecciones

### 2. Insomnia
- ✅ También soporta WebSocket
- ✅ Interfaz más simple

### 3. WebSocket King (Chrome Extension)
- ✅ Específico para WebSocket
- ✅ Muy visual

### 4. wscat (Terminal)
```bash
npm install -g wscat
wscat -c ws://localhost:8080/ws-delivery
```

---

## 🐛 TROUBLESHOOTING

### Error: "WebSocket connection failed"
**Solución:**
1. Verifica que el backend esté corriendo
2. Verifica la URL: `ws://localhost:8080/ws-delivery` (no `wss://`)
3. Revisa los logs del backend

### Error: "401 Unauthorized"
**Solución:**
1. Verifica que el token sea válido
2. Agrega el header: `Authorization: Bearer {token}`
3. El token expira en 24 horas, haz login nuevamente

### No recibo mensajes en WebSocket
**Solución:**
1. Verifica que te suscribiste al topic correcto: `/topic/delivery/{pedidoId}`
2. Verifica que el `pedidoId` en el mensaje coincida con el topic
3. Revisa los logs del backend

### Error al subir imagen
**Solución:**
1. Verifica que el Content-Type sea `multipart/form-data`
2. La imagen debe ser JPG o PNG
3. Tamaño máximo recomendado: 5MB

---

## 📝 EJEMPLO DE PRUEBA COMPLETA

### Script de prueba (puedes copiar y pegar en Postman):

```javascript
// 1. Login
pm.sendRequest({
    url: 'http://localhost:8080/api/auth/login',
    method: 'POST',
    header: 'Content-Type: application/json',
    body: {
        mode: 'raw',
        raw: JSON.stringify({
            username: "admin@farmacia.com",
            password: "admin123"
        })
    }
}, function (err, res) {
    const token = res.json().token;
    pm.environment.set("token", token);
    console.log("Token guardado:", token);
});

// 2. Obtener producto (QR)
pm.sendRequest({
    url: 'http://localhost:8080/api/productos/1/mobile',
    method: 'GET',
    header: {
        'Authorization': 'Bearer ' + pm.environment.get("token")
    }
}, function (err, res) {
    console.log("Producto:", res.json());
});

// 3. Obtener puntos
pm.sendRequest({
    url: 'http://localhost:8080/api/fidelizacion/cliente/1',
    method: 'GET',
    header: {
        'Authorization': 'Bearer ' + pm.environment.get("token")
    }
}, function (err, res) {
    console.log("Puntos:", res.json());
});
```

---

## ✅ CHECKLIST DE PRUEBAS

- [ ] Login exitoso
- [ ] Token guardado
- [ ] Obtener producto por QR
- [ ] Subir receta digital
- [ ] Ver puntos de fidelización
- [ ] Canjear puntos
- [ ] Registrar dispositivo
- [ ] Conectar WebSocket
- [ ] Enviar ubicación
- [ ] Recibir ubicación
- [ ] Cambiar estado de pedido

---

**🎉 ¡Puedes probar TODO desde Postman sin necesidad de celular!**

Para pruebas más realistas con mapa, necesitarás la app Android, pero toda la lógica del backend se puede probar con Postman.
