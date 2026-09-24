package modelo;

import java.util.Arrays;

public class AutenticacionServicio {
    public static final int LONGITUD_MINIMA_CONTRASENA = 6;

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
        if (nombre == null || nombre.isBlank() || apellido == null || apellido.isBlank()
                || cedula == null || cedula.isBlank() || username == null || username.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }
        if (rol == null || rol == Rol.ADMINISTRADOR) {
            throw new IllegalArgumentException("El rol seleccionado no es válido para registro público.");
        }
        if (password.length() < LONGITUD_MINIMA_CONTRASENA) {
            throw new IllegalArgumentException("La contraseña debe tener al menos "
                    + LONGITUD_MINIMA_CONTRASENA + " caracteres.");
        }
    }

    static String normalizar(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    public boolean existe(String username) {
        return usuarioRepositorio.existePorUsername(username);
    }
}
