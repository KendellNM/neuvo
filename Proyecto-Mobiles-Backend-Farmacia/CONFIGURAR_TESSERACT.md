# OCR con Tesseract en Docker

## Arquitectura

```
┌─────────────────┐     HTTP      ┌─────────────────┐
│  Tu Backend     │ ──────────►  │  Docker OCR     │
│  (Java normal)  │   POST       │  (Tesseract)    │
│  Puerto 8090    │   /ocr       │  Puerto 5000    │
└─────────────────┘              └─────────────────┘
```

## Uso

### 1. Iniciar el servicio OCR en Docker

```bash
cd Proyecto-Mobiles-Backend-Farmacia
docker-compose up -d
```

### 2. Verificar que está corriendo

```bash
# Ver estado
docker ps

# Probar el servicio
curl http://localhost:5000/health
```

### 3. Ejecutar tu backend Java normal

```bash
./gradlew bootRun
```

Tu backend Java llama automáticamente a `http://localhost:5000/ocr`

## Sin Docker (Modo Simulación)

Si no tienes Docker corriendo, el backend usa modo simulación automáticamente.
Genera texto de receta simulado para probar el flujo.

## Comandos útiles

```bash
# Iniciar OCR
docker-compose up -d

# Ver logs
docker-compose logs -f ocr-service

# Detener
docker-compose down

# Reconstruir
docker-compose up -d --build
```

## Probar OCR manualmente

```bash
curl -X POST http://localhost:5000/ocr \
  -F "image=@mi_receta.jpg"
```

## Configuración

En `application.properties` puedes cambiar la URL del servicio:

```properties
ocr.service.url=http://localhost:5000/ocr
```
