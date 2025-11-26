# 🚀 Configuración de Nuevas Funcionalidades

## ✅ Funcionalidades Implementadas

### 1. Sistema de Recetas Digitales con OCR
### 2. Programa de Fidelización con Puntos
### 3. Sistema de Notificaciones Push

---

## 📋 PASOS DE CONFIGURACIÓN

### 1️⃣ BASE DE DATOS

Ejecuta el script SQL para crear las nuevas tablas:

```bash
mysql -u tu_usuario -p tu_base_de_datos < database/nuevas_tablas.sql
```

O ejecuta manualmente el contenido del archivo `database/nuevas_tablas.sql`

---

### 2️⃣ TESSERACT OCR (Para Recetas Digitales)

**Windows:**
1. Descarga Tesseract: https://github.com/UB-Mannheim/tesseract/wiki
2. Instala en `C:\Program Files\Tesseract-OCR`
3. Descarga datos en español:
   - Ve a: https://github.com/tesseract-ocr/tessdata
   - Descarga `spa.traineddata`
   - Copia a: `C:\Program Files\Tesseract-OCR\tessdata\`

4. Crea carpeta `tessdata` en la raíz del proyecto
