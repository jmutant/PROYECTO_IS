package vista;

/** Lo que el controlador necesita de la ventana "reserva" (por ahora solo regresar). */
public interface ReservarVista {

    void setAccionRegresar(Runnable accion);

    void mostrar();

    void cerrar();
}
