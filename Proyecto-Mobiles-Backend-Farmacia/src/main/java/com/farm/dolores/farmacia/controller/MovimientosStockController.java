package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.MovimientosStock;
import com.farm.dolores.farmacia.service.MovimientosStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/movimientosstock")
@Tag(name = "📊 Inventario", description = "Control de stock y movimientos de inventario")
@SecurityRequirement(name = "bearer-jwt")
public class MovimientosStockController {

    @Autowired
    private MovimientosStockService movimientosstockService;

    @GetMapping
    public ResponseEntity<List<MovimientosStock>> readAll() {
        try {
            List<MovimientosStock> movimientosstocks = movimientosstockService.readAll();

            if (movimientosstocks.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(movimientosstocks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<MovimientosStock> create(@Valid @RequestBody MovimientosStock movimientosstock) {
        try {
            MovimientosStock movimientosstockCreated = movimientosstockService.create(movimientosstock);
            return new ResponseEntity<>(movimientosstockCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientosStock> getById(@PathVariable("id") Long id) {
        try {
            MovimientosStock movimientosstock = movimientosstockService.read(id).get();
            return new ResponseEntity<>(movimientosstock, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MovimientosStock> delete(@PathVariable("id") Long id) {
        try {
            movimientosstockService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody MovimientosStock movimientosstock) {

        Optional<MovimientosStock> movimientosstockOptional = movimientosstockService.read(id);
        if (movimientosstockOptional.isPresent()) {
            return new ResponseEntity<>(movimientosstockService.update(movimientosstock), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
