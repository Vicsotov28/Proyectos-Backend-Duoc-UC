package com.ecomarketspa.Controller;

import com.ecomarketspa.Assembler.UsuarioModelAssembler; // Importa el ensamblador
import com.ecomarketspa.Model.Usuario;
import com.ecomarketspa.Service.UsuarioService;
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
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con la gestión de usuarios en EcoMarket SPA")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioModelAssembler assembler; // Inyecta el ensamblador

    @Operation(summary = "Obtener todos los usuarios",
            description = "Recupera una lista de todos los usuarios registrados en el sistema, enriquecida con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios recuperada exitosamente",
                    content = @Content(mediaType = "application/hal+json", // Importante: mediaType para HAL+JSON
                            schema = @Schema(implementation = CollectionModel.class))), // Esquema de colección
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public CollectionModel<EntityModel<Usuario>> listarUsuarios() {
        List<EntityModel<Usuario>> usuarios = usuarioService.listarUsuarios().stream()
                .map(assembler::toModel) // Convierte cada Usuario a EntityModel<Usuario>
                .collect(Collectors.toList());

        return CollectionModel.of(usuarios, linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel());
    }

    @Operation(summary = "Obtener un usuario por ID",
            description = "Recupera los detalles de un usuario específico utilizando su ID, enriquecido con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/hal+json", // Importante: mediaType para HAL+JSON
                            schema = @Schema(implementation = EntityModel.class))), // Esquema de entidad
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(assembler::toModel) // Convierte el Usuario a EntityModel<Usuario>
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear o actualizar un usuario",
            description = "Crea un nuevo usuario o actualiza uno existente si el ID es proporcionado, con respuesta enriquecida con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado o actualizado exitosamente",
                    content = @Content(mediaType = "application/hal+json", // Importante: mediaType para HAL+JSON
                            schema = @Schema(implementation = EntityModel.class))), // Esquema de entidad
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Usuario>> guardarUsuario(@RequestBody Usuario usuario) {
        Usuario savedUsuario = usuarioService.guardarUsuario(usuario);
        // Retorna el EntityModel del usuario guardado o actualizado
        return ResponseEntity
                .created(linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(savedUsuario.getId())).toUri())
                .body(assembler.toModel(savedUsuario));
    }

    @Operation(summary = "Eliminar un usuario por ID",
            description = "Elimina un usuario del sistema utilizando su ID. No retorna contenido con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        if (usuarioService.obtenerUsuarioPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // --- Nuevos endpoints personalizados con HATEOAS ---

    @Operation(summary = "Obtener usuarios por nombre",
            description = "Recupera una lista de usuarios cuyo nombre contiene la cadena especificada (búsqueda parcial e insensible a mayúsculas/minúsculas), con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios por nombre recuperada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = CollectionModel.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/nombre/{nombre}")
    public CollectionModel<EntityModel<Usuario>> obtenerUsuariosPorNombre(@PathVariable String nombre) {
        List<EntityModel<Usuario>> usuarios = usuarioService.buscarPorNombre(nombre).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).obtenerUsuariosPorNombre(nombre)).withSelfRel());
    }

    @Operation(summary = "Obtener usuarios por correo electrónico",
            description = "Recupera una lista de usuarios cuyo correo electrónico contiene la cadena especificada (búsqueda parcial e insensible a mayúsculas/minúsculas), con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios por correo electrónico recuperada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = CollectionModel.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/correo/{correo}")
    public CollectionModel<EntityModel<Usuario>> obtenerUsuariosPorCorreo(@PathVariable String correo) {
        List<EntityModel<Usuario>> usuarios = usuarioService.buscarPorCorreo(correo).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).obtenerUsuariosPorCorreo(correo)).withSelfRel());
    }

    @Operation(summary = "Obtener usuarios con pedidos pendientes",
            description = "Recupera una lista de todos los usuarios que tienen al menos un pedido en estado 'PENDIENTE', con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios con pedidos pendientes recuperada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = CollectionModel.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/con-pedidos-pendientes")
    public CollectionModel<EntityModel<Usuario>> obtenerUsuariosConPedidosPendientes() {
        List<EntityModel<Usuario>> usuarios = usuarioService.buscarUsuariosConPedidosPendientes().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).obtenerUsuariosConPedidosPendientes()).withSelfRel());
    }
}