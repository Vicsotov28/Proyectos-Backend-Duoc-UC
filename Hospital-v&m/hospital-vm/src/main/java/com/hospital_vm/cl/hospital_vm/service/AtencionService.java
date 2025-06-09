package com.hospital_vm.cl.hospital_vm.service;

import com.hospital_vm.cl.hospital_vm.model.Atencion;
import java.util.List;
import java.util.Map;

public interface AtencionService {
    List<Atencion> findAll();
    Atencion findById(Long id);
    Atencion save(Atencion atencion);
    void delete(Long id);
    List<Atencion> findByPacienteId(Long pacienteId);
    List<Atencion> findByMedicoId(Long medicoId);
    List<Object[]> obtenerCostosPorTipoUsuario();
    Map<String, Long> contarAtencionesPorFecha();
}