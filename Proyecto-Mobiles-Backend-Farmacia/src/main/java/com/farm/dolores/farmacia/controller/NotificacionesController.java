package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.Notificaciones;
import com.farm.dolores.farmacia.service.NotificacionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/notificaciones")
@Tag(name = "📬 Notificaciones Sistema", description = "Notificaciones internas del sistema")
@SecurityRequirement(name = "bearer-jwt")
public class NotificacionesController {

    @Autowired
    private NotificacionesService notificacionesService;

    @GetMapping
    public ResponseEntity<List<Notificaciones>> readAll() {
        try {
            List<Notificaciones> notificacioness = notificacionesService.readAll();

            if (notificacioness.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(notificacioness, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<Notificaciones> create(@Valid @RequestBody Notificaciones notificaciones) {
        try {
            Notificaciones notificacionesCreated = notificacionesService.create(notificaciones);
            return new ResponseEntity<>(notificacionesCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificaciones> getById(@PathVariable("id") Long id) {
        try {
            Notificaciones notificaciones = notificacionesService.read(id).get();
            return new ResponseEntity<>(notificaciones, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Notificaciones> delete(@PathVariable("id") Long id) {
        try {
            notificacionesService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody Notificaciones notificaciones) {

        Optional<Notificaciones> notificacionesOptional = notificacionesService.read(id);
        if (notificacionesOptional.isPresent()) {
            return new ResponseEntity<>(notificacionesService.update(notificaciones), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
