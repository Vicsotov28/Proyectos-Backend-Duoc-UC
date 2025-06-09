package com.hospital_vm.cl.hospital_vm.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "medico")
@Data
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false)
    private String especialidad;

    @Column(nullable = false)
    private String correo;
}
