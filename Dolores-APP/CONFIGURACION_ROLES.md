# 🔐 Configuración de Roles - Guía Completa

## ✅ Sistema de Roles Implementado

### 📋 Roles Disponibles:

1. **👤 CLIENTE** - Usuario normal (compra productos, ve tracking)
2. **🚚 REPARTIDOR** - Envía ubicación en tiempo real
3. **👨‍💼 ADMIN** - Administrador del sistema
4. **💊 FARMACEUTICO** - Valida recetas

---

## 🏗️ Archivos Creados

### 1. `UserRole.kt` - Enum y Manager

```kotlin
enum class UserRole {
    CLIENTE, REPARTIDOR, ADMIN, FARMACEUTICO
}

object RoleManager {
    fun saveUserRole(context, role)
    fun getUserRole(context): UserRole
    fun isCliente(context): Boolean
    fun isRepartidor(context): Boolean
    fun parseRoleFromBackend(roles): UserRole
}
```

### 2. `HomeActivity.kt` - Pantalla principal por rol

- Carga layout diferente según rol
- Muestra opciones específicas para cada rol

### 3. Layouts por Rol:

- `activity_home_cliente.xml` - Opciones de cliente
- `activity_home_repartidor.xml` - Panel de entregas
- `activity_home_admin.xml` - Panel admin
- `activity_home_farmaceutico.xml` - Panel farmacéutico

---

## 🔧 Integración con Backend

### Respuesta del Backend en Login:

El backend retorna algo como:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "roles": ["ROLE_CLIENTE"]
}
```

O para repartidor:

```json
{
  "token": "...",
  "roles": ["ROLE_REPARTIDOR"]
}
```

### Actualizar LoginActivity:

Necesitas modificar `LoginActivity.kt` para guardar el rol:

```kotlin
// En el success del login:
lifecycleScope.launch {
    try {
        val resp = api.login(AuthRequest(correo = email, password = password))

        // Guardar token
        TokenStore.saveToken(resp.token)

        // NUEVO: Guardar rol
        val userRole = RoleManager.parseRoleFromBackend(resp.roles)
        RoleManager.saveUserRole(this@LoginActivity, userRole)

        // Guardar ID de usuario
        val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
        prefs.edit()
            .putLong(ApiConstants.Prefs.USER_ID, resp.userId)
            .putString(ApiConstants.Prefs.USER_EMAIL, email)
            .putBoolean(ApiConstants.Prefs.IS_LOGGED_IN, true)
            .apply()

        Toast.makeText(this@LoginActivity, "Login exitoso", Toast.LENGTH_SHORT).show()

        // Ir a HomeActivity (no MainActivity)
        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
        finish()
    } catch (e: Exception) {
        Toast.makeText(this@LoginActivity, e.message ?: "Error", Toast.LENGTH_LONG).show()
    }
}
```

---

## 📱 Flujo de la Aplicación

### 1. Login

```
Usuario ingresa credenciales
    ↓
Backend valida y retorna token + roles
    ↓
App guarda token y rol
    ↓
Redirige a HomeActivity
```

### 2. HomeActivity

```
HomeActivity lee el rol guardado
    ↓
Si es CLIENTE → Muestra opciones de cliente
Si es REPARTIDOR → Muestra panel de entregas
Si es ADMIN → Muestra panel admin
Si es FARMACEUTICO → Muestra panel farmacéutico
```

### 3. Navegación por Rol

#### Cliente ve:

- 📷 Escanear QR
- 📋 Recetas Digitales
- 🎁 Fidelización
- 🚚 Seguir mi Pedido (DeliveryTrackingActivity)
- 🔔 Notificaciones

#### Repartidor ve:

- 🚚 Iniciar Entrega (RepartidorActivity)
- 📦 Mis Entregas Pendientes
- 📊 Historial

---

## 🔐 Seguridad por Rol

### Verificar Rol Antes de Acciones:

```kotlin
// En cualquier Activity
if (!RoleManager.isRepartidor(this)) {
    Toast.makeText(this, "Solo repartidores pueden acceder", Toast.LENGTH_SHORT).show()
    finish()
    return
}
```

### Ejemplo en RepartidorActivity:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Verificar que sea repartidor
    if (!RoleManager.isRepartidor(this)) {
        Toast.makeText(this, "Acceso denegado", Toast.LENGTH_SHORT).show()
        finish()
        return
    }

    setContentView(R.layout.activity_repartidor)
    // ... resto del código
}
```

---

## 🧪 Testing con Diferentes Roles

### Crear Usuarios de Prueba en Backend:

#### Cliente:

```sql
INSERT INTO clientes (dni, nombre, apellido, correo, password)
VALUES ('12345678', 'Juan', 'Pérez', 'cliente@test.com', 'password123');
```

#### Repartidor:

```sql
INSERT INTO repartidores (nombre, apellido, correo, password, telefono)
VALUES ('Carlos', 'Delivery', 'repartidor@test.com', 'password123', '999888777');
```

### Login en la App:

**Como Cliente:**

```
Email: cliente@test.com
Password: password123
→ Ve opciones de cliente
```

**Como Repartidor:**

