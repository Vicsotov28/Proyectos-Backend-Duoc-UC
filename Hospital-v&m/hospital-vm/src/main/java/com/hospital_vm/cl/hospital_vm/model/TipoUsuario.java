package com.hospital_vm.cl.hospital_vm.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "tipo_usuario")
@Data
public class TipoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;
}
