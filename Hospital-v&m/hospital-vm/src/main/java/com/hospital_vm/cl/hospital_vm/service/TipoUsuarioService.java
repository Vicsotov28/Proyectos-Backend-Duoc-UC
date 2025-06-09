package com.hospital_vm.cl.hospital_vm.service;

import com.hospital_vm.cl.hospital_vm.model.TipoUsuario;
import com.hospital_vm.cl.hospital_vm.repository.TipoUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoUsuarioService {
    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    public List<TipoUsuario> findAll() {
        return tipoUsuarioRepository.findAll();
    }

    public TipoUsuario findById(Long id) {
        return tipoUsuarioRepository.findById(id).orElse(null);
    }

    public TipoUsuario save(TipoUsuario tipoUsuario) {
        return tipoUsuarioRepository.save(tipoUsuario);
    }

    public void delete(Long id) {
        tipoUsuarioRepository.deleteById(id);
    }
}