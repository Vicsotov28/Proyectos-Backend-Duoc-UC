# Hospital V&M – Sistema de Gestión Clínica

Proyecto backend desarrollado para **Hospital Vida y Meditación** (Puerto Montt), como parte del curso **Desarrollo Full Stack I** (Duoc UC), enfocado en optimizar la gestión de pacientes, médicos y atenciones.

## 🧩 Objetivo

Implementar un sistema capaz de:

- Registrar y administrar pacientes, médicos y atenciones.
- Integrar terapias naturales a los tratamientos.
- Generar reportes clínicos personalizados (historiales, agendamientos, costos, etc).

## 🧱 Tecnologías utilizadas

- Java 21
- Spring Boot
- JPA / Hibernate
- Base de datos H2 (pruebas) y configuración para MySQL
- Postman
- IntelliJ IDEA

## 🧪 Funcionalidades principales

- CRUD de Pacientes, Médicos y Atenciones
- Relaciones `@OneToMany`, `@ManyToOne`, `@OneToOne`
- Reportes personalizados con consultas `@Query`
- Validación de integridad en la base de datos
- Manejo adecuado de errores (HTTP 400, 404, etc)

## 📊 Reportes implementados

1. Atenciones por paciente y médico
2. Historial clínico completo
3. Costos agrupados por tipo de usuario
4. Especialidades médicas
5. Agendamiento por fecha

## 🧪 Pruebas

- Validación completa con **Postman**
- Verificación en consola **H2**
- Datos de prueba: 5 pacientes, 3 médicos, 10 atenciones

## 📌 Conclusión

Este sistema robusto demuestra cómo aplicar relaciones JPA, diseño REST y consultas avanzadas para crear una solución realista y escalable en el área de salud.

