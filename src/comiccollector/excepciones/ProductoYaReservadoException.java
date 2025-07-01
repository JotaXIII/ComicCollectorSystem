package comiccollector.excepciones;

// Excepción para que usuario no reserve multiples veces el producto
public class ProductoYaReservadoException extends Exception {

    public ProductoYaReservadoException(String codigoProducto) {
        super("El producto con código '" + codigoProducto + "' ya está reservado en preventa.");
    }
}
