package com.ecomarketspa.Service;

import com.ecomarketspa.Model.Pedido; // Asegúrate de que el path del modelo es correcto
import com.ecomarketspa.Model.Usuario; // También necesitarás el modelo Usuario
import com.ecomarketspa.Repository.PedidoRepository; // Asegúrate de que el path del repositorio es correcto
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    // Objetos de prueba
    private Pedido pedido1;
    private Pedido pedido2;
    private Usuario usuario1;

    @BeforeEach
    void setUp() {
        // Inicializamos un objeto Usuario para asociarlo a los pedidos
        usuario1 = new Usuario();
        usuario1.setId(10L); // Un ID de usuario para el test
        usuario1.setNombre("Cliente Test");
        usuario1.setCorreo("cliente@test.com");
        usuario1.setTelefono("123456789");

        // Inicializamos los objetos Pedido con datos de prueba
        pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setEstado("Pendiente");
        pedido1.setFecha(new Date()); // Fecha actual para la prueba
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
    @DisplayName("Test para listar todos los pedidos")
    void testListarPedidos() {
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedido1, pedido2));

        List<Pedido> pedidos = pedidoService.listarPedidos();

        assertNotNull(pedidos, "La lista de pedidos no debería ser nula");
        assertEquals(2, pedidos.size(), "La lista debería contener 2 pedidos");
        assertTrue(pedidos.contains(pedido1), "La lista debería contener a pedido1");
        assertTrue(pedidos.contains(pedido2), "La lista debería contener a pedido2");

        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test para guardar un nuevo pedido")
    void testGuardarPedido() {
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setEstado("Nuevo");
        nuevoPedido.setFecha(new Date());
        nuevoPedido.setTotal(50.00);
        nuevoPedido.setUsuario(usuario1);

        // Mocking: Cuando save() sea llamado con cualquier Pedido, devolver pedido1 (con ID asignado)
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido1);

        Pedido pedidoGuardado = pedidoService.guardarPedido(nuevoPedido);

        assertNotNull(pedidoGuardado, "El pedido guardado no debería ser nulo");
        assertEquals(1L, pedidoGuardado.getId(), "El ID del pedido guardado debería ser 1");
        assertEquals("Pendiente", pedidoGuardado.getEstado(), "El estado del pedido debe coincidir con el mock");
        assertEquals(usuario1.getId(), pedidoGuardado.getUsuario().getId(), "El ID del usuario asociado debe coincidir");
        // Puedes añadir más aserciones sobre el total, fecha, etc., si es relevante que coincidan con el mock

        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Test para obtener un pedido por ID existente")
    void testObtenerPedidoPorIdExistente() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido1));

        Optional<Pedido> pedidoEncontrado = pedidoService.obtenerPedidoPorId(1L);

        assertTrue(pedidoEncontrado.isPresent(), "Se debería encontrar el pedido");
        assertEquals(pedido1.getEstado(), pedidoEncontrado.get().getEstado(), "El estado del pedido debe coincidir");
        assertEquals(pedido1.getTotal(), pedidoEncontrado.get().getTotal(), 0.001, "El total del pedido debe coincidir");
        assertEquals(usuario1.getId(), pedidoEncontrado.get().getUsuario().getId(), "El ID del usuario del pedido debe coincidir");

        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test para obtener un pedido por ID no existente")
    void testObtenerPedidoPorIdNoExistente() {
        when(pedidoRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Pedido> pedidoEncontrado = pedidoService.obtenerPedidoPorId(99L);

        assertFalse(pedidoEncontrado.isPresent(), "No se debería encontrar el pedido");

        verify(pedidoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Test para eliminar un pedido existente")
    void testEliminarPedido() {
        doNothing().when(pedidoRepository).deleteById(1L);

        pedidoService.eliminarPedido(1L);

        verify(pedidoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test para actualizar un pedido")
    void testActualizarPedido() {
        // Objeto con los datos que simulan una actualización
        Pedido pedidoActualizadoDatos = new Pedido();
        pedidoActualizadoDatos.setId(1L); // ID existente
        pedidoActualizadoDatos.setEstado("Enviado");
        pedidoActualizadoDatos.setFecha(new Date());
        pedidoActualizadoDatos.setTotal(160.00); // Nuevo total
        pedidoActualizadoDatos.setUsuario(usuario1); // El mismo usuario

        // Mockeamos el comportamiento de save: cuando se le pase cualquier pedido,
        // queremos que devuelva el 'pedidoActualizadoDatos'
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoActualizadoDatos);

        // Llamamos al método del servicio
        Pedido result = pedidoService.guardarPedido(pedidoActualizadoDatos);

        // Verificamos el resultado
        assertNotNull(result);
        assertEquals(1L, result.getId(), "El ID debería ser el mismo");
        assertEquals("Enviado", result.getEstado(), "El estado debería estar actualizado");
        assertEquals(160.00, result.getTotal(), 0.001, "El total debería estar actualizado");
        assertEquals(usuario1.getId(), result.getUsuario().getId(), "El ID del usuario debería ser el mismo");

        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }
}
