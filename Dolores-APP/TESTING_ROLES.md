# 🧪 Testing del Sistema de Roles

## ✅ Cambios Aplicados

### Archivos Modificados:

1. ✅ `AuthResponseDto.kt` - Creado con campo `roles`
2. ✅ `LoginActivity.kt` - Actualizado para guardar roles
3. ✅ `AuthApi.kt` - Actualizado para usar `AuthResponseDto`
4. ✅ `RepartidorActivity.kt` - Agregada verificación de rol
5. ✅ `DeliveryTrackingActivity.kt` - Agregada verificación de rol

---

## 🧪 Cómo Probar

### 1. Iniciar el Backend

```bash
cd Proyecto-Mobiles-Backend-Farmacia
./gradlew bootRun
```

Espera a ver: `Started FarmaciaApplication in X seconds`

### 2. Compilar la App

```bash
cd Dolores-APP
./gradlew clean build
./gradlew installDebug
```

O desde Android Studio: **Run → Run 'app'**

---

## 👥 Usuarios de Prueba

### Cliente

```
Email: cliente001@test.com
Password: password123
Rol: ROLE_CLIENTE
```

**Debe ver:**

- ✅ HomeActivity con layout de cliente
- ✅ Botones: QR, Recetas, Fidelización, Tracking, Notificaciones
- ✅ Puede abrir DeliveryTrackingActivity
- ❌ NO puede abrir RepartidorActivity

### Admin

```
Email: admin@test.com
Password: password123
Rol: ROLE_ADMIN
```

**Debe ver:**

- ✅ HomeActivity con layout de admin
- ✅ Panel de administración

### Farmacéutico

```
Email: farmaceutico001@test.com
Password: password123
Rol: ROLE_FARMACEUTICO
```

**Debe ver:**

- ✅ HomeActivity con layout de farmacéutico
- ✅ Panel de farmacéutico

### Repartidor (si existe en tu BD)

```
Email: repartidor@test.com
Password: password123
Rol: ROLE_REPARTIDOR
```

**Debe ver:**

- ✅ HomeActivity con layout de repartidor
- ✅ Botón "Iniciar Entrega"
- ✅ Puede abrir RepartidorActivity
- ❌ NO puede abrir DeliveryTrackingActivity

---

## 📝 Pasos de Testing

### Test 1: Login como Cliente

1. Abrir app
2. Ingresar: `cliente001@test.com` / `password123`
3. Presionar "Acceder"

**Resultado Esperado:**

- ✅ Toast: "Login exitoso como CLIENTE"
- ✅ Redirige a HomeActivity
- ✅ Ve layout con opciones de cliente
- ✅ Puede presionar "🚚 Seguir mi Pedido"
- ✅ Se abre DeliveryTrackingActivity

### Test 2: Login como Admin

1. Logout (si estás logueado)
2. Ingresar: `admin@test.com` / `password123`
3. Presionar "Acceder"

**Resultado Esperado:**

- ✅ Toast: "Login exitoso como ADMIN"
- ✅ Redirige a HomeActivity
- ✅ Ve layout de administrador

### Test 3: Verificación de Seguridad

**Intentar acceder a RepartidorActivity siendo Cliente:**

1. Login como cliente
2. Desde código, intentar abrir RepartidorActivity

**Resultado Esperado:**

- ✅ Toast: "⚠️ Solo repartidores pueden acceder a esta función"
- ✅ Activity se cierra inmediatamente
- ✅ Vuelve a HomeActivity

---

## 🔍 Debugging

### Ver Rol Guardado

Agrega esto en HomeActivity.onCreate():

```kotlin
val rol = RoleManager.getUserRole(this)
Log.d("DEBUG_ROL", "Rol actual: $rol")
Toast.makeText(this, "Rol: $rol", Toast.LENGTH_SHORT).show()
```

### Ver Respuesta del Backend

Agrega esto en LoginActivity después del login:

```kotlin
val resp = api.login(...)
Log.d("DEBUG_LOGIN", "Token: ${resp.token}")
Log.d("DEBUG_LOGIN", "Roles: ${resp.roles}")
```

### Verificar SharedPreferences

```kotlin
val prefs = getSharedPreferences(ApiConstants.Prefs.NAME, MODE_PRIVATE)
val rol = prefs.getString("user_role", "NO_GUARDADO")
Log.d("DEBUG_PREFS", "Rol en prefs: $rol")
```

---

## ⚠️ Problemas Comunes

### Problema 1: Siempre muestra layout de CLIENTE

**Causa:** El rol no se está guardando correctamente

**Solución:**

```kotlin
// Verificar que esto esté en LoginActivity:
val userRole = RoleManager.parseRoleFromBackend(resp.roles)
RoleManager.saveUserRole(this, userRole)
```

### Problema 2: Error "roles" not found

**Causa:** El DTO no tiene el campo `roles`

**Solución:** Verificar que `AuthResponseDto` tenga:

```kotlin
data class AuthResponseDto(
    val token: String,
    val roles: List<String>  // ← Debe existir
)
```

### Problema 3: Backend retorna 401

**Causa:** Credenciales incorrectas

**Solución:** Verificar usuarios en Swagger:

```
http://localhost:8090/swagger-ui.html
```

### Problema 4: App crashea al abrir HomeActivity

**Causa:** HomeActivity no encuentra el layout

**Solución:** Verificar que existan:

- `activity_home_cliente.xml`
- `activity_home_repartidor.xml`
- `activity_home_admin.xml`
- `activity_home_farmaceutico.xml`

---

## 📊 Checklist de Verificación

### Antes de Probar:

- [ ] Backend corriendo en puerto 8090
- [ ] App compilada sin errores
- [ ] Usuarios de prueba existen en BD

### Durante Testing:

- [ ] Login exitoso muestra rol en Toast
- [ ] HomeActivity carga layout correcto
- [ ] Botones funcionan según rol
- [ ] Verificación de seguridad funciona

### Después de Probar:

- [ ] Logout limpia rol correctamente
- [ ] Re-login funciona
- [ ] Cambiar de usuario cambia layout

---

## 🎯 Flujo Completo Exitoso

```
1. Usuario abre app
   ↓
2. LoginActivity
   ↓
3. Ingresa: cliente001@test.com / password123
   ↓
4. Backend responde: { token: "...", roles: ["ROLE_CLIENTE"] }
   ↓
5. App guarda token y rol CLIENTE
   ↓
6. Toast: "Login exitoso como CLIENTE"
   ↓
7. Redirige a HomeActivity
   ↓
8. HomeActivity lee rol = CLIENTE
   ↓
9. Carga activity_home_cliente.xml
   ↓
10. Usuario ve opciones de cliente
    ↓
11. Presiona "🚚 Seguir mi Pedido"
    ↓
12. DeliveryTrackingActivity verifica rol = CLIENTE ✅
    ↓
13. Se abre el mapa de tracking
```

---

## 🚀 Siguiente Paso

Si todo funciona:

1. ✅ Implementar logout que limpie rol
2. ✅ Agregar más funcionalidades por rol
3. ✅ Testing en dispositivo físico

---

**¡Listo para probar!** 🎉

---

**Última actualización:** 2025-01-25
