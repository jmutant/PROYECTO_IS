package ve.ucv.campusexpress.modelo;

/** Tipo de ruta contemplado por HU-003. */
public enum TipoRuta {
    URBANA("Urbana"),
    EXTRAURBANA("Extraurbana");

    private final String etiqueta;

    TipoRuta(String etiqueta) {
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
