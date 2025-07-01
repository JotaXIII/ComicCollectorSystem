package comiccollector.excepciones;

// Excepción para cuando se utilice un rut no valido.
public class RutInvalidoException extends Exception {

    public RutInvalidoException(String rut) {
        super("El RUT '" + rut + "' no es válido.");
    }
}
