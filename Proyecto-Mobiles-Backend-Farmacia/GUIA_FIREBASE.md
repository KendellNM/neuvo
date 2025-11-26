# 🔥 Guía de Integración Firebase Cloud Messaging

## PASO 1: Crear Proyecto en Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Click en "Agregar proyecto" o "Add project"
3. Nombre del proyecto: `farmacia-dolores` (o el que prefieras)
4. Acepta los términos y crea el proyecto

## PASO 2: Agregar App Android/iOS

### Para Android:
1. En Firebase Console, click en el ícono de Android
2. Nombre del paquete: `com.farm.dolores.farmacia` (debe coincidir con tu app móvil)
3. Descargar `google-services.json`
4. Este archivo va en tu proyecto Android (app móvil)

### Para iOS:
1. Click en el ícono de iOS
2. Bundle ID: `com.farm.dolores.farmacia`
3. Descargar `GoogleService-Info.plist`
4. Este archivo va en tu proyecto iOS

## PASO 3: Obtener Credenciales del Servidor (Backend)

1. En Firebase Console, ve a **Project Settings** (⚙️ arriba a la izquierda)
2. Ve a la pestaña **Service Accounts**
3. Click en **Generate new private key**
4. Se descargará un archivo JSON (ejemplo: `farmacia-dolores-firebase-adminsdk-xxxxx.json`)
5. **RENOMBRA** este archivo a: `firebase-service-account.json`
6. **COLOCA** este archivo en la raíz de tu proyecto Spring Boot

```
Proyecto-Mobiles-Backend-Farmacia/
├── src/
├── build.gradle
├── firebase-service-account.json  ← AQUÍ
└── ...
```

## PASO 4: Configurar Backend (Ya está hecho)

El código ya está implementado en `FirebaseConfig.java`:

```java
@Configuration
public class FirebaseConfig {
    @PostConstruct
    public void initialize() {
        try {
            FileInputStream serviceAccount = 
                new FileInputStream("firebase-service-account.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            System.err.println("Error inicializando Firebase: " + e.getMessage());
        }
    }
}
```

## PASO 5: Configurar App Móvil (Android)

### 5.1 Agregar dependencias en `build.gradle` (app móvil):

```gradle
dependencies {
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-messaging'
    implementation 'com.google.firebase:firebase-analytics'
}
```

### 5.2 Agregar plugin en `build.gradle` (nivel proyecto):

```gradle
buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.4.0'
    }
}
```

### 5.3 Aplicar plugin en `build.gradle` (app):

```gradle
plugins {
    id 'com.android.application'
    id 'com.google.gms.google-services'  // ← Agregar esto
}
```

### 5.4 Colocar `google-services.json` en:
```
app/
├── src/
├── build.gradle
└── google-services.json  ← AQUÍ
```

### 5.5 Crear servicio de notificaciones (Kotlin/Java):

**Kotlin:**
```kotlin
// MyFirebaseMessagingService.kt
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Enviar token al backend
        sendTokenToServer(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Mostrar notificación
        showNotification(message.notification?.title, message.notification?.body)
    }

    private fun sendTokenToServer(token: String) {
        // Llamar a tu API: POST /api/notificaciones/registrar-dispositivo
        val request = RegistrarDispositivoRequest(
            clienteId = getCurrentUserId(),
            fcmToken = token,
            plataforma = "ANDROID"
        )
        // Hacer llamada HTTP a tu backend
    }
}
```

### 5.6 Registrar servicio en `AndroidManifest.xml`:

```xml
<manifest>
    <application>
        <!-- Otros componentes -->
        
        <service
            android:name=".MyFirebaseMessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
    </application>
</manifest>
```

### 5.7 Obtener token FCM en tu Activity/Fragment:

```kotlin
import com.google.firebase.messaging.FirebaseMessaging

FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        val token = task.result
        // Enviar token al backend
        registrarDispositivo(token)
    }
}
```

## PASO 6: Probar Notificaciones

### Opción 1: Desde Firebase Console (Manual)
1. Ve a Firebase Console > Cloud Messaging
2. Click en "Send your first message"
3. Escribe título y mensaje
4. Selecciona tu app
5. Enviar

### Opción 2: Desde tu Backend (Automático)
```bash
# Registrar dispositivo
POST http://localhost:8080/api/notificaciones/registrar-dispositivo
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "clienteId": 1,
  "fcmToken": "TOKEN_FCM_DEL_DISPOSITIVO",
  "plataforma": "ANDROID"
}

# Enviar notificación
POST http://localhost:8080/api/notificaciones/enviar-promocion?clienteId=1&mensaje=¡Oferta especial!
Authorization: Bearer YOUR_JWT_TOKEN
```

## PASO 7: Verificar Integración

### Backend:
1. Iniciar aplicación Spring Boot
2. Verificar logs: "Firebase initialized successfully" (o error si falta archivo)

### App Móvil:
1. Ejecutar app en dispositivo real (no emulador para notificaciones)
2. Verificar que se obtiene el token FCM
3. Verificar que se registra en el backend
4. Enviar notificación de prueba

## 🔒 SEGURIDAD

### ⚠️ IMPORTANTE: NO SUBIR A GIT

Agregar a `.gitignore`:
```
# Firebase
firebase-service-account.json
google-services.json
GoogleService-Info.plist
```

### Variables de Entorno (Producción)

En lugar de archivo, usar variable de entorno:

```java
@Configuration
public class FirebaseConfig {
    @PostConstruct
    public void initialize() {
        try {
            String firebaseConfig = System.getenv("FIREBASE_CONFIG");
            if (firebaseConfig != null) {
                InputStream stream = new ByteArrayInputStream(
                    firebaseConfig.getBytes(StandardCharsets.UTF_8)
                );
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(stream))
                    .build();
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
```

## 📱 EJEMPLO COMPLETO DE FLUJO

1. **Usuario instala app móvil**
2. **App obtiene token FCM** automáticamente
3. **App envía token al backend** → `POST /api/notificaciones/registrar-dispositivo`
4. **Backend guarda token** en tabla `DispositivosClientes`
5. **Usuario hace un pedido**
6. **Pedido cambia a "LISTO"**
7. **Backend envía notificación** automáticamente
8. **Firebase entrega notificación** al dispositivo
9. **Usuario ve notificación** en su teléfono

## 🧪 TESTING

### Probar sin app móvil (usando token de prueba):

1. Obtener token de prueba desde Firebase Console
2. Usar Postman para registrar dispositivo
3. Enviar notificación desde backend
4. Verificar en Firebase Console > Cloud Messaging > Logs

## ❓ TROUBLESHOOTING

### Error: "firebase-service-account.json not found"
- Verifica que el archivo esté en la raíz del proyecto
- Verifica el nombre exacto del archivo

### Error: "Invalid credentials"
- Descarga nuevamente el archivo desde Firebase Console
- Verifica que sea del proyecto correcto

### No llegan notificaciones
- Verifica que el token FCM sea válido
- Verifica que el dispositivo tenga internet
- Verifica logs del backend
- Prueba enviar desde Firebase Console directamente

### Token FCM cambia
- Es normal, el token puede cambiar
- Implementa `onNewToken()` para actualizar en backend

## 📚 RECURSOS

- [Firebase Console](https://console.firebase.google.com/)
- [Documentación FCM](https://firebase.google.com/docs/cloud-messaging)
- [Firebase Admin SDK](https://firebase.google.com/docs/admin/setup)
- [Android FCM Guide](https://firebase.google.com/docs/cloud-messaging/android/client)

---

**✅ Con esta guía tendrás Firebase completamente integrado en tu proyecto**
