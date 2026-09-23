package ve.ucv.campusexpress.modelo;

/**
 * Rol de un usuario registrado en el SGTU.
 *
 * <p>Para la autorización solo importa si el usuario es {@link #ADMINISTRADOR} o
 * "pasajero" (cualquiera de los demás roles): esa es la distinción que usa HU-001
 * para decidir a qué pantalla principal se redirige.</p>
 */
public enum Rol {
    ESTUDIANTE("Estudiante"),
    EMPLEADO("Empleado"),
    PROFESOR("Profesor"),
    PUBLICO_GENERAL("Público General"),
    ADMINISTRADOR("Administrador");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Nombre legible del rol, para mostrar en pantalla. */
    public String getEtiqueta() {
        return etiqueta;
    }

    public boolean esAdministrador() {
        return this == ADMINISTRADOR;
    }

    public boolean esPasajero() {
        return !esAdministrador();
    }
}
