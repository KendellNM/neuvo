# 🔧 Instrucciones para Verificar Creación de Usuarios

## Problema

Los usuarios no se están creando automáticamente al iniciar el backend.

## Solución Paso a Paso

### 1️⃣ Detener el Backend

Si está corriendo, detén el servidor Spring Boot.

### 2️⃣ Limpiar la Base de Datos

Ejecuta estos comandos SQL en MySQL:

```sql
USE dbfuncionas;

-- Ver usuarios actuales
SELECT * FROM usuarios;
SELECT * FROM roles;
SELECT * FROM usuario_rol;

-- Si quieres empezar desde cero (CUIDADO: borra todo)
DROP TABLE IF EXISTS usuario_rol;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS categoria;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS detalle_pedido;
```

### 3️⃣ Iniciar el Backend

Inicia el backend con Gradle:

```bash
cd Proyecto-Mobiles-Backend-Farmacia
./gradlew bootRun
```

O desde tu IDE (IntelliJ/Eclipse).

### 4️⃣ Verificar los Logs

Deberías ver en la consola algo como:

```
═══════════════════════════════════════════════════════════
Inicializando roles...
✓ 4 roles creados
═══════════════════════════════════════════════════════════
Inicializando usuario admin...
✓ Usuario creado: admin@dolores.com (ID: 1)
✓ Rol ADMIN asignado a admin@dolores.com
✓ Usuario admin creado exitosamente
  - Usuario: admin
  - Contraseña: admin123
  - Correo: admin@dolores.com
═══════════════════════════════════════════════════════════
Inicializando usuarios de prueba...
═══════════════════════════════════════════════════════════
Creando usuario CLIENTE...
✓ Usuario creado: cliente@dolores.com (ID: 2)
✓ Rol CLIENTE asignado a cliente@dolores.com
Creando usuario REPARTIDOR...
✓ Usuario creado: delivery@dolores.com (ID: 3)
✓ Rol REPARTIDOR asignado a delivery@dolores.com
Creando usuario FARMACEUTICO...
✓ Usuario creado: farmaceutico@dolores.com (ID: 4)
✓ Rol FARMACEUTICO asignado a farmaceutico@dolores.com
═══════════════════════════════════════════════════════════
  ✅ USUARIOS DE PRUEBA DISPONIBLES:
  📧 admin@dolores.com / admin123 (ADMIN)
  📧 cliente@dolores.com / cliente123 (CLIENTE)
  📧 delivery@dolores.com / delivery123 (REPARTIDOR)
  📧 farmaceutico@dolores.com / farmaceutico123 (FARMACEUTICO)
═══════════════════════════════════════════════════════════
```

### 5️⃣ Verificar en la Base de Datos

Ejecuta este SQL para verificar:

```sql
USE dbfuncionas;

-- Ver todos los usuarios
SELECT u.id, u.usuario, u.correo, u.estado, r.nombre as rol
FROM usuarios u
LEFT JOIN usuario_rol ur ON u.id = ur.usuario_id
LEFT JOIN roles r ON ur.rol_id = r.id;
```

Deberías ver 4 usuarios:

- admin@dolores.com (ADMIN)
- cliente@dolores.com (CLIENTE)
- delivery@dolores.com (REPARTIDOR)
- farmaceutico@dolores.com (FARMACEUTICO)

## 🔍 Si No Se Crean los Usuarios

### Verificar que DataInitializer se está ejecutando

Busca en los logs al inicio:

```
Inicializando roles...
```

Si NO ves este mensaje, el DataInitializer no se está ejecutando.

### Posibles Causas:

1. **Error de conexión a la base de datos**

   - Verifica que MySQL esté corriendo
   - Verifica usuario/contraseña en `application.properties`
   - Verifica que la base de datos `dbfuncionas` exista

2. **Error en las entidades**

   - Revisa si hay errores de compilación
   - Verifica que las anotaciones JPA estén correctas

3. **Transacción fallando**
   - Revisa el log completo para ver errores de SQL
   - Puede haber problemas con constraints o foreign keys

### Verificar Configuración de MySQL

```sql
-- Verificar que la base de datos existe
SHOW DATABASES LIKE 'dbfuncionas';

-- Si no existe, créala
CREATE DATABASE IF NOT EXISTS dbfuncionas;
```

## 📝 Usuarios de Prueba

Una vez creados, puedes usar estos usuarios:

| Rol             | Email                    | Contraseña      |
| --------------- | ------------------------ | --------------- |
| 👨‍💼 Admin        | admin@dolores.com        | admin123        |
| 👤 Cliente      | cliente@dolores.com      | cliente123      |
| 🚚 Repartidor   | delivery@dolores.com     | delivery123     |
| 💊 Farmacéutico | farmaceutico@dolores.com | farmaceutico123 |

## 🆘 Última Opción: Crear Usuarios Manualmente

Si todo falla, ejecuta este SQL:

```sql
USE dbfuncionas;

-- Insertar roles
INSERT INTO roles (nombre, descripcion, estado) VALUES
('ADMIN', 'Administrador del sistema', 'activo'),
('CLIENTE', 'Cliente de la farmacia', 'activo'),
('REPARTIDOR', 'Repartidor de pedidos', 'activo'),
('FARMACEUTICO', 'Farmacéutico', 'activo');

-- Insertar usuarios (contraseñas encriptadas con BCrypt)
-- admin123 = $2a$10$xqKhZ8Z8Z8Z8Z8Z8Z8Z8ZuXxXxXxXxXxXxXxXxXxXxXxXxXxXx
-- cliente123 = $2a$10$yYyYyYyYyYyYyYyYyYyYyuXxXxXxXxXxXxXxXxXxXxXxXxXxXx
-- etc...

-- Nota: Necesitas generar las contraseñas con BCrypt
-- Usa el backend para esto o una herramienta online
```

Para generar contraseñas BCrypt, puedes usar el backend temporalmente o una herramienta online.
