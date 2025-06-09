package com.hospital_vm.cl.hospital_vm.controller;

import com.hospital_vm.cl.hospital_vm.model.*;
import com.hospital_vm.cl.hospital_vm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/reportes")
public class ReportesController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private AtencionService atencionService;

    @Autowired
    private MedicoService medicoService;

    @GetMapping("/atenciones-por-paciente/{id}")
    public ResponseEntity<List<Atencion>> atencionesPorPaciente(@PathVariable Long id) {
        return ResponseEntity.ok(atencionService.findByPacienteId(id));
    }

    @GetMapping("/atenciones-por-medico/{id}")
    public ResponseEntity<List<Atencion>> atencionesPorMedico(@PathVariable Long id) {
        return ResponseEntity.ok(atencionService.findByMedicoId(id));
    }

    @GetMapping("/historial-paciente/{id}")
    public ResponseEntity<Map<String, Object>> historialPaciente(@PathVariable Long id) {
        Paciente paciente = pacienteService.findById(id);
        if (paciente == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("paciente", paciente);
        response.put("ficha", paciente.getFichaPaciente());
        response.put("atenciones", atencionService.findByPacienteId(id));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/medicos-especialidades")
    public ResponseEntity<List<Medico>> listarMedicosConEspecialidades() {
        return ResponseEntity.ok(medicoService.findAll());
    }

    @GetMapping("/costos-por-tipo-usuario")
    public ResponseEntity<List<Object[]>> obtenerCostosPorTipo() {
        return ResponseEntity.ok(atencionService.obtenerCostosPorTipoUsuario());
    }

    @GetMapping("/atenciones-por-fecha")
    public ResponseEntity<Map<String, Long>> atencionesAgrupadasPorFecha() {
        return ResponseEntity.ok(atencionService.contarAtencionesPorFecha());
    }
}