package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.Laboratorios;
import com.farm.dolores.farmacia.service.LaboratoriosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/laboratorios")
@Tag(name = "🏭 Laboratorios", description = "Gestión de laboratorios farmacéuticos")
@SecurityRequirement(name = "bearer-jwt")
public class LaboratoriosController {

    @Autowired
    private LaboratoriosService laboratoriosService;

    @GetMapping
    public ResponseEntity<List<Laboratorios>> readAll() {
        try {
            List<Laboratorios> laboratorioss = laboratoriosService.readAll();

            if (laboratorioss.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(laboratorioss, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<Laboratorios> create(@Valid @RequestBody Laboratorios laboratorios) {
        try {
            Laboratorios laboratoriosCreated = laboratoriosService.create(laboratorios);
            return new ResponseEntity<>(laboratoriosCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Laboratorios> getById(@PathVariable("id") Long id) {
        try {
            Laboratorios laboratorios = laboratoriosService.read(id).get();
            return new ResponseEntity<>(laboratorios, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Laboratorios> delete(@PathVariable("id") Long id) {
        try {
            laboratoriosService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody Laboratorios laboratorios) {

        Optional<Laboratorios> laboratoriosOptional = laboratoriosService.read(id);
        if (laboratoriosOptional.isPresent()) {
            return new ResponseEntity<>(laboratoriosService.update(laboratorios), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
