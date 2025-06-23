package com.ecomarketspa.Repository;

import com.ecomarketspa.Model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoria(String categoria);

    List<Producto> findByStockLessThan(int stock);

    List<Producto> findByPrecioBetween(double minPrecio, double maxPrecio);
}