package ve.ucv.campusexpress.modelo;

import java.util.Locale;
import java.util.Objects;

/**
 * Usuario registrado del sistema (entidad compartida por todas las historias de usuario).
 *
 * <p>Es inmutable. Nunca guarda la contraseña en claro: solo el hash generado por
 * {@code ContrasenaHasher}. El nombre de usuario se normaliza (sin espacios en los
 * extremos y en minúsculas) para que el inicio de sesión no distinga mayúsculas.</p>
 */
public final class Usuario {

    private final String nombreUsuario;
    private final String nombreCompleto;
    private final Rol rol;
    private final String hashContrasena;

    /**
     * @throws IllegalArgumentException si algún dato obligatorio es nulo o está en blanco
     */
    public Usuario(String nombreUsuario, String nombreCompleto, Rol rol, String hashContrasena) {
        this.nombreUsuario = normalizar(exigirTexto(nombreUsuario, "El nombre de usuario es obligatorio"));
        this.nombreCompleto = exigirTexto(nombreCompleto, "El nombre completo es obligatorio").trim();
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
        this.rol = rol;
        this.hashContrasena = exigirTexto(hashContrasena, "El hash de la contraseña es obligatorio");
    }

    /** Forma canónica de un nombre de usuario: sin espacios en los extremos y en minúsculas. */
    public static String normalizar(String nombreUsuario) {
        return nombreUsuario.trim().toLowerCase(Locale.ROOT);
    }

    private static String exigirTexto(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public Rol getRol() {
        return rol;
    }

    public String getHashContrasena() {
        return hashContrasena;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Usuario)) {
            return false;
        }
        return nombreUsuario.equals(((Usuario) otro).nombreUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreUsuario);
    }

    /** No incluye el hash de la contraseña para no filtrarlo en logs. */
    @Override
    public String toString() {
        return "Usuario{nombreUsuario='" + nombreUsuario + "', rol=" + rol + "}";
    }
}
