package com.ecomarketspa.Service;

import com.ecomarketspa.Model.Notificacion; // Asegúrate de que el path del modelo es correcto
import com.ecomarketspa.Model.Usuario;      // También necesitarás el modelo Usuario
import com.ecomarketspa.Repository.NotificacionRepository; // Asegúrate de que el path del repositorio es correcto
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
public class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    // Objetos de prueba
    private Notificacion notificacion1;
    private Notificacion notificacion2;
    private Usuario usuario1; // Para la asociación con Usuario

    @BeforeEach
    void setUp() {
        // Inicializamos un objeto Usuario para asociarlo a las notificaciones
        usuario1 = new Usuario();
        usuario1.setId(10L); // Un ID de usuario para el test
        usuario1.setNombre("Usuario Notificado");
        usuario1.setCorreo("notificado@example.com");
        usuario1.setTelefono("900000000");

        // Inicializamos los objetos Notificacion con datos de prueba
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
    @DisplayName("Test para listar todas las notificaciones")
    void testListarNotificaciones() {
        when(notificacionRepository.findAll()).thenReturn(Arrays.asList(notificacion1, notificacion2));

        List<Notificacion> notificaciones = notificacionService.listarNotificaciones();

        assertNotNull(notificaciones, "La lista de notificaciones no debería ser nula");
        assertEquals(2, notificaciones.size(), "La lista debería contener 2 notificaciones");
        assertTrue(notificaciones.contains(notificacion1), "La lista debería contener a notificacion1");
        assertTrue(notificaciones.contains(notificacion2), "La lista debería contener a notificacion2");

        verify(notificacionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test para guardar una nueva notificación")
    void testGuardarNotificacion() {
        Notificacion nuevaNotificacion = new Notificacion();
        nuevaNotificacion.setTipo("Nueva Promoción");
        nuevaNotificacion.setMensaje("Descubre nuestros nuevos productos.");
        nuevaNotificacion.setUsuario(usuario1);

        // Mocking: Cuando save() sea llamado con cualquier Notificacion, devolver notificacion1 (con ID asignado)
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion1);

        Notificacion notificacionGuardada = notificacionService.guardarNotificacion(nuevaNotificacion);

        assertNotNull(notificacionGuardada, "La notificación guardada no debería ser nula");
        assertEquals(1L, notificacionGuardada.getId(), "El ID de la notificación guardada debería ser 1");
        assertEquals("Pedido Confirmado", notificacionGuardada.getTipo(), "El tipo de notificación debe coincidir con el mock");
        assertEquals(usuario1.getId(), notificacionGuardada.getUsuario().getId(), "El ID del usuario asociado debe coincidir");

        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("Test para obtener una notificación por ID existente")
    void testObtenerNotificacionPorIdExistente() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion1));

        Optional<Notificacion> notificacionEncontrada = notificacionService.obtenerNotificacionPorId(1L);

        assertTrue(notificacionEncontrada.isPresent(), "Se debería encontrar la notificación");
        assertEquals(notificacion1.getTipo(), notificacionEncontrada.get().getTipo(), "El tipo de notificación debe coincidir");
        assertEquals(notificacion1.getMensaje(), notificacionEncontrada.get().getMensaje(), "El mensaje de la notificación debe coincidir");
        assertEquals(usuario1.getId(), notificacionEncontrada.get().getUsuario().getId(), "El ID del usuario de la notificación debe coincidir");

        verify(notificacionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test para obtener una notificación por ID no existente")
    void testObtenerNotificacionPorIdNoExistente() {
        when(notificacionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Notificacion> notificacionEncontrada = notificacionService.obtenerNotificacionPorId(99L);

        assertFalse(notificacionEncontrada.isPresent(), "No se debería encontrar la notificación");

        verify(notificacionRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Test para eliminar una notificación existente")
    void testEliminarNotificacion() {
        doNothing().when(notificacionRepository).deleteById(1L);

        notificacionService.eliminarNotificacion(1L);

        verify(notificacionRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test para actualizar una notificación")
    void testActualizarNotificacion() {
        // Objeto con los datos que simulan una actualización
        Notificacion notificacionActualizadaDatos = new Notificacion();
        notificacionActualizadaDatos.setId(1L); // ID existente
        notificacionActualizadaDatos.setTipo("Actualización de Envío");
        notificacionActualizadaDatos.setMensaje("Tu pedido #123 ha sido enviado.");
        notificacionActualizadaDatos.setUsuario(usuario1); // El mismo usuario

        // Mockeamos el comportamiento de save
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacionActualizadaDatos);

        // Llamamos al método del servicio
        Notificacion result = notificacionService.guardarNotificacion(notificacionActualizadaDatos);

        // Verificamos el resultado
        assertNotNull(result);
        assertEquals(1L, result.getId(), "El ID debería ser el mismo");
        assertEquals("Actualización de Envío", result.getTipo(), "El tipo debería estar actualizado");
        assertEquals("Tu pedido #123 ha sido enviado.", result.getMensaje(), "El mensaje debería estar actualizado");
        assertEquals(usuario1.getId(), result.getUsuario().getId(), "El ID del usuario debería ser el mismo");

        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }
}