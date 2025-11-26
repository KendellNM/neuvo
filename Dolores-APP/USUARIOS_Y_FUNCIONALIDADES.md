# 👥 Usuarios de Prueba y Funcionalidades por Rol

## 🔐 Usuarios de Prueba (Seeder)

Al iniciar el backend, se crean automáticamente estos usuarios:

| Rol             | Email                     | Contraseña      |
| --------------- | ------------------------- | --------------- |
| 👨‍💼 ADMIN        | admin@farmacia.com        | admin123        |
| 👤 CLIENTE      | cliente@farmacia.com      | cliente123      |
| 🚚 REPARTIDOR   | repartidor@farmacia.com   | repartidor123   |
| 💊 FARMACEUTICO | farmaceutico@farmacia.com | farmaceutico123 |

---

## 📱 Funcionalidades por Rol

### 👤 CLIENTE

| Funcionalidad        | Estado | Descripción                     |
| -------------------- | ------ | ------------------------------- |
| 🛍️ Ver Productos     | ✅     | Catálogo completo de productos  |
| 📷 Escanear QR       | ✅     | Escanear código QR de productos |
| 📋 Recetas Digitales | ✅     | Subir recetas con OCR           |
| 🎁 Fidelización      | ✅     | Ver puntos, niveles, canjear    |
| 🚚 Seguir Pedido     | ✅     | Tracking en tiempo real         |
| 🔔 Notificaciones    | ✅     | Ver notificaciones              |

### 🚚 REPARTIDOR

| Funcionalidad      | Estado | Descripción                        |
| ------------------ | ------ | ---------------------------------- |
| 🚚 Iniciar Entrega | ✅     | Envía ubicación GPS en tiempo real |
| 📦 Mis Entregas    | 🔜     | Lista de entregas pendientes       |
| 📊 Historial       | 🔜     | Historial de entregas completadas  |

### 💊 FARMACEUTICO

| Funcionalidad      | Estado | Descripción                     |
| ------------------ | ------ | ------------------------------- |
| 📷 Escanear QR     | ✅     | Verificar productos             |
| 📋 Validar Recetas | ✅     | Validar recetas pendientes      |
| 💊 Inventario      | ✅     | Ver productos disponibles       |
| 💬 Consultas       | 🔜     | Responder consultas de clientes |
| 🔔 Notificaciones  | ✅     | Ver notificaciones              |

### 👨‍💼 ADMIN

| Funcionalidad          | Estado | Descripción              |
| ---------------------- | ------ | ------------------------ |
| 💊 Gestionar Productos | ✅     | Ver/editar productos     |
| 📦 Ver Pedidos         | 🔜     | Gestionar pedidos        |
| 👥 Gestionar Usuarios  | 🔜     | Administrar usuarios     |
| 📋 Recetas Pendientes  | ✅     | Ver recetas por validar  |
| 📊 Reportes            | 🔜     | Estadísticas del sistema |
| 🔔 Notificaciones      | ✅     | Ver notificaciones       |

---

## 🎯 Funcionalidades Principales Implementadas

### 1. 📷 Escaneo QR de Productos

- Escaneo continuo con ZXing
- Obtiene información del producto desde el backend
- Disponible para: CLIENTE, FARMACEUTICO

### 2. 📋 Recetas Digitales con OCR

- Captura desde cámara o galería
- Procesamiento OCR en backend (Tesseract)
- Estados: PENDIENTE → PROCESADA → VALIDADA/RECHAZADA
- Disponible para: CLIENTE, FARMACEUTICO, ADMIN

### 3. 🎁 Programa de Fidelización

- 4 niveles: BRONCE 🥉, PLATA 🥈, ORO 🥇, PLATINO 💎
- Acumulación de puntos por compras
- Canje de puntos por cupones
- Historial de movimientos
- Disponible para: CLIENTE

### 4. 🚚 Tracking en Tiempo Real

- **Repartidor**: Envía ubicación GPS cada 5 segundos
- **Cliente**: Recibe ubicación y ve ruta en mapa
- WebSocket para comunicación en tiempo real
- Rutas con OSRM (sin API key)

### 5. 🔔 Notificaciones Sin Firebase

- Polling cada minuto (NotificationService)
- WebSocket en tiempo real (NotificationWebSocketService)
- Notificaciones locales
- Disponible para: TODOS

### 6. 🛍️ Catálogo de Productos

- Lista en grid de 2 columnas
- Detalle de producto con imagen
- Selector de cantidad
- Botón agregar al carrito
- Disponible para: TODOS

---

## 🗄️ Datos de Prueba (Seeder)

### Categorías (6)

- Medicamentos
- Vitaminas
- Cuidado Personal
- Bebés
- Dermocosméticos
- Primeros Auxilios

### Productos (12)

- Paracetamol 500mg - S/ 8.50
- Ibuprofeno 400mg - S/ 12.00
- Amoxicilina 500mg - S/ 25.00 (requiere receta)
- Omeprazol 20mg - S/ 15.00
- Loratadina 10mg - S/ 10.00
- Vitamina C 1000mg - S/ 18.00
- Vitamina D3 2000UI - S/ 22.00
- Complejo B - S/ 20.00
- Omega 3 - S/ 35.00
- Alcohol en Gel 500ml - S/ 12.00
- Protector Solar SPF50 - S/ 45.00
- Crema Hidratante - S/ 38.00

---

## 🚀 Cómo Probar

### 1. Iniciar Backend

```bash
cd Proyecto-Mobiles-Backend-Farmacia
./gradlew bootRun
```

### 2. Verificar Usuarios Creados

En los logs verás:

```
═══════════════════════════════════════════════════════════
  USUARIOS DE PRUEBA:
  📧 admin@farmacia.com / admin123 (ADMIN)
  📧 cliente@farmacia.com / cliente123 (CLIENTE)
  📧 repartidor@farmacia.com / repartidor123 (REPARTIDOR)
  📧 farmaceutico@farmacia.com / farmaceutico123 (FARMACEUTICO)
═══════════════════════════════════════════════════════════
```

### 3. Probar en la App

1. Compila e instala la app
2. Login con cualquier usuario de prueba
3. Verás el home correspondiente a tu rol

---

## 🎨 Modo Oscuro

La app es compatible con modo oscuro:

- Colores adaptados automáticamente
- Iconos con tint dinámico
- Fondos y textos ajustados

Para probar: Configuración del dispositivo → Pantalla → Modo oscuro

---

**Última actualización:** 2025-11-26
