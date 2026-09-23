package ve.ucv.campusexpress.modelo;

/** Estado operativo de una unidad del SGTU. */
public enum EstadoUnidad {
    ACTIVO("Activo"),
    EN_MANTENIMIENTO("En mantenimiento"),
    FUERA_DE_SERVICIO("Fuera de servicio");

    private final String etiqueta;

    EstadoUnidad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
