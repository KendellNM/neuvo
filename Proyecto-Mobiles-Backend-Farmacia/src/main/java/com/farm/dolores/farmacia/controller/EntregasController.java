package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.Entregas;
import com.farm.dolores.farmacia.service.EntregasService;
import com.farm.dolores.farmacia.dto.DeliveryScheduleDto;
import com.farm.dolores.farmacia.entity.Pedidos;
import com.farm.dolores.farmacia.entity.Repartidores;
import com.farm.dolores.farmacia.repository.PedidosRepository;
import com.farm.dolores.farmacia.repository.RepartidoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/entregas")
@Tag(name = "📦 Entregas", description = "Gestión y programación de entregas")
@SecurityRequirement(name = "bearer-jwt")
public class EntregasController {

    @Autowired
    private EntregasService entregasService;

    @Autowired
    private PedidosRepository pedidosRepository;

    @Autowired
    private RepartidoresRepository repartidoresRepository;

    @GetMapping
    public ResponseEntity<List<Entregas>> readAll() {
        try {
            List<Entregas> entregass = entregasService.readAll();

            if (entregass.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(entregass, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // New: Schedule delivery endpoint
    @PostMapping("/schedule")
    public ResponseEntity<?> schedule(@Valid @RequestBody DeliveryScheduleDto dto) {
        try {
            Optional<Pedidos> pedidoOpt = pedidosRepository.findById(dto.getPedidoId());
            if (pedidoOpt.isEmpty()) {
                return new ResponseEntity<>("Pedido no encontrado", HttpStatus.NOT_FOUND);
            }
            Optional<Repartidores> repOpt = repartidoresRepository.findById(dto.getRepartidorId());
            if (repOpt.isEmpty()) {
                return new ResponseEntity<>("Repartidor no encontrado", HttpStatus.NOT_FOUND);
            }

            Entregas ent = new Entregas();
            ent.setPedidos(pedidoOpt.get());
            ent.setRepartidores(repOpt.get());
            ent.setFecha_asignacion(new java.util.Date());
            ent.setFecha_recojo(dto.getFechaRecojo());
            ent.setFecha_entrega(dto.getFechaEntrega());
            ent.setObservaciones(dto.getObservaciones());
            ent.setEstado("PROGRAMADA");

            Entregas saved = entregasService.create(ent);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Entregas> create(@Valid @RequestBody Entregas entregas) {
        try {
            Entregas entregasCreated = entregasService.create(entregas);
            return new ResponseEntity<>(entregasCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Entregas> getById(@PathVariable("id") Long id) {
        try {
            Entregas entregas = entregasService.read(id).get();
            return new ResponseEntity<>(entregas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Entregas> delete(@PathVariable("id") Long id) {
        try {
            entregasService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody Entregas entregas) {

        Optional<Entregas> entregasOptional = entregasService.read(id);
        if (entregasOptional.isPresent()) {
            return new ResponseEntity<>(entregasService.update(entregas), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
