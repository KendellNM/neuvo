# 📱 Guía de Integración - App Móvil Farmacia Dolores

## 🔗 Información del Backend

**Base URL:** `http://localhost:8090` (cambiar en producción)

**Autenticación:** JWT Bearer Token

---

## 1️⃣ AUTENTICACIÓN

### Login
```
POST /api/auth/login
Content-Type: application/json

Request:
{
  "username": "12345678",  // DNI del cliente
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "roles": ["ROLE_CLIENTE"]
}
```

**Guardar el token** para usarlo en todas las peticiones posteriores:
```
Authorization: Bearer {token}
```

---

## 2️⃣ ESCANEO QR DE PRODUCTOS

### Flujo de Implementación:

1. **Generar QR en el backend** (para cada producto):
   - El QR debe contener solo el ID del producto
   - Ejemplo: QR contiene el texto `"123"` (ID del producto)

2. **Escanear QR en la app móvil**:
   - Usar librería de escaneo QR (ej: `react-native-camera`, `expo-barcode-scanner`)
   - Extraer el ID del producto del QR

3. **Obtener información del producto**:

```
GET /api/productos/{id}/mobile
Authorization: Bearer {token}

Response:
{
  "id": 123,
  "nombre": "Aspirina 500mg",
  "descripcion": "Analgésico y antipirético",
  "precio": 5.50,
  "stock": 100,
  "disponible": true,
  "imagenUrl": "http://localhost:8080/uploads/productos/aspirina.jpg",
  "laboratorioNombre": "Bayer",
  "categoria": "Analgésicos",
  "requiereReceta": false
}
```

### Ejemplo de Código (React Native):

```javascript
// Escanear QR
import { BarCodeScanner } from 'expo-barcode-scanner';

const handleBarCodeScanned = async ({ data }) => {
  const productoId = data; // ID del producto
  
  // Obtener información del producto
  const response = await fetch(
    `http://localhost:8080/api/productos/${productoId}/mobile`,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );
  
  const producto = await response.json();
  // Mostrar información del producto
};
```

---

## 3️⃣ RECETAS DIGITALES CON OCR

### Subir y Procesar Receta

```
POST /api/recetas-digitales/procesar
Content-Type: multipart/form-data
Authorization: Bearer {token}

Request (FormData):
- imagen: [archivo de imagen]
- clienteId: 1

Response:
{
  "success": true,
  "message": "Receta procesada exitosamente",
  "data": {
    "id": 1,
    "imagenUrl": "http://localhost:8080/uploads/recetas/receta_123.jpg",
    "textoExtraido": "Aspirina 500mg\nTomar 1 cada 8 horas...",
    "estado": "PENDIENTE",
    "fechaProcesamiento": "2025-11-13T10:30:00",
    "detalles": [
      {
        "id": 1,
        "medicamentoTexto": "Aspirina 500mg",
        "productoId": 123,
        "productoNombre": "Aspirina 500mg",
        "validado": false
      }
    ]
  }
}
```

### Obtener Recetas del Cliente

```
GET /api/recetas-digitales/cliente/{clienteId}
Authorization: Bearer {token}

Response:
[
  {
    "id": 1,
    "imagenUrl": "...",
    "textoExtraido": "...",
    "estado": "VALIDADA",
    "fechaProcesamiento": "2025-11-13T10:30:00",
    "detalles": [...]
  }
]
```

### Estados de Receta:
- `PENDIENTE`: Recién subida, esperando procesamiento
- `PROCESADA`: OCR completado, esperando validación
- `VALIDADA`: Aprobada por farmacéutico
- `RECHAZADA`: Rechazada por farmacéutico

### Ejemplo de Código (React Native):

```javascript
// Subir receta
const subirReceta = async (imageUri, clienteId) => {
  const formData = new FormData();
  formData.append('imagen', {
    uri: imageUri,
    type: 'image/jpeg',
    name: 'receta.jpg'
  });
  formData.append('clienteId', clienteId);

  const response = await fetch(
    'http://localhost:8080/api/recetas-digitales/procesar',
    {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'multipart/form-data'
      },
      body: formData
    }
  );

  const result = await response.json();
  return result;
};
```

---

## 4️⃣ PROGRAMA DE FIDELIZACIÓN Y PUNTOS

### Crear Programa de Fidelización (primera vez)

```
POST /api/fidelizacion/crear?clienteId={clienteId}
Authorization: Bearer {token}

