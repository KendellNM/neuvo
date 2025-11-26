package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.dto.RecetaDigitalDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface RecetaDigitalService {
    RecetaDigitalDto procesarReceta(MultipartFile imagen, Long clienteId);
    Optional<RecetaDigitalDto> obtenerReceta(Long id);
    List<RecetaDigitalDto> obtenerRecetasPorCliente(Long clienteId);
    RecetaDigitalDto validarReceta(Long id, List<Long> productosIds);
    String extraerTextoDeImagen(MultipartFile imagen);
}
