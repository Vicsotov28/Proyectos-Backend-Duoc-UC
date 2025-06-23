package com.ecomarketspa.Controller;

import com.ecomarketspa.Assembler.PedidoModelAssembler;
import com.ecomarketspa.Model.Pedido;
import com.ecomarketspa.Service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel; // Para el caso de conteo
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat; // Importar para el formato de fechas

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Operaciones relacionadas con la gestión de pedidos de los usuarios")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoModelAssembler assembler;

    @Operation(summary = "Obtener todos los pedidos",
            description = "Recupera una lista de todos los pedidos registrados en el sistema, enriquecida con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos recuperada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = CollectionModel.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public CollectionModel<EntityModel<Pedido>> listarPedidos() {
        List<EntityModel<Pedido>> pedidos = pedidoService.listarPedidos().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(pedidos, linkTo(methodOn(PedidoController.class).listarPedidos()).withSelfRel());
    }

    @Operation(summary = "Obtener un pedido por ID",
            description = "Recupera los detalles de un pedido específico utilizando su ID, enriquecido con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = EntityModel.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Pedido>> obtenerPedidoPorId(@PathVariable Long id) {
        return pedidoService.obtenerPedidoPorId(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear o actualizar un pedido",
            description = "Crea un nuevo pedido o actualiza uno existente si el ID es proporcionado, con respuesta enriquecida con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido creado o actualizado exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = EntityModel.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (ej. usuario asociado no existe)"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Pedido>> guardarPedido(@RequestBody Pedido pedido) {
        Pedido savedPedido = pedidoService.guardarPedido(pedido);
        return ResponseEntity
                .created(linkTo(methodOn(PedidoController.class).obtenerPedidoPorId(savedPedido.getId())).toUri())
                .body(assembler.toModel(savedPedido));
    }

    @Operation(summary = "Eliminar un pedido por ID",
            description = "Elimina un pedido del sistema utilizando su ID. No retorna contenido con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        if (pedidoService.obtenerPedidoPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Obtener pedidos por estado",
            description = "Recupera una lista de pedidos filtrados por un estado específico (ej. PENDIENTE, ENVIADO, ENTREGADO, CANCELADO), con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos por estado recuperada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = CollectionModel.class))),
            @ApiResponse(responseCode = "400", description = "Estado de pedido inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/estado/{estado}")
    public CollectionModel<EntityModel<Pedido>> obtenerPedidosPorEstado(@PathVariable String estado) {
        List<EntityModel<Pedido>> pedidos = pedidoService.buscarPedidosPorEstado(estado).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(pedidos,
                linkTo(methodOn(PedidoController.class).obtenerPedidosPorEstado(estado)).withSelfRel());
    }

    @Operation(summary = "Obtener pedidos por ID de usuario",
            description = "Recupera una lista de todos los pedidos realizados por un usuario específico, con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos por usuario recuperada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = CollectionModel.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado o sin pedidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/usuario/{usuarioId}")
    public CollectionModel<EntityModel<Pedido>> obtenerPedidosPorUsuario(@PathVariable Long usuarioId) {
        List<EntityModel<Pedido>> pedidos = pedidoService.buscarPedidosPorUsuario(usuarioId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(pedidos,
                linkTo(methodOn(PedidoController.class).obtenerPedidosPorUsuario(usuarioId)).withSelfRel());
    }

    @Operation(summary = "Obtener pedidos entre dos fechas",
            description = "Recupera una lista de pedidos realizados dentro de un rango de fechas específico. Formato de fecha esperado: yyyy-MM-dd'T'HH:mm:ss.SSSZ (ej. 2023-10-26T10:00:00.000+0000), o un formato simple como yyyy-MM-dd para solo día. La hora y la zona horaria pueden ser relevantes si tu DB las almacena.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(name = "inicio", description = "Fecha y hora de inicio (ej. 2023-01-01T00:00:00.000+0000)", required = true),
                    @io.swagger.v3.oas.annotations.Parameter(name = "fin", description = "Fecha y hora de fin (ej. 2023-12-31T23:59:59.999+0000)", required = true)
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos entre fechas recuperada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = CollectionModel.class))),
            @ApiResponse(responseCode = "400", description = "Formato de fecha inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/rango-fechas")
    public CollectionModel<EntityModel<Pedido>> obtenerPedidosEntreFechas(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ") Date inicio, // Ajustar el patrón si tu fecha es diferente
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ") Date fin) { // Ajustar el patrón si tu fecha es diferente

        List<EntityModel<Pedido>> pedidos = pedidoService.buscarPedidosEntreFechas(inicio, fin).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(pedidos,
                linkTo(methodOn(PedidoController.class).obtenerPedidosEntreFechas(inicio, fin)).withSelfRel());
    }

    @Operation(summary = "Contar pedidos por usuario",
            description = "Obtiene el número total de pedidos realizados por un usuario específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo de pedidos por usuario recuperado exitosamente",
                    content = @Content(mediaType = "application/hal+json", // Mantener application/hal+json
                            schema = @Schema(implementation = Map.class))), // Indicar que el esquema es un Map
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/usuario/{usuarioId}/count")
    public ResponseEntity<EntityModel<Map<String, Long>>> contarPedidosPorUsuario(@PathVariable Long usuarioId) {
        Long count = pedidoService.contarPedidosPorUsuario(usuarioId);

        Map<String, Long> countMap = new HashMap<>();
        countMap.put("totalPedidos", count); // Clave "totalPedidos" con el valor del conteo

        EntityModel<Map<String, Long>> resource = EntityModel.of(countMap);
        resource.add(linkTo(methodOn(PedidoController.class).contarPedidosPorUsuario(usuarioId)).withSelfRel());
        resource.add(linkTo(methodOn(PedidoController.class).obtenerPedidosPorUsuario(usuarioId)).withRel("pedidos-del-usuario"));

        return ResponseEntity.ok(resource);
    }
}