Response:
{
  "id": 1,
  "clienteId": 1,
  "puntosActuales": 0,
  "puntosAcumulados": 0,
  "nivelMembresia": "BRONCE",
  "siguienteNivel": "PLATA",
  "puntosParaSiguienteNivel": 2000,
  "fechaCreacion": "2025-11-13T10:30:00"
}
```

### Obtener Puntos del Cliente

```
GET /api/fidelizacion/cliente/{clienteId}
Authorization: Bearer {token}

Response:
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

### Niveles de Membresía:
- **BRONCE**: 0 - 1,999 puntos
- **PLATA**: 2,000 - 4,999 puntos
- **ORO**: 5,000 - 9,999 puntos
- **PLATINO**: 10,000+ puntos

### Canjear Puntos por Cupón

```
POST /api/fidelizacion/canjear
Content-Type: application/json
Authorization: Bearer {token}

Request:
{
  "clienteId": 1,
  "puntosACanjear": 500,
  "descripcionCupon": "Descuento 10%"
}

Response:
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

### Historial de Movimientos

```
GET /api/fidelizacion/historial/{clienteId}
Authorization: Bearer {token}

Response:
[
  {
    "id": 1,
    "tipo": "ACUMULACION",
    "puntos": 100,
    "descripcion": "Compra de productos",
    "fecha": "2025-11-13T10:30:00"
  },
  {
    "id": 2,
    "tipo": "CANJE",
    "puntos": -500,
    "descripcion": "Canje por cupón",
    "fecha": "2025-11-13T11:00:00"
  }
]
```

### Ejemplo de Código (React Native):

```javascript
// Obtener puntos
const obtenerPuntos = async (clienteId) => {
  const response = await fetch(
    `http://localhost:8080/api/fidelizacion/cliente/${clienteId}`,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );
  return await response.json();
};

// Canjear puntos
const canjearPuntos = async (clienteId, puntos) => {
  const response = await fetch(
    'http://localhost:8080/api/fidelizacion/canjear',
    {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        clienteId: clienteId,
        puntosACanjear: puntos,
        descripcionCupon: 'Descuento 10%'
      })
    }
  );
  return await response.json();
};
```

---

## 5️⃣ NOTIFICACIONES PUSH

### Registrar Dispositivo (Firebase FCM)

```
POST /api/notificaciones/registrar-dispositivo
Content-Type: application/json
Authorization: Bearer {token}

Request:
{
  "clienteId": 1,
  "fcmToken": "fGHj8K9L0mN1oP2qR3sT4uV5wX6yZ7...",
  "plataforma": "ANDROID"  // o "IOS"
}

Response:
{
  "success": true,
  "message": "Dispositivo registrado exitosamente"
}
```

### Obtener Notificaciones del Cliente

```
GET /api/notificaciones/cliente/{clienteId}
Authoearer {token}

Response:
[
  {
    "id": 1,
    "titulo": "Pedido Listo",
    "mensaje": "Tu pedido #123 está listo para recoger",
    "tipo": "PEDIDO",
    "leida": false,
    "fecha": "2025-11-13T10:30:00",
    "datos": {
      "pedidoId": 123
    }
  },
  {
    "id": 2,
    "titulo": "Promoción Especial",
    "mensaje": "20% de descuento en vitaminas",
    "tipo": "PROMOCION",
    "leida": true,
    "fecha": "2025-11-12T15:00:00"
  }
]
```

### Marcar Notificación como Leída

```
PUT /api/notificaciones/{id}/marcar-leida
Authorization: Bearer {token}

Response:
{
  "success": true,
  "message": "Notificación marcada como leída"
}
```

### Tipos de Notificaciones:
- `PEDIDO`: Estado de pedidos
- `PROMOCION`: Ofertas y descuentos
- `RECORDATORIO`: Recordatorios de medicamentos
- `SISTEMA`: Notificaciones del sistema

### Configuración Firebase (React Native):

```javascript
// 1. Instalar Firebase
// npm install @react-native-firebase/app @react-native-firebase/messaging

// 2. Obtener token FCM
import messaging from '@react-native-firebase/messaging';

const obtenerTokenFCM = async () => {
  const token = await messaging().getToken();
  return token;
};

