package ve.ucv.campusexpress.modelo.excepcion;

import ve.ucv.campusexpress.util.Mensajes;

/**
 * El usuario o la contraseña ingresados no son correctos (HU-001, escenario 2).
 * El mensaje es siempre el mismo para no revelar cuál de los dos datos falló.
 */
public class CredencialesInvalidasException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CredencialesInvalidasException() {
        super(Mensajes.get("login.error.credenciales"));
    }
}
