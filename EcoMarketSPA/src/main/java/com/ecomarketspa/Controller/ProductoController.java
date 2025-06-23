package com.ecomarketspa.Controller;

import com.ecomarketspa.Assembler.ProductoModelAssembler; // Importa el ensamblador
import com.ecomarketspa.Model.Producto;
import com.ecomarketspa.Service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel; // Importa CollectionModel
import org.springframework.hateoas.EntityModel;   // Importa EntityModel
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Añadir para static import de linkTo/methodOn

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Operaciones relacionadas con la gestión de productos en EcoMarket SPA")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoModelAssembler assembler; // Inyecta el ensamblador

    @Operation(summary = "Obtener todos los productos",
            description = "Recupera una lista de todos los productos disponibles en el inventario, enriquecida con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos recuperada exitosamente",
                    content = @Content(mediaType = "application/hal+json", // Importante: mediaType para HAL+JSON
                            schema = @Schema(implementation = CollectionModel.class))), // Esquema de colección
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public CollectionModel<EntityModel<Producto>> listarProductos() {
        List<EntityModel<Producto>> productos = productoService.listarProductos().stream()
                .map(assembler::toModel) // Convierte cada Producto a EntityModel<Producto>
                .collect(Collectors.toList());

        return CollectionModel.of(productos, linkTo(methodOn(ProductoController.class).listarProductos()).withSelfRel());
    }

    @Operation(summary = "Obtener un producto por ID",
            description = "Recupera los detalles de un producto específico utilizando su ID, enriquecido con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/hal+json", // Importante: mediaType para HAL+JSON
                            schema = @Schema(implementation = EntityModel.class))), // Esquema de entidad
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> obtenerProductoPorId(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id)
                .map(assembler::toModel) // Convierte el Producto a EntityModel<Producto>
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear o actualizar un producto",
            description = "Crea un nuevo producto o actualiza uno existente si el ID es proporcionado, con respuesta enriquecida con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto creado o actualizado exitosamente",
                    content = @Content(mediaType = "application/hal+json", // Importante: mediaType para HAL+JSON
                            schema = @Schema(implementation = EntityModel.class))), // Esquema de entidad
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Producto>> guardarProducto(@RequestBody Producto producto) {
        Producto savedProducto = productoService.guardarProducto(producto);
        // Retorna el EntityModel del producto guardado o actualizado
        return ResponseEntity
                .created(linkTo(methodOn(ProductoController.class).obtenerProductoPorId(savedProducto.getId())).toUri())
                .body(assembler.toModel(savedProducto));
    }

    @Operation(summary = "Eliminar un producto por ID",
            description = "Elimina un producto del sistema utilizando su ID. No retorna contenido con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        if (productoService.obtenerProductoPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    // --- Nuevos endpoints personalizados con HATEOAS ---

    @Operation(summary = "Obtener productos por categoría",
            description = "Recupera una lista de productos filtrados por una categoría específica, con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos por categoría recuperada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = CollectionModel.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada o sin productos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/categoria/{categoria}")
    public CollectionModel<EntityModel<Producto>> obtenerProductosPorCategoria(@PathVariable String categoria) {
        List<EntityModel<Producto>> productos = productoService.buscarPorCategoria(categoria).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(productos,
                linkTo(methodOn(ProductoController.class).obtenerProductosPorCategoria(categoria)).withSelfRel());
    }

    @Operation(summary = "Obtener productos con stock bajo un umbral",
            description = "Recupera una lista de productos cuyo stock es menor que el valor especificado, con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos con stock bajo umbral recuperada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = CollectionModel.class))),
            @ApiResponse(responseCode = "400", description = "Umbral de stock inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/stock-menor-que/{stockUmbral}")
    public CollectionModel<EntityModel<Producto>> obtenerProductosConStockMenorQue(@PathVariable int stockUmbral) {
        List<EntityModel<Producto>> productos = productoService.buscarPorStockBajoUmbral(stockUmbral).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(productos,
                linkTo(methodOn(ProductoController.class).obtenerProductosConStockMenorQue(stockUmbral)).withSelfRel());
    }

    @Operation(summary = "Obtener productos por rango de precio",
            description = "Recupera una lista de productos cuyo precio se encuentra entre un mínimo y un máximo, con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos por rango de precio recuperada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = CollectionModel.class))),
            @ApiResponse(responseCode = "400", description = "Rango de precio inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/precio-entre")
    public CollectionModel<EntityModel<Producto>> obtenerProductosPorRangoDePrecio(
            @RequestParam double minPrecio,
            @RequestParam double maxPrecio) {
        List<EntityModel<Producto>> productos = productoService.buscarPorRangoDePrecio(minPrecio, maxPrecio).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(productos,
                linkTo(methodOn(ProductoController.class).obtenerProductosPorRangoDePrecio(minPrecio, maxPrecio)).withSelfRel());
    }
}