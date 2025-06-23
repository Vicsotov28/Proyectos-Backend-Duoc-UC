package com.ecomarketspa.Service;


import com.ecomarketspa.Model.Usuario;
import com.ecomarketspa.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    // --- Nuevos métodos de servicio para HATEOAS ---

    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreoContainingIgnoreCase(correo);
    }

    public List<Usuario> buscarUsuariosConPedidosPendientes() {
        return usuarioRepository.findUsuariosConPedidosPorEstado("PENDIENTE"); // Aquí fijamos el estado a "PENDIENTE"
    }
}
