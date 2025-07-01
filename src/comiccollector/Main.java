package comiccollector;

import comiccollector.servicios.ComicCollectorSystem;
import comiccollector.util.MenuPrincipal;

public class Main {
    public static void main(String[] args) {
        ComicCollectorSystem sistema = new ComicCollectorSystem();
        MenuPrincipal menu = new MenuPrincipal(sistema);
        menu.iniciar();
    }
}
