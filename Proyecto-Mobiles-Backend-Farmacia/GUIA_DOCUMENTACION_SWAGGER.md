# 📚 Guía de Documentación con Swagger/OpenAPI

## 🎯 Configuración Completada

Se ha mejorado la configuración de Swagger con:

✅ **Información detallada de la API** con emojis y descripciones completas
✅ **Múltiples servidores** (Local, Staging, Producción)
✅ **Tags organizados** por funcionalidad con emojis
✅ **Seguridad JWT** configurada y documentada
✅ **Propiedades optimizadas** para mejor experiencia de usuario
✅ **Ejemplo completo** en AuthController

## 🌐 Acceso a Swagger UI

Una vez iniciada la aplicación, accede a:

- **Swagger UI**: http://localhost:8090/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8090/api-docs
- **OpenAPI YAML**: http://localhost:8090/api-docs.yaml

## 📝 Cómo Documentar un Controlador

### 1. Anotaciones a Nivel de Clase

```java
@RestController
@RequestMapping("api/productos")
@Tag(name = "💊 Productos", description = "API para gestión de productos y catálogo de farmacia")
public class ProductosController {
    // ...
}
```

### 2. Anotaciones a Nivel de Método

#### Ejemplo Completo:

```java
@Operation(
    summary = "Buscar productos",
    description = """
            Busca productos en el catálogo con filtros avanzados.

            **Filtros disponibles:**
            - Búsqueda por texto (nombre, descripción)
            - Categoría
            - Laboratorio
            - Requiere receta
            - Rango de precios

            **Ordenamiento:**
            - Por nombre (asc/desc)
            - Por precio (asc/desc)
            - Por stock (asc/desc)
            """,
    tags = {"💊 Productos"}
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "Productos encontrados",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Page.class),
            examples = @ExampleObject(
                name = "Lista de productos",
                value = """
                    {
                      "content": [
                        {
                          "id": 1,
                          "nombre": "Paracetamol 500mg",
                          "precio": 5.50,
                          "stock": 100
                        }
                      ],
                      "totalElements": 50,
                      "totalPages": 5,
                      "size": 10,
                      "number": 0
                    }
                    """
            )
        )
    ),
    @ApiResponse(
        responseCode = "400",
        description = "Parámetros inválidos"
    ),
    @ApiResponse(
        responseCode = "401",
        description = "No autenticado"
    )
})
@GetMapping("/buscar")
public ResponseEntity<Page<ProductoDto>> buscar(
    @Parameter(
        description = "Texto de búsqueda (nombre o descripción)",
        example = "paracetamol"
    )
    @RequestParam(required = false) String q,

    @Parameter(
        description = "ID de la categoría",
        example = "1"
    )
    @RequestParam(required = false) Long categoriaId,

    @Parameter(
        description = "Número de página (inicia en 0)",
        example = "0"
    )
    @RequestParam(defaultValue = "0") int page,

    @Parameter(
        description = "Tamaño de página",
        example = "10"
    )
    @RequestParam(defaultValue = "10") int size
) {
    // Implementación
}
```

### 3. Documentar Request Body

```java
@PostMapping
public ResponseEntity<?> crear(
    @Parameter(
        description = "Datos del nuevo producto",
        required = true,
        content = @Content(
            examples = @ExampleObject(
                name = "Nuevo producto",
                value = """
                    {
                      "nombre": "Ibuprofeno 400mg",
                      "descripcion": "Antiinflamatorio",
                      "precio": 8.50,
                      "stock": 50,
                      "categoriaId": 1,
                      "laboratorioId": 2,
                      "requiereReceta": false
                    }
                    """
            )
        )
    )
    @Valid @RequestBody ProductoRequest request
) {
    // Implementación
}
```

### 4. Documentar Respuestas con Ejemplos

```java
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "Operación exitosa",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ProductoDto.class),
            examples = {
                @ExampleObject(
                    name = "Producto con receta",
                    value = """
                        {
                          "id": 1,
                          "nombre": "Amoxicilina 500mg",
                          "precio": 15.00,
                          "requiereReceta": true
                        }
                        """
                ),
                @ExampleObject(
                    name = "Producto sin receta",
                    value = """
                        {
                          "id": 2,
                          "nombre": "Vitamina C",
                          "precio": 12.00,
                          "requiereReceta": false
                        }
                        """
                )
            }
        )
    )
})
```

### 5. Endpoints sin Autenticación

Para endpoints públicos (como login, register):

```java
@SecurityRequirement(name = "")  // Deshabilita el requisito de JWT
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody AuthRequest request) {
    // ...
}
```

