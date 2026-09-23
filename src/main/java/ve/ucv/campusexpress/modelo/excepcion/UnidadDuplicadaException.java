package ve.ucv.campusexpress.modelo.excepcion;

/** Ya existe una unidad registrada con la placa indicada. */
public class UnidadDuplicadaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnidadDuplicadaException(String placa) {
        super("Ya existe una unidad registrada con la placa '" + placa + "'");
    }
}
