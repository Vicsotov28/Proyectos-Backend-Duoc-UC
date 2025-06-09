package com.hospital_vm.cl.hospital_vm.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "ficha_paciente")
@Data
public class FichaPaciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String alergias;

    @Column(nullable = false)
    private String enfermedadesCronicas;

    @Column(nullable = false)
    private String medicamentos;

    @OneToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;
}
