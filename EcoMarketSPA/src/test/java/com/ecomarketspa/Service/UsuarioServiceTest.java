package com.ecomarketspa.Service;

import com.ecomarketspa.Model.Usuario;
import com.ecomarketspa.Repository.UsuarioRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

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
    @DisplayName("Test para listar todos los usuarios")
    void testListarUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario1, usuario2));
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        assertNotNull(usuarios, "La lista de usuarios no debería ser nula");
        assertEquals(2, usuarios.size(), "La lista debería contener 2 usuarios");
        assertTrue(usuarios.contains(usuario1), "La lista debería contener a usuario1");
        assertTrue(usuarios.contains(usuario2), "La lista debería contener a usuario2");
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test para guardar un nuevo usuario")
    void testGuardarUsuario() {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre("Pedro Gomez");
        nuevoUsuario.setCorreo("pedro.gomez@example.com");
        nuevoUsuario.setTelefono("955554444");

        // Cuando se llama a save con cualquier Usuario, retorna usuario1 con ID
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario1);

        Usuario usuarioGuardado = usuarioService.guardarUsuario(nuevoUsuario);

        assertNotNull(usuarioGuardado, "El usuario guardado no debería ser nulo");
        assertEquals(1L, usuarioGuardado.getId(), "El ID del usuario guardado debería ser 1");
        assertEquals("Vicente Soto", usuarioGuardado.getNombre(), "El nombre del usuario guardado debería ser Vicente Soto");
        assertEquals("vicente.soto@example.com", usuarioGuardado.getCorreo(), "El correo del usuario guardado debería ser vicente.soto@example.com");

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Test para obtener un usuario por ID existente")
    void testObtenerUsuarioPorIdExistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario1));
        Optional<Usuario> usuarioEncontrado = usuarioService.obtenerUsuarioPorId(1L);
        assertTrue(usuarioEncontrado.isPresent(), "Se debería encontrar el usuario");
        assertEquals(usuario1.getNombre(), usuarioEncontrado.get().getNombre(), "El nombre del usuario debe coincidir");
        assertEquals(usuario1.getCorreo(), usuarioEncontrado.get().getCorreo(), "El correo del usuario debe coincidir");
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test para obtener un usuario por ID no existente")
    void testObtenerUsuarioPorIdNoExistente() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        Optional<Usuario> usuarioEncontrado = usuarioService.obtenerUsuarioPorId(99L);
        assertFalse(usuarioEncontrado.isPresent(), "No se debería encontrar el usuario");
        verify(usuarioRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Test para eliminar un usuario existente")
    void testEliminarUsuario() {
        doNothing().when(usuarioRepository).deleteById(1L);
        usuarioService.eliminarUsuario(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test para actualizar un usuario")
    void testActualizarUsuario() {
        // Objeto con los datos que simulan una actualización
        Usuario usuarioActualizadoDatos = new Usuario();
        usuarioActualizadoDatos.setId(1L); // Es crucial que tenga un ID para que Spring JPA lo trate como actualización
        usuarioActualizadoDatos.setNombre("Vicente Soto Actualizado");
        usuarioActualizadoDatos.setCorreo("vicente.actualizado@example.com");
        usuarioActualizadoDatos.setTelefono("999999999");

        // Mockeamos el comportamiento de save: cuando se le pase cualquier usuario,
        // queremos que devuelva el 'usuarioActualizadoDatos'
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActualizadoDatos);

        // Llamamos al método del servicio
        Usuario result = usuarioService.guardarUsuario(usuarioActualizadoDatos);

        // Verificamos el resultado
        assertNotNull(result);
        assertEquals(1L, result.getId(), "El ID debería ser el mismo");
        assertEquals("Vicente Soto Actualizado", result.getNombre(), "El nombre debería estar actualizado");
        assertEquals("vicente.actualizado@example.com", result.getCorreo(), "El correo debería estar actualizado");
        assertEquals("999999999", result.getTelefono(), "El teléfono debería estar actualizado");

        // Verificamos que el método save fue llamado una vez con cualquier objeto Usuario.
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}