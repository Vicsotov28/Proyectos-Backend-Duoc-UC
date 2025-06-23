package com.ecomarketspa.Repository;

import com.ecomarketspa.Model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEstado(String estado);

    List<Pedido> findByUsuarioId(Long usuarioId);

    List<Pedido> findByFechaBetween(Date startDate, Date endDate); // Nota: Nombre de campo 'fecha' en tu modelo

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.usuario.id = :usuarioId")

    Long countByUsuarioId(@Param("usuarioId") Long usuarioId);
}