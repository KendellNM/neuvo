# 🚀 Inicialización Automática del Backend

## ✅ QUÉ SE EJECUTA AL INICIAR

Cuando inicies el backend con `./gradlew bootRun`, se ejecutarán automáticamente:

### 1️⃣ Script SQL (`data.sql`)
Carga datos de prueba en la base de datos.

### 2️⃣ Simulador de Delivery
Envía ubicaciones cada 5 segundos por WebSocket.

---

## 📊 DATOS DE PRUEBA CARGADOS

### 👤 USUARIOS (Contraseña para todos: `password123`)

| Email | Rol | DNI (si es cliente) |
|-------|-----|---------------------|
| admin@farmacia.com | ADMIN | - |
| cliente1@email.com | CLIENTE | 12345678 |
| farmaceutico@farmacia.com | FARMACEUTICO | - |
| medico@hospital.com | MEDICO | - |
| repartidor@farmacia.com | REPARTIDOR | - |

### 👥 CLIENTES

| ID | Nombre | DNI | Teléfono |
|----|--------|-----|----------|
| 1 | Juan Pérez García | 12345678 | 987654321 |
| 2 | María López Rodríguez | 87654321 | 987654322 |
| 3 | Carlos Sánchez Torres | 11223344 | 987654323 |

### 💊 PRODUCTOS (8 productos)

| ID | Nombre | Precio | Stock | Requiere Receta |
|----|--------|--------|-------|-----------------|
| 1 | Aspirina 500mg | S/ 5.50 | 100 | No |
| 2 | Paracetamol 500mg | S/ 3.50 | 150 | No |
| 3 | Amoxicilina 500mg | S/ 12.00 | 80 | Sí |
| 4 | Ibuprofeno 400mg | S/ 6.50 | 120 | No |
| 5 | Vitamina C 1000mg | S/ 8.00 | 200 | No |
| 6 | Omeprazol 20mg | S/ 10.50 | 90 | No |
| 7 | Loratadina 10mg | S/ 7.00 | 110 | No |
| 8 | Crema Hidratante | S/ 15.00 | 60 | No |

### 🏷️ CATEGORÍAS (5 categorías)
- Analgésicos
- Antibióticos
- Vitaminas
- Antiinflamatorios
- Dermatológicos

### 🏭 LABORATORIOS (5 laboratorios)
- Bayer (Alemania)
- Pfizer (Estados Unidos)
- Roche (Suiza)
- Novartis (Suiza)
- Sanofi (Francia)

### 📦 PEDIDOS (2 pedidos de ejemplo)
- Pedido #1: Cliente Juan Pérez - S/ 25.50 - PENDIENTE
- Pedido #2: Cliente María López - S/ 45.00 - EN_PREPARACION

### 🎯 PROGRAMA DE FIDELIZACIÓN (3 clientes)
- Cliente 1: 350 puntos - Nivel BRONCE
- Cliente 2: 2,500 puntos - Nivel PLATA
- Cliente 3: 5,500 puntos - Nivel ORO

---

## 📍 SIMULADOR DE DELIVERY

### Qué hace:
- Se inicia automáticamente 10 segundos después del arranque
- Envía ubicaciones cada 5 segundos
- Simula un repartidor moviéndose del punto A al punto B
- Envía 5 ubicaciones y luego reinicia el ciclo

### Ruta simulada (Pedido #123):
1. **Farmacia:** Lat: -12.0464, Lng: -77.0428
2. **Saliendo:** Lat: -12.0480, Lng: -77.0440
3. **En camino:** Lat: -12.0500, Lng: -77.0450
4. **Cerca:** Lat: -12.0530, Lng: -77.0470
5. **Llegando:** Lat: -12.0550, Lng: -77.0480

### Cómo verlo:
1. Inicia el backend
2. Abre Postman
3. Conecta a `ws://localhost:8090/ws-delivery`
4. Suscríbete a `/topic/delivery/123`
5. Verás las ubicaciones llegar automáticamente cada 5 segundos

---

## 🔌 CÓMO PROBAR

### Paso 1: Iniciar Backend
```bash
./gradlew bootRun
```

Verás en la consola:
```
🚀 Backend iniciado correctamente!
📍 Simulador de delivery activo - Enviando ubicaciones cada 5 segundos
🔌 WebSocket: ws://localhost:8090/ws-delivery
📊 Suscríbete a: /topic/delivery/123
```

