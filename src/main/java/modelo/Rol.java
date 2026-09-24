package modelo;

public enum Rol {
    ADMINISTRADOR("Administrador"),
    ESTUDIANTE("Estudiante"),
    EMPLEADO("Empleado"),
    PROFESOR("Profesor"),
    PUBLICO_GENERAL("Público General");

    private final String nombreVisible;

    Rol(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getNombreVisible() { return nombreVisible; }
    public String getEtiqueta() { return nombreVisible; }

    public boolean esAdministrador() { return this == ADMINISTRADOR; }
    public boolean esPasajero() { return this != ADMINISTRADOR; }

    @Override
    public String toString() { return nombreVisible; }

    public static Rol desdeCadena(String texto) {
        if (texto == null) return PUBLICO_GENERAL;
        String t = texto.trim();
        for (Rol r : values()) {
            if (r.name().equalsIgnoreCase(t) || r.nombreVisible.equalsIgnoreCase(t)) {
                return r;
            }
        }
        return PUBLICO_GENERAL;
    }
}
