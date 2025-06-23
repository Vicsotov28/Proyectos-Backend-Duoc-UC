package com.ecomarketspa.Service;

import com.ecomarketspa.Model.Pedido;
import com.ecomarketspa.Repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido guardarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public void eliminarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }

    public List<Pedido> buscarPedidosPorEstado(String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public List<Pedido> buscarPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    public List<Pedido> buscarPedidosEntreFechas(Date startDate, Date endDate) {
        return pedidoRepository.findByFechaBetween(startDate, endDate);
    }

    public Long contarPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.countByUsuarioId(usuarioId);
    }
}
