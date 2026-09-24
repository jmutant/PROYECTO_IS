package modelo;

import util.Mensajes;

/** El usuario no tiene el rol necesario para usar la funcionalidad solicitada. */
public class AccesoDenegadoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccesoDenegadoException() {
        super(Mensajes.get("autorizacion.error.soloAdministrador"));
    }
}
