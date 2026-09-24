package modelo;


/**
 * Reglas de autorización según el rol (Tarea 3 de HU-001).
 *
 * <p>Es el lugar único donde se decide "quién puede qué". Las demás historias lo reutilizan; por
 * ejemplo, HU-013 (Generar Reportes) debe llamar a {@link #exigirAdministrador(Usuario)}.</p>
 */
public class AutorizacionServicio {

    public boolean esAdministrador(Usuario usuario) {
        return usuario != null && usuario.getRol().esAdministrador();
    }

    public boolean esPasajero(Usuario usuario) {
        return usuario != null && usuario.getRol().esPasajero();
    }

    /**
     * @throws AccesoDenegadoException si no hay usuario autenticado o no es Administrador
     */
    public void exigirAdministrador(Usuario usuario) {
        if (!esAdministrador(usuario)) {
            throw new AccesoDenegadoException();
        }
    }
}
