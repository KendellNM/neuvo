# 🔍 Análisis de Flujos - Farmacia Dolores

## 📊 Resumen de Problemas Encontrados

| #   | Problema                                                      | Severidad | Estado                    |
| --- | ------------------------------------------------------------- | --------- | ------------------------- |
| 1   | SplashActivity redirige a MainActivity en vez de HomeActivity | 🔴 Alta   | ✅ Corregido              |
| 2   | NotificationService usa MainActivity en vez de HomeActivity   | 🟡 Media  | ✅ Corregido              |
| 3   | QRScannerActivity no tiene verificación de rol                | 🟢 Baja   | OK (accesible para todos) |
| 4   | Falta manejo de sesión expirada                               | 🟡 Media  | Pendiente (opcional)      |

---

## 🔴 PROBLEMA 1: Flujo de Navegación Inicial

### Descripción

`SplashActivity` redirige a `MainActivity` cuando hay token, pero debería ir directamente a `HomeActivity`.

### Flujo Actual (INCORRECTO):

```
SplashActivity
    ↓ (tiene token)
    MainActivity
        ↓
        HomeActivity
```

### Flujo Correcto:

```
SplashActivity
    ↓ (tiene token)
    HomeActivity (directo)
```

### Archivo Afectado:

`SplashActivity.kt` - Línea ~80

### Código Problemático:

```kotlin
if (token.isNullOrBlank()) {
    startActivity(Intent(this, LoginActivity::class.java))
} else {
    startActivity(Intent(this, MainActivity::class.java)) // ❌ Debería ser HomeActivity
}
```

### Solución:

```kotlin
if (token.isNullOrBlank()) {
    startActivity(Intent(this, LoginActivity::class.java))
} else {
    startActivity(Intent(this, HomeActivity::class.java)) // ✅ Correcto
}
```

---

## 🟡 PROBLEMA 2: NotificationService Redirige a MainActivity

### Descripción

Cuando el usuario toca una notificación, se abre `MainActivity` en vez de `HomeActivity`.

### Archivo Afectado:

`NotificationService.kt` - Línea ~130

### Código Problemático:

```kotlin
val intent = Intent(this, MainActivity::class.java).apply {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
}
```

### Solución:

```kotlin
val intent = Intent(this, HomeActivity::class.java).apply {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
}
```

---

## 🟡 PROBLEMA 3: Falta Manejo de Sesión Expirada

### Descripción

Si el token JWT expira, la app no redirige al login automáticamente.

### Flujo Actual:

```
Usuario con token expirado
    ↓
    Hace petición al backend
    ↓
    Error 401 Unauthorized
    ↓
    Toast "Error de autenticación" (confuso)
```

### Flujo Correcto:

```
Usuario con token expirado
    ↓
    Hace petición al backend
    ↓
    Error 401 Unauthorized
    ↓
    Limpiar sesión + Redirigir a Login
```

### Solución Propuesta:

Agregar interceptor en `NetworkClient.kt`:

```kotlin
class AuthInterceptor(...) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(request)

        if (response.code == 401) {
            // Token expirado - limpiar sesión
            TokenStore.clear()
            // Notificar a la UI (usar EventBus o LiveData)
        }

        return response
    }
}
```

---

## ✅ FLUJOS CORRECTOS

### Flujo de Login

```
LoginActivity
    ↓ (credenciales válidas)
    Guardar token + rol
    ↓
    HomeActivity (según rol)
```

**Estado:** ✅ Correcto

### Flujo de Registro

```
RegisterActivity (paso 1)
    ↓
RegisterActivity (paso 2)
    ↓ (registro exitoso)
    Guardar token + rol
    ↓
    HomeActivity
```

**Estado:** ✅ Correcto

### Flujo de Pedidos (Cliente)

```
HomeActivity (Cliente)
    ↓
    ProductosActivity
    ↓
    ProductoDetalleActivity (agregar al carrito)
    ↓
    CarritoActivity
    ↓
    CheckoutActivity
    ↓
    MisPedidosActivity
```

**Estado:** ✅ Correcto

### Flujo de Entregas (Repartidor)

```
HomeActivity (Repartidor)
    ↓
    PedidosAsignadosActivity
    ↓
    Seleccionar pedido → RepartidorActivity (tracking GPS)
    ↓
    Marcar como entregado
```

**Estado:** ✅ Correcto

### Flujo de Gestión (Admin)

```
HomeActivity (Admin)
    ↓
    GestionPedidosActivity
    ↓
    Cambiar estado / Asignar repartidor
```

**Estado:** ✅ Correcto

### Flujo de Tracking (Cliente)

```
MisPedidosActivity
    ↓ (pedido EN_CAMINO)
    DeliveryTrackingActivity
    ↓
    Ver ubicación del repartidor en tiempo real
```

**Estado:** ✅ Correcto

---

## 📋 MATRIZ DE ROLES Y ACCESOS

