package comiccollector.util;

import comiccollector.servicios.ComicCollectorSystem;
import comiccollector.modelos.Comic;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Submenú dedicado a la gestión de productos.
 * Permite agregar, eliminar y listar productos del inventario.
 */
public class MenuProductos {
    private final ComicCollectorSystem sistema;
    private final Scanner scanner;

    public MenuProductos(ComicCollectorSystem sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void menuProductos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Menú Productos ---");
            System.out.println("1. Agregar producto");
            System.out.println("2. Eliminar producto");
            System.out.println("3. Listar inventario");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1":
                    agregarComic();
                    break;
                case "2":
                    eliminarComic();
                    break;
                case "3":
                    listarProductosInventario();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    // Agrega un nuevo producto con validaciones y confirmación.
    private void agregarComic() {
        String tipo = seleccionarTipoProducto();
        String nombre = pedirDato("Ingrese nombre: ", false);
        String autor = pedirDato("Ingrese autor o fabricante: ", false);
        int cantidad = pedirDatoEntero("Ingrese cantidad disponible: ", 0, Integer.MAX_VALUE);

        String esPreventa;
        LocalDate fechaLlegada = null;
        while (true) {
            esPreventa = pedirDato("¿Es preventa? (s/n): ", false).trim().toLowerCase();
            if (esPreventa.equals("s") || esPreventa.equals("n")) break;
            System.out.println("Debe ingresar 's' para sí o 'n' para no.");
        }
        if (esPreventa.equals("s")) {
            fechaLlegada = pedirFecha("Ingrese fecha de llegada (DD-MM-AAAA): ");
        }
        double precio = pedirDatoDouble("Ingrese precio: ", 0, Double.MAX_VALUE);

        System.out.println("\nResumen de nuevo producto:");
        System.out.println("Tipo: " + tipo);
        System.out.println("Nombre: " + nombre);
        System.out.println("Autor/Fabricante: " + autor);
        System.out.println("Cantidad: " + cantidad);
        System.out.println("Fecha llegada: " + (fechaLlegada == null ? "En Tienda" : formatearFecha(fechaLlegada)));
        System.out.println("Precio: " + precio);
        System.out.print("¿Confirma agregar este producto? (s/n): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();
        if (!confirmacion.equals("s")) {
            System.out.println("Operación cancelada por el usuario.");
            pausar();
            return;
        }

        try {
            Comic nuevoComic = new Comic(null, tipo, nombre, autor, cantidad, fechaLlegada, precio);
            sistema.agregarComic(nuevoComic);
            System.out.println("Producto agregado correctamente. Código asignado: " + nuevoComic.getCodigo());
        } catch (Exception e) {
            System.out.println("ERROR. " + e.getMessage());
        }
        pausar();
    }

    // Eliminar un producto tras confirmación.
    private void eliminarComic() {
        String tipo = seleccionarTipoProducto();
        Comic comic = buscarComicInteractivoPorTipo(tipo);
        if (comic == null) {
            System.out.println("No se encontró producto de tipo seleccionado.");
            pausar();
            return;
        }
        System.out.println("\nResumen de eliminación:");
        System.out.println("Tipo: " + comic.getTipo());
        System.out.println("Producto: " + comic.getNombre() + " (" + comic.getCodigo() + ")");
        System.out.print("¿Confirma eliminar este producto? (s/n): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();
        if (!confirmacion.equals("s")) {
            System.out.println("Operación cancelada por el usuario.");
            pausar();
            return;
        }
        boolean eliminado = sistema.eliminarComic(comic.getCodigo());
        if (eliminado) {
            System.out.println("Producto eliminado correctamente.");
        } else {
            System.out.println("No se pudo eliminar el producto.");
        }
        pausar();
    }

    // Lista de productos en inventario
    private void listarProductosInventario() {
        List<Comic> lista = new ArrayList<>(sistema.getComics());
        if (lista.isEmpty()) {
            System.out.println("No hay productos en inventario.");
        } else {
            lista.sort((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()));
            System.out.println("Inventario actual:");
            System.out.printf("%-5s\t%-14s\t%-20s\t%-22s\t%-8s\t%-13s\t%-8s%n",
                    "Cod", "Tipo", "Nombre", "Autor/Fab.", "Cantidad", "Fecha llegada", "Precio");
            for (Comic c : lista) {
                String fechaStr;
                if (c.getCantidadDisponible() == 0) {
                    fechaStr = "Agotado";
                } else if (c.getFechaLlegada() == null || !c.getFechaLlegada().isAfter(LocalDate.now())) {
                    fechaStr = "En Tienda";
                } else {
                    fechaStr = formatearFecha(c.getFechaLlegada());
                }
                System.out.printf("%-5s\t%-14s\t%-20s\t%-22s\t%-8d\t%-13s\t%-8.2f%n",
                        c.getCodigo(), c.getTipo(), c.getNombre(), c.getAutorOFabricante(),
                        c.getCantidadDisponible(), fechaStr, c.getPrecio());
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

    private double pedirDatoDouble(String mensaje, double min, double max) {
        double valor;
        while (true) {
            try {
                System.out.print(mensaje);
                valor = Double.parseDouble(scanner.nextLine().trim());
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

    private LocalDate pedirFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String fechaStr = scanner.nextLine().trim();
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                return LocalDate.parse(fechaStr, formatter);
            } catch (Exception e) {
                System.out.println("Ingrese una fecha válida (ejemplo: 01-11-2025).");
            }
        }
    }

    private void pausar() {
        System.out.print("Presione Enter para continuar...");
        scanner.nextLine();
    }

    private String seleccionarTipoProducto() {
        String[] tipos = {"Cómic", "Novela gráfica", "Manga", "Coleccionable"};
        System.out.println("Seleccione tipo de producto:");
        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i + 1) + ". " + tipos[i]);
        }
        int opcion = pedirDatoEntero("Opción: ", 1, tipos.length);
        return tipos[opcion - 1];
    }

    private Comic buscarComicInteractivoPorTipo(String tipo) {
        List<Comic> coincidencias = new ArrayList<>();
        for (Comic c : sistema.getComics()) {
            if (c.getTipo().equalsIgnoreCase(tipo)) {
                coincidencias.add(c);
            }
        }
        if (coincidencias.isEmpty()) {
            return null;
        }
        if (coincidencias.size() == 1) {
            return coincidencias.get(0);
        }
        System.out.println("Se encontraron varios productos:");
        for (int i = 0; i < coincidencias.size(); i++) {
            Comic c = coincidencias.get(i);
            System.out.printf("[%d] %s | %s | %s | Cantidad: %d%n", i+1, c.getCodigo(), c.getNombre(), c.getAutorOFabricante(), c.getCantidadDisponible());
        }
        int opcion = pedirDatoEntero("Seleccione el número del producto: ", 1, coincidencias.size());
        return coincidencias.get(opcion-1);
    }

    private static String formatearFecha(LocalDate fecha) {
        if (fecha == null) return "-";
        return fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
