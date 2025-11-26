package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.Farmaceuticos;
import com.farm.dolores.farmacia.service.FarmaceuticosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/farmaceuticos")
@Tag(name = "💊 Farmacéuticos", description = "Gestión de farmacéuticos y turnos")
@SecurityRequirement(name = "bearer-jwt")
public class FarmaceuticosController {

    @Autowired
    private FarmaceuticosService farmaceuticosService;

    @GetMapping
    public ResponseEntity<List<Farmaceuticos>> readAll() {
        try {
            List<Farmaceuticos> farmaceuticoss = farmaceuticosService.readAll();

            if (farmaceuticoss.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(farmaceuticoss, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<Farmaceuticos> create(@Valid @RequestBody Farmaceuticos farmaceuticos) {
        try {
            Farmaceuticos farmaceuticosCreated = farmaceuticosService.create(farmaceuticos);
            return new ResponseEntity<>(farmaceuticosCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Farmaceuticos> getById(@PathVariable("id") Long id) {
        try {
            Farmaceuticos farmaceuticos = farmaceuticosService.read(id).get();
            return new ResponseEntity<>(farmaceuticos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Farmaceuticos> delete(@PathVariable("id") Long id) {
        try {
            farmaceuticosService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody Farmaceuticos farmaceuticos) {

        Optional<Farmaceuticos> farmaceuticosOptional = farmaceuticosService.read(id);
        if (farmaceuticosOptional.isPresent()) {
            return new ResponseEntity<>(farmaceuticosService.update(farmaceuticos), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
