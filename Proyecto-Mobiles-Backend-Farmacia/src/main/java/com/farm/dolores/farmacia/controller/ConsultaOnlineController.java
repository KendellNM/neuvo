package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.ConsultaOnline;
import com.farm.dolores.farmacia.service.ConsultaOnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/consultaonline")
@Tag(name = "💬 Consultas Online", description = "Consultas médicas en línea")
@SecurityRequirement(name = "bearer-jwt")
public class ConsultaOnlineController {

    @Autowired
    private ConsultaOnlineService consultaonlineService;

    @GetMapping
    public ResponseEntity<List<ConsultaOnline>> readAll() {
        try {
            List<ConsultaOnline> consultaonlines = consultaonlineService.readAll();

            if (consultaonlines.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(consultaonlines, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<ConsultaOnline> create(@Valid @RequestBody ConsultaOnline consultaonline) {
        try {
            ConsultaOnline consultaonlineCreated = consultaonlineService.create(consultaonline);
            return new ResponseEntity<>(consultaonlineCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaOnline> getById(@PathVariable("id") Long id) {
        try {
            ConsultaOnline consultaonline = consultaonlineService.read(id).get();
            return new ResponseEntity<>(consultaonline, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ConsultaOnline> delete(@PathVariable("id") Long id) {
        try {
            consultaonlineService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody ConsultaOnline consultaonline) {

        Optional<ConsultaOnline> consultaonlineOptional = consultaonlineService.read(id);
        if (consultaonlineOptional.isPresent()) {
            return new ResponseEntity<>(consultaonlineService.update(consultaonline), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