### Paso 2: Probar Login
```bash
POST http://localhost:8090/api/auth/login
Content-Type: application/json

{
  "username": "12345678",
  "password": "password123"
}
```

### Paso 3: Ver Productos
```bash
GET http://localhost:8090/api/productos
Authorization: Bearer {token}
```

### Paso 4: Escanear QR (Producto 1)
```bash
GET http://localhost:8090/api/productos/1/mobile
Authorization: Bearer {token}
```

### Paso 5: Ver Puntos de Fidelización
```bash
GET http://localhost:8090/api/fidelizacion/cliente/1
Authorization: Bearer {token}
```

### Paso 6: Ver Delivery en Tiempo Real
```
1. Postman WebSocket: ws://localhost:8090/ws-delivery
2. Suscribirse:
   SUBSCRIBE
   id:sub-0
   destination:/topic/delivery/123
   
3. Esperar y ver ubicaciones llegar automáticamente
```

---

## 📝 LOGS DEL BACKEND

Cuando el backend inicie, verás:

```
✅ Datos de prueba cargados correctamente
👤 Usuario Admin: admin@farmacia.com / password123
👤 Usuario Cliente: cliente1@email.com / password123
📦 8 productos creados
👥 3 clientes creados
🎯 Programa de fidelización activo

🚀 Backend iniciado correctamente!
📍 Simulador de delivery activo - Enviando ubicaciones cada 5 segundos
🔌 WebSocket: ws://localhost:8090/ws-delivery
📊 Suscríbete a: /topic/delivery/123

... (espera 10 segundos) ...

📍 Ubicación enviada: Pedido 123 - Lat: -12.0464, Lng: -77.0428
📍 Ubicación enviada: Pedido 123 - Lat: -12.0480, Lng: -77.0440
📍 Ubicación enviada: Pedido 123 - Lat: -12.0500, Lng: -77.0450
📍 Ubicación enviada: Pedido 123 - Lat: -12.0530, Lng: -77.0470
📍 Ubicación enviada: Pedido 123 - Lat: -12.0550, Lng: -77.0480
🔄 Ruta completada, reiniciando simulación...
```

---

## 🎯 CASOS DE USO LISTOS

### 1. Login con DNI
```json
{
  "username": "12345678",
  "password": "password123"
}
```

### 2. Escanear QR de Aspirina
```
GET /api/productos/1/mobile
```

### 3. Ver puntos del cliente
```
GET /api/fidelizacion/cliente/1
```

### 4. Canjear 100 puntos
```json
POST /api/fidelizacion/canjear
{
  "clienteId": 1,
  "puntosACanjear": 100,
  "descripcionCupon": "Descuento 10%"
}
```

### 5. Ver delivery en tiempo real
```
WebSocket: ws://localhost:8090/ws-delivery
Topic: /topic/delivery/123
```

---

## 🔄 REINICIAR DATOS

Si quieres reiniciar los datos de prueba:

### Opción 1: Borrar y recrear base de datos
```sql
DROP DATABASE dbfuncionas;
CREATE DATABASE dbfuncionas;
```
Luego reinicia el backend.

### Opción 2: Ejecutar script manualmente
```bash
mysql -u root -p dbfuncionas < src/main/resources/data.sql
```

---

## ⚙️ CONFIGURACIÓN

### Desactivar inicialización automática:
En `application.properties`, cambia:
```properties
spring.sql.init.mode=never
```

### Desactivar simulador de delivery:
Comenta la anotación en `DeliverySimulatorService.java`:
```java
// @Scheduled(fixedDelay = 5000, initialDelay = 10000)
```

---

## ✅ CHECKLIST DE VERIFICACIÓN

Después de iniciar el backend, verifica:

- [ ] Backend corriendo en puerto 8090
- [ ] Datos de prueba cargados (ver logs)
- [ ] Login funciona con `12345678 / password123`
- [ ] Productos disponibles (8 productos)
- [ ] WebSocket conecta correctamente
- [ ] Simulador enviando ubicaciones cada 5 segundos
- [ ] Swagger UI accesible: http://localhost:8090/swagger-ui.html

---

## 🎉 RESULTADO

Con esta configuración, el backend está **100% listo** para:
- ✅ Probar todas las funcionalidades
- ✅ Desarrollar la app Android
- ✅ Ver delivery en tiempo real
- ✅ Hacer demos
- ✅ Testing completo

**¡Todo funciona automáticamente al iniciar!** 🚀
