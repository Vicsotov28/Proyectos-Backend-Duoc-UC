package com.ecomarketspa;

import com.ecomarketspa.Model.Usuario;
import com.ecomarketspa.Model.Producto;
import com.ecomarketspa.Model.Pedido;
import com.ecomarketspa.Model.Notificacion;
import com.ecomarketspa.Repository.UsuarioRepository;
import com.ecomarketspa.Repository.ProductoRepository;
import com.ecomarketspa.Repository.PedidoRepository;
import com.ecomarketspa.Repository.NotificacionRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Profile("dev")
@Component // Marca esta clase como un componente Spring para que sea detectado e inyectado
public class DataLoader implements CommandLineRunner { // Implementa CommandLineRunner para ejecutar al inicio

    // Inyectamos los repositorios directamente con @Autowired
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private NotificacionRepository notificacionRepository;

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker(new Locale("es", "CL"));
        Random random = new Random();


        if (usuarioRepository.count() == 0) {
            System.out.println("Generando usuarios de prueba...");
            for (int i = 0; i < 10; i++) {
                Usuario usuario = new Usuario();
                usuario.setNombre(faker.name().fullName());
                usuario.setCorreo(faker.internet().emailAddress());
                usuario.setTelefono(faker.phoneNumber().phoneNumber());
                usuarioRepository.save(usuario);
                System.out.println("Usuario generado: " + usuario.getNombre() + " - " + usuario.getCorreo());
            }
            System.out.println("Usuarios de prueba generados exitosamente.");
        } else {
            System.out.println("La tabla de usuarios ya contiene datos, omitiendo la generación de usuarios.");
        }

        List<Usuario> usuarios = usuarioRepository.findAll();

        // --- Generar Productos ---
        if (productoRepository.count() == 0) {
            System.out.println("Generando productos de prueba...");
            for (int i = 0; i < 20; i++) { // Generamos 20 productos
                Producto producto = new Producto();
                producto.setNombre(faker.commerce().productName());
                producto.setCategoria(faker.commerce().department());
                producto.setPrecio(faker.number().randomDouble(2, 1000, 100000));
                producto.setStock(faker.number().numberBetween(1, 100));
                producto.setDescripcion(faker.lorem().sentence());
                productoRepository.save(producto);
                System.out.println("Producto generado: " + producto.getNombre() + " - $" + String.format("%.2f", producto.getPrecio()));
            }
            System.out.println("Productos de prueba generados exitosamente.");
        } else {
            System.out.println("La tabla de productos ya contiene datos, omitiendo la generación de productos.");
        }

        // --- Generar Pedidos ---
        if (pedidoRepository.count() == 0 && !usuarios.isEmpty()) {
            System.out.println("Generando pedidos de prueba...");
            String[] estadosPedido = {"Pendiente", "Procesando", "Enviado", "Entregado", "Cancelado"};
            for (int i = 0; i < 15; i++) { // Generamos 15 pedidos
                Pedido pedido = new Pedido();
                pedido.setEstado(estadosPedido[random.nextInt(estadosPedido.length)]);
                // El método past() de datafaker ya devuelve un java.util.Date
                pedido.setFecha(faker.date().past(30, TimeUnit.DAYS));
                pedido.setTotal(faker.number().randomDouble(2, 5000, 500000));
                pedido.setUsuario(usuarios.get(random.nextInt(usuarios.size())));
                pedidoRepository.save(pedido);
                System.out.println("Pedido generado: #" + pedido.getId() + " - Estado: " + pedido.getEstado() + " - Usuario: " + pedido.getUsuario().getNombre());
            }
            System.out.println("Pedidos de prueba generados exitosamente.");
        } else if (pedidoRepository.count() == 0 && usuarios.isEmpty()) {
            System.out.println("No hay usuarios disponibles para generar pedidos. Asegúrate de que se generen usuarios primero.");
        } else {
            System.out.println("La tabla de pedidos ya contiene datos, omitiendo la generación de pedidos.");
        }

        // --- Generar Notificaciones ---
        if (notificacionRepository.count() == 0 && !usuarios.isEmpty()) {
            System.out.println("Generando notificaciones de prueba...");
            String[] tiposNotificacion = {"Email", "SMS", "App"};
            for (int i = 0; i < 25; i++) { // Generamos 25 notificaciones
                Notificacion notificacion = new Notificacion();
                notificacion.setTipo(tiposNotificacion[random.nextInt(tiposNotificacion.length)]);
                notificacion.setMensaje(faker.lorem().sentence());
                notificacion.setUsuario(usuarios.get(random.nextInt(usuarios.size())));
                notificacionRepository.save(notificacion);
                System.out.println("Notificacion generada para: " + notificacion.getUsuario().getNombre() + " - Tipo: " + notificacion.getTipo());
            }
            System.out.println("Notificaciones de prueba generadas exitosamente.");
        } else if (notificacionRepository.count() == 0 && usuarios.isEmpty()) {
            System.out.println("No hay usuarios disponibles para generar notificaciones. Asegúrate de que se generen usuarios primero.");
        } else {
            System.out.println("La tabla de notificaciones ya contiene datos, omitiendo la generación de notificaciones.");
        }
    }
}