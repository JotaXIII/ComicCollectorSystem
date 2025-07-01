package comiccollector.modelos;

import java.time.LocalDate;

// Clase para representar producto en la tienda.
public class Comic {

    private String codigo;
    private String tipo;
    private String nombre;
    private String autorOFabricante;
    private int cantidadDisponible;
    // Fecha de llegada (null si ya está disponible)
    private LocalDate fechaLlegada;
    private double precio;

    // Constructor principal
    public Comic(String codigo, String tipo, String nombre, String autorOFabricante, int cantidadDisponible, LocalDate fechaLlegada, double precio) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.nombre = nombre;
        this.autorOFabricante = autorOFabricante;
        this.cantidadDisponible = cantidadDisponible;
        this.fechaLlegada = fechaLlegada;
        this.precio = precio;
    }

    public String getCodigo() {
        return codigo;
    }
    public String getTipo() {
        return tipo;
    }
    public String getNombre() {
        return nombre;
    }
    public String getAutorOFabricante() {
        return autorOFabricante;
    }
    public int getCantidadDisponible() {
        return cantidadDisponible;
    }
    public LocalDate getFechaLlegada() {
        return fechaLlegada;
    }
    public double getPrecio() {
        return precio;
    }

    // Asigna código al producto
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    // Detalles del producto en texto.
    @Override
    public String toString() {
        return "Código: " + (codigo != null ? codigo : "(pendiente)") +
                ", Tipo: " + tipo +
                ", Nombre: " + nombre +
                ", Autor/Fabricante: " + autorOFabricante +
                ", Cantidad: " + cantidadDisponible +
                ", Fecha llegada: " + (fechaLlegada != null ? fechaLlegada : "En Tienda") +
                ", Precio: $" + precio;
    }
}
