package com.hospital_vm.cl.hospital_vm.service;

import com.hospital_vm.cl.hospital_vm.model.FichaPaciente;
import com.hospital_vm.cl.hospital_vm.repository.FichaPacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FichaPacienteService {

    @Autowired
    private FichaPacienteRepository fichaPacienteRepository;

    public FichaPaciente save(FichaPaciente fichaPaciente) {
        return fichaPacienteRepository.save(fichaPaciente);
    }

    public FichaPaciente findById(Long id) {
        return fichaPacienteRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        fichaPacienteRepository.deleteById(id);
    }
}
