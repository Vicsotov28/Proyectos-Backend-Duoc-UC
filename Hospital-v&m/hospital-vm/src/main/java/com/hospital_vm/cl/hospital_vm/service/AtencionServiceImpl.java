package com.hospital_vm.cl.hospital_vm.service;

import com.hospital_vm.cl.hospital_vm.model.Atencion;
import com.hospital_vm.cl.hospital_vm.repository.AtencionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AtencionServiceImpl implements AtencionService {

    @Autowired
    private AtencionRepository atencionRepository;

    @Override
    public List<Atencion> findAll() {
        return atencionRepository.findAll();
    }

    @Override
    public Atencion findById(Long id) {
        return atencionRepository.findById(id).orElse(null);
    }

    @Override
    public Atencion save(Atencion atencion) {
        return atencionRepository.save(atencion);
    }

    @Override
    public void delete(Long id) {
        atencionRepository.deleteById(id);
    }

    @Override
    public List<Atencion> findByPacienteId(Long pacienteId) {
        return atencionRepository.findByPacienteId(pacienteId);
    }

    @Override
    public List<Atencion> findByMedicoId(Long medicoId) {
        return atencionRepository.findByMedicoId(medicoId);
    }

    @Override
    public List<Object[]> obtenerCostosPorTipoUsuario() {
        return atencionRepository.findCostosAgrupadosPorTipoUsuario();
    }

    @Override
    public Map<String, Long> contarAtencionesPorFecha() {
        return atencionRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        a -> a.getFecha().toString(),
                        Collectors.counting()
                ));
    }
}