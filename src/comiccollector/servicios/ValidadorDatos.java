package comiccollector.servicios;

import comiccollector.excepciones.EmailYaRegistradoException;
import comiccollector.excepciones.RutInvalidoException;

import java.util.HashSet;
import java.util.regex.Pattern;

// Clase para validar datos de entrada
public class ValidadorDatos {

    // RUT : 1.234.567-8 o 12.345.678-9
    private static final Pattern RUT_PATTERN = Pattern.compile("^\\d{1,2}\\.\\d{3}\\.\\d{3}-[\\dkK]$");
    // Formato corréo
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$");
    // Celular de 8 dígitos
    private static final Pattern CELULAR_PATTERN = Pattern.compile("^\\d{8}$");

    // Valida formato de RUT
    public void validarRut(String rut) throws RutInvalidoException {
        if (rut == null || !RUT_PATTERN.matcher(rut).matches()) {
            throw new RutInvalidoException("RUT Inválido.");
        }
    }

    // Valida que string no este vacio
    public void validarNoVacio(String dato, String campo) {
        if (dato == null || dato.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + campo + "' no puede estar vacío.");
        }
    }

    // Valida que valores no sean negativos
    public void validarNoNegativo(int valor, String campo) {
        if (valor < 0) {
            throw new IllegalArgumentException("El campo '" + campo + "' no puede ser negativo.");
        }
    }

    public void validarNoNegativo(double valor, String campo) {
        if (valor < 0) {
            throw new IllegalArgumentException("El campo '" + campo + "' no puede ser negativo.");
        }
    }

    // Validación de 8 dígitos
    public void validarCelular(String celular) {
        if (celular == null || !CELULAR_PATTERN.matcher(celular).matches()) {
            throw new IllegalArgumentException("El celular debe tener 8 dígitos.");
        }
    }

    // HashSet para que valide que correo no esté repetido
    public void validarEmailUnico(String email, HashSet<String> emailsRegistrados) throws EmailYaRegistradoException {
        if (emailsRegistrados.contains(email)) {
            throw new EmailYaRegistradoException("El email '" + email + "' ya está registrado.");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("El email ingresado no tiene un formato válido.");
        }
    }

    // Formato para nombres, primera letra mayúscula.
    public String formatearNombre(String nombre) {
        if (nombre == null) return "";
        String[] palabras = nombre.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                sb.append(Character.toUpperCase(palabra.charAt(0)));
                if (palabra.length() > 1) {
                    sb.append(palabra.substring(1));
                }
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }
}
