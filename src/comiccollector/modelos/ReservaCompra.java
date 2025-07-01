package comiccollector.modelos;

public class ReservaCompra {
    private Comic comic;
    private int cantidad;

    public ReservaCompra(Comic comic, int cantidad) {
        this.comic = comic;
        this.cantidad = cantidad;
    }

    public Comic getComic() {
        return comic;
    }

    public int getCantidad() {
        return cantidad;
    }
}
