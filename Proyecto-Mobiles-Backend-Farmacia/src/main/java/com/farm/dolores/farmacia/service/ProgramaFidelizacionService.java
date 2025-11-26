package com.farm.dolores.farmacia.service;

import com.farm.dolores.farmacia.dto.ProgramaFidelizacionDto;
import com.farm.dolores.farmacia.entity.MovimientoPuntos;

import java.util.List;
import java.util.Optional;

public interface ProgramaFidelizacionService {
    ProgramaFidelizacionDto crearPrograma(Long clienteId);
    Optional<ProgramaFidelizacionDto> obtenerPorCliente(Long clienteId);
    ProgramaFidelizacionDto agregarPuntos(Long clienteId, Integer puntos, String descripcion, Long pedidoId);
    ProgramaFidelizacionDto canjearPuntos(Long clienteId, Integer puntos, String descripcion);
    List<MovimientoPuntos> obtenerHistorialPuntos(Long clienteId);
    String calcularNivelMembresia(Integer puntosAcumulados);
}
