package vista;

/** Lo que el controlador necesita de las pantallas principales (pasajero y administrador). */
public interface PrincipalVista {

    void setAccionCerrarSesion(Runnable accion);

    void mostrar();

    void cerrar();
}
