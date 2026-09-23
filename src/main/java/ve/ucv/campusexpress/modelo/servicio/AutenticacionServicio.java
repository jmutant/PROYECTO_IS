package ve.ucv.campusexpress.modelo.servicio;

import java.util.Objects;
import ve.ucv.campusexpress.modelo.Rol;
import ve.ucv.campusexpress.modelo.Usuario;
import ve.ucv.campusexpress.modelo.excepcion.CredencialesInvalidasException;
import ve.ucv.campusexpress.modelo.excepcion.UsuarioDuplicadoException;
import ve.ucv.campusexpress.modelo.repositorio.UsuarioRepositorio;
import ve.ucv.campusexpress.modelo.seguridad.ContrasenaHasher;

/**
 * Registro de usuarios y validación de credenciales (Tarea 2 de HU-001).
 */
public class AutenticacionServicio {

    /** Longitud mínima de contraseña al registrar un usuario (ajustable por el equipo). */
    public static final int LONGITUD_MINIMA_CONTRASENA = 6;

    private final UsuarioRepositorio repositorio;

    public AutenticacionServicio(UsuarioRepositorio repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio es obligatorio");
    }

    /**
     * Registra un usuario nuevo guardando solo el hash de su contraseña.
     *
     * @throws IllegalArgumentException si falta algún dato o la contraseña es demasiado corta
     * @throws UsuarioDuplicadoException si el nombre de usuario ya está registrado
     */
    public Usuario registrar(String nombreUsuario, String nombreCompleto, char[] contrasena, Rol rol) {
        if (contrasena == null || contrasena.length < LONGITUD_MINIMA_CONTRASENA) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos " + LONGITUD_MINIMA_CONTRASENA + " caracteres");
        }
        // El constructor de Usuario valida el resto de los datos
        Usuario usuario = new Usuario(nombreUsuario, nombreCompleto, rol, ContrasenaHasher.hashear(contrasena));
        if (repositorio.existePorNombreUsuario(usuario.getNombreUsuario())) {
            throw new UsuarioDuplicadoException(usuario.getNombreUsuario());
        }
        repositorio.guardar(usuario);
        return usuario;
    }

    /**
     * Valida las credenciales y devuelve el usuario autenticado.
     *
     * <p>Cualquier falla (campos vacíos, usuario inexistente o contraseña incorrecta) produce la
     * misma excepción y el mismo mensaje, como pide el escenario 2 de HU-001.</p>
     *
     * @throws CredencialesInvalidasException si las credenciales no son válidas
     */
    /** Indica si ya existe un usuario registrado. */
    public boolean existe(String nombreUsuario) {
        return repositorio.existePorNombreUsuario(nombreUsuario);
    }

    public Usuario autenticar(String nombreUsuario, char[] contrasena) {
        if (nombreUsuario == null || nombreUsuario.isBlank() || contrasena == null || contrasena.length == 0) {
            throw new CredencialesInvalidasException();
        }
        Usuario usuario = repositorio.buscarPorNombreUsuario(nombreUsuario)
                .orElseThrow(CredencialesInvalidasException::new);
        if (!ContrasenaHasher.verificar(contrasena, usuario.getHashContrasena())) {
            throw new CredencialesInvalidasException();
        }
        return usuario;
    }
}
