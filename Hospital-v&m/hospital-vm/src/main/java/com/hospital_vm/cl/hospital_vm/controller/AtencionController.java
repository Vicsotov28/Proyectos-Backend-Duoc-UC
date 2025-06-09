package com.hospital_vm.cl.hospital_vm.controller;

import com.hospital_vm.cl.hospital_vm.model.Atencion;
import com.hospital_vm.cl.hospital_vm.service.AtencionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atenciones")
public class AtencionController {

    @Autowired
    private AtencionService atencionService;

    @GetMapping
    public ResponseEntity<List<Atencion>> getAllAtenciones() {
        return ResponseEntity.ok(atencionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atencion> getAtencionById(@PathVariable Long id) {
        Atencion atencion = atencionService.findById(id);
        return atencion != null ? ResponseEntity.ok(atencion) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Atencion> createAtencion(@RequestBody Atencion atencion) {
        return ResponseEntity.status(201).body(atencionService.save(atencion));
    }

    @GetMapping("/por-paciente/{pacienteId}")
    public ResponseEntity<List<Atencion>> getAtencionesByPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(atencionService.findByPacienteId(pacienteId));
    }

    @GetMapping("/por-medico/{medicoId}")
    public ResponseEntity<List<Atencion>> getAtencionesByMedico(@PathVariable Long medicoId) {
        return ResponseEntity.ok(atencionService.findByMedicoId(medicoId));
    }
}