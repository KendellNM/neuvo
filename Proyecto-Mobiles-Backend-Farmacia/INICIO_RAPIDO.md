# 🚀 Inicio Rápido

## 1. Ejecutar el proyecto
```bash
./gradlew bootRun
```

## 2. Credenciales automáticas
- **Admin:** `admin@test.com` / `password123`
- **Cliente:** `user001@test.com` / `password123` (DNI: `12345678`)

## 3. URLs importantes
- **Backend:** `http://localhost:8090`
- **Swagger:** `http://localhost:8090/swagger-ui.html`
- **WebSocket:** `ws://localhost:8090/ws-delivery`

## 4. WebSocket de prueba
- **Pedido:** #123 (estado: EN_CAMINO)
- **Suscripción:** `/topic/delivery/123`

## ✅ Todo automático con Seeder Java
- ✅ Tablas creadas por JPA/Hibernate
- ✅ Datos insertados por DataSeeder
- ✅ Carpetas de imágenes creadas
- ✅ Simulador de delivery activo
- ✅ Sistema de fidelización listo

## 📱 Endpoints de imágenes
- `POST /api/upload/perfil` - Subir foto de perfil
- `POST /api/upload/producto` - Subir foto de producto
- `POST /api/upload/receta` - Subir receta digital
- `GET /uploads/perfiles/imagen.jpg` - Ver imagen

¡Solo ejecuta y funciona! 🎉