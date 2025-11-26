package com.farm.dolores.farmacia.serviceImpl;

import com.farm.dolores.farmacia.dto.ProgramaFidelizacionDto;
import com.farm.dolores.farmacia.entity.Clientes;
import com.farm.dolores.farmacia.entity.MovimientoPuntos;
import com.farm.dolores.farmacia.entity.Pedidos;
import com.farm.dolores.farmacia.entity.ProgramaFidelizacion;
import com.farm.dolores.farmacia.repository.ClientesRepository;
import com.farm.dolores.farmacia.repository.MovimientoPuntosRepository;
import com.farm.dolores.farmacia.repository.PedidosRepository;
import com.farm.dolores.farmacia.repository.ProgramaFidelizacionRepository;
import com.farm.dolores.farmacia.service.ProgramaFidelizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProgramaFidelizacionServiceImpl implements ProgramaFidelizacionService {

    @Autowired
    private ProgramaFidelizacionRepository programaRepository;

    @Autowired
    private MovimientoPuntosRepository movimientoRepository;

    @Autowired
    private ClientesRepository clienteRepository;

    @Autowired
    private PedidosRepository pedidoRepository;

    @Override
    @Transactional
    public ProgramaFidelizacionDto crearPrograma(Long clienteId) {
        Clientes cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        ProgramaFidelizacion programa = new ProgramaFidelizacion();
        programa.setCliente(cliente);
        programa.setPuntosActuales(0);
        programa.setPuntosAcumulados(0);
        programa.setNivelMembresia("BRONCE");
        programa.setFechaRegistro(new Date());
        programa.setFechaUltimaActualizacion(new Date());

        programa = programaRepository.save(programa);
        return convertirADto(programa);
    }

    @Override
    public Optional<ProgramaFidelizacionDto> obtenerPorCliente(Long clienteId) {
        return programaRepository.findByClienteIdClientes(clienteId)
                .map(this::convertirADto);
    }

    @Override
    @Transactional
    public ProgramaFidelizacionDto agregarPuntos(Long clienteId, Integer puntos, String descripcion, Long pedidoId) {
        ProgramaFidelizacion programa = programaRepository.findByClienteIdClientes(clienteId)
                .orElseGet(() -> {
                    ProgramaFidelizacionDto dto = crearPrograma(clienteId);
                    return programaRepository.findById(dto.getId()).get();
                });

        programa.setPuntosActuales(programa.getPuntosActuales() + puntos);
        programa.setPuntosAcumulados(programa.getPuntosAcumulados() + puntos);
        programa.setNivelMembresia(calcularNivelMembresia(programa.getPuntosAcumulados()));
        programa.setFechaUltimaActualizacion(new Date());

        MovimientoPuntos movimiento = new MovimientoPuntos();
        movimiento.setProgramaFidelizacion(programa);
        movimiento.setPuntos(puntos);
        movimiento.setTipo("GANADO");
        movimiento.setDescripcion(descripcion);
        movimiento.setFecha(new Date());
        
        if (pedidoId != null) {
            pedidoRepository.findById(pedidoId).ifPresent(movimiento::setPedido);
        }

        movimientoRepository.save(movimiento);
        programa = programaRepository.save(programa);

        return convertirADto(programa);
    }

    @Override
    @Transactional
    public ProgramaFidelizacionDto canjearPuntos(Long clienteId, Integer puntos, String descripcion) {
        ProgramaFidelizacion programa = programaRepository.findByClienteIdClientes(clienteId)
                .orElseThrow(() -> new com.farm.dolores.farmacia.exception.ResourceNotFoundException(
                        "Programa de fidelización", "clienteId", clienteId));

        if (programa.getPuntosActuales() < puntos) {
            throw new com.farm.dolores.farmacia.exception.InsufficientPointsException(
                    puntos, programa.getPuntosActuales());
        }

        programa.setPuntosActuales(programa.getPuntosActuales() - puntos);
        programa.setFechaUltimaActualizacion(new Date());

        MovimientoPuntos movimiento = new MovimientoPuntos();
        movimiento.setProgramaFidelizacion(programa);
        movimiento.setPuntos(-puntos);
        movimiento.setTipo("CANJEADO");
        movimiento.setDescripcion(descripcion);
        movimiento.setFecha(new Date());

        movimientoRepository.save(movimiento);
        programa = programaRepository.save(programa);

        return convertirADto(programa);
    }

    @Override
    public List<MovimientoPuntos> obtenerHistorialPuntos(Long clienteId) {
        ProgramaFidelizacion programa = programaRepository.findByClienteIdClientes(clienteId)
                .orElseThrow(() -> new RuntimeException("Programa no encontrado"));
        
        return movimientoRepository
                .findByProgramaFidelizacionIdProgramaFidelizacionOrderByFechaDesc(
                    programa.getIdProgramaFidelizacion());
    }

    @Override
    public String calcularNivelMembresia(Integer puntosAcumulados) {
        if (puntosAcumulados >= 10000) return "PLATINO";
        if (puntosAcumulados >= 5000) return "ORO";
        if (puntosAcumulados >= 2000) return "PLATA";
        return "BRONCE";
    }

    private ProgramaFidelizacionDto convertirADto(ProgramaFidelizacion programa) {
        String siguienteNivel = obtenerSiguienteNivel(programa.getNivelMembresia());
        Integer puntosParaSiguiente = calcularPuntosParaSiguienteNivel(programa.getPuntosAcumulados());

        return ProgramaFidelizacionDto.builder()
                .id(programa.getIdProgramaFidelizacion())
                .clienteId(programa.getCliente().getIdClientes())
                .clienteNombre(programa.getCliente().getNombres() + " " + programa.getCliente().getApellidos())
                .puntosActuales(programa.getPuntosActuales())
                .puntosAcumulados(programa.getPuntosAcumulados())
                .nivelMembresia(programa.getNivelMembresia())
                .fechaRegistro(programa.getFechaRegistro())
                .siguienteNivel(siguienteNivel)
                .puntosParaSiguienteNivel(puntosParaSiguiente)
                .build();
    }

    private String obtenerSiguienteNivel(String nivelActual) {
        switch (nivelActual) {
            case "BRONCE": return "PLATA";
            case "PLATA": return "ORO";
            case "ORO": return "PLATINO";
            default: return "MAXIMO";
        }
    }

    private Integer calcularPuntosParaSiguienteNivel(Integer puntosActuales) {
        if (puntosActuales < 2000) return 2000 - puntosActuales;
        if (puntosActuales < 5000) return 5000 - puntosActuales;
        if (puntosActuales < 10000) return 10000 - puntosActuales;
        return 0;
    }
}
