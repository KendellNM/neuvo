# 📚 Documentación API - Farmacia Dolores

## ✅ IMPLEMENTACIÓN COMPLETA

### 🎯 Requisitos Cumplidos

#### 1. **APIs REST Robustas y Seguras**
- ✅ Todas las APIs implementadas con Spring Boot
- ✅ Seguridad con JWT (Bearer Token)
- ✅ Validación de datos con `@Valid`
- ✅ Control de acceso con `@PreAuthorize`
- ✅ CORS configurado correctamente

#### 2. **Documentación con Swagger/OpenAPI**
- ✅ Swagger UI disponible en: `http://localhost:8080/swagger-ui.html`
- ✅ API Docs JSON en: `http://localhost:8080/api-docs`
- ✅ Todas las APIs documentadas con anotaciones
- ✅ Ejemplos de request/response
- ✅ Códigos de estado HTTP documentados

#### 3. **Manejo de Errores Robusto**
- ✅ `GlobalExceptionHandler` para manejo centralizado
- ✅ Respuestas estandarizadas con `ApiResponse<T>`
- ✅ Excepciones personalizadas:
  - `ResourceNotFoundException` (404)
  - `BadRequestException` (400)
  - `UnauthorizedException` (401)
  - `InsufficientPointsException` (400)
- ✅ Validación de campos con mensajes claros
- ✅ Manejo de errores de subida de archivos

#### 4. **Persistencia Funcional**
- ✅ JPA/Hibernate configurado
- ✅ Repositories con Spring Data JPA
- ✅ Transacciones con `@Transactional`
- ✅ Relaciones entre entidades correctas
- ✅ Scripts SQL para crear tablas

---

## 🚀 FUNCIONALIDADES IMPLEMENTADAS

### 1. **Escaneo QR de Productos**
**Endpoint:** `GET /api/productos/{id}/mobile`

**Descripción:** Endpoint optimizado para app móvil. El QR contiene solo el ID del producto.

**Flujo:**
1. Usuario escanea QR → obtiene ID
2. App llama al endpoint con el ID
3. Backend retorna toda la información del producto

**Response:**
```json
{
  "id": 123,
  "nombre": "Aspirina 500mg",
  "precio": 5.50,
  "stock": 100,
  "disponible": true,
  "imagenUrl": "...",
  "descripcion": "...",
  "laboratorioNombre": "Bayer"
}
```

---

### 2. **Recetas Digitales con OCR**
**Endpoints:**
- `POST /api/recetas-digitales/procesar` - Procesar receta con OCR
- `GET /api/recetas-digitales/{id}` - Obtener receta
- `GET /api/recetas-digitales/cliente/{clienteId}` - Recetas del cliente
- `PUT /api/recetas-digitales/{id}/validar` - Validar receta

**Características:**
- ✅ Extracción de texto con Tesseract OCR
- ✅ Detección automática de medicamentos
- ✅ Validación con base de datos de productos
- ✅ Estados: PENDIENTE, PROCESADA, VALIDADA, RECHAZADA
- ✅ Seguridad: Solo cliente propietario o admin

**Ejemplo Request:**
```bash
POST /api/recetas-digitales/procesar
Content-Type: multipart/form-data

imagen: [archivo.jpg]
clienteId: 1
```

**Response:**
```json
{
  "success": true,
  "message": "Receta procesada exitosamente",
  "data": {
    "id": 1,
    "imagenUrl": "uploads/recetas/abc123.jpg",
    "textoExtraido": "Aspirina 500mg...",
    "estado": "PENDIENTE",
    "detalles": [
      {
        "medicamentoTexto": "Aspirina 500mg",
        "validado": false
      }
    ]
  }
}
```

---

### 3. **Programa de Fidelización**
**Endpoints:**
- `POST /api/fidelizacion/crear` - Crear programa
- `GET /api/fidelizacion/cliente/{clienteId}` - Obtener puntos
- `POST /api/fidelizacion/canjear` - Canjear puntos
- `GET /api/fidelizacion/historial/{clienteId}` - Historial de movimientos

**Características:**
- ✅ Acumulación automática de puntos por compra
- ✅ Niveles de membresía: BRONCE, PLATA, ORO, PLATINO
- ✅ Canje de puntos por cupones
- ✅ Historial completo de movimientos
- ✅ Cálculo automático de nivel según puntos

**Niveles:**
- BRONCE: 0 - 1,999 puntos
- PLATA: 2,000 - 4,999 puntos
- ORO: 5,000 - 9,999 puntos
- PLATINO: 10,000+ puntos

