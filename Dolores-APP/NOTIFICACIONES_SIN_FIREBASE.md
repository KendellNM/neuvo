# 🔔 Notificaciones Sin Firebase - Guía Completa

## ❓ ¿Por qué sin Firebase?

El backend de Farmacia Dolores **NO tiene Firebase configurado**, por lo que implementamos notificaciones usando métodos alternativos que funcionan perfectamente.

## 🎯 3 Opciones Implementadas

### Opción 1: **Polling con Servicio en Background** ⭐ RECOMENDADO

**Archivo:** `NotificationService.kt`

#### ✅ Ventajas:

- Simple de implementar
- No requiere cambios en el backend
- Funciona con el backend actual
- Confiable

#### ❌ Desventajas:

- Consume más batería (consulta cada 1 minuto)
- No es instantáneo (delay de hasta 1 minuto)

#### 📝 Cómo funciona:

1. Servicio en foreground se ejecuta en background
2. Cada 60 segundos consulta: `GET /api/notificaciones/cliente/{clienteId}`
3. Compara con última notificación guardada
4. Muestra notificaciones nuevas
5. Guarda en SQLite local

#### 🚀 Uso:

```kotlin
// Iniciar servicio
NotificationService.start(context)

// Detener servicio
NotificationService.stop(context)
```

---

### Opción 2: **WebSocket en Tiempo Real** 🚀 MÁS EFICIENTE

**Archivo:** `NotificationWebSocketService.kt`

#### ✅ Ventajas:

- Notificaciones instantáneas
- Consume menos batería
- Más eficiente

#### ❌ Desventajas:

- **Requiere que el backend implemente WebSocket de notificaciones**
- Más complejo

#### 📝 Cómo funciona:

1. Conecta a `ws://localhost:8090/ws-notifications`
2. Se suscribe a `/topic/notifications/{clienteId}`
3. Recibe notificaciones en tiempo real
4. Muestra notificación local
5. Guarda en SQLite

#### 🔧 Configuración Backend Necesaria:

El backend debe agregar un endpoint WebSocket similar al de delivery:

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-notifications")
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
```

Y un controlador para enviar notificaciones:

```java
@RestController
public class NotificationWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Long clienteId, Notificacion notif) {
        messagingTemplate.convertAndSend(
            "/topic/notifications/" + clienteId,
            notif
        );
    }
}
```

#### 🚀 Uso:

```kotlin
// Iniciar servicio WebSocket
NotificationWebSocketService.start(context)

// Detener servicio
NotificationWebSocketService.stop(context)
```

---

### Opción 3: **Notificaciones Locales Manuales** 💡 SIMPLE

**Archivo:** `NotificationHelper.kt`

#### ✅ Ventajas:

- Muy simple
- No consume batería
- Control total

#### ❌ Desventajas:

- Requiere que la app esté abierta
- No es automático

#### 📝 Cómo funciona:

Helper con funciones para mostrar notificaciones locales cuando ocurren eventos en la app.

#### 🚀 Uso:

```kotlin
// Notificación de pedido listo
NotificationHelper.notifyPedidoListo(context, pedidoId)

// Notificación de pedido en camino
NotificationHelper.notifyPedidoEnCamino(context, pedidoId)

// Notificación de promoción
NotificationHelper.notifyPromocion(context, "20% OFF", "En vitaminas")

// Notificación de puntos
NotificationHelper.notifyPuntosAcumulados(context, 100)

// Notificación de receta
NotificationHelper.notifyRecetaProcesada(context, recetaId, "VALIDADA")
```

---

## 📱 Activity de Notificaciones

**Archivo:** `NotificacionesActivity.kt`

Pantalla para:

- Ver historial de notificaciones
- Iniciar/detener servicio de notificaciones
- Marcar notificaciones como leídas

### Layout:

- Botones para iniciar/detener servicio
- RecyclerView con historial
- Indicador de notificaciones no leídas (negrita)

---

## 🔧 Configuración

### 1. Permisos en AndroidManifest.xml ✅

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
```

### 2. Servicios Registrados ✅

```xml
<service android:name=".services.NotificationService" />
<service android:name=".services.NotificationWebSocketService" />
```

### 3. Canal de Notificaciones ✅

Se crea automáticamente al iniciar el servicio.

---

## 🎯 ¿Cuál Opción Usar?

