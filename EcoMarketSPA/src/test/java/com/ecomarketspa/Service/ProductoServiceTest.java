package com.ecomarketspa.Service;

import com.ecomarketspa.Model.Producto;
import com.ecomarketspa.Repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {


    @Mock
    private ProductoRepository productoRepository;

    // @InjectMocks inyecta los mocks creados (productoRepository) en ProductoService
    @InjectMocks
    private ProductoService productoService;

    // Objetos de Producto de prueba
    private Producto producto1;
    private Producto producto2;

    // Se ejecuta antes de cada método de test
    @BeforeEach
    void setUp() {
        // Inicializamos los objetos Producto con datos de prueba
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
    @DisplayName("Test para listar todos los productos")
    void testListarProductos() {
        // Mocking: Cuando productoRepository.findAll() sea llamado, devolver la lista de productos de prueba
        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto1, producto2));

        // Llamada al método del servicio
        List<Producto> productos = productoService.listarProductos();

        // Verificación de los resultados
        assertNotNull(productos, "La lista de productos no debería ser nula");
        assertEquals(2, productos.size(), "La lista debería contener 2 productos");
        assertTrue(productos.contains(producto1), "La lista debería contener a producto1");
        assertTrue(productos.contains(producto2), "La lista debería contener a producto2");

        // Verificación de la interacción con el mock
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test para guardar un nuevo producto")
    void testGuardarProducto() {
        // Producto a guardar (sin ID, ya que se generará al guardar)
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre("Cepillo de Bambú");
        nuevoProducto.setCategoria("Cuidado Personal");
        nuevoProducto.setPrecio(3.50);
        nuevoProducto.setStock(200);
        nuevoProducto.setDescripcion("Cepillo de dientes biodegradable.");

        // Mocking: Cuando save() sea llamado con cualquier Producto, devolver producto1
        // (Simulamos que el repositorio le asigna el ID 1L)
        when(productoRepository.save(any(Producto.class))).thenReturn(producto1);

        // Llamada al método del servicio
        Producto productoGuardado = productoService.guardarProducto(nuevoProducto);

        // Verificación
        assertNotNull(productoGuardado, "El producto guardado no debería ser nulo");
        assertEquals(1L, productoGuardado.getId(), "El ID del producto guardado debería ser 1");
        assertEquals("Jabón Artesanal", productoGuardado.getNombre(), "El nombre del producto guardado debe coincidir con el mock");
        // Asegúrate de que las aserciones coincidan con los datos del mock que se devuelve
        assertEquals("Cuidado Personal", productoGuardado.getCategoria());
        assertEquals(5.99, productoGuardado.getPrecio());
        assertEquals(100, productoGuardado.getStock());

        // Verificación de la interacción
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Test para obtener un producto por ID existente")
    void testObtenerProductoPorIdExistente() {
        // Mocking: Cuando findById(1L) sea llamado, devolver Optional con producto1
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));

        // Llamada al método del servicio
        Optional<Producto> productoEncontrado = productoService.obtenerProductoPorId(1L);

        // Verificación
        assertTrue(productoEncontrado.isPresent(), "Se debería encontrar el producto");
        assertEquals(producto1.getNombre(), productoEncontrado.get().getNombre(), "El nombre del producto debe coincidir");
        assertEquals(producto1.getCategoria(), productoEncontrado.get().getCategoria());

        // Verificación de la interacción
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test para obtener un producto por ID no existente")
    void testObtenerProductoPorIdNoExistente() {
        // Mocking: Cuando findById() con cualquier Long, devolver Optional vacío
        when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Llamada al método del servicio
        Optional<Producto> productoEncontrado = productoService.obtenerProductoPorId(99L);

        // Verificación
        assertFalse(productoEncontrado.isPresent(), "No se debería encontrar el producto");

        // Verificación de la interacción
        verify(productoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Test para eliminar un producto existente")
    void testEliminarProducto() {
        // Mocking para métodos void: no hacer nada cuando se llame a deleteById(1L)
        doNothing().when(productoRepository).deleteById(1L);

        // Llamada al método del servicio
        productoService.eliminarProducto(1L);

        // Verificación de la interacción
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test para actualizar un producto")
    void testActualizarProducto() {
        // Producto con datos actualizados y con un ID existente
        Producto productoActualizadoDatos = new Producto();
        productoActualizadoDatos.setId(1L); // Es crucial que tenga un ID para simular una actualización
        productoActualizadoDatos.setNombre("Jabón Artesanal Mejorado");
        productoActualizadoDatos.setCategoria("Cuidado Personal Premium");
        productoActualizadoDatos.setPrecio(6.50);
        productoActualizadoDatos.setStock(90); // Stock reducido
        productoActualizadoDatos.setDescripcion("Nueva fórmula mejorada.");

        // Mockeamos el comportamiento de save: cuando se le pase cualquier producto,
        // queremos que devuelva el 'productoActualizadoDatos'
        when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizadoDatos);

        // Llamamos al método del servicio (guardarProducto maneja la actualización)
        Producto result = productoService.guardarProducto(productoActualizadoDatos);

        // Verificamos el resultado
        assertNotNull(result);
        assertEquals(1L, result.getId(), "El ID debería ser el mismo");
        assertEquals("Jabón Artesanal Mejorado", result.getNombre(), "El nombre debería estar actualizado");
        assertEquals("Cuidado Personal Premium", result.getCategoria(), "La categoría debería estar actualizada");
        assertEquals(6.50, result.getPrecio(), 0.001, "El precio debería estar actualizado"); // Usar delta para doubles
        assertEquals(90, result.getStock(), "El stock debería estar actualizado");
        assertEquals("Nueva fórmula mejorada.", result.getDescripcion(), "La descripción debería estar actualizada");

        // Verificamos que el método save fue llamado una vez con cualquier objeto Producto.
        verify(productoRepository, times(1)).save(any(Producto.class));
    }
}
