package comiccollector.modelos;

import java.util.ArrayList;
import java.util.List;

public class Usuario implements Comparable<Usuario> {
    private String rut;
    private String nombre;
    private String email;
    private String celular;

    // Historial de reservas/preventas
    private List<ReservaCompra> historialReservas;
    // Historial de compras
    private List<ReservaCompra> historialCompras;

    public Usuario(String rut, String nombre, String email, String celular) {
        this.rut = rut;
        this.nombre = nombre;
        this.email = email;
        this.celular = celular;
        this.historialReservas = new ArrayList<>();
        this.historialCompras = new ArrayList<>();
    }

    public String getRut() {
        return rut;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getCelular() {
        return celular;
    }

    public List<ReservaCompra> getHistorialReservas() {
        return historialReservas;
    }

    public List<ReservaCompra> getHistorialCompras() {
        return historialCompras;
    }

    // Agrega una preventa al historial
    public void agregarReserva(Comic comic, int cantidad) {
        historialReservas.add(new ReservaCompra(comic, cantidad));
    }

    // Agrega una compra al historial
    public void agregarCompra(Comic comic, int cantidad) {
        historialCompras.add(new ReservaCompra(comic, cantidad));
    }

    // Ranking por compras
    public int totalCompras() {
        int total = 0;
        for (ReservaCompra rc : historialCompras) {
            total += rc.getCantidad();
        }
        return total;
    }

    // Permite ordenar ranking por compras, en caso de empates por RUT
    @Override
    public int compareTo(Usuario otro) {
        int cmp = Integer.compare(otro.totalCompras(), this.totalCompras());
        if (cmp != 0) return cmp;
        return this.rut.compareTo(otro.rut);
    }

    // Detalles del usuario en texto.
    @Override
    public String toString() {
        return "Nombre: " + nombre +
                ", RUT: " + rut +
                ", Email: " + email +
                ", Celular: " + celular;
    }
}
