package com.ecomarketspa.Controller;

import com.ecomarketspa.Model.Notificacion;
import com.ecomarketspa.Service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Operaciones relacionadas con la gestión de notificaciones para usuarios")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Operation(summary = "Obtener todas las notificaciones",
            description = "Recupera una lista de todas las notificaciones enviadas en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificaciones recuperada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Notificacion.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public List<Notificacion> listarNotificaciones() {
        return notificacionService.listarNotificaciones();
    }

    @Operation(summary = "Obtener una notificación por ID",
            description = "Recupera los detalles de una notificación específica utilizando su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Notificacion.class))),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> obtenerNotificacionPorId(@PathVariable Long id) {
        return notificacionService.obtenerNotificacionPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear o actualizar una notificación",
            description = "Crea una nueva notificación o actualiza una existente si el ID es proporcionado. " +
                    "Si el ID existe, actualiza la notificación; si no, crea una nueva. " +
                    "Asegúrate de que el usuario receptor de la notificación exista.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación creada o actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Notificacion.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (ej. usuario asociado no existe)"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public Notificacion guardarNotificacion(@RequestBody Notificacion notificacion) {
        return notificacionService.guardarNotificacion(notificacion);
    }

    @Operation(summary = "Eliminar una notificación por ID",
            description = "Elimina una notificación del sistema utilizando su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notificación eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNotificacion(@PathVariable Long id) {
        if (notificacionService.obtenerNotificacionPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        notificacionService.eliminarNotificacion(id);
        return ResponseEntity.noContent().build();
    }
}
