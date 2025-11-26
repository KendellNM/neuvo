package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.RecetaDetalle;
import com.farm.dolores.farmacia.service.RecetaDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/recetadetalle")
@Tag(name = "📋 Detalle Recetas", description = "Detalles de medicamentos en recetas")
@SecurityRequirement(name = "bearer-jwt")
public class RecetaDetalleController {

    @Autowired
    private RecetaDetalleService recetadetalleService;

    @GetMapping
    public ResponseEntity<List<RecetaDetalle>> readAll() {
        try {
            List<RecetaDetalle> recetadetalles = recetadetalleService.readAll();

            if (recetadetalles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(recetadetalles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<RecetaDetalle> create(@Valid @RequestBody RecetaDetalle recetadetalle) {
        try {
            RecetaDetalle recetadetalleCreated = recetadetalleService.create(recetadetalle);
            return new ResponseEntity<>(recetadetalleCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecetaDetalle> getById(@PathVariable("id") Long id) {
        try {
            RecetaDetalle recetadetalle = recetadetalleService.read(id).get();
            return new ResponseEntity<>(recetadetalle, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RecetaDetalle> delete(@PathVariable("id") Long id) {
        try {
            recetadetalleService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody RecetaDetalle recetadetalle) {

        Optional<RecetaDetalle> recetadetalleOptional = recetadetalleService.read(id);
        if (recetadetalleOptional.isPresent()) {
            return new ResponseEntity<>(recetadetalleService.update(recetadetalle), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
