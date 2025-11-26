package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.SeguimientoEntrega;
import com.farm.dolores.farmacia.service.SeguimientoEntregaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/seguimientoentrega")
@Tag(name = "🚚 Seguimiento", description = "Tracking y seguimiento de entregas")
@SecurityRequirement(name = "bearer-jwt")
public class SeguimientoEntregaController {

    @Autowired
    private SeguimientoEntregaService seguimientoentregaService;

    @GetMapping
    public ResponseEntity<List<SeguimientoEntrega>> readAll() {
        try {
            List<SeguimientoEntrega> seguimientoentregas = seguimientoentregaService.readAll();

            if (seguimientoentregas.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(seguimientoentregas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<SeguimientoEntrega> create(@Valid @RequestBody SeguimientoEntrega seguimientoentrega) {
        try {
            SeguimientoEntrega seguimientoentregaCreated = seguimientoentregaService.create(seguimientoentrega);
            return new ResponseEntity<>(seguimientoentregaCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeguimientoEntrega> getById(@PathVariable("id") Long id) {
        try {
            SeguimientoEntrega seguimientoentrega = seguimientoentregaService.read(id).get();
            return new ResponseEntity<>(seguimientoentrega, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SeguimientoEntrega> delete(@PathVariable("id") Long id) {
        try {
            seguimientoentregaService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody SeguimientoEntrega seguimientoentrega) {

        Optional<SeguimientoEntrega> seguimientoentregaOptional = seguimientoentregaService.read(id);
        if (seguimientoentregaOptional.isPresent()) {
            return new ResponseEntity<>(seguimientoentregaService.update(seguimientoentrega), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
