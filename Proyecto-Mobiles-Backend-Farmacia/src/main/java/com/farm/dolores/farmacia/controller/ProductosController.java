package com.farm.dolores.farmacia.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.farm.dolores.farmacia.entity.Productos;
import com.farm.dolores.farmacia.service.ProductosService;
import com.farm.dolores.farmacia.dto.ProductCatalogDto;
import com.farm.dolores.farmacia.dto.ProductDetailDto;
import com.farm.dolores.farmacia.dto.ProductStockDto;
import com.farm.dolores.farmacia.dto.ProductCatalogSearchDto;
import com.farm.dolores.farmacia.dto.ProductMobileDto;
import com.farm.dolores.farmacia.mapper.ProductosMapper;
import com.farm.dolores.farmacia.util.PaginationUtils;
import com.farm.dolores.farmacia.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("api/productos")
@Tag(name = "Productos", description = "API para gestión de productos y catálogo de farmacia")
public class ProductosController {

    @Autowired
    private ProductosService productosService;

    @GetMapping
    public ResponseEntity<List<Productos>> readAll() {
        try {
            List<Productos> productoss = productosService.readAll();

            if (productoss.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(productoss, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/catalogo")
    public ResponseEntity<Page<ProductCatalogDto>> catalog(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "laboratorioId", required = false) Long laboratorioId,
            @RequestParam(value = "requiereReceta", required = false) Boolean requiereReceta,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "nombre,asc") String sort) {
        try {
            Pageable pageable = PaginationUtils.buildPageable(sort, page, size);
            Page<Productos> pageData = productosService.catalog(q, categoriaId, laboratorioId, requiereReceta,
                    pageable);
            Page<ProductCatalogDto> dtoPage = pageData.map(ProductosMapper::toCatalogDto);
            return new ResponseEntity<>(dtoPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/catalogo/search")
    public ResponseEntity<Page<ProductCatalogDto>> catalogSearch(@RequestBody ProductCatalogSearchDto criteria) {
        try {
            String sort = criteria.getSort() != null ? criteria.getSort() : "nombre,asc";
            int page = criteria.getPage() != null ? criteria.getPage() : 0;
            int size = criteria.getSize() != null ? criteria.getSize() : 10;
            Pageable pageable = PaginationUtils.buildPageable(sort, page, size);

            Page<Productos> pageData = productosService.catalog(
                    criteria.getQ(),
                    criteria.getCategoriaId(),
                    criteria.getLaboratorioId(),
                    criteria.getRequiereReceta(),
                    pageable);
            Page<ProductCatalogDto> dtoPage = pageData.map(ProductosMapper::toCatalogDto);
            return new ResponseEntity<>(dtoPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Productos> create(@Valid @RequestBody Productos productos) {
        try {
            Productos productosCreated = productosService.create(productos);
            return new ResponseEntity<>(productosCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/detalle")
    public ResponseEntity<ProductDetailDto> getDetailById(@PathVariable("id") Long id) {
        try {
            Optional<Productos> opt = productosService.read(id);
            if (opt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(ProductosMapper.toDetailDto(opt.get()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/mobile")
    @Operation(
        summary = "Obtener producto para app móvil (QR)",
        description = "Endpoint optimizado para escaneo QR. Retorna información completa del producto para la aplicación móvil"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado",
            content = @Content(schema = @Schema(implementation = ProductMobileDto.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ProductMobileDto> getMobileById(
            @Parameter(description = "ID del producto escaneado del QR", required = true)
            @PathVariable("id") Long id) {
        Productos producto = productosService.read(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        return ResponseEntity.ok(ProductosMapper.toMobileDto(producto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Productos> getById(@PathVariable("id") Long id) {
        try {
            Productos productos = productosService.read(id).get();
            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<ProductStockDto> getStockById(@PathVariable("id") Long id) {
        try {
            Optional<Productos> opt = productosService.read(id);
            if (opt.isEmpty())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(ProductosMapper.toStockDto(opt.get()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stock")
    public ResponseEntity<ProductStockDto> getStockByCodigo(@RequestParam("codigoBarras") String codigoBarras) {
        try {
            Optional<Productos> opt = productosService.findByCodigoBarras(codigoBarras);
            if (opt.isEmpty())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            Productos p = opt.get();
            return new ResponseEntity<>(ProductosMapper.toStockDto(p), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/codigo/{codigoBarras}")
    @Operation(
        summary = "Buscar producto por código de barras",
        description = "Endpoint para escaneo QR/código de barras. Retorna el producto completo"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Productos> getByCodigoBarras(@PathVariable("codigoBarras") String codigoBarras) {
        try {
            Optional<Productos> opt = productosService.findByCodigoBarras(codigoBarras);
            if (opt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(opt.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Productos> delete(@PathVariable("id") Long id) {
        try {
            productosService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody Productos productos) {

        Optional<Productos> productosOptional = productosService.read(id);
        if (productosOptional.isPresent()) {
            return new ResponseEntity<>(productosService.update(productos), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
