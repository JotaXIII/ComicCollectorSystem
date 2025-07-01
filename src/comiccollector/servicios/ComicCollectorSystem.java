package comiccollector.servicios;

import comiccollector.modelos.Comic;
import comiccollector.modelos.Usuario;
import comiccollector.excepciones.EmailYaRegistradoException;
import comiccollector.excepciones.ProductoYaReservadoException;
import comiccollector.excepciones.RutInvalidoException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

// Clase para administrar la logica, datos y operaciones
public class ComicCollectorSystem {

    private ArrayList<Comic> comics;
    private HashMap<String, Usuario> usuarios;
    private HashSet<String> emailsRegistrados;
    private HashSet<String> productosReservados;
    private TreeSet<LocalDate> fechasLanzamiento;
    private TreeSet<Usuario> rankingUsuarios;
    private ValidadorDatos validador;
    private HashMap<String, Comic> inventario;
    private int ultimoCodigoProducto = 0;

    private static final String COMICS_CSV = "comics.csv";
    private static final String USUARIOS_TXT = "usuarios.txt";
    private static final String RESERVAS_TXT = "reservas.txt";

    // Constructor. Inicializa colecciones y carga los datos desde archivos.
    public ComicCollectorSystem() {
        this.comics = new ArrayList<>();
        this.usuarios = new HashMap<>();
        this.emailsRegistrados = new HashSet<>();
        this.productosReservados = new HashSet<>();
        this.fechasLanzamiento = new TreeSet<>();
        this.rankingUsuarios = new TreeSet<>();
        this.validador = new ValidadorDatos();
        this.inventario = new HashMap<>();
        cargarDatosDesdeArchivos();
    }

    private void cargarDatosDesdeArchivos() {
        cargarComicsDesdeCSV(COMICS_CSV);
        cargarUsuariosDesdeTxt(USUARIOS_TXT);
        cargarReservasDesdeTxt(RESERVAS_TXT);
        actualizarInventarioEnMemoria();
    }

    // Lectura de .csv no incluye header

    private void cargarComicsDesdeCSV(String archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            br.readLine(); // Salta la cabecera

            while ((linea = br.readLine()) != null) {
                // Formato: tipo,codigo,nombre,autor,cantidad,fechaLlegada,precio
                String[] partes = linea.split(",");
                if (partes.length >= 7) {
                    String tipo = partes[0];
                    String codigo = partes[1];
                    String nombre = partes[2];
                    String autor = partes[3];
                    int cantidad = Integer.parseInt(partes[4]);
                    LocalDate fecha = partes[5].equals("null") ? null : LocalDate.parse(partes[5]);
                    double precio = Double.parseDouble(partes[6]);
                    Comic c = new Comic(codigo, tipo, nombre, autor, cantidad, fecha, precio);
                    comics.add(c);
                    inventario.put(codigo, c);
                    if (fecha != null) fechasLanzamiento.add(fecha);

                    // Actualiza el contador de códigos
                    try {
                        int num = Integer.parseInt(codigo);
                        if (num > ultimoCodigoProducto) ultimoCodigoProducto = num;
                    } catch (NumberFormatException e) {
                        // Si algún código no es numérico lo ignora
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("No se encontró archivo de cómics, se comenzará de cero.");
        }
    }

    private void cargarUsuariosDesdeTxt(String archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Formato: rut|nombre|email|celular
                String[] partes = linea.split("\\|");
                if (partes.length >= 4) {
                    Usuario u = new Usuario(partes[0], partes[1], partes[2], partes[3]);
                    usuarios.put(u.getRut(), u);
                    emailsRegistrados.add(u.getEmail());
                }
            }
        } catch (Exception e) {
            System.out.println("No se encontró archivo de usuarios, se comenzará de cero.");
        }
    }

    private void cargarReservasDesdeTxt(String archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length >= 3) {
                    String rut = partes[0];
                    String codigoComic = partes[1];
                    int cantidad = Integer.parseInt(partes[2]);
                    productosReservados.add(codigoComic);
                    Usuario u = usuarios.get(rut);
                    Comic c = inventario.get(codigoComic);
                    if (u != null && c != null) {
                        u.agregarReserva(c, cantidad);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("No se encontró archivo de reservas, se comenzará de cero.");
        }
    }

    private void actualizarInventarioEnMemoria() {
        for (Comic c : comics) {
            inventario.put(c.getCodigo(), c);
        }
    }

    // Gestión de Usuarios

    public void registrarUsuario(String rut, String nombre, String email, String celular)
            throws EmailYaRegistradoException, RutInvalidoException, IllegalArgumentException {
        validador.validarRut(rut);
        validador.validarNoVacio(nombre, "nombre");
        validador.validarNoVacio(email, "email");
        validador.validarNoVacio(celular, "celular");
        validador.validarCelular(celular);
        validador.validarEmailUnico(email, emailsRegistrados);

        nombre = validador.formatearNombre(nombre);

        Usuario usuario = new Usuario(rut, nombre, email, celular);
        usuarios.put(rut, usuario);
        emailsRegistrados.add(email);

        guardarUsuarioEnArchivo(usuario);
    }

