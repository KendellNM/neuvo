# ✅ Integración Backend - Sistema de Roles

## 🎉 ¡BUENAS NOTICIAS!

Tu backend **YA tiene el sistema de roles implementado** y funciona perfectamente con la app Android.

---

## 📋 Lo que el Backend Retorna

### Endpoint: `POST /api/auth/login`

**Request:**

```json
{
  "username": "admin@test.com",
  "password": "password123"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "roles": ["ROLE_CLIENTE"]
}
```

O para otros roles:

```json
{
  "roles": ["ROLE_ADMIN"]
}
{
  "roles": ["ROLE_FARMACEUTICO"]
}
{
  "roles": ["ROLE_REPARTIDOR"]
}
```

---

## ✅ Compatibilidad Perfecta

El backend retorna exactamente lo que la app necesita:

### Backend (AuthController.java):

```java
String token = jwtUtil.generateToken(userDetails.getUsername());
List<String> roles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());
return ResponseEntity.ok(new AuthResponse(token, roles));
```

### App Android (RoleManager.kt):

```kotlin
fun parseRoleFromBackend(roles: List<String>): UserRole {
    return when {
        roles.any { it.contains("ADMIN", ignoreCase = true) } -> UserRole.ADMIN
        roles.any { it.contains("REPARTIDOR", ignoreCase = true) } -> UserRole.REPARTIDOR
        roles.any { it.contains("FARMACEUTICO", ignoreCase = true) } -> UserRole.FARMACEUTICO
        else -> UserRole.CLIENTE
    }
}
```

**¡Funciona perfectamente!** ✅

---

## 🔧 Lo que DEBES Hacer en la App

### 1. Actualizar el DTO de AuthResponse

Crea o actualiza el archivo de respuesta del login:

**Archivo:** `Dolores-APP/app/src/main/java/com/example/doloresapp/data/remote/dto/AuthResponse.kt`

```kotlin
package com.example.doloresapp.data.remote.dto

data class AuthResponse(
    val token: String,
    val roles: List<String>
)
```

### 2. Actualizar LoginActivity

Modifica `LoginActivity.kt` para guardar el rol:

```kotlin
// En el success del login (línea ~120):
lifecycleScope.launch {
    try {
        val resp = api.login(AuthRequest(correo = email, password = password))

        // Guardar token
        TokenStore.saveToken(resp.token)

        // NUEVO: Guardar rol
        val userRole = RoleManager.parseRoleFromBackend(resp.roles)
        RoleManager.saveUserRole(this@LoginActivity, userRole)

        // Guardar datos de usuario
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        prefs.edit()
            .putString(ApiConstants.Prefs.USER_EMAIL, email)
            .putBoolean(ApiConstants.Prefs.IS_LOGGED_IN, true)
            .apply()

        Toast.makeText(this@LoginActivity, "Login exitoso", Toast.LENGTH_SHORT).show()

        // Ir a HomeActivity (no MainActivity)
        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
        finish()
    } catch (e: Exception) {
        Toast.makeText(this@LoginActivity, e.message ?: "Error", Toast.LENGTH_LONG).show()
    } finally {
        loginButton.isEnabled = true
    }
}
```

---

## 👥 Usuarios de Prueba del Backend

Según el backend, estos usuarios ya existen:

### 1. Admin

```
Email: admin@test.com
Password: password123
Rol: ROLE_ADMIN
```

### 2. Cliente

```
Email: cliente001@test.com
Password: password123
Rol: ROLE_CLIENTE
```

### 3. Farmacéutico

```
Email: farmaceutico001@test.com
Password: password123
Rol: ROLE_FARMACEUTICO
```

### 4. Repartidor (si existe)

```
Email: repartidor@test.com
Password: password123
Rol: ROLE_REPARTIDOR
```

---

## 🧪 Testing Paso a Paso

### 1. Iniciar Backend

```bash
cd Proyecto-Mobiles-Backend-Farmacia
./gradlew bootRun
```

### 2. Probar Login en Swagger

```
http://localhost:8090/swagger-ui.html
```

Ir a: **🔐 Autenticación → POST /api/auth/login**

Probar con:

```json
{
  "username": "admin@test.com",
  "password": "password123"
}
```

Deberías ver:

```json
{
  "token": "eyJ...",
  "roles": ["ROLE_ADMIN"]
}
```

### 3. Probar en la App

**Como Cliente:**

1. Login con `cliente001@test.com`
2. Deberías ver: HomeActivity con opciones de cliente
3. Opciones: QR, Recetas, Fidelización, Tracking

**Como Repartidor:**

1. Login con `repartidor@test.com`
2. Deberías ver: HomeActivity con panel de repartidor
3. Opciones: Iniciar Entrega, Mis Entregas

---

## 🔍 Verificar Roles en el Backend

### Consultar Roles de un Usuario

Si tienes acceso a la base de datos:

```sql
-- Ver usuarios y sus roles
SELECT u.correo, r.nombre as rol
FROM usuarios u
LEFT JOIN usuario_rol ur ON u.id = ur.usuario_id
LEFT JOIN roles r ON ur.rol_id = r.id;
```

