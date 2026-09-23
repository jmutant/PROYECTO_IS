package ve.ucv.campusexpress.modelo.excepcion;

/** Ya existe un usuario registrado con ese nombre de usuario. */
public class UsuarioDuplicadoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UsuarioDuplicadoException(String nombreUsuario) {
        super("Ya existe un usuario registrado con el nombre '" + nombreUsuario + "'");
    }
}
