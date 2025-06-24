🛒 EcoMarket SPA – Backend REST API
Proyecto desarrollado en Java usando Spring Boot para la gestión de usuarios, productos, pedidos y notificaciones. Este sistema representa el backend de una plataforma de comercio electrónico y fue creado como parte de la Evaluación Parcial 3 del ramo Desarrollo Fullstack I en Duoc UC.

🚀 Tecnologías utilizadas
Java 17

Spring Boot 3.2.x

Spring Web

Spring Data JPA

MySQL

Springdoc OpenAPI (Swagger)

Spring HATEOAS

JUnit 5 + Mockito

Maven

📦 Arquitectura
El proyecto tiene una estructura modular monolítica, organizada en capas:


├── controller       # Controladores REST
├── service          # Lógica de negocio
├── repository       # Acceso a base de datos
├── model            # Entidades JPA
├── assembler        # Clases HATEOAS
└── config           # Configuraciones adicionales

🧪 Pruebas

Se utilizaron JUnit 5 y Mockito para validar el comportamiento de los servicios.

Las pruebas de integración se ejecutaron con Spring Boot Test.

Las pruebas funcionales se realizaron en Postman, verificando todos los endpoints.

🔗 Documentación Swagger

Disponible en:
http://localhost:8080/swagger-ui.html

Permite visualizar, explorar y probar los endpoints de la API siguiendo el estándar OpenAPI.

🌐 HATEOAS

La API implementa HATEOAS, enriqueciendo las respuestas con enlaces _links para facilitar la navegación desde el cliente.

Ejemplo de respuesta:
{
  "id": 1,
  "fecha": "2025-06-20",
  "estado": "PENDIENTE",
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/v2/pedidos/1"
    },
    "pedidos": {
      "href": "http://localhost:8080/api/v2/pedidos"
    }
  }
}

⚙️ Configuración

En el archivo application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/db_ecomarket
spring.datasource.username=root
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update

📌 Cómo ejecutar el proyecto

Clona el repositorio:

git clone https://github.com/Vicsotov28/EcoMarketSPA.git

Importa el proyecto en tu IDE (IntelliJ IDEA, NetBeans, etc.).

Asegúrate de tener MySQL corriendo con la base de datos creada.

Ejecuta la clase EcoMarketSpaApplication.java.

Accede a la documentación en:

http://localhost:8080/swagger-ui.html

👨‍🎓 Autores

Vicente Soto, Javier Rojas, Jesus Garcia
Estudiantes de Ingeniería en Informática
Duoc UC – Sede Antonio Varas
Sección 011V
2025

