package com.hospital_vm.cl.hospital_vm.service;

import com.hospital_vm.cl.hospital_vm.model.Paciente;
import com.hospital_vm.cl.hospital_vm.repository.PacienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;


    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }


    public Paciente findById(long id) {
        return pacienteRepository.findById(id).orElse(null);
    }


    public Paciente save(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }


    public void delete(long id) {
        pacienteRepository.deleteById(id);
    }

    public Paciente findByRun(String run) {
        return pacienteRepository.findByRun(run);
    }
}
