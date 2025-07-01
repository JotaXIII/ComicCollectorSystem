package comiccollector.excepciones;

// Excepción para que no pueda registrar correo ya existente en sistema
public class EmailYaRegistradoException extends Exception {

    public EmailYaRegistradoException(String email) {
        super("El email '" + email + "' ya está registrado en el sistema. Por favor, utilice otro.");
    }
}
