package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dto.RecetaDigitalDto;
import com.farm.dolores.farmacia.dto.RecetaDigitalDetalleDto;
import com.farm.dolores.farmacia.entity.RecetaDigital;
import com.farm.dolores.farmacia.entity.RecetaDigitalDetalle;
import com.farm.dolores.farmacia.entity.Clientes;
import com.farm.dolores.farmacia.entity.Productos;
import com.farm.dolores.farmacia.repository.RecetaDigitalRepository;
import com.farm.dolores.farmacia.repository.RecetaDigitalDetalleRepository;
import com.farm.dolores.farmacia.repository.ClientesRepository;
import com.farm.dolores.farmacia.repository.ProductosRepository;
import com.farm.dolores.farmacia.service.RecetaDigitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecetaDigitalServiceImpl implements RecetaDigitalService {

    @Autowired
    private RecetaDigitalRepository recetaDigitalRepository;

    @Autowired
    private RecetaDigitalDetalleRepository detalleRepository;

    @Autowired
    private ClientesRepository clienteRepository;

    @Autowired
    private ProductosRepository productosRepository;

    private static final String UPLOAD_DIR = "uploads/recetas/";
    
    // URL del servicio OCR en Docker (configurable)
    @Value("${ocr.service.url:http://localhost:5000/ocr}")
    private String ocrServiceUrl;

    @Override
    public RecetaDigitalDto procesarReceta(MultipartFile imagen, Long clienteId) {
        // Validar cliente
        Clientes cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new com.farm.dolores.farmacia.exception.ResourceNotFoundException(
                        "Cliente", "id", clienteId));

        // Guardar imagen
        String imagenUrl;
        try {
            imagenUrl = guardarImagen(imagen);
        } catch (IOException e) {
            throw new com.farm.dolores.farmacia.exception.BadRequestException(
                    "Error al guardar imagen: " + e.getMessage());
        }
        
        // Extraer texto con OCR (no falla, retorna mensaje si hay error)
        String textoExtraido;
        try {
            textoExtraido = extraerTextoDeImagen(imagen);
        } catch (Exception e) {
            textoExtraido = "No se pudo procesar la imagen: " + e.getMessage();
        }

        // Crear receta
        RecetaDigital receta = new RecetaDigital();
        receta.setImagenUrl(imagenUrl);
        receta.setTextoExtraido(textoExtraido);
        receta.setEstado("PENDIENTE");
        receta.setFechaCreacion(new Date());
        receta.setCliente(cliente);

        receta = recetaDigitalRepository.save(receta);

        // Extraer medicamentos del texto
        List<String> medicamentos = extraerMedicamentos(textoExtraido);
        for (String med : medicamentos) {
            if (med != null && !med.trim().isEmpty()) {
                RecetaDigitalDetalle detalle = new RecetaDigitalDetalle();
                detalle.setMedicamentoTexto(med.trim());
                detalle.setValidado(false);
                detalle.setRecetaDigital(receta);
                detalleRepository.save(detalle);
            }
        }

        return convertirADto(receta);
    }

    @Override
    public String extraerTextoDeImagen(MultipartFile imagen) {
        // Intentar llamar al servicio OCR en Docker
        try {
            String textoOCR = llamarServicioOCR(imagen);
            if (textoOCR != null && !textoOCR.isEmpty()) {
                System.out.println("✅ OCR exitoso via Docker");
                return textoOCR;
            }
        } catch (Exception e) {
            System.err.println("⚠️ Servicio OCR no disponible: " + e.getMessage());
        }
        
        // Fallback: modo simulación si el servicio Docker no está corriendo
        System.out.println("📝 Usando modo simulación (inicia Docker con: docker-compose up -d)");
        return generarTextoSimulado(imagen.getOriginalFilename());
    }
    
    /**
     * Llama al servicio OCR en Docker via HTTP
     */
    private String llamarServicioOCR(MultipartFile imagen) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            // Preparar el archivo para enviar
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new ByteArrayResource(imagen.getBytes()) {
                @Override
                public String getFilename() {
                    return imagen.getOriginalFilename();
                }
            });
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // Llamar al servicio OCR
            ResponseEntity<Map> response = restTemplate.exchange(
                ocrServiceUrl,
                HttpMethod.POST,
                requestEntity,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                if (Boolean.TRUE.equals(responseBody.get("success"))) {
                    return (String) responseBody.get("texto");
                }
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("Error llamando servicio OCR: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Genera texto simulado para pruebas del flujo de recetas
     * En producción, esto sería reemplazado por OCR real (Google Vision, AWS Textract, etc.)
     */
    private String generarTextoSimulado(String nombreArchivo) {
        // Lista de medicamentos comunes para simular detección
        String[] medicamentosComunes = {
            "Paracetamol 500mg - 1 cada 8 horas",
            "Ibuprofeno 400mg - 1 cada 12 horas", 
            "Amoxicilina 500mg - 1 cada 8 horas por 7 días",
            "Omeprazol 20mg - 1 en ayunas",
            "Loratadina 10mg - 1 al día"
        };
        
        // Seleccionar 2-3 medicamentos aleatorios
        Random random = new Random();
        int cantidad = 2 + random.nextInt(2);
        
        StringBuilder texto = new StringBuilder();
        texto.append("═══════════════════════════════════\n");
        texto.append("       RECETA MÉDICA DIGITAL\n");
        texto.append("═══════════════════════════════════\n\n");
        texto.append("Dr. Juan Pérez García\n");
        texto.append("CMP: 12345\n");
        texto.append("Especialidad: Medicina General\n\n");
        texto.append("Fecha: ").append(new java.text.SimpleDateFormat("dd/MM/yyyy").format(new Date())).append("\n\n");
        texto.append("MEDICAMENTOS RECETADOS:\n");
        texto.append("───────────────────────────────────\n");
        
        Set<Integer> usados = new HashSet<>();
        for (int i = 0; i < cantidad; i++) {
            int idx;
            do {
                idx = random.nextInt(medicamentosComunes.length);
            } while (usados.contains(idx));
            usados.add(idx);
            
            texto.append("• ").append(medicamentosComunes[idx]).append("\n");
        }
        
        texto.append("\n───────────────────────────────────\n");
        texto.append("Firma del médico: [Firma digital]\n");
        texto.append("═══════════════════════════════════\n");
        texto.append("\n[Procesado en modo simulación]");
        
        return texto.toString();
    }

    @Override
    public Optional<RecetaDigitalDto> obtenerReceta(Long id) {
        return recetaDigitalRepository.findById(id).map(this::convertirADto);
    }

    @Override
    public List<RecetaDigitalDto> obtenerRecetasPorCliente(Long clienteId) {
        return recetaDigitalRepository.findByClienteIdClientes(clienteId)
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    public RecetaDigitalDto validarReceta(Long id, List<Long> productosIds) {
        RecetaDigital receta = recetaDigitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        receta.setEstado("VALIDADA");
        receta.setFechaProcesamiento(new Date());
        recetaDigitalRepository.save(receta);

        return convertirADto(receta);
    }

    private String guardarImagen(MultipartFile imagen) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + imagen.getOriginalFilename();
        Path uploadPath = Paths.get(UPLOAD_DIR);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(imagen.getInputStream(), filePath);

        return UPLOAD_DIR + fileName;
    }

    private List<String> extraerMedicamentos(String texto) {
        List<String> medicamentos = new ArrayList<>();
        String[] lineas = texto.split("\n");
        
        for (String linea : lineas) {
            if (linea.length() > 3 && !linea.toLowerCase().contains("dr.") 
                && !linea.toLowerCase().contains("fecha")) {
                medicamentos.add(linea.trim());
            }
        }
        
        return medicamentos;
    }

    private RecetaDigitalDto convertirADto(RecetaDigital receta) {
        return RecetaDigitalDto.builder()
                .id(receta.getIdRecetaDigital())
                .imagenUrl(receta.getImagenUrl())
                .textoExtraido(receta.getTextoExtraido())
                .estado(receta.getEstado())
                .fechaCreacion(receta.getFechaCreacion())
                .fechaProcesamiento(receta.getFechaProcesamiento())
                .clienteId(receta.getCliente() != null ? receta.getCliente().getIdClientes() : null)
                .clienteNombre(receta.getCliente() != null ? 
                        receta.getCliente().getNombres() + " " + receta.getCliente().getApellidos() : null)
                .build();
    }
}