// 3. Registrar dispositivo
const registrarDispositivo = async (clienteId) => {
  const fcmToken = await obtenerTokenFCM();
  
  await fetch('http://localhost:8080/api/notificaciones/registrar-dispositivo', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      clienteId: clienteId,
      fcmToken: fcmToken,
      plataforma: Platform.OS === 'ios' ? 'IOS' : 'ANDROID'
    })
  });
};

// 4. Escuchar notificaciones
messaging().onMessage(async remoteMessage => {
  console.log('Notificación recibida:', remoteMessage);
  // Mostrar notificación local
});
```

---

## 6️⃣ SEGUIMIENTO EN TIEMPO REAL DE DELIVERY

### Conexión WebSocket

```
WebSocket URL: ws://localhost:8080/ws-delivery
```

### Suscribirse a Actualizaciones de Pedido

```javascript
// Usando SockJS y Stomp
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const conectarWebSocket = (token, pedidoId, onUbicacionActualizada, onEstadoActualizado) => {
  const socket = new SockJS('http://localhost:8080/ws-delivery');
  const stompClient = Stomp.over(socket);

  // Conectar con token en headers
  stompClient.connect({ token: token }, () => {
    // Suscribirse a actualizaciones del pedido
    stompClient.subscribe(`/topic/delivery/${pedidoId}`, (message) => {
      const data = JSON.parse(message.body);
      
      if (data.tipo === 'UBICACION') {
        onUbicacionActualizada(data);
      } else if (data.tipo === 'ESTADO') {
        onEstadoActualizado(data);
      }
    });
  });

  return stompClient;
};
```

### Formato de Mensajes WebSocket

**Actualización de Ubicación:**
```json
{
  "tipo": "UBICACION",
  "pedidoId": 123,
  "latitud": -12.0464,
  "longitud": -77.0428,
  "timestamp": "2025-11-13T10:30:00"
}
```

**Actualización de Estado:**
```json
{
  "tipo": "ESTADO",
  "pedidoId": 123,
  "estado": "EN_CAMINO",
  "mensaje": "El repartidor está en camino",
  "timestamp": "2025-11-13T10:30:00"
}
```

### Estados de Delivery:
- `PENDIENTE`: Pedido creado
- `PREPARANDO`: En preparación
- `LISTO`: Listo para envío
- `EN_CAMINO`: Repartidor en camino
- `ENTREGADO`: Entregado al cliente
- `CANCELADO`: Cancelado

### Endpoints REST Alternativos (sin WebSocket)

Si no puedes usar WebSocket, puedes hacer polling:

```
GET /api/delivery/pedido/{pedidoId}/ubicacion
Authorization: Bearer {token}

Response:
{
  "pedidoId": 123,
  "latitud": -12.0464,
  "longitud": -77.0428,
  "estado": "EN_CAMINO",
  "ultimaActualizacion": "2025-11-13T10:30:00"
}
```

### Ejemplo Completo (React Native con Mapa):

```javascript
import React, { useEffect, useState } from 'react';
import MapView, { Marker } from 'react-native-maps';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const SeguimientoDelivery = ({ pedidoId }) => {
  const [ubicacion, setUbicacion] = useState(null);
  const [estado, setEstado] = useState('PENDIENTE');

  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/ws-delivery');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
      stompClient.subscribe(`/topic/delivery/${pedidoId}`, (message) => {
        const data = JSON.parse(message.body);
        
        if (data.tipo === 'UBICACION') {
          setUbicacion({
            latitude: data.latitud,
            longitude: data.longitud
          });
        } else if (data.tipo === 'ESTADO') {
          setEstado(data.estado);
        }
      });
    });

    return () => {
      if (stompClient) {
        stompClient.disconnect();
      }
    };
  }, [pedidoId]);

  return (
    <MapView
      style={{ flex: 1 }}
      region={{
        latitude: ubicacion?.latitude || -12.0464,
        longitude: ubicacion?.longitude || -77.0428,
        latitudeDelta: 0.01,
        longitudeDelta: 0.01
      }}
    >
      {ubicacion && (
        <Marker
          coordinate={ubicacion}
          title="Repartidor"
          description={`Estado: ${estado}`}
        />
      )}
    </MapView>
  );
};
```

---

## 📦 DEPENDENCIAS NECESARIAS (React Native)

```json
{
  "dependencies": {
    "@react-native-firebase/app": "^18.0.0",
    "@react-native-firebase/messaging": "^18.0.0",
    "expo-barcode-scanner": "^12.0.0",
    "react-native-maps": "^1.7.0",
    "sockjs-client": "^1.6.1",
    "@stomp/stompjs": "^7.0.0",
    "axios": "^1.4.0"
  }
}
```

---

## 🔧 CONFIGURACIÓN INICIAL

### 1. Configurar Base URL

```javascript
// config.js
export const API_BASE_URL = 'http://localhost:8080';
// En producción: 'https://api.farmaciadolores.com'
```

### 2. Configurar Axios con Interceptor

```javascript
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const api = axios.create({
  baseURL: API_BASE_URL
});

