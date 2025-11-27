package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.dto.RecetaDigitalDto;
import com.farm.dolores.farmacia.dto.RecetaConUbicacionRequest;
import com.farm.dolores.farmacia.dto.ProcesarRecetaFarmaceuticoRequest;
import com.farm.dolores.farmacia.dto.PedidoDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface RecetaDigitalService {
    RecetaDigitalDto procesarReceta(MultipartFile imagen, Long clienteId);
    RecetaDigitalDto procesarRecetaFarmaceutico(MultipartFile imagen, String observaciones);
    Optional<RecetaDigitalDto> obtenerReceta(Long id);
    List<RecetaDigitalDto> obtenerRecetasPorCliente(Long clienteId);
    RecetaDigitalDto validarReceta(Long id, List<Long> productosIds);
    String extraerTextoDeImagen(MultipartFile imagen);
    
    // Flujo Cliente -> Farmacéutico -> Delivery
    RecetaDigitalDto enviarRecetaConUbicacion(MultipartFile imagen, RecetaConUbicacionRequest request);
    List<RecetaDigitalDto> obtenerRecetasPendientes();
    PedidoDto procesarRecetaYCrearPedido(ProcesarRecetaFarmaceuticoRequest request);
    RecetaDigitalDto rechazarReceta(Long recetaId, String motivo);
}
