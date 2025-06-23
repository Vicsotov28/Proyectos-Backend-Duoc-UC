package com.ecomarketspa.Controller;

import com.ecomarketspa.Model.Producto;
import com.ecomarketspa.Service.ProductoService;
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

@WebMvcTest(ProductoController.class) // Enfocado en ProductoController
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Mock de ProductoService
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Producto producto1;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        producto1 = new Producto();
        producto1.setId(1L);
        producto1.setNombre("Jabón Artesanal");
        producto1.setCategoria("Cuidado Personal");
        producto1.setPrecio(5.99);
        producto1.setStock(100);
        producto1.setDescripcion("Jabón hecho a mano con ingredientes naturales.");

        producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Bolsa de Tela Reutilizable");
        producto2.setCategoria("Accesorios");
        producto2.setPrecio(12.50);
        producto2.setStock(50);
        producto2.setDescripcion("Bolsa ecológica de algodón orgánico.");
    }

    @Test
    @DisplayName("Test para obtener todos los productos - GET /api/productos")
    void testListarProductos() throws Exception {
        when(productoService.listarProductos()).thenReturn(Arrays.asList(producto1, producto2));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Jabón Artesanal"))
                .andExpect(jsonPath("$[1].categoria").value("Accesorios"));
    }

    @Test
    @DisplayName("Test para obtener un producto por ID - GET /api/productos/{id} - Existente")
    void testObtenerProductoPorIdExistente() throws Exception {
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(producto1));

        mockMvc.perform(get("/api/productos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Jabón Artesanal"))
                .andExpect(jsonPath("$.precio").value(5.99));
    }

    @Test
    @DisplayName("Test para obtener un producto por ID - GET /api/productos/{id} - No Existente")
    void testObtenerProductoPorIdNoExistente() throws Exception {
        when(productoService.obtenerProductoPorId(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test para crear un nuevo producto - POST /api/productos")
    void testGuardarProducto() throws Exception {
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre("Cepillo de Bambú");
        nuevoProducto.setCategoria("Cuidado Personal");
        nuevoProducto.setPrecio(3.50);
        nuevoProducto.setStock(200);
        nuevoProducto.setDescripcion("Cepillo de dientes biodegradable.");

        // Cuando el servicio guarde cualquier Producto, devolver producto1 como si fuera el guardado
        when(productoService.guardarProducto(any(Producto.class))).thenReturn(producto1);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoProducto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Jabón Artesanal")) // Coincide con el mock
                .andExpect(jsonPath("$.categoria").value("Cuidado Personal"));
    }

    @Test
    @DisplayName("Test para eliminar un producto - DELETE /api/productos/{id} - Existente")
    void testEliminarProductoExistente() throws Exception {
        // Para que el controlador elimine, primero debe encontrar el producto
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(producto1));
        doNothing().when(productoService).eliminarProducto(1L);

        mockMvc.perform(delete("/api/productos/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test para eliminar un producto - DELETE /api/productos/{id} - No Existente")
    void testEliminarProductoNoExistente() throws Exception {
        when(productoService.obtenerProductoPorId(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/productos/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test para actualizar un producto - POST /api/productos")
    void testActualizarProducto() throws Exception {
        Producto productoActualizado = new Producto();
        productoActualizado.setId(1L);
        productoActualizado.setNombre("Jabón Artesanal Mejorado");
        productoActualizado.setCategoria("Cuidado Personal Premium");
        productoActualizado.setPrecio(6.50);
        productoActualizado.setStock(90);
        productoActualizado.setDescripcion("Nueva fórmula mejorada.");

        // Cuando el servicio guarde cualquier Producto, devolver el producto actualizado
        when(productoService.guardarProducto(any(Producto.class))).thenReturn(productoActualizado);

        // Tu controlador usa POST para guardar/actualizar
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoActualizado)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Jabón Artesanal Mejorado"))
                .andExpect(jsonPath("$.precio").value(6.50));
    }
}
