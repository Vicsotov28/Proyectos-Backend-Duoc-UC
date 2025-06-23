package com.ecomarketspa.Controller;

import com.ecomarketspa.Model.Notificacion;
import com.ecomarketspa.Model.Usuario; // Necesitamos el modelo Usuario
import com.ecomarketspa.Service.NotificacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificacionController.class) // Enfocado en NotificacionController
public class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Mock de NotificacionService
    private NotificacionService notificacionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Notificacion notificacion1;
    private Notificacion notificacion2;
    private Usuario usuario1; // Para la relación con Notificacion

    @BeforeEach
    void setUp() {
        // Inicializar un usuario mock para asociarlo a las notificaciones
        usuario1 = new Usuario();
        usuario1.setId(10L);
        usuario1.setNombre("Usuario Notificado");
        usuario1.setCorreo("notificado@example.com");
        usuario1.setTelefono("900000000");

        notificacion1 = new Notificacion();
        notificacion1.setId(1L);
        notificacion1.setTipo("Pedido Confirmado");
        notificacion1.setMensaje("Tu pedido #123 ha sido confirmado.");
        notificacion1.setUsuario(usuario1); // Asociamos el usuario

        notificacion2 = new Notificacion();
        notificacion2.setId(2L);
        notificacion2.setTipo("Oferta Especial");
        notificacion2.setMensaje("¡Grandes descuentos en productos ecológicos!");
        notificacion2.setUsuario(usuario1); // Asociamos el mismo usuario
    }

    @Test
    @DisplayName("Test para obtener todas las notificaciones - GET /api/notificaciones")
    void testListarNotificaciones() throws Exception {
        when(notificacionService.listarNotificaciones()).thenReturn(Arrays.asList(notificacion1, notificacion2));

        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].tipo").value("Pedido Confirmado"))
                .andExpect(jsonPath("$[1].mensaje").value("¡Grandes descuentos en productos ecológicos!"));
        // Similar al pedido, puedes verificar usuario.id si lo necesitas:
        // .andExpect(jsonPath("$[0].usuario.id").value(usuario1.getId()));
    }

    @Test
    @DisplayName("Test para obtener una notificación por ID - GET /api/notificaciones/{id} - Existente")
    void testObtenerNotificacionPorIdExistente() throws Exception {
        when(notificacionService.obtenerNotificacionPorId(1L)).thenReturn(Optional.of(notificacion1));

        mockMvc.perform(get("/api/notificaciones/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tipo").value("Pedido Confirmado"))
                .andExpect(jsonPath("$.mensaje").value("Tu pedido #123 ha sido confirmado."))
                .andExpect(jsonPath("$.usuario.id").value(usuario1.getId())); // Verificamos el ID del usuario asociado
    }

    @Test
    @DisplayName("Test para obtener una notificación por ID - GET /api/notificaciones/{id} - No Existente")
    void testObtenerNotificacionPorIdNoExistente() throws Exception {
        when(notificacionService.obtenerNotificacionPorId(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/notificaciones/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test para crear una nueva notificación - POST /api/notificaciones")
    void testGuardarNotificacion() throws Exception {
        Notificacion nuevaNotificacion = new Notificacion();
        nuevaNotificacion.setTipo("Alerta de Stock");
        nuevaNotificacion.setMensaje("Producto agotado pronto.");
        nuevaNotificacion.setUsuario(usuario1); // Asociar el usuario mock

        // Cuando el servicio guarde cualquier Notificacion, devolver notificacion1 (con su ID)
        when(notificacionService.guardarNotificacion(any(Notificacion.class))).thenReturn(notificacion1);

        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevaNotificacion)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tipo").value("Pedido Confirmado")) // Coincide con el mock devuelto
                .andExpect(jsonPath("$.usuario.id").value(usuario1.getId())); // Coincide con el mock
    }

    @Test
    @DisplayName("Test para eliminar una notificación - DELETE /api/notificaciones/{id} - Existente")
    void testEliminarNotificacionExistente() throws Exception {
        // Para que el controlador elimine, primero debe encontrar la notificación
        when(notificacionService.obtenerNotificacionPorId(1L)).thenReturn(Optional.of(notificacion1));
        doNothing().when(notificacionService).eliminarNotificacion(1L);

        mockMvc.perform(delete("/api/notificaciones/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test para eliminar una notificación - DELETE /api/notificaciones/{id} - No Existente")
    void testEliminarNotificacionNoExistente() throws Exception {
        when(notificacionService.obtenerNotificacionPorId(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/notificaciones/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test para actualizar una notificación - POST /api/notificaciones")
    void testActualizarNotificacion() throws Exception {
        Notificacion notificacionActualizada = new Notificacion();
        notificacionActualizada.setId(1L);
        notificacionActualizada.setTipo("Recordatorio de Pago");
        notificacionActualizada.setMensaje("Tu factura vence mañana.");
        notificacionActualizada.setUsuario(usuario1); // El mismo usuario asociado

        when(notificacionService.guardarNotificacion(any(Notificacion.class))).thenReturn(notificacionActualizada);

        // Tu controlador usa POST para guardar/actualizar
        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionActualizada)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tipo").value("Recordatorio de Pago"))
                .andExpect(jsonPath("$.mensaje").value("Tu factura vence mañana."))
                .andExpect(jsonPath("$.usuario.id").value(usuario1.getId()));
    }
}

