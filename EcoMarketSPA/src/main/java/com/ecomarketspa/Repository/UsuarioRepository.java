package com.ecomarketspa.Repository;

import com.ecomarketspa.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {


    List<Usuario> findByNombreContainingIgnoreCase(String nombre); // 'Containing' para búsqueda parcial, 'IgnoreCase' para que no distinga mayúsculas/minúsculas

    List<Usuario> findByCorreoContainingIgnoreCase(String correo);

    @Query("SELECT DISTINCT u FROM Usuario u JOIN Pedido p ON u.id = p.usuario.id WHERE p.estado = :estadoPedido")

    List<Usuario> findUsuariosConPedidosPorEstado(@Param("estadoPedido") String estadoPedido);

}

