package com.ecomarketspa.Assembler;

import com.ecomarketspa.Controller.UsuarioController; // Importa el controlador correcto
import com.ecomarketspa.Model.Usuario;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(usuario, //
                linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(usuario.getId())).withSelfRel(), // Enlace a sí mismo
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("usuarios")); // Enlace a la colección de usuarios
    }
}