package com.ecomarketspa.Service;

import com.ecomarketspa.Model.Producto;
import com.ecomarketspa.Repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    public List<Producto> buscarPorStockBajoUmbral(int stock) {
        return productoRepository.findByStockLessThan(stock);
    }

    public List<Producto> buscarPorRangoDePrecio(double minPrecio, double maxPrecio) {
        return productoRepository.findByPrecioBetween(minPrecio, maxPrecio);
    }
}