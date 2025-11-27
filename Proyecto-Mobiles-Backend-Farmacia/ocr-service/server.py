"""
Microservicio OCR con Tesseract
Tu backend Java llama a este servicio via HTTP
"""
from flask import Flask, request, jsonify
import pytesseract
from PIL import Image
import io
import os

app = Flask(__name__)

@app.route('/health', methods=['GET'])
def health():
    return jsonify({"status": "ok", "service": "ocr-tesseract"})

@app.route('/ocr', methods=['POST'])
def ocr():
    """
    Recibe una imagen y retorna el texto extraído
    """
    try:
        if 'image' not in request.files:
            return jsonify({"error": "No se envió imagen"}), 400
        
        file = request.files['image']
        if file.filename == '':
            return jsonify({"error": "Archivo vacío"}), 400
        
        # Leer imagen
        image = Image.open(io.BytesIO(file.read()))
        
        # Extraer texto con Tesseract (español + inglés)
        texto = pytesseract.image_to_string(image, lang='spa+eng')
        
        return jsonify({
            "success": True,
            "texto": texto,
            "filename": file.filename
        })
        
    except Exception as e:
        return jsonify({
            "success": False,
            "error": str(e)
        }), 500

if __name__ == '__main__':
    print("🔍 Servicio OCR iniciado en puerto 5000")
    print("📋 Idiomas disponibles:", pytesseract.get_languages())
    app.run(host='0.0.0.0', port=5000)