### 6. Documentar DTOs

```java
@Schema(description = "Datos de un producto del catálogo")
public class ProductoDto {

    @Schema(
        description = "ID único del producto",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
        description = "Nombre del producto",
        example = "Paracetamol 500mg",
        required = true,
        minLength = 3,
        maxLength = 200
    )
    private String nombre;

    @Schema(
        description = "Precio unitario en soles",
        example = "5.50",
        minimum = "0",
        required = true
    )
    private Double precio;

    @Schema(
        description = "Indica si requiere receta médica",
        example = "false",
        defaultValue = "false"
    )
    private Boolean requiereReceta;
}
```

## 🎨 Mejores Prácticas

### ✅ DO (Hacer)

1. **Usar descripciones claras y concisas**

   ```java
   @Operation(summary = "Obtener producto por ID")
   ```

2. **Incluir ejemplos realistas**

   ```java
   @Parameter(description = "ID del producto", example = "123")
   ```

3. **Documentar todos los códigos de respuesta posibles**

   ```java
   @ApiResponse(responseCode = "200", description = "Éxito")
   @ApiResponse(responseCode = "404", description = "No encontrado")
   @ApiResponse(responseCode = "500", description = "Error del servidor")
   ```

4. **Usar emojis en tags para mejor visualización**

   ```java
   @Tag(name = "💊 Productos")
   ```

5. **Agrupar endpoints relacionados**
   ```java
   tags = {"💊 Productos", "📦 Inventario"}
   ```

### ❌ DON'T (No hacer)

1. **No dejar endpoints sin documentar**
2. **No usar descripciones genéricas** ("Obtiene datos", "Guarda información")
3. **No olvidar documentar parámetros opcionales**
4. **No omitir ejemplos de request/response**
5. **No documentar endpoints deprecados sin marcarlos**

## 🔧 Anotaciones Útiles

### @Operation

Define información general del endpoint

```java
@Operation(
    summary = "Título corto",
    description = "Descripción detallada",
    tags = {"Tag1", "Tag2"},
    deprecated = false
)
```

### @Parameter

Documenta parámetros de query, path, header

```java
@Parameter(
    name = "id",
    description = "ID del recurso",
    required = true,
    example = "123",
    schema = @Schema(type = "integer")
)
```

### @ApiResponse

Define respuestas posibles

```java
@ApiResponse(
    responseCode = "200",
    description = "Éxito",
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = MiDto.class)
    )
)
```

### @Schema

Documenta modelos/DTOs

```java
@Schema(
    description = "Descripción del campo",
    example = "valor ejemplo",
    required = true,
    minimum = "0",
    maximum = "100"
)
```

### @SecurityRequirement

Define requisitos de seguridad

```java
@SecurityRequirement(name = "bearer-jwt")  // Requiere JWT
@SecurityRequirement(name = "")            // Público
```

## 📊 Ejemplo de Controlador Completo

Ver `AuthController.java` para un ejemplo completo de documentación.

## 🚀 Próximos Pasos

1. **Documentar todos los controladores** siguiendo el patrón de AuthController
2. **Agregar ejemplos** a todos los DTOs
3. **Documentar códigos de error** específicos
4. **Agregar descripciones** a los enums
5. **Crear colecciones Postman** desde Swagger

## 📱 Exportar Documentación

### Generar Postman Collection

1. Ir a http://localhost:8090/api-docs
2. Copiar el JSON
3. En Postman: Import → Raw text → Pegar JSON

### Generar Cliente

Usar OpenAPI Generator para generar clientes en diferentes lenguajes:

```bash
openapi-generator-cli generate -i http://localhost:8090/api-docs -g typescript-axios -o ./client
```

## 🎯 Checklist de Documentación

Para cada controlador:

- [ ] Tag con emoji y descripción
- [ ] @Operation en cada método
- [ ] @ApiResponses con todos los códigos posibles
- [ ] @Parameter en todos los parámetros
- [ ] Ejemplos en request bodies
- [ ] Ejemplos en responses
- [ ] @SecurityRequirement apropiado
- [ ] Descripciones claras y útiles

## 💡 Tips Adicionales

1. **Usar text blocks (""")** para descripciones largas
2. **Incluir información de autenticación** en descripciones
3. **Documentar validaciones** (min, max, pattern)
4. **Agregar notas importantes** en descripciones
5. **Mantener consistencia** en el estilo de documentación

## 🔗 Referencias

- [Swagger Annotations](https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations)
- [SpringDoc OpenAPI](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
