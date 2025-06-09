package com.hospital_vm.cl.hospital_vm.repository;

import com.hospital_vm.cl.hospital_vm.model.Atencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtencionRepository extends JpaRepository<Atencion, Long> {
    List<Atencion> findByPacienteId(Long pacienteId);
    List<Atencion> findByMedicoId(Long medicoId);

    @Query("SELECT p.tipoUsuario.nombre, SUM(a.costo) " +
            "FROM Atencion a JOIN a.paciente p " +
            "GROUP BY p.tipoUsuario.nombre")
    List<Object[]> findCostosAgrupadosPorTipoUsuario();
}