**Response:**
```json
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

---

### 4. **Notificaciones Push**
**Endpoints:**
- `POST /api/notificaciones/registrar-dispositivo` - Registrar dispositivo
- `GET /api/notificaciones/cliente/{clienteId}` - Obtener notificaciones
- `PUT /api/notificaciones/{id}/marcar-leida` - Marcar como leída
- `POST /api/notificaciones/enviar-promocion` - Enviar promoción (Admin)

**Características:**
- ✅ Integración con Firebase Cloud Messaging (FCM)
- ✅ Notificaciones automáticas cuando pedido está listo
- ✅ Notificaciones de promociones
- ✅ Historial de notificaciones
- ✅ Estado leído/no leído

**Tipos de Notificaciones:**
- PEDIDO: Estado de pedidos
- PROMOCION: Ofertas y descuentos
- RECORDATORIO: Recordatorios de medicamentos
- SISTEMA: Notificaciones del sistema

---

### 5. **Seguimiento en Tiempo Real (WebSocket)**
**Endpoints WebSocket:**
- Conexión: `ws://localhost:8080/ws-delivery`
- Suscripción: `/topic/delivery/{pedidoId}`
- Envío ubicación: `/app/delivery/location`
- Envío estado: `/app/delivery/status`

**Endpoints REST:**
- `POST /api/delivery/location` - Actualizar ubicación
- `POST /api/delivery/status` - Actualizar estado

**Características:**
- ✅ Actualización de ubicación en tiempo real
- ✅ Notificaciones de cambio de estado
- ✅ Cliente y delivery conectados simultáneamente
- ✅ Historial de ubicaciones

---

## 🔐 SEGURIDAD

### Autenticación

#### Login Flexible
**Endpoint:** `POST /api/auth/login`

El sistema acepta login con **DNI** (para clientes) o **correo** (para admin/staff):

**Login de Cliente (con DNI):**
```json
{
  "username": "12345678",
  "password": "password123"
}
```

**Login de Admin/Staff (con correo):**
```json
{
  "username": "admin@farmacia.com",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "roles": ["ROLE_CLIENTE"]
}
```

**Lógica de autenticación:**
1. El sistema busca primero por DNI en la tabla `Clientes`
2. Si no encuentra, busca por correo en la tabla `Usuarios`
3. Esto permite que clientes usen DNI y personal administrativo use correo

#### Uso del Token
Todas las APIs protegidas requieren JWT Bearer Token:

```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Roles y Permisos
- **CLIENTE**: Acceso a sus propios datos
- **ADMIN**: Acceso completo
- **DELIVERY**: Actualizar ubicación y estado de pedidos

---

## 📊 MANEJO DE ERRORES

### Respuesta de Error Estándar
```json
{
  "success": false,
  "error": "Producto no encontrado con id: '999'",
  "timestamp": "2025-11-13T10:30:00",
  "path": "/api/productos/999"
}
```

### Códigos de Estado HTTP
- `200 OK` - Operación exitosa
- `201 Created` - Recurso creado
- `400 Bad Request` - Datos inválidos
- `401 Unauthorized` - No autenticado
- `403 Forbidden` - Sin permisos
- `404 Not Found` - Recurso no encontrado
- `500 Internal Server Error` - Error del servidor

---

## 🗄️ BASE DE DATOS

### Nuevas Tablas Creadas
1. **RecetasDigitales** - Almacena recetas escaneadas
2. **RecetasDigitalesDetalles** - Medicamentos detectados
3. **ProgramaFidelizacion** - Puntos y niveles de clientes
4. **MovimientosPuntos** - Historial de puntos
5. **Cupones** - Cupones canjeables
6. **NotificacionesPush** - Historial de notificaciones
7. **DispositivosClientes** - Tokens FCM de dispositivos

**Script SQL:** Ver `database/nuevas_tablas.sql`

---

## 🧪 TESTING CON SWAGGER

### Acceder a Swagger UI
1. Iniciar aplicación: `./gradlew bootRun`
2. Abrir navegador: `http://localhost:8080/swagger-ui.html`
3. Autenticarse con JWT (botón "Authorize")
4. Probar endpoints directamente desde la interfaz

### Características de Swagger
- ✅ Documentación interactiva
- ✅ Probar APIs sin Postman
- ✅ Ver ejemplos de request/response
- ✅ Exportar OpenAPI JSON
- ✅ Filtrado y búsqueda de endpoints

---

## 📦 DEPENDENCIAS AGREGADAS

```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13'
implementation 'net.sourceforge.tess4j:tess4j:5.9.0'
implementation 'com.google.firebase:firebase-admin:9.2.0'
implementation 'org.springframework.boot:spring-boot-starter-websocket'
```

---

## 🚀 PRÓXIMOS PASOS

### Para Producción
1. Configurar Firebase (archivo `firebase-service-account.json`)
2. Descargar Tesseract OCR data files (`tessdata/spa.traineddata`)
3. Configurar base de datos MySQL
4. Ejecutar script SQL de tablas
5. Configurar variables de entorno
6. Desplegar en servidor

### Configuración Firebase
```java
// Colocar firebase-service-account.json en la raíz del proyecto
// Obtener desde: Firebase Console > Project Settings > Service Accounts
```

### Configuración Tesseract
```bash
# Descargar tessdata
mkdir tessdata
cd tessdata
wget https://github.com/tesseract-ocr/tessdata/raw/main/spa.traineddata
```

---

## 📞 SOPORTE

Para dudas o problemas:
- Revisar logs de la aplicación
- Verificar Swagger UI para documentación
- Consultar este documento
- Revisar código fuente con comentarios

---

**✅ TODAS LAS FUNCIONALIDADES ESTÁN IMPLEMENTADAS Y LISTAS PARA USAR**
