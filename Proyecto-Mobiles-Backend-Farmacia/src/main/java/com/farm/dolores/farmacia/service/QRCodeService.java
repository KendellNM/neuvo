package com.farm.dolores.farmacia.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class QRCodeService {

    @Value("${app.qr.upload-dir:src/main/resources/static/uploads/qr}")
    private String uploadDir;
    
    @Value("${app.barcode.upload-dir:src/main/resources/static/uploads/barcode}")
    private String barcodeUploadDir;

    @Value("${app.qr.size:300}")
    private int qrSize;

    /**
     * Genera un código QR y lo guarda como imagen PNG
     * @param contenido El contenido a codificar en el QR
     * @param nombreArchivo Nombre del archivo sin extensión
     * @return La ruta relativa del archivo guardado
     */
    public String generarYGuardarQR(String contenido, String nombreArchivo) {
        try {
            // Crear directorio si no existe
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // Generar QR
            BufferedImage qrImage = generarQRImage(contenido);
            
            // Guardar archivo
            String fileName = nombreArchivo + ".png";
            Path filePath = dirPath.resolve(fileName);
            ImageIO.write(qrImage, "PNG", filePath.toFile());
            
            log.info("QR generado y guardado: {}", filePath);
            return "/uploads/qr/" + fileName;
            
        } catch (Exception e) {
            log.error("Error generando QR: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Genera un código QR y lo retorna como Base64
     */
    public String generarQRBase64(String contenido) {
        try {
            BufferedImage qrImage = generarQRImage(contenido);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            log.error("Error generando QR Base64: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Genera la imagen del código QR
     */
    private BufferedImage generarQRImage(String contenido) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(contenido, BarcodeFormat.QR_CODE, qrSize, qrSize, hints);
        
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * Genera QR para un producto usando su ID (para escaneo móvil)
     * El QR contiene solo el ID del producto
     */
    public String generarQRProducto(Long productoId, String codigoBarras) {
        // El QR contiene el ID del producto para que el móvil consulte por ID
        String contenido = String.valueOf(productoId);
        String nombreArchivo = "producto_qr_" + productoId;
        return generarYGuardarQR(contenido, nombreArchivo);
    }
    
    /**
     * Genera código de barras (Code128) para un producto
     */
    public String generarCodigoBarrasProducto(Long productoId, String codigoBarras) {
        try {
            // Crear directorio si no existe
            Path dirPath = Paths.get(barcodeUploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            // Contenido del código de barras
            String contenido = codigoBarras != null && !codigoBarras.isEmpty() 
                ? codigoBarras 
                : "PROD" + String.format("%08d", productoId);
            
            // Generar código de barras
            BufferedImage barcodeImage = generarBarcodeImage(contenido);
            
            // Guardar archivo
            String fileName = "producto_barcode_" + productoId + ".png";
            Path filePath = dirPath.resolve(fileName);
            ImageIO.write(barcodeImage, "PNG", filePath.toFile());
            
            log.info("Código de barras generado: {}", filePath);
            return "/uploads/barcode/" + fileName;
            
        } catch (Exception e) {
            log.error("Error generando código de barras: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Genera la imagen del código de barras Code128
     */
    private BufferedImage generarBarcodeImage(String contenido) throws WriterException {
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(contenido, BarcodeFormat.CODE_128, 300, 100);
        
        BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        
        // Agregar texto debajo del código de barras
        BufferedImage finalImage = new BufferedImage(300, 130, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = finalImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 300, 130);
        g.drawImage(barcodeImage, 0, 0, null);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(contenido);
        g.drawString(contenido, (300 - textWidth) / 2, 125);
        g.dispose();
        
        return finalImage;
    }
    
    /**
     * Genera QR y código de barras para un producto
     * @return Map con las URLs de ambas imágenes
     */
    public Map<String, String> generarCodigosProducto(Long productoId, String codigoBarras) {
        Map<String, String> result = new HashMap<>();
        
        String qrUrl = generarQRProducto(productoId, codigoBarras);
        String barcodeUrl = generarCodigoBarrasProducto(productoId, codigoBarras);
        
        result.put("qrImageUrl", qrUrl);
        result.put("barcodeImageUrl", barcodeUrl);
        
        return result;
    }
}
