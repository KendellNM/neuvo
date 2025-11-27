# Microservicio OCR con Tesseract
FROM python:3.11-slim

# Instalar Tesseract
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-spa \
    tesseract-ocr-eng \
    && rm -rf /var/lib/apt/lists/*

# Instalar dependencias Python
RUN pip install flask pytesseract pillow

# Crear directorio de trabajo
WORKDIR /app

# Copiar el servidor
COPY server.py .

# Exponer puerto
EXPOSE 5000

# Ejecutar
CMD ["python", "server.py"]