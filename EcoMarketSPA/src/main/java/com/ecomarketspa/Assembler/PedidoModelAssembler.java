package com.ecomarketspa.Assembler;

import com.ecomarketspa.Controller.PedidoController; // Asegúrate de importar el controlador correcto
import com.ecomarketspa.Model.Pedido;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PedidoModelAssembler implements RepresentationModelAssembler<Pedido, EntityModel<Pedido>> {

    @Override
    public EntityModel<Pedido> toModel(Pedido pedido) {
        return EntityModel.of(pedido, //
                linkTo(methodOn(PedidoController.class).obtenerPedidoPorId(pedido.getId())).withSelfRel(), // Enlace a sí mismo
                linkTo(methodOn(PedidoController.class).listarPedidos()).withRel("pedidos")); // Enlace a la colección de pedidos
    }
}