```
Email: repartidor@test.com
Password: password123
→ Ve panel de entregas
```

---

## 📝 Modelo de Datos del Backend

### AuthResponse (lo que retorna el backend):

```java
public class AuthResponse {
    private String token;
    private List<String> roles;
    private Long userId;
    private String email;

    // getters y setters
}
```

### En el Controller:

```java
@PostMapping("/api/auth/login")
public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    // Validar credenciales
    Usuario usuario = authService.authenticate(request);

    // Generar token
    String token = jwtService.generateToken(usuario);

    // Obtener roles
    List<String> roles = usuario.getRoles().stream()
        .map(rol -> rol.getNombre())
        .collect(Collectors.toList());

    AuthResponse response = new AuthResponse();
    response.setToken(token);
    response.setRoles(roles);
    response.setUserId(usuario.getId());
    response.setEmail(usuario.getEmail());

    return ResponseEntity.ok(response);
}
```

---

## 🎯 Casos de Uso por Rol

### 👤 CLIENTE:

1. Escanea QR de productos
2. Sube recetas digitales
3. Ve sus puntos de fidelización
4. Canjea puntos
5. **Ve tracking de su pedido** (DeliveryTrackingActivity)
6. Recibe notificaciones

### 🚚 REPARTIDOR:

1. Ve lista de entregas asignadas
2. **Inicia modo delivery** (RepartidorActivity)
3. **Envía ubicación en tiempo real**
4. Marca entregas como completadas
5. Ve historial de entregas

### 👨‍💼 ADMIN:

1. Gestiona usuarios
2. Ve estadísticas
3. Gestiona productos
4. Gestiona pedidos

### 💊 FARMACEUTICO:

1. Valida recetas digitales
2. Gestiona inventario
3. Atiende consultas

---

## 🔄 Cambiar de Rol (Para Testing)

### Opción 1: Logout y Login con Otro Usuario

```kotlin
// En cualquier Activity
fun logout() {
    val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
    prefs.edit().clear().apply()

    TokenStore.clearToken()
    RoleManager.clearUserRole(this)

    startActivity(Intent(this, LoginActivity::class.java))
    finish()
}
```

### Opción 2: Selector de Rol (Solo para Development)

```kotlin
// En HomeActivity para testing
fun cambiarRol(nuevoRol: UserRole) {
    RoleManager.saveUserRole(this, nuevoRol)
    recreate() // Recarga la activity
}
```

---

## 📊 Diagrama de Flujo

```
┌─────────────┐
│ LoginActivity│
└──────┬──────┘
       │ Login exitoso
       │ Guarda: token + rol + userId
       ↓
┌─────────────┐
│ HomeActivity │
└──────┬──────┘
       │
       ├─→ Si rol = CLIENTE ──→ Layout Cliente
       │                        ├─ QR Scanner
       │                        ├─ Recetas
       │                        ├─ Fidelización
       │                        └─ Tracking (ver)
       │
       ├─→ Si rol = REPARTIDOR ─→ Layout Repartidor
       │                          ├─ Iniciar Delivery
       │                          ├─ Mis Entregas
       │                          └─ Historial
       │
       ├─→ Si rol = ADMIN ──────→ Layout Admin
       │
       └─→ Si rol = FARMACEUTICO → Layout Farmacéutico
```

---

## ✅ Checklist de Implementación

### Backend:

- [ ] Endpoint `/api/auth/login` retorna roles
- [ ] Tabla de roles en base de datos
- [ ] Usuarios tienen roles asignados
- [ ] JWT incluye roles en claims

### App Android:

- [x] `UserRole.kt` creado
- [x] `RoleManager.kt` creado
- [x] `HomeActivity.kt` creado
- [x] Layouts por rol creados
- [ ] `LoginActivity.kt` actualizado para guardar rol
- [ ] Verificación de rol en activities sensibles
- [ ] Logout limpia rol

---

## 🚀 Próximos Pasos

1. **Actualizar LoginActivity** para guardar rol del backend
2. **Actualizar MainActivity** para redirigir a HomeActivity
3. **Agregar verificación de rol** en RepartidorActivity
4. **Implementar logout** que limpie rol
5. **Testing** con diferentes usuarios

---

## 💡 Tips

### Debugging:

```kotlin
// Ver rol actual
val rol = RoleManager.getUserRole(this)
Log.d("DEBUG", "Rol actual: $rol")
```

### Forzar Rol (Solo Development):

```kotlin
// Para testing sin backend
RoleManager.saveUserRole(this, UserRole.REPARTIDOR)
```

### Verificar en Runtime:

```kotlin
when (RoleManager.getUserRole(this)) {
    UserRole.CLIENTE -> Log.d("DEBUG", "Es cliente")
    UserRole.REPARTIDOR -> Log.d("DEBUG", "Es repartidor")
    else -> Log.d("DEBUG", "Otro rol")
}
```

---

## 📚 Documentación Relacionada

- `TRACKING_TIEMPO_REAL.md` - Cómo funciona el tracking
- `README.md` - Guía general
- `IMPLEMENTACION_COMPLETA.md` - Todo lo implementado

---

**Última actualización:** 2025-01-25  
**Versión:** 1.0.0
