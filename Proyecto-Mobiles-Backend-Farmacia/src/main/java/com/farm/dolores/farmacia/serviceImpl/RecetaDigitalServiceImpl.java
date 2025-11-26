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
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
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

    @Override
    public RecetaDigitalDto procesarReceta(MultipartFile imagen, Long clienteId) {
        Clientes cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new com.farm.dolores.farmacia.exception.ResourceNotFoundException(
                        "Cliente", "id", clienteId));

        String imagenUrl;
        try {
            imagenUrl = guardarImagen(imagen);
        } catch (IOException e) {
            throw new com.farm.dolores.farmacia.exception.BadRequestException(
                    "Error al guardar imagen: " + e.getMessage());
        }
        
        String textoExtraido = extraerTextoDeImagen(imagen);

        RecetaDigital receta = new RecetaDigital();
        receta.setImagenUrl(imagenUrl);
        receta.setTextoExtraido(textoExtraido);
        receta.setEstado("PENDIENTE");
        receta.setFechaCreacion(new Date());
        receta.setCliente(cliente);

        receta = recetaDigitalRepository.save(receta);

        List<String> medicamentos = extraerMedicamentos(textoExtraido);
        for (String med : medicamentos) {
            RecetaDigitalDetalle detalle = new RecetaDigitalDetalle();
            detalle.setMedicamentoTexto(med);
            detalle.setValidado(false);
            detalle.setRecetaDigital(receta);
            detalleRepository.save(detalle);
        }

        return convertirADto(receta);
    }

    @Override
    public String extraerTextoDeImagen(MultipartFile imagen) {
        try {
            File tempFile = File.createTempFile("receta", ".jpg");
            imagen.transferTo(tempFile);

            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("tessdata");
            tesseract.setLanguage("spa");

            String texto = tesseract.doOCR(tempFile);
            tempFile.delete();

            return texto;
        } catch (Exception e) {
            throw new com.farm.dolores.farmacia.exception.BadRequestException(
                    "Error al procesar imagen: " + e.getMessage());
        }
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
