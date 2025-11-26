package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.Medicos;
import com.farm.dolores.farmacia.service.MedicosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/medicos")
@Tag(name = "👨‍⚕️ Médicos", description = "Gestión de médicos y especialistas")
@SecurityRequirement(name = "bearer-jwt")
public class MedicosController {

    @Autowired
    private MedicosService medicosService;

    @GetMapping
    public ResponseEntity<List<Medicos>> readAll() {
        try {
            List<Medicos> medicoss = medicosService.readAll();

            if (medicoss.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(medicoss, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<Medicos> create(@Valid @RequestBody Medicos medicos) {
        try {
            Medicos medicosCreated = medicosService.create(medicos);
            return new ResponseEntity<>(medicosCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medicos> getById(@PathVariable("id") Long id) {
        try {
            Medicos medicos = medicosService.read(id).get();
            return new ResponseEntity<>(medicos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Medicos> delete(@PathVariable("id") Long id) {
        try {
            medicosService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody Medicos medicos) {

        Optional<Medicos> medicosOptional = medicosService.read(id);
        if (medicosOptional.isPresent()) {
            return new ResponseEntity<>(medicosService.update(medicos), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
