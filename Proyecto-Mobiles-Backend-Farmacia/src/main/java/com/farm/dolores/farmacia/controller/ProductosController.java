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
    
    @Autowired
    private com.farm.dolores.farmacia.service.QRCodeService qrCodeService;

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
            
            // Generar QR automáticamente
            String qrUrl = qrCodeService.generarQRProducto(
                productosCreated.getIdProductos(), 
                productosCreated.getCodigoBarras()
            );
            if (qrUrl != null) {
                productosCreated.setQrImageUrl(qrUrl);
                productosCreated = productosService.update(productosCreated);
            }
            
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

    @PutMapping("/{id}/stock")
    @Operation(
        summary = "Actualizar stock de producto",
        description = "Actualiza el stock de un producto específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock actualizado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Productos> updateStock(
            @PathVariable("id") Long id,
            @RequestParam("stock") Integer stock) {
        try {
            Optional<Productos> opt = productosService.read(id);
            if (opt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Productos producto = opt.get();
            producto.setStock(stock);
            Productos updated = productosService.update(producto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/qr")
    @Operation(
        summary = "Obtener QR del producto",
        description = "Retorna la URL de la imagen QR del producto. Si no existe, la genera."
    )
    public ResponseEntity<?> getQR(@PathVariable("id") Long id) {
        try {
            Optional<Productos> opt = productosService.read(id);
            if (opt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            Productos producto = opt.get();
            
            // Si no tiene QR, generarlo
            if (producto.getQrImageUrl() == null || producto.getQrImageUrl().isEmpty()) {
                String qrUrl = qrCodeService.generarQRProducto(
                    producto.getIdProductos(), 
                    producto.getCodigoBarras()
                );
                if (qrUrl != null) {
                    producto.setQrImageUrl(qrUrl);
                    productosService.update(producto);
                }
            }
            
            java.util.Map<String, String> response = new java.util.HashMap<>();
            response.put("qrImageUrl", producto.getQrImageUrl());
            response.put("codigoBarras", producto.getCodigoBarras());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/qr/regenerar")
    @Operation(
        summary = "Regenerar QR del producto",
        description = "Genera una nueva imagen QR para el producto"
    )
    public ResponseEntity<?> regenerarQR(@PathVariable("id") Long id) {
        try {
            Optional<Productos> opt = productosService.read(id);
            if (opt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            Productos producto = opt.get();
            String qrUrl = qrCodeService.generarQRProducto(
                producto.getIdProductos(), 
                producto.getCodigoBarras()
            );
            
            if (qrUrl != null) {
                producto.setQrImageUrl(qrUrl);
                productosService.update(producto);
                
                java.util.Map<String, String> response = new java.util.HashMap<>();
                response.put("qrImageUrl", qrUrl);
                response.put("mensaje", "QR regenerado exitosamente");
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generando QR");
            
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/qr/generar-todos")
    @Operation(
        summary = "Generar QR para todos los productos",
        description = "Genera imágenes QR para todos los productos que no tengan una"
    )
    public ResponseEntity<?> generarTodosQR() {
        try {
            List<Productos> productos = productosService.readAll();
            int generados = 0;
            
            for (Productos producto : productos) {
                if (producto.getQrImageUrl() == null || producto.getQrImageUrl().isEmpty()) {
                    String qrUrl = qrCodeService.generarQRProducto(
                        producto.getIdProductos(), 
                        producto.getCodigoBarras()
                    );
                    if (qrUrl != null) {
                        producto.setQrImageUrl(qrUrl);
                        productosService.update(producto);
                        generados++;
                    }
                }
            }
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("totalProductos", productos.size());
            response.put("qrGenerados", generados);
            response.put("mensaje", "QR generados exitosamente");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/codigos/generar-todos")
    @Operation(
        summary = "Generar QR y códigos de barras para todos los productos",
        description = "Genera imágenes QR y códigos de barras para todos los productos. Las imágenes se guardan en el servidor."
    )
    public ResponseEntity<?> generarTodosCodigos() {
        try {
            List<Productos> productos = productosService.readAll();
            int qrGenerados = 0;
            int barcodesGenerados = 0;
            
            for (Productos producto : productos) {
                boolean actualizado = false;
                
                // Generar QR si no existe
                if (producto.getQrImageUrl() == null || producto.getQrImageUrl().isEmpty()) {
                    String qrUrl = qrCodeService.generarQRProducto(
                        producto.getIdProductos(), 
                        producto.getCodigoBarras()
                    );
                    if (qrUrl != null) {
                        producto.setQrImageUrl(qrUrl);
                        qrGenerados++;
                        actualizado = true;
                    }
                }
                
                // Generar código de barras si no existe
                if (producto.getBarcodeImageUrl() == null || producto.getBarcodeImageUrl().isEmpty()) {
                    String barcodeUrl = qrCodeService.generarCodigoBarrasProducto(
                        producto.getIdProductos(), 
                        producto.getCodigoBarras()
                    );
                    if (barcodeUrl != null) {
                        producto.setBarcodeImageUrl(barcodeUrl);
                        barcodesGenerados++;
                        actualizado = true;
                    }
                }
                
                if (actualizado) {
                    productosService.update(producto);
                }
            }
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("totalProductos", productos.size());
            response.put("qrGenerados", qrGenerados);
            response.put("barcodesGenerados", barcodesGenerados);
            response.put("mensaje", "Códigos generados exitosamente");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}/codigos")
    @Operation(
        summary = "Obtener QR y código de barras del producto",
        description = "Retorna las URLs de las imágenes QR y código de barras. Si no existen, las genera."
    )
    public ResponseEntity<?> getCodigos(@PathVariable("id") Long id) {
        try {
            Optional<Productos> opt = productosService.read(id);
            if (opt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            Productos producto = opt.get();
            boolean actualizado = false;
            
            // Generar QR si no existe
            if (producto.getQrImageUrl() == null || producto.getQrImageUrl().isEmpty()) {
                String qrUrl = qrCodeService.generarQRProducto(
                    producto.getIdProductos(), 
                    producto.getCodigoBarras()
                );
                if (qrUrl != null) {
                    producto.setQrImageUrl(qrUrl);
                    actualizado = true;
                }
            }
            
            // Generar código de barras si no existe
            if (producto.getBarcodeImageUrl() == null || producto.getBarcodeImageUrl().isEmpty()) {
                String barcodeUrl = qrCodeService.generarCodigoBarrasProducto(
                    producto.getIdProductos(), 
                    producto.getCodigoBarras()
                );
                if (barcodeUrl != null) {
                    producto.setBarcodeImageUrl(barcodeUrl);
                    actualizado = true;
                }
            }
            
            if (actualizado) {
                productosService.update(producto);
            }
            
            java.util.Map<String, String> response = new java.util.HashMap<>();
            response.put("qrImageUrl", producto.getQrImageUrl());
            response.put("barcodeImageUrl", producto.getBarcodeImageUrl());
            response.put("codigoBarras", producto.getCodigoBarras());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