| Pantalla                 | Cliente | Repartidor | Admin | Farmacéutico |
| ------------------------ | ------- | ---------- | ----- | ------------ |
| ProductosActivity        | ✅      | ❌         | ✅    | ✅           |
| CarritoActivity          | ✅      | ❌         | ❌    | ❌           |
| CheckoutActivity         | ✅      | ❌         | ❌    | ❌           |
| MisPedidosActivity       | ✅      | ❌         | ❌    | ❌           |
| DeliveryTrackingActivity | ✅      | ❌         | ❌    | ❌           |
| PedidosAsignadosActivity | ❌      | ✅         | ❌    | ❌           |
| RepartidorActivity       | ❌      | ✅         | ❌    | ❌           |
| GestionPedidosActivity   | ❌      | ❌         | ✅    | ❌           |
| RecetaDigitalActivity    | ✅      | ❌         | ✅    | ✅           |
| FidelizacionActivity     | ✅      | ❌         | ❌    | ❌           |
| QRScannerActivity        | ✅      | ❌         | ❌    | ✅           |
| NotificacionesActivity   | ✅      | ✅         | ✅    | ✅           |

---

## 🔧 ACCIONES REQUERIDAS

### Prioridad Alta 🔴

1. [x] Corregir `SplashActivity` para ir a `HomeActivity` ✅ CORREGIDO

### Prioridad Media 🟡

2. [x] Corregir `NotificationService` para ir a `HomeActivity` ✅ CORREGIDO
3. [ ] Implementar manejo de token expirado (opcional)

### Prioridad Baja 🟢

4. [ ] Agregar verificación de rol en más activities (opcional)

---

## 📝 NOTAS ADICIONALES

### Sobre MainActivity

`MainActivity` actualmente solo sirve como "puente" hacia `HomeActivity`. Podría eliminarse completamente y hacer que `SplashActivity` vaya directo a `HomeActivity` o `LoginActivity`.

### Sobre el Token

El token JWT tiene una expiración. Actualmente no se maneja la renovación automática ni la redirección al login cuando expira.

### Sobre WebSocket

El WebSocket para tracking funciona correctamente, pero no tiene reconexión automática si se pierde la conexión.

---

**Última actualización:** 2025-11-26

---

## 🆕 MEJORAS IMPLEMENTADAS (Sesión Actual)

### Selección de Dirección en Checkout

**Antes:**

- El cliente escribía la dirección manualmente
- No había opción de usar ubicación GPS

**Ahora:**

- ✅ Botón "Mis Direcciones" - Muestra direcciones guardadas del cliente
- ✅ Botón "Mi Ubicación" - Obtiene ubicación GPS actual y la convierte a dirección
- ✅ El teléfono se pre-carga automáticamente del perfil del cliente
- ✅ Las coordenadas (lat/lng) se guardan para el tracking

### Endpoints Agregados

**Backend:**

- `GET /api/direcciones/cliente/{clienteId}` - Obtener direcciones del cliente

**App Android:**

- `DireccionesApi.kt` - Nueva API para gestionar direcciones

### Flujo Mejorado de Checkout

```
CheckoutActivity
    ↓
    Cargar clienteId automáticamente
    ↓
    Cargar direcciones guardadas del cliente
    ↓
    Opciones:
        1. Seleccionar dirección guardada
        2. Usar ubicación GPS actual
        3. Escribir dirección manualmente
    ↓
    Pre-cargar teléfono del cliente
    ↓
    Confirmar pedido con clienteId correcto
```

---

**Última actualización:** 2025-11-26

### Información que ve el Repartidor

**En la lista de pedidos asignados:**

- 📦 Número de pedido
- 👤 Nombre del cliente
- 📍 Dirección de entrega
- 💰 Total del pedido
- 🏷️ Estado (Asignado, En Camino, Entregado)

**Botones disponibles:**

- 📍 **Ver Mapa** - Abre Google Maps con navegación al destino
- 🚚 **Iniciar** - Cambia estado a EN_CAMINO y abre tracking GPS
- ✅ **Entregado** - Marca el pedido como entregado

**Al iniciar entrega:**

- Se muestra confirmación con dirección y nombre del cliente
- Se pasan las coordenadas (lat/lng) al RepartidorActivity
- El repartidor puede ver la ruta en el mapa y enviar su ubicación en tiempo real

### Flujo Completo Repartidor → Cliente

```
1. Admin asigna pedido a repartidor
   ↓
2. Repartidor ve pedido en "Pedidos Asignados"
   ↓
3. Repartidor toca "Iniciar Entrega"
   ↓
4. Estado cambia a EN_CAMINO
   ↓
5. Se abre RepartidorActivity con tracking GPS
   ↓
6. Ubicación se envía por WebSocket cada 5 segundos
   ↓
7. Cliente puede ver ubicación en DeliveryTrackingActivity
   ↓
8. Repartidor marca como "Entregado"
   ↓
9. Estado cambia a ENTREGADO
```