### Para Desarrollo/Testing:

**Opción 1 (Polling)** - Funciona inmediatamente sin cambios en backend

### Para Producción:

**Opción 2 (WebSocket)** - Más eficiente, pero requiere implementar en backend

### Para Eventos en la App:

**Opción 3 (Helper)** - Complementa las otras opciones

---

## 💡 Recomendación: Usar Opción 1 + Opción 3

```kotlin
// Al iniciar sesión
NotificationService.start(context)

// Cuando ocurre un evento en la app
NotificationHelper.notifyPedidoListo(context, pedidoId)

// Al cerrar sesión
NotificationService.stop(context)
```

---

## 📊 Comparación con Firebase

| Característica | Firebase               | Polling    | WebSocket    |
| -------------- | ---------------------- | ---------- | ------------ |
| Instantáneo    | ✅                     | ❌ (1 min) | ✅           |
| Batería        | ✅ Eficiente           | ⚠️ Media   | ✅ Eficiente |
| Backend        | ❌ Requiere            | ✅ No      | ⚠️ Sí        |
| Complejidad    | ⚠️ Media               | ✅ Simple  | ⚠️ Media     |
| Costo          | 💰 Gratis hasta límite | ✅ Gratis  | ✅ Gratis    |
| Offline        | ✅                     | ❌         | ❌           |

---

## 🔒 Seguridad

### Polling:

- Usa token JWT en headers
- Solo obtiene notificaciones del cliente autenticado

### WebSocket:

- Puede agregar autenticación en handshake
- Suscripción por clienteId

---

## 🧪 Testing

### 1. Iniciar Backend

```bash
cd Proyecto-Mobiles-Backend-Farmacia
./gradlew bootRun
```

### 2. Crear Notificación desde Backend

Usar Swagger UI o Postman:

```
POST http://localhost:8090/api/notificaciones/enviar-promocion
{
  "titulo": "Promoción Especial",
  "mensaje": "20% de descuento en vitaminas",
  "clienteIds": [1]
}
```

### 3. Ver en App

- Abrir NotificacionesActivity
- Iniciar servicio
- Esperar hasta 1 minuto
- Ver notificación aparecer

---

## 📝 Ejemplo Completo

### En MainActivity.kt:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Crear canal de notificaciones
    NotificationHelper.createNotificationChannel(this)

    // Iniciar servicio de notificaciones
    NotificationService.start(this)

    // Botón para ver notificaciones
    btnNotificaciones.setOnClickListener {
        startActivity(Intent(this, NotificacionesActivity::class.java))
    }
}

override fun onDestroy() {
    super.onDestroy()
    // Opcional: detener servicio al cerrar app
    // NotificationService.stop(this)
}
```

### Cuando se crea un pedido:

```kotlin
lifecycleScope.launch {
    val response = pedidosApi.crearPedido(pedido)
    if (response.isSuccessful) {
        // Notificación local inmediata
        NotificationHelper.showNotification(
            this@MainActivity,
            pedido.id.toInt(),
            "Pedido Creado",
            "Tu pedido #${pedido.id} ha sido creado exitosamente"
        )
    }
}
```

---

## 🚀 Próximos Pasos

### Si quieres mejorar:

1. **Implementar WorkManager** para polling más eficiente
2. **Agregar WebSocket en backend** para notificaciones instantáneas
3. **Implementar retry logic** para reconexión automática
4. **Agregar notificaciones programadas** (recordatorios de medicamentos)

---

## 📚 Archivos Creados

1. `NotificationService.kt` - Servicio de polling
2. `NotificationWebSocketService.kt` - Servicio WebSocket
3. `NotificationHelper.kt` - Helper para notificaciones locales
4. `NotificacionesActivity.kt` - Pantalla de notificaciones
5. `NotificacionesAdapter.kt` - Adapter para lista
6. `activity_notificaciones.xml` - Layout
7. `item_notificacion.xml` - Item de lista

---

## ✅ Conclusión

**Sin Firebase, tienes 3 opciones funcionales:**

1. ⭐ **Polling** - Usa esto ahora (ya funciona)
2. 🚀 **WebSocket** - Implementa después para mejor rendimiento
3. 💡 **Helper** - Usa para eventos inmediatos en la app

**Todas funcionan sin Firebase y sin costo adicional!** 🎉

---

**Última actualización:** 2025-01-25
