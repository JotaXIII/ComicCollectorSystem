package comiccollector.util;

import comiccollector.servicios.ComicCollectorSystem;
import comiccollector.modelos.Usuario;
import comiccollector.excepciones.EmailYaRegistradoException;
import comiccollector.excepciones.RutInvalidoException;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * Submenú dedicado a la gestión de usuarios.
 * Permite registrar, listar y consultar historial de usuarios.
 */
public class MenuUsuarios {
    private final ComicCollectorSystem sistema;
    private final Scanner scanner;

    public MenuUsuarios(ComicCollectorSystem sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void menuUsuarios() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Menú Usuarios ---");
            System.out.println("1. Registrar usuario");
            System.out.println("2. Listar usuarios registrados");
            System.out.println("3. Ver historial de un usuario");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1":
                    registrarUsuario();
                    break;
                case "2":
                    listarUsuariosRegistrados();
                    break;
                case "3":
                    verHistorialUnificado();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    // Registra nuevo usuario con validaciones.
    private void registrarUsuario() {
        String rut;
        while (true) {
            rut = pedirDato("Ingrese RUT: ", false);
            try {
                sistema.getValidador().validarRut(rut);
                break;
            } catch (RutInvalidoException e) {
                System.out.println("ERROR " + e.getMessage());
            }
        }
        String nombre = pedirDato("Ingrese nombre: ", false);
        String email;
        while (true) {
            email = pedirDato("Ingrese email: ", false);
            try {
                sistema.getValidador().validarEmailUnico(email, sistema.getEmailsRegistrados());
                break;
            } catch (EmailYaRegistradoException e) {
                System.out.println("ERROR " + e.getMessage());
            }
        }
        String celular;
        while (true) {
            celular = pedirDato("Ingrese celular (8 dígitos): ", false);
            try {
                sistema.getValidador().validarCelular(celular);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("ERROR " + e.getMessage());
            }
        }

        System.out.println("\nResumen de usuario:");
        System.out.println("  Nombre : " + nombre);
        System.out.println("  RUT    : " + rut);
        System.out.println("  Correo : " + email);
        System.out.println("  Celular: " + celular);
        System.out.print("¿Confirma registrar este usuario? (s/n): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();
        if (!confirmacion.equals("s")) {
            System.out.println("Operación cancelada por el usuario.");
            pausar();
            return;
        }

        try {
            sistema.registrarUsuario(rut, nombre, email, celular);
            System.out.println("Usuario registrado correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
        }
        pausar();
    }

    // Lista de usuarios con detalle
    private void listarUsuariosRegistrados() {
        Collection<Usuario> usuarios = sistema.getUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
        } else {
            System.out.println("Lista de usuarios registrados:");
            System.out.printf("%-20s %-30s %-12s%n", "Nombre", "Correo", "Celular");
            System.out.println("-----------------------------------------------------------------");
            for (Usuario u : usuarios) {
                System.out.printf("%-20s %-30s %-12s%n", u.getNombre(), u.getEmail(), u.getCelular());
            }
        }
        pausar();
    }

    // Historial de usuarios compras y reservas.
    private void verHistorialUnificado() {
        String rut = pedirDato("Ingrese RUT del usuario: ", false);

        Usuario usuario = sistema.buscarUsuarioPorRut(rut);
        if (usuario == null) {
            System.out.println("Usuario no encontrado.");
            pausar();
            return;
        }
        List<?> reservas = usuario.getHistorialReservas();
        List<?> compras = usuario.getHistorialCompras();

        System.out.println("==== Historial del usuario ====");
        System.out.println("Datos del usuario:");
        System.out.println("  Nombre : " + usuario.getNombre());
        System.out.println("  RUT    : " + usuario.getRut());
        System.out.println("  Correo : " + usuario.getEmail());
        System.out.println("  Celular: " + usuario.getCelular());
        System.out.println(">>> Reservas / Preventas:");
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas/preventas.");
        } else {
            for (Object obj : reservas) {
                System.out.println(obj.toString());
            }
        }
        System.out.println(">>> Compras:");
        if (compras.isEmpty()) {
            System.out.println("No hay compras registradas.");
        } else {
            for (Object obj : compras) {
                System.out.println(obj.toString());
            }
        }
        pausar();
    }

    private String pedirDato(String mensaje, boolean permiteVacio) {
        String dato;
        do {
            System.out.print(mensaje);
            dato = scanner.nextLine().trim();
            if (!permiteVacio && dato.isEmpty()) {
                System.out.println("Este campo no puede estar vacío.");
            }
        } while (!permiteVacio && dato.isEmpty());
        return dato;
    }

    // Enter para continuar...
    private void pausar() {
        System.out.print("Presione Enter para continuar...");
        scanner.nextLine();
    }
}
