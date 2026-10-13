package vista;

/** Operaciones que necesita el controlador del monedero virtual. */
public interface MonederoVista {
    /** Se ejecuta al presionar "Aceptar" en la pantalla de recarga, con los datos escritos. */
    void setAccionAceptarRecarga(java.util.function.Consumer<MonederoRecargaDatos> accion);
    void setAccionCerrar(Runnable accion);
    void mostrarSaldo(String saldo);
    /** Vuelve a la pantalla principal del monedero. */
    void mostrarPantallaMonedero();
    /** Muestra la pantalla de recarga con el formulario vacío. */
    void mostrarPantallaRecarga();
    void mostrarMensaje(String mensaje, String titulo, int tipo);
    void mostrarError(String mensaje);
    void mostrar();
    void cerrar();
}
