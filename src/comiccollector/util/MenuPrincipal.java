package comiccollector.util;

import comiccollector.servicios.ComicCollectorSystem;
import java.util.Scanner;

/**
 * Menú principal: muestra las opciones generales y deriva a los sub-menús
 * de usuarios, productos y operaciones.
 */
public class MenuPrincipal {

    private final ComicCollectorSystem sistema;
    private final Scanner scanner;
    private final MenuUsuarios menuUsuarios;
    private final MenuProductos menuProductos;
    private final MenuOperaciones menuOperaciones;

    public MenuPrincipal(ComicCollectorSystem sistema) {
        this.sistema = sistema;
        this.scanner = new Scanner(System.in);
        // Instancia los submenús, mismo scanner para evitar conflictos de input
        this.menuUsuarios = new MenuUsuarios(sistema, scanner);
        this.menuProductos = new MenuProductos(sistema, scanner);
        this.menuOperaciones = new MenuOperaciones(sistema, scanner);
    }

    public void iniciar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n=== Menú Principal ComicCollectorSystem ===");
            System.out.println("1. Gestión de usuarios");
            System.out.println("2. Gestión de productos");
            System.out.println("3. Operaciones (compras, reservas, lanzamientos, ranking)");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1":
                    menuUsuarios.menuUsuarios();
                    break;
                case "2":
                    menuProductos.menuProductos();
                    break;
                case "3":
                    menuOperaciones.menuOperaciones();
                    break;
                case "0":
                    System.out.println("Guardando inventario actualizado...");
                    sistema.guardarInventarioActualizado();
                    System.out.println("¡Gracias por usar ComicCollectorSystem!");
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
    }
}
