package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.PedidoDetalle;
import com.farm.dolores.farmacia.service.PedidoDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/pedidodetalle")
@Tag(name = "🛒 Detalle Pedidos", description = "Detalles de productos en pedidos")
@SecurityRequirement(name = "bearer-jwt")
public class PedidoDetalleController {

    @Autowired
    private PedidoDetalleService pedidodetalleService;

    @GetMapping
    public ResponseEntity<List<PedidoDetalle>> readAll() {
        try {
            List<PedidoDetalle> pedidodetalles = pedidodetalleService.readAll();

            if (pedidodetalles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(pedidodetalles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<PedidoDetalle> create(@Valid @RequestBody PedidoDetalle pedidodetalle) {
        try {
            PedidoDetalle pedidodetalleCreated = pedidodetalleService.create(pedidodetalle);
            return new ResponseEntity<>(pedidodetalleCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDetalle> getById(@PathVariable("id") Long id) {
        try {
            PedidoDetalle pedidodetalle = pedidodetalleService.read(id).get();
            return new ResponseEntity<>(pedidodetalle, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PedidoDetalle> delete(@PathVariable("id") Long id) {
        try {
            pedidodetalleService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody PedidoDetalle pedidodetalle) {

        Optional<PedidoDetalle> pedidodetalleOptional = pedidodetalleService.read(id);
        if (pedidodetalleOptional.isPresent()) {
            return new ResponseEntity<>(pedidodetalleService.update(pedidodetalle), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