// Interceptor para agregar token automáticamente
api.interceptors.request.use(async (config) => {
  const token = await AsyncStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
```

### 3. Servicio de Autenticación

```javascript
// services/auth.js
import api from './api';
import AsyncStorage from '@react-native-async-storage/async-storage';

export const login = async (dni, password) => {
  const response = await api.post('/api/auth/login', {
    username: dni,
    password: password
  });
  
  const { token, roles } = response.data;
  
  // Guardar token
  await AsyncStorage.setItem('token', token);
  await AsyncStorage.setItem('roles', JSON.stringify(roles));
  
  return { token, roles };
};

export const logout = async () => {
  await AsyncStorage.removeItem('token');
  await AsyncStorage.removeItem('roles');
};
```

---

## 🚨 MANEJO DE ERRORES

```javascript
// Ejemplo de manejo de errores
const obtenerProducto = async (id) => {
  try {
    const response = await api.get(`/api/productos/${id}/mobile`);
    return response.data;
  } catch (error) {
    if (error.response) {
      // Error del servidor
      switch (error.response.status) {
        case 401:
          // Token expirado, redirigir a login
          await logout();
          navigation.navigate('Login');
          break;
        case 404:
          Alert.alert('Error', 'Producto no encontrado');
          break;
        case 500:
          Alert.alert('Error', 'Error del servidor');
          break;
        default:
          Alert.alert('Error', error.response.data.error || 'Error desconocido');
      }
    } else if (error.request) {
      // Sin respuesta del servidor
      Alert.alert('Error', 'No se pudo conectar con el servidor');
    } else {
      Alert.alert('Error', error.message);
    }
  }
};
```

---

## 📝 NOTAS IMPORTANTES

1. **Token JWT expira en 24 horas** - Implementar refresh token o re-login
2. **Firebase requiere configuración** - Ver archivo `firebase-service-account.json`
3. **WebSocket puede fallar** - Implementar reconexión automática
4. **OCR requiere Tesseract** - Backend debe tener tessdata configurado
5. **Imágenes grandes** - Comprimir antes de subir (max 5MB recomendado)

---

## 🔗 RECURSOS ADICIONALES

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Documentación completa:** Ver `DOCUMENTACION_API.md`
- **Guía Firebase:** Ver `GUIA_FIREBASE.md`
- **Inicio rápido:** Ver `INICIO_RAPIDO.md`

---

## ✅ CHECKLIST DE IMPLEMENTACIÓN

### Autenticación
- [ ] Implementar pantalla de login
- [ ] Guardar token en AsyncStorage
- [ ] Configurar interceptor de Axios
- [ ] Implementar logout

### Escaneo QR
- [ ] Integrar librería de escaneo QR
- [ ] Implementar pantalla de escaneo
- [ ] Mostrar información del producto
- [ ] Agregar al carrito desde QR

### Recetas Digitales
- [ ] Implementar selector de imagen
- [ ] Subir receta al servidor
- [ ] Mostrar lista de recetas
- [ ] Ver detalle de receta procesada

### Fidelización
- [ ] Mostrar puntos actuales
- [ ] Mostrar nivel de membresía
- [ ] Implementar canje de puntos
- [ ] Mostrar historial de movimientos

### Notificaciones Push
- [ ] Configurar Firebase
- [ ] Obtener token FCM
- [ ] Registrar dispositivo
- [ ] Mostrar notificaciones
- [ ] Marcar como leídas

### Seguimiento Delivery
- [ ] Integrar mapa (Google Maps / Apple Maps)
- [ ] Conectar WebSocket
- [ ] Actualizar ubicación en tiempo real
- [ ] Mostrar estado del pedido
- [ ] Implementar reconexión automática

---

**¡Listo para implementar!** 🚀

Copia este documento y compártelo con la IA que desarrollará tu app móvil.
