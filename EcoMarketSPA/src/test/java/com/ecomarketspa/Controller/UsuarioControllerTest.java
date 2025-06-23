package com.ecomarketspa.Controller;

import com.ecomarketspa.Model.Usuario;
import com.ecomarketspa.Service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper; // Para convertir objetos Java a JSON y viceversa
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType; // Para definir el tipo de contenido HTTP
import org.springframework.test.web.servlet.MockMvc; // Objeto para simular peticiones HTTP

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // Para get, post, put, delete
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*; // Para status, content, jsonPath

@WebMvcTest(UsuarioController.class) // Indica a Spring Boot que solo levante el contexto para UsuarioController
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc; // Inyecta MockMvc para simular las peticiones HTTP

    @MockBean // Crea un mock del UsuarioService y lo inyecta en el contexto de Spring
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos Java a JSON y JSON a objetos Java

    private Usuario usuario1;
    private Usuario usuario2;

    @BeforeEach
    void setUp() {
        usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setNombre("Vicente Soto");
        usuario1.setCorreo("vicente.soto@example.com");
        usuario1.setTelefono("912345678");

        usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNombre("Maria Lopez");
        usuario2.setCorreo("maria.lopez@example.com");
        usuario2.setTelefono("987654321");
    }

    @Test
    @DisplayName("Test para obtener todos los usuarios - GET /api/usuarios")
    void testListarUsuarios() throws Exception {
        // Mockeamos el comportamiento del servicio: cuando se llame a listarUsuarios(), devolverá una lista de prueba.
        when(usuarioService.listarUsuarios()).thenReturn(Arrays.asList(usuario1, usuario2));

        // Realizamos la petición GET a /api/usuarios
        mockMvc.perform(get("/api/usuarios"))
                // Verificamos el estado HTTP (200 OK)
                .andExpect(status().isOk())
                // Verificamos que el tipo de contenido de la respuesta es JSON
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Verificamos el tamaño de la lista JSON devuelta
                .andExpect(jsonPath("$.length()").value(2))
                // Verificamos el nombre del primer usuario en la lista
                .andExpect(jsonPath("$[0].nombre").value("Vicente Soto"))
                // Verificamos el correo del segundo usuario en la lista
                .andExpect(jsonPath("$[1].correo").value("maria.lopez@example.com"));
    }

    @Test
    @DisplayName("Test para obtener un usuario por ID - GET /api/usuarios/{id} - Existente")
    void testObtenerUsuarioPorIdExistente() throws Exception {
        // Mockeamos el servicio: cuando se llame a obtenerUsuarioPorId(1L), devolverá un Optional con usuario1.
        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(Optional.of(usuario1));

        // Realizamos la petición GET a /api/usuarios/1
        mockMvc.perform(get("/api/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Vicente Soto"))
                .andExpect(jsonPath("$.correo").value("vicente.soto@example.com"));
    }

    @Test
    @DisplayName("Test para obtener un usuario por ID - GET /api/usuarios/{id} - No Existente")
    void testObtenerUsuarioPorIdNoExistente() throws Exception {
        // Mockeamos el servicio: cuando se llame a obtenerUsuarioPorId con cualquier ID, devolverá un Optional vacío.
        when(usuarioService.obtenerUsuarioPorId(anyLong())).thenReturn(Optional.empty());

        // Realizamos la petición GET a /api/usuarios/99 (un ID que no existe)
        mockMvc.perform(get("/api/usuarios/{id}", 99L))
                .andExpect(status().isNotFound()); // Esperamos un 404 Not Found
    }

    @Test
    @DisplayName("Test para crear un nuevo usuario - POST /api/usuarios")
    void testGuardarUsuario() throws Exception {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre("Carlos Ruiz");
        nuevoUsuario.setCorreo("carlos.ruiz@example.com");
        nuevoUsuario.setTelefono("933332222");

        // Mockeamos el servicio: cuando se llame a guardarUsuario(cualquier Usuario), devolverá usuario1 (simulando que se guarda con ID 1L)
        when(usuarioService.guardarUsuario(any(Usuario.class))).thenReturn(usuario1);

        // Realizamos la petición POST a /api/usuarios
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON) // Indicamos que el cuerpo de la petición es JSON
                        .content(objectMapper.writeValueAsString(nuevoUsuario))) // Convertimos el objeto Usuario a JSON
                .andExpect(status().isOk()) // Esperamos un 200 OK (o 201 Created si tu @PostMapping lo devuelve)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Vicente Soto")) // Coincide con el mock returnado
                .andExpect(jsonPath("$.correo").value("vicente.soto@example.com"));
    }

    @Test
    @DisplayName("Test para eliminar un usuario - DELETE /api/usuarios/{id} - Existente")
    void testEliminarUsuarioExistente() throws Exception {
        // Mockeamos el servicio: primero, que el usuario exista para la verificación del controlador
        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(Optional.of(usuario1));
        // Luego, que la eliminación no haga nada (método void)
        doNothing().when(usuarioService).eliminarUsuario(1L);

        // Realizamos la petición DELETE a /api/usuarios/1
        mockMvc.perform(delete("/api/usuarios/{id}", 1L))
                .andExpect(status().isNoContent()); // Esperamos un 204 No Content
    }

    @Test
    @DisplayName("Test para eliminar un usuario - DELETE /api/usuarios/{id} - No Existente")
    void testEliminarUsuarioNoExistente() throws Exception {
        // Mockeamos el servicio: que el usuario no exista
        when(usuarioService.obtenerUsuarioPorId(anyLong())).thenReturn(Optional.empty());

        // Realizamos la petición DELETE a /api/usuarios/99 (un ID que no existe)
        mockMvc.perform(delete("/api/usuarios/{id}", 99L))
                .andExpect(status().isNotFound()); // Esperamos un 404 Not Found
    }

    @Test
    @DisplayName("Test para actualizar un usuario - PUT /api/usuarios")
    void testActualizarUsuario() throws Exception {
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L); // ID del usuario a actualizar
        usuarioActualizado.setNombre("Vicente Soto Actualizado");
        usuarioActualizado.setCorreo("vicente.actualizado@example.com");
        usuarioActualizado.setTelefono("999999999");

        // Mockeamos el servicio: cuando se llame a guardarUsuario(cualquier Usuario), devolverá el usuario actualizado.
        when(usuarioService.guardarUsuario(any(Usuario.class))).thenReturn(usuarioActualizado);

        // Realizamos la petición POST (o PUT, dependiendo de cómo lo hayas implementado para actualizaciones)
        // Tu controlador usa POST para guardar/actualizar, así que usaremos POST.
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioActualizado)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Vicente Soto Actualizado"))
                .andExpect(jsonPath("$.correo").value("vicente.actualizado@example.com"))
                .andExpect(jsonPath("$.telefono").value("999999999"));
    }
}
