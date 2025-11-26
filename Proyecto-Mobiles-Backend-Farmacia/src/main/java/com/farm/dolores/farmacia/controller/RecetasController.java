package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.dto.CreateRecetaRequest;
import com.farm.dolores.farmacia.dto.RecetaResponse;
import com.farm.dolores.farmacia.dto.UsuarioSummary;
import com.farm.dolores.farmacia.dto.ClienteSummary;
import com.farm.dolores.farmacia.dto.MedicoSummary;
import com.farm.dolores.farmacia.entity.Clientes;
import com.farm.dolores.farmacia.entity.Medicos;
import com.farm.dolores.farmacia.entity.Recetas;
import com.farm.dolores.farmacia.entity.UsuarioRol;
import com.farm.dolores.farmacia.entity.Usuarios;
import com.farm.dolores.farmacia.service.ClientesService;
import com.farm.dolores.farmacia.service.MedicosService;
import com.farm.dolores.farmacia.service.RecetasService;
import com.farm.dolores.farmacia.service.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:5173/")
@RequestMapping("api/recetas")
@Tag(name = "📄 Recetas", description = "Gestión de recetas médicas tradicionales")
@SecurityRequirement(name = "bearer-jwt")
public class RecetasController {

    private final RecetasService recetasService;
    private final UsuariosService usuariosService;
    private final ClientesService clientesService;
    private final MedicosService medicosService;

    @Autowired
    public RecetasController(RecetasService recetasService,
                             UsuariosService usuariosService,
                             ClientesService clientesService,
                             MedicosService medicosService) {
        this.recetasService = recetasService;
        this.usuariosService = usuariosService;
        this.clientesService = clientesService;
        this.medicosService = medicosService;
    }

    private RecetaResponse mapToResponse(Recetas r) {
        RecetaResponse dto = new RecetaResponse();
        dto.setIdRecetas(r.getIdRecetas());
        dto.setFecha_emision(r.getFecha_emision());
        dto.setFecha_vencimiento(r.getFecha_vencimiento());
        dto.setDiagnostico(r.getDiagnostico());
        dto.setObservaciones(r.getObservaciones());
        dto.setEstado(r.getEstado());
        dto.setNumero_receta(r.getNumero_receta());

        if (r.getUsuarios() != null) {
            UsuarioSummary us = new UsuarioSummary(r.getUsuarios().getIdUsuarios(), r.getUsuarios().getUsuario());
            dto.setUsuarios(us);
        }
        if (r.getClientes() != null) {
            ClienteSummary cs = new ClienteSummary(
                    r.getClientes().getIdClientes(),
                    r.getClientes().getNombres(),
                    r.getClientes().getApellidos(),
                    r.getClientes().getTelefono()
            );
            dto.setClientes(cs);
        }
        if (r.getMedicos() != null) {
            MedicoSummary ms = new MedicoSummary(
                    r.getMedicos().getIdMedicos(),
                    r.getMedicos().getNombres(),
                    r.getMedicos().getApellidos(),
                    r.getMedicos().getEspecialidad(),
                    r.getMedicos().getTelefono()
            );
            dto.setMedicos(ms);
        }

        return dto;
    }

    @GetMapping
    public ResponseEntity<List<RecetaResponse>> readAll() {
        try {
            List<Recetas> recetass = recetasService.readAll();

            if (recetass.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            List<RecetaResponse> result = recetass.stream().map(this::mapToResponse).toList();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateRecetaRequest request) {
        try {
            // Usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Usuarios usuario = usuariosService.findByUsuario(username)
                    .orElseGet(() -> usuariosService.findByCorreo(username)
                            .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado")));

            // Verificar rol MEDICO
            boolean esMedico = usuario.getUsuariorols().stream()
                    .map(UsuarioRol::getRoles)
                    .anyMatch(r -> r.getNombre() != null && r.getNombre().equalsIgnoreCase("MEDICO"));

            if (!esMedico) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Debe tener el rol MEDICO para crear recetas");
            }

            // Validaciones de negocio
            if (request.getFechaVencimiento().before(request.getFechaEmision())) {
                return ResponseEntity.badRequest().body("La fecha de vencimiento debe ser igual o posterior a la fecha de emisión");
            }

            // Validar cliente y médico
            Clientes cliente = clientesService.read(request.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            Medicos medico = medicosService.read(request.getMedicoId())
                    .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

            // Crear entidad Recetas
            Recetas receta = new Recetas();
            receta.setFecha_emision(request.getFechaEmision());
            receta.setFecha_vencimiento(request.getFechaVencimiento());
            receta.setDiagnostico(request.getDiagnostico());
            receta.setObservaciones(request.getObservaciones());
            receta.setEstado("PENDIENTE");
            receta.setEsValidado(false);
            receta.setNumero_receta(request.getNumeroReceta());
            receta.setUsuarios(usuario);
            receta.setClientes(cliente);
            receta.setMedicos(medico);

            Recetas creada = recetasService.create(receta);
            RecetaResponse response = mapToResponse(creada);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la receta: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecetaResponse> getById(@PathVariable("id") Long id) {
        try {
            Optional<Recetas> recetasOpt = recetasService.read(id);
            if (recetasOpt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            RecetaResponse dto = mapToResponse(recetasOpt.get());
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Recetas> delete(@PathVariable("id") Long id) {
        try {
            recetasService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody Recetas recetas) {

        Optional<Recetas> recetasOptional = recetasService.read(id);
        if (recetasOptional.isPresent()) {
            return new ResponseEntity<>(recetasService.update(recetas), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
