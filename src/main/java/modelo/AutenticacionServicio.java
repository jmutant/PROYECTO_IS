package modelo;

import java.util.Arrays;

public class AutenticacionServicio {
    public static final int LONGITUD_MINIMA_CONTRASENA = 6;

    /** La cédula debe ser un número de 8 dígitos entre CEDULA_MINIMA y CEDULA_MAXIMA (inclusive). */
    public static final int CEDULA_MINIMA = 10_000_000;
    public static final int CEDULA_MAXIMA = 99_999_999;
    public static final String MENSAJE_CEDULA_INVALIDA =
            "La cédula debe ser un número entre " + CEDULA_MINIMA + " y " + CEDULA_MAXIMA + ".";

    private final UsuarioRepositorio usuarioRepositorio;

    public AutenticacionServicio(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public Usuario iniciarSesion(String username, String password) {
        if (password == null) return autenticar(username, (char[]) null);
        return autenticar(username, password.toCharArray());
    }

    public Usuario autenticar(String username, char[] password) {
        if (username == null || username.isBlank() || password == null || password.length == 0) {
            throw new CredencialesInvalidasException();
        }
        Usuario usuario = usuarioRepositorio.buscarPorUsername(username.trim())
                .orElseThrow(() -> new CredencialesInvalidasException());
        String ingresada = new String(password);
        try {
            if (!usuario.coincidePassword(ingresada)) {
                throw new CredencialesInvalidasException();
            }
            return usuario;
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    public Usuario autenticar(String username, String password) {
        if (password == null) throw new CredencialesInvalidasException();
        return autenticar(username, password.toCharArray());
    }

    public void registrarUsuario(String nombre, String apellido, String cedula, Rol rol,
                                String username, String password) {
        validarRegistro(nombre, apellido, cedula, rol, username, password);
        Usuario nuevo = new Usuario(
                nombre.trim(), apellido.trim(), cedula.trim(), rol,
                username.trim(), password
        );
        usuarioRepositorio.guardar(nuevo);
    }

    /**
     * Crea un usuario con rol Administrador. Las mismas validaciones del registro público
     * (campos obligatorios, cédula, contraseña) aplican aquí; la verificación de que quien
     * lo invoca es Administrador la hace el navegador con AutorizacionServicio.
     */
    public void registrarAdministrador(String nombre, String apellido, String cedula,
                                       String username, String password) {
        validarCamposObligatorios(nombre, apellido, cedula, username, password);
        validarCedula(cedula);
        validarPassword(password);
        Usuario nuevo = new Usuario(
                nombre.trim(), apellido.trim(), cedula.trim(), Rol.ADMINISTRADOR,
                username.trim(), password
        );
        usuarioRepositorio.guardar(nuevo);
    }

    // API anterior conservada para los tests y partes existentes del proyecto.
    public void registrar(String username, String nombreCompleto, char[] password, Rol rol) {
        if (username == null || username.isBlank() || nombreCompleto == null || nombreCompleto.isBlank()
                || rol == null || password == null || password.length < LONGITUD_MINIMA_CONTRASENA) {
            throw new IllegalArgumentException("Datos de registro inválidos.");
        }
        String nombre = nombreCompleto.trim();
        String apellido = "";
        int espacio = nombre.indexOf(' ');
        if (espacio > 0) {
            apellido = nombre.substring(espacio + 1).trim();
            nombre = nombre.substring(0, espacio).trim();
        }
        registrarUsuarioInterno(nombre, apellido, "", rol, username, new String(password));
        Arrays.fill(password, '\0');
    }

    private void registrarUsuarioInterno(String nombre, String apellido, String cedula, Rol rol,
                                        String username, String password) {
        Usuario nuevo = new Usuario(nombre, apellido, cedula, rol,
                username.trim(), password);
        usuarioRepositorio.guardar(nuevo);
    }

    private void validarRegistro(String nombre, String apellido, String cedula, Rol rol,
                                String username, String password) {
        validarCamposObligatorios(nombre, apellido, cedula, username, password);
        if (rol == null || rol == Rol.ADMINISTRADOR) {
            throw new IllegalArgumentException("El rol seleccionado no es válido para registro público.");
        }
        validarCedula(cedula);
        validarPassword(password);
    }

    private void validarCamposObligatorios(String nombre, String apellido, String cedula,
                                           String username, String password) {
        if (nombre == null || nombre.isBlank() || apellido == null || apellido.isBlank()
                || cedula == null || cedula.isBlank() || username == null || username.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }
    }

    /** Solo acepta 8 dígitos ASCII cuyo valor esté entre CEDULA_MINIMA y CEDULA_MAXIMA. */
    static void validarCedula(String cedula) {
        String limpia = cedula == null ? "" : cedula.trim();
        if (!limpia.matches("[0-9]{8}")) {
            throw new IllegalArgumentException(MENSAJE_CEDULA_INVALIDA);
        }
        int valor = Integer.parseInt(limpia);
        if (valor < CEDULA_MINIMA || valor > CEDULA_MAXIMA) {
            throw new IllegalArgumentException(MENSAJE_CEDULA_INVALIDA);
        }
    }

    private void validarPassword(String password) {
        if (password.length() < LONGITUD_MINIMA_CONTRASENA) {
            throw new IllegalArgumentException("La contraseña debe tener al menos "
                    + LONGITUD_MINIMA_CONTRASENA + " caracteres.");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un número.");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra mayúscula.");
        }
    }

    static String normalizar(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    public boolean existe(String username) {
        return usuarioRepositorio.existePorUsername(username);
    }
}