### Crear Usuario Repartidor (si no existe)

```sql
-- 1. Crear rol REPARTIDOR (si no existe)
INSERT INTO roles (nombre, descripcion)
VALUES ('ROLE_REPARTIDOR', 'Repartidor de pedidos');

-- 2. Crear usuario repartidor
INSERT INTO usuarios (usuario, correo, contrasena, estado)
VALUES ('repartidor', 'repartidor@test.com', '$2a$10$...', 'ACTIVO');
-- Nota: La contraseña debe estar encriptada con BCrypt

-- 3. Asignar rol
INSERT INTO usuario_rol (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u, roles r
WHERE u.correo = 'repartidor@test.com'
AND r.nombre = 'ROLE_REPARTIDOR';
```

---

## 📊 Mapeo de Roles

| Backend             | App Android             | Pantalla                         |
| ------------------- | ----------------------- | -------------------------------- |
| `ROLE_CLIENTE`      | `UserRole.CLIENTE`      | `activity_home_cliente.xml`      |
| `ROLE_REPARTIDOR`   | `UserRole.REPARTIDOR`   | `activity_home_repartidor.xml`   |
| `ROLE_ADMIN`        | `UserRole.ADMIN`        | `activity_home_admin.xml`        |
| `ROLE_FARMACEUTICO` | `UserRole.FARMACEUTICO` | `activity_home_farmaceutico.xml` |

---

## 🔐 Seguridad Adicional

### Verificar Rol en Activities Sensibles

**En RepartidorActivity.kt:**

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Verificar que sea repartidor
    if (!RoleManager.isRepartidor(this)) {
        Toast.makeText(this, "Solo repartidores pueden acceder", Toast.LENGTH_SHORT).show()
        finish()
        return
    }

    setContentView(R.layout.activity_repartidor)
    // ... resto del código
}
```

**En DeliveryTrackingActivity.kt:**

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Verificar que sea cliente
    if (!RoleManager.isCliente(this)) {
        Toast.makeText(this, "Solo clientes pueden ver tracking", Toast.LENGTH_SHORT).show()
        finish()
        return
    }

    setContentView(R.layout.activity_delivery_tracking)
    // ... resto del código
}
```

---

## 🚀 Flujo Completo

```
1. Usuario abre app
   ↓
2. LoginActivity
   ↓
3. Ingresa credenciales
   ↓
4. Backend valida y retorna: { token, roles }
   ↓
5. App guarda token y parsea rol
   ↓
6. Redirige a HomeActivity
   ↓
7. HomeActivity lee rol guardado
   ↓
8. Muestra layout según rol:
   - CLIENTE → activity_home_cliente.xml
   - REPARTIDOR → activity_home_repartidor.xml
   - ADMIN → activity_home_admin.xml
   - FARMACEUTICO → activity_home_farmaceutico.xml
```

---

## ✅ Checklist de Implementación

### Backend:

- [x] Endpoint `/api/auth/login` retorna roles ✅
- [x] Roles en base de datos ✅
- [x] JWT incluye información de usuario ✅
- [x] Usuarios de prueba creados ✅

### App Android:

- [x] `UserRole.kt` creado ✅
- [x] `RoleManager.kt` creado ✅
- [x] `HomeActivity.kt` creado ✅
- [x] Layouts por rol creados ✅
- [ ] `AuthResponse.kt` con roles ⚠️ **CREAR**
- [ ] `LoginActivity.kt` actualizado ⚠️ **ACTUALIZAR**
- [ ] Verificación de rol en activities ⚠️ **AGREGAR**
- [ ] Testing con diferentes usuarios ⚠️ **PROBAR**

---

## 🐛 Troubleshooting

### Error: "roles" no se encuentra en la respuesta

**Solución:** Verifica que el DTO `AuthResponse` en la app tenga el campo `roles`:

```kotlin
data class AuthResponse(
    val token: String,
    val roles: List<String>  // ← Debe existir
)
```

### Error: Siempre muestra layout de CLIENTE

**Solución:** Verifica que estés guardando el rol:

```kotlin
val userRole = RoleManager.parseRoleFromBackend(resp.roles)
RoleManager.saveUserRole(this, userRole)
```

### Error: Backend retorna 401

**Solución:** Verifica credenciales. Usuarios de prueba:

- `admin@test.com` / `password123`
- `cliente001@test.com` / `password123`

---

## 📝 Resumen

### ✅ Lo que YA funciona:

- Backend retorna roles correctamente
- Sistema de roles en app implementado
- Layouts por rol creados
- RoleManager funcional

### ⚠️ Lo que DEBES hacer:

1. Crear `AuthResponse.kt` con campo `roles`
2. Actualizar `LoginActivity.kt` para guardar rol
3. Redirigir a `HomeActivity` (no `MainActivity`)
4. Probar con diferentes usuarios

### 🎯 Resultado esperado:

- Login como cliente → Ve opciones de cliente
- Login como repartidor → Ve panel de entregas
- Login como admin → Ve panel admin

---

**¡Tu backend está listo! Solo falta conectar la app correctamente.** 🎉

---

**Última actualización:** 2025-01-25
