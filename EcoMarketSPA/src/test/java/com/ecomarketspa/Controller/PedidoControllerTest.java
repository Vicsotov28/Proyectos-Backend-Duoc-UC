package com.ecomarketspa.Controller;

import com.ecomarketspa.Model.Pedido;
import com.ecomarketspa.Model.Usuario; // Necesitamos el modelo Usuario
import com.ecomarketspa.Service.PedidoService;
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
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class) // Enfocado en PedidoController
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Mock de PedidoService
    private PedidoService pedidoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Pedido pedido1;
    private Pedido pedido2;
    private Usuario usuario1; // Para la relación con Pedido

    @BeforeEach
    void setUp() {
        // Inicializar un usuario mock para asociarlo a los pedidos
        usuario1 = new Usuario();
        usuario1.setId(10L);
        usuario1.setNombre("Cliente Test");
        usuario1.setCorreo("cliente@test.com");
        usuario1.setTelefono("123456789");

        pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setEstado("Pendiente");
        pedido1.setFecha(new Date());
        pedido1.setTotal(150.75);
        pedido1.setUsuario(usuario1); // Asociamos el usuario

        pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setEstado("Completado");
        pedido2.setFecha(new Date(System.currentTimeMillis() - 86400000)); // Un día antes
        pedido2.setTotal(25.00);
        pedido2.setUsuario(usuario1); // Asociamos el mismo usuario
    }

    @Test
    @DisplayName("Test para obtener todos los pedidos - GET /api/pedidos")
    void testListarPedidos() throws Exception {
        when(pedidoService.listarPedidos()).thenReturn(Arrays.asList(pedido1, pedido2));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].estado").value("Pendiente"))
                .andExpect(jsonPath("$[1].total").value(25.00));
        // Aquí no podemos verificar directamente el objeto Usuario anidado con jsonPath simple
        // Si necesitas verificar los detalles del usuario anidado, necesitarías .andExpect(jsonPath("$[0].usuario.id").value(10L))
        // o mapear la respuesta JSON a un objeto y verificarlo directamente.
    }

    @Test
    @DisplayName("Test para obtener un pedido por ID - GET /api/pedidos/{id} - Existente")
    void testObtenerPedidoPorIdExistente() throws Exception {
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido1));

        mockMvc.perform(get("/api/pedidos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estado").value("Pendiente"))
                .andExpect(jsonPath("$.total").value(150.75))
                .andExpect(jsonPath("$.usuario.id").value(usuario1.getId())); // Verificamos el ID del usuario asociado
    }

    @Test
    @DisplayName("Test para obtener un pedido por ID - GET /api/pedidos/{id} - No Existente")
    void testObtenerPedidoPorIdNoExistente() throws Exception {
        when(pedidoService.obtenerPedidoPorId(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pedidos/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test para crear un nuevo pedido - POST /api/pedidos")
    void testGuardarPedido() throws Exception {
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setEstado("Nuevo");
        nuevoPedido.setFecha(new Date());
        nuevoPedido.setTotal(50.00);
        nuevoPedido.setUsuario(usuario1); // Asociar el usuario mock

        // Cuando el servicio guarde cualquier Pedido, devolver pedido1 (con su ID)
        when(pedidoService.guardarPedido(any(Pedido.class))).thenReturn(pedido1);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoPedido)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estado").value("Pendiente")) // Coincide con el mock
                .andExpect(jsonPath("$.usuario.id").value(usuario1.getId())); // Coincide con el mock
    }

    @Test
    @DisplayName("Test para eliminar un pedido - DELETE /api/pedidos/{id} - Existente")
    void testEliminarPedidoExistente() throws Exception {
        // Para que el controlador elimine, primero debe encontrar el pedido
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido1));
        doNothing().when(pedidoService).eliminarPedido(1L);

        mockMvc.perform(delete("/api/pedidos/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test para eliminar un pedido - DELETE /api/pedidos/{id} - No Existente")
    void testEliminarPedidoNoExistente() throws Exception {
        when(pedidoService.obtenerPedidoPorId(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/pedidos/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test para actualizar un pedido - POST /api/pedidos")
    void testActualizarPedido() throws Exception {
        Pedido pedidoActualizado = new Pedido();
        pedidoActualizado.setId(1L);
        pedidoActualizado.setEstado("Enviado");
        pedidoActualizado.setFecha(new Date());
        pedidoActualizado.setTotal(160.00);
        pedidoActualizado.setUsuario(usuario1); // El mismo usuario asociado

        when(pedidoService.guardarPedido(any(Pedido.class))).thenReturn(pedidoActualizado);

        // Tu controlador usa POST para guardar/actualizar
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoActualizado)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estado").value("Enviado"))
                .andExpect(jsonPath("$.total").value(160.00))
                .andExpect(jsonPath("$.usuario.id").value(usuario1.getId()));
    }
}
