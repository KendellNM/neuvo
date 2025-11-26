package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.Direcciones;
import com.farm.dolores.farmacia.service.DireccionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/direcciones")
@Tag(name = "📍 Direcciones", description = "Gestión de direcciones de entrega de clientes")
@SecurityRequirement(name = "bearer-jwt")
public class DireccionesController {

    @Autowired
    private DireccionesService direccionesService;

    @GetMapping
    public ResponseEntity<List<Direcciones>> readAll() {
        try {
            List<Direcciones> direccioness = direccionesService.readAll();

            if (direccioness.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(direccioness, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<Direcciones> create(@Valid @RequestBody Direcciones direcciones) {
        try {
            Direcciones direccionesCreated = direccionesService.create(direcciones);
            return new ResponseEntity<>(direccionesCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Direcciones> getById(@PathVariable("id") Long id) {
        try {
            Direcciones direcciones = direccionesService.read(id).get();
            return new ResponseEntity<>(direcciones, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Direcciones> delete(@PathVariable("id") Long id) {
        try {
            direccionesService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody Direcciones direcciones) {

        Optional<Direcciones> direccionesOptional = direccionesService.read(id);
        if (direccionesOptional.isPresent()) {
            return new ResponseEntity<>(direccionesService.update(direcciones), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
