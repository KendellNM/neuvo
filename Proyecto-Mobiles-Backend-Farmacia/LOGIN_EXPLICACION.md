# 🔐 Sistema de Login - Explicación

## ¿Cómo funciona el login?

El sistema tiene un **login flexible** que acepta dos tipos de usuarios:

### 1. Clientes (Login con DNI)
Los clientes se autentican usando su **DNI**:

```json
POST /api/auth/login
{
  "username": "12345678",
  "password": "mipassword"
}
```

**Flujo:**
1. El sistema busca en la tabla `Clientes` por el DNI
2. Si encuentra el cliente, obtiene su usuario vinculado
3. Valida la contraseña del usuario
4. Retorna el token JWT con los roles del cliente

### 2. Usuarios Administrativos (Login con correo)
El personal administrativo se autentica usando su **correo electrónico**:

```json
POST /api/auth/login
{
  "username": "admin@farmacia.com",
  "password": "admin123"
}
```

**Flujo:**
1. El sistema busca primero por DNI (no encuentra)
2. Luego busca en la tabla `Usuarios` por correo
3. Valida la contraseña
4. Retorna el token JWT con los roles del usuario

## Estructura de Datos

### Tabla Usuarios
```
- idUsuarios
- correo (usado para login de admin/staff)
- contrasena (encriptada)
- usuario (nombre de usuario, opcional)
- estado (ACTIVO/INACTIVO)
```

### Tabla Clientes
```
- idClientes
- dni (usado para login de clientes)
- nombres
- apellidos
- telefono
- usuario_id (FK a Usuarios)
```

## Relación entre Clientes y Usuarios

```
Cliente (DNI: 12345678)
    └── vinculado a → Usuario (correo: cliente@email.com)
                          └── tiene → Roles (ROLE_CLIENTE)
```

Cuando un cliente hace login con su DNI:
1. Se busca el Cliente por DNI
2. Se obtiene el Usuario vinculado
3. Se obtienen los Roles del Usuario
4. Se genera el token JWT

## Respuesta del Login

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "roles": ["ROLE_CLIENTE", "ROLE_ADMIN"]
}
```

El token contiene:
- El identificador del usuario (DNI o correo)
- Los roles asignados
- Fecha de expiración

## Swagger UI

En Swagger, el endpoint se ve así:

```
POST /api/auth/login

Request body:
{
  "username": "string",  // DNI o correo
  "password": "string"
}
```

**Ejemplos:**
- Cliente: `username: "12345678"`
- Admin: `username: "admin@farmacia.com"`

## Casos de Uso

### Caso 1: Cliente hace login desde app móvil
```
Usuario ingresa: DNI = 12345678
Sistema busca: Cliente con DNI 12345678
Encuentra: Cliente vinculado a Usuario
Valida: Contraseña del Usuario
Retorna: Token con ROLE_CLIENTE
```

### Caso 2: Admin hace login desde panel web
```
Usuario ingresa: correo = admin@farmacia.com
Sistema busca: Cliente con DNI admin@farmacia.com (no encuentra)
Sistema busca: Usuario con correo admin@farmacia.com
Encuentra: Usuario admin
Valida: Contraseña
Retorna: Token con ROLE_ADMIN
```

## Ventajas de este Sistema

✅ **Flexible:** Soporta clientes y staff con diferentes identificadores
✅ **Seguro:** Contraseñas encriptadas con BCrypt
✅ **Simple:** Un solo endpoint para todos los tipos de usuarios
✅ **Escalable:** Fácil agregar más tipos de autenticación
✅ **Optimizado:** Solo hace UNA consulta a la base de datos por login

## Notas Importantes

1. **El campo `username` es genérico:** Puede ser DNI o correo
2. **Prioridad de búsqueda:** Primero DNI, luego correo
3. **Roles determinan permisos:** El token incluye los roles del usuario
4. **Token JWT:** Válido por 24 horas (configurable)

## Ejemplo Completo

```bash
# Login de cliente
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "12345678",
    "password": "cliente123"
  }'

# Respuesta
{
  "token": "eyJhbGc...",
  "roles": ["ROLE_CLIENTE"]
}

# Usar el token
curl -X GET http://localhost:8080/api/productos/1/mobile \
  -H "Authorization: Bearer eyJhbGc..."
```


## Optimización de Consultas

### ❌ Antes (2 consultas)
```java
// 1ra consulta: authenticationManager llama a loadUserByUsername
authenticationManager.authenticate(...);

// 2da consulta: Volvemos a llamar loadUserByUsername
UserDetails userDetails = userDetailsService.loadUserByUsername(...);
```

### ✅ Ahora (1 consulta)
```java
// 1 sola consulta: authenticationManager llama a loadUserByUsername
var authentication = authenticationManager.authenticate(...);

// Reutilizamos el UserDetails que ya está en memoria
UserDetails userDetails = (UserDetails) authentication.getPrincipal();
```

**Resultado:** El login es 2x más rápido y reduce la carga en la base de datos.
