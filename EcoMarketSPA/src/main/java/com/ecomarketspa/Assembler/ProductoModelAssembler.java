package com.ecomarketspa.Assembler;

import com.ecomarketspa.Controller.ProductoController; // Importa el controlador correcto
import com.ecomarketspa.Model.Producto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ProductoModelAssembler implements RepresentationModelAssembler<Producto, EntityModel<Producto>> {

    @Override
    public EntityModel<Producto> toModel(Producto producto) {
        return EntityModel.of(producto, //
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(producto.getId())).withSelfRel(), // Enlace a sí mismo
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos")); // Enlace a la colección de productos
    }
}
