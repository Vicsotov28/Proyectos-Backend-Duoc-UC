# EcoMarket SPA – Sistema de Microservicios para Comercio Electrónico

Proyecto desarrollado como parte del curso **Desarrollo Full Stack I** (Duoc UC), cuyo objetivo es modernizar la infraestructura tecnológica de la empresa EcoMarket SPA a través de una arquitectura distribuida basada en microservicios.

## 🧩 Descripción General

El sistema fue construido usando **Spring Boot**, **Java**, **Maven**, y una base de datos **MySQL**. Se implementaron los siguientes microservicios:

- **Usuarios:** Registro y gestión de usuarios.
- **Productos:** Administración del catálogo de productos ecológicos.
- **Pedidos:** Registro y seguimiento de pedidos por usuario.
- **Notificaciones:** Envío de mensajes personalizados (email, SMS, etc).

Cada microservicio está diseñado con arquitectura en capas (`Model`, `Repository`, `Service`, `Controller`) para mantener el código modular, mantenible y escalable.

## 🧱 Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Maven
- Postman (para pruebas)
- GitHub Actions (CI/CD)

## 🧪 Pruebas realizadas

- Todos los endpoints fueron validados con **Postman**.
- Se confirmó el correcto manejo de relaciones (`@ManyToOne`) entre entidades como Pedido y Usuario, Notificación y Usuario.
- La base de datos fue validada con **MySQL Workbench**.

## ⚙️ CI/CD

Se implementó integración continua (CI) con **GitHub Actions** usando un archivo de configuración `.github/workflows/maven.yml` que ejecuta `mvn clean package` en cada `push`.

## 📌 Conclusión

Este sistema representa un avance importante hacia la modernización del backend de EcoMarket SPA, permitiendo escalabilidad, mantenibilidad y preparación para despliegue en producción o nube.