    private void guardarUsuarioEnArchivo(Usuario usuario) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USUARIOS_TXT, true))) {
            pw.println(usuario.getRut() + "|" + usuario.getNombre() + "|" + usuario.getEmail() + "|" + usuario.getCelular());
        } catch (Exception e) {
            System.out.println("ERROR. No se pudo guardar el usuario en archivo.");
        }
    }

    // Gestión de Productos

    private String generarNuevoCodigo() {
        ultimoCodigoProducto++;
        return String.format("%03d", ultimoCodigoProducto); // Ej: 001, 002, ...
    }

    public void agregarComic(Comic nuevoComic) throws IllegalArgumentException {
        validador.validarNoVacio(nuevoComic.getNombre(), "nombre");
        validador.validarNoVacio(nuevoComic.getAutorOFabricante(), "autor/fabricante");
        validador.validarNoNegativo(nuevoComic.getCantidadDisponible(), "cantidad disponible");
        validador.validarNoNegativo(nuevoComic.getPrecio(), "precio");

        String nuevoCodigo = generarNuevoCodigo();
        nuevoComic.setCodigo(nuevoCodigo);

        comics.add(nuevoComic);
        inventario.put(nuevoCodigo, nuevoComic);

        if (nuevoComic.getFechaLlegada() != null) {
            fechasLanzamiento.add(nuevoComic.getFechaLlegada());
        }
    }

    public boolean eliminarComic(String codigo) {
        Comic comic = inventario.get(codigo);
        if (comic != null) {
            comics.remove(comic);
            inventario.remove(codigo);
            productosReservados.remove(codigo);
            if (comic.getFechaLlegada() != null) {
                fechasLanzamiento.remove(comic.getFechaLlegada());
            }
            return true;
        }
        return false;
    }

    // Compras y Reservas

    public void realizarCompra(String rutUsuario, String codigoComic, int cantidad)
            throws IllegalArgumentException {
        Usuario usuario = usuarios.get(rutUsuario);
        Comic comic = inventario.get(codigoComic);
        validador.validarNoNegativo(cantidad, "cantidad a comprar");
        if (usuario == null) throw new IllegalArgumentException("Usuario no encontrado.");
        if (comic == null) throw new IllegalArgumentException("Producto no encontrado.");
        if (comic.getFechaLlegada() != null && comic.getFechaLlegada().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("El producto aún no está disponible para la venta.");
        }
        if (comic.getCantidadDisponible() < cantidad) {
            throw new IllegalArgumentException("No hay suficiente stock disponible.");
        }
        comic.setCantidadDisponible(comic.getCantidadDisponible() - cantidad);
        usuario.agregarCompra(comic, cantidad);
        actualizarRankingUsuarios(usuario);
    }

    public void hacerReserva(String rutUsuario, String codigoComic, int cantidad)
            throws ProductoYaReservadoException, IllegalArgumentException {
        Usuario usuario = usuarios.get(rutUsuario);
        Comic comic = inventario.get(codigoComic);
        if (usuario == null) throw new IllegalArgumentException("Usuario no encontrado.");
        if (comic == null) throw new IllegalArgumentException("Producto no encontrado.");
        if (comic.getFechaLlegada() == null || comic.getFechaLlegada().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Este producto no está en preventa.");
        }
        if (comic.getCantidadDisponible() < cantidad) {
            throw new IllegalArgumentException("No hay suficiente stock disponible para reservar.");
        }
        comic.setCantidadDisponible(comic.getCantidadDisponible() - cantidad);
        productosReservados.add(codigoComic);
        usuario.agregarReserva(comic, cantidad);
        guardarReservaEnArchivo(usuario.getRut(), codigoComic, cantidad);
    }

    private void guardarReservaEnArchivo(String rut, String codigoComic, int cantidad) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RESERVAS_TXT, true))) {
            pw.println(rut + "|" + codigoComic + "|" + cantidad);
        } catch (Exception e) {
            System.out.println("ERROR. No se pudo guardar la reserva en archivo.");
        }
    }

    //  Gestión de Fechas y Ranking

    private void actualizarRankingUsuarios(Usuario usuario) {
        rankingUsuarios.remove(usuario);
        rankingUsuarios.add(usuario);
    }

    //  Guardar Inventario Actualizado

    public void guardarInventarioActualizado() {
        int sufijo = 2;
        File nuevoArchivo;
        do {
            nuevoArchivo = new File("comics_" + sufijo + ".csv");
            sufijo++;
        } while (nuevoArchivo.exists());

        try (PrintWriter pw = new PrintWriter(new FileWriter(nuevoArchivo))) {
            // Escribe cabecera
            pw.println("tipo,codigo,nombre,autor,cantidad,fechaLlegada,precio");
            for (Comic c : inventario.values()) {
                pw.println(
                        c.getTipo() + "," +
                                c.getCodigo() + "," +
                                c.getNombre() + "," +
                                c.getAutorOFabricante() + "," +
                                c.getCantidadDisponible() + "," +
                                (c.getFechaLlegada() != null ? c.getFechaLlegada() : "null") + "," +
                                c.getPrecio()
                );
            }
            System.out.println("Inventario actualizado guardado en: " + nuevoArchivo.getName());
        } catch (Exception e) {
            System.out.println("ERROR. No se pudo guardar el archivo de inventario actualizado.");
        }
    }

    // GETTERS PÚBLICOS PARA EL MENÚ Y OTRAS CLASES

    public ArrayList<Comic> getComics() {
        return comics;
    }

    public Collection<Usuario> getUsuarios() {
        return usuarios.values();
    }

    public HashSet<String> getProductosReservados() {
        return productosReservados;
    }

    public HashSet<String> getEmailsRegistrados() {
        return emailsRegistrados;
    }

    public ValidadorDatos getValidador() {
        return this.validador;
    }

    public Set<LocalDate> getFechasLanzamiento() {
        return Collections.unmodifiableSet(fechasLanzamiento);
    }

    public Set<Usuario> getRankingUsuarios() {
        return Collections.unmodifiableSet(rankingUsuarios);
    }

    public Comic buscarComicPorCodigo(String codigo) {
        return inventario.get(codigo);
    }

    public Usuario buscarUsuarioPorRut(String rut) {
        return usuarios.get(rut);
    }
}
