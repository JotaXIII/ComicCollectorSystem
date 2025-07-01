package comiccollector.util;

import comiccollector.servicios.ComicCollectorSystem;
import comiccollector.modelos.Comic;
import comiccollector.modelos.Usuario;
import comiccollector.excepciones.ProductoYaReservadoException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Submenú para operaciones del sistema:
 * Realizar compras, reservas/preventas, ver lanzamientos y ranking de usuarios.
 */
public class MenuOperaciones {
    private final ComicCollectorSystem sistema;
    private final Scanner scanner;

    public MenuOperaciones(ComicCollectorSystem sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void menuOperaciones() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Menú Operaciones ---");
            System.out.println("1. Realizar una compra");
            System.out.println("2. Hacer una reserva/preventa");
            System.out.println("3. Ver fechas de lanzamiento futuras");
            System.out.println("4. Ver ranking de usuarios por compras");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1":
                    realizarCompra();
                    break;
                case "2":
                    hacerReserva();
                    break;
                case "3":
                    mostrarFechasLanzamiento();
                    break;
                case "4":
                    mostrarRankingUsuarios();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void realizarCompra() {
        String rut;
        while (true) {
            rut = pedirDato("Ingrese RUT del usuario: ", false);
            if (sistema.buscarUsuarioPorRut(rut) == null) {
                System.out.println("ERROR. Usuario no encontrado. Intente nuevamente.");
            } else {
                break;
            }
        }
        Comic comic = buscarComicInteractivo();
        int cantidad = pedirDatoEntero("Ingrese cantidad a comprar: ", 1, comic.getCantidadDisponible());

        Usuario usuario = sistema.buscarUsuarioPorRut(rut);
        System.out.println("\nResumen de la operación:");
        System.out.println("Datos del usuario:");
        System.out.println("  Nombre : " + usuario.getNombre());
        System.out.println("  RUT    : " + usuario.getRut());
        System.out.println("  Correo : " + usuario.getEmail());
        System.out.println("  Celular: " + usuario.getCelular());
        System.out.println("Producto: " + comic.getNombre() + " (" + comic.getCodigo() + ")");
        System.out.println("Cantidad: " + cantidad);
        System.out.println("Total: $" + (comic.getPrecio() * cantidad));
        System.out.print("¿Confirma la operación? (s/n): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();
        if (!confirmacion.equals("s")) {
            System.out.println("Operación cancelada por el usuario.");
            pausar();
            return;
        }
        try {
            sistema.realizarCompra(rut, comic.getCodigo(), cantidad);
            System.out.println("Compra realizada correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR. " + e.getMessage());
        }
        pausar();
    }

    // Reserva de producto en preventa. Validaciones y confirmación.
    private void hacerReserva() {
        String rut;
        while (true) {
            rut = pedirDato("Ingrese RUT del usuario: ", false);
            if (sistema.buscarUsuarioPorRut(rut) == null) {
                System.out.println("Usuario no encontrado. Intente nuevamente.");
            } else {
                break;
            }
        }
        Comic comic = buscarComicInteractivo();
        int maxReservable = comic.getCantidadDisponible();
        if (maxReservable == 0) {
            System.out.println("No hay stock disponible para reservar este producto.");
            pausar();
            return;
        }
        int cantidadReservar = pedirDatoEntero("Ingrese cantidad a reservar: ", 1, maxReservable);

        Usuario usuario = sistema.buscarUsuarioPorRut(rut);
        System.out.println("\nResumen de la operación:");
        System.out.println("Datos del usuario:");
        System.out.println("  Nombre : " + usuario.getNombre());
        System.out.println("  RUT    : " + usuario.getRut());
        System.out.println("  Correo : " + usuario.getEmail());
        System.out.println("  Celular: " + usuario.getCelular());
        System.out.println("Producto: " + comic.getNombre() + " (" + comic.getCodigo() + ")");
        System.out.println("Cantidad a reservar: " + cantidadReservar);
        String fechaLlegadaResumen = (comic.getFechaLlegada() == null || !comic.getFechaLlegada().isAfter(LocalDate.now()))
                ? "En Tienda"
                : formatearFecha(comic.getFechaLlegada());
        System.out.println("Fecha de llegada: " + fechaLlegadaResumen);
        System.out.print("¿Confirma la operación? (s/n): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();
        if (!confirmacion.equals("s")) {
            System.out.println("Operación cancelada por el usuario.");
            pausar();
            return;
        }
        try {
            sistema.hacerReserva(rut, comic.getCodigo(), cantidadReservar);
            System.out.println("Reserva/preventa realizada correctamente.");
        } catch (ProductoYaReservadoException | IllegalArgumentException e) {
            System.out.println("ERROR. " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado al hacer la reserva.");
        }
        pausar();
    }

    // Lista de preventa
    private void mostrarFechasLanzamiento() {
        List<Comic> lanzamientos = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        for (Comic c : sistema.getComics()) {
            if (c.getFechaLlegada() != null && c.getFechaLlegada().isAfter(hoy)) {
                lanzamientos.add(c);
            }
        }
        if (lanzamientos.isEmpty()) {
            System.out.println("No hay lanzamientos futuros registrados.");
        } else {
            System.out.println("Próximos lanzamientos:");
            lanzamientos.sort((a, b) -> a.getFechaLlegada().compareTo(b.getFechaLlegada()));
            for (Comic c : lanzamientos) {
                System.out.println("Fecha: " + formatearFecha(c.getFechaLlegada())
                        + " | Código: " + c.getCodigo()
                        + " | Tipo: " + c.getTipo()
                        + " | Nombre: " + c.getNombre()
                        + " | Autor/Fabricante: " + c.getAutorOFabricante()
                        + " | Cantidad: " + c.getCantidadDisponible());
            }
        }
        pausar();
    }

    // Ranking según compras realizadas.
    private void mostrarRankingUsuarios() {
        List<Usuario> usuarios = new ArrayList<>(sistema.getUsuarios());
        usuarios.sort((a, b) -> Integer.compare(b.totalCompras(), a.totalCompras()));
        System.out.println("Ranking de usuarios por compras:");
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);
            System.out.printf("[%d] %s (RUT: %s) - Total comprados: %d%n", i+1, u.getNombre(), u.getRut(), u.totalCompras());
        }
        pausar();
    }

    // Métodos auxiliares

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

    private int pedirDatoEntero(String mensaje, int min, int max) {
        int valor;
        while (true) {
            try {
                System.out.print(mensaje);
                valor = Integer.parseInt(scanner.nextLine().trim());
                if (valor < min || valor > max) {
                    System.out.println("El valor debe estar entre " + min + " y " + max + ".");
                } else {
                    return valor;
                }
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }
    }

    private Comic buscarComicInteractivo() {
        while (true) {
            String criterio = pedirDato("Buscar por [1] Código, [2] Nombre, [3] Autor/Fabricante: ", false);
            List<Comic> coincidencias = new ArrayList<>();
            if (criterio.equals("1")) {
                String codigo = pedirDato("Ingrese código: ", false);
                Comic c = sistema.buscarComicPorCodigo(codigo);
                if (c != null) coincidencias.add(c);
            } else if (criterio.equals("2")) {
                String nombre = pedirDato("Ingrese nombre: ", false);
                for (Comic c : sistema.getComics()) {
                    if (c.getNombre().equalsIgnoreCase(nombre)) coincidencias.add(c);
                }
            } else if (criterio.equals("3")) {
                String autor = pedirDato("Ingrese autor o fabricante: ", false);
                for (Comic c : sistema.getComics()) {
                    if (c.getAutorOFabricante().equalsIgnoreCase(autor)) coincidencias.add(c);
                }
            }
            if (coincidencias.isEmpty()) {
                System.out.println("No se encontró producto. Intente nuevamente.");
            } else if (coincidencias.size() == 1) {
                return coincidencias.get(0);
            } else {
                System.out.println("Se encontraron varios productos:");
                for (int i = 0; i < coincidencias.size(); i++) {
                    Comic c = coincidencias.get(i);
                    System.out.printf("[%d] %s | %s | %s | Cantidad: %d%n", i+1, c.getCodigo(), c.getNombre(), c.getAutorOFabricante(), c.getCantidadDisponible());
                }
                int opcion = pedirDatoEntero("Seleccione el número del producto: ", 1, coincidencias.size());
                return coincidencias.get(opcion-1);
            }
        }
    }

    private void pausar() {
        System.out.print("Presione Enter para continuar...");
        scanner.nextLine();
    }

    private static String formatearFecha(LocalDate fecha) {
        if (fecha == null) return "-";
        return